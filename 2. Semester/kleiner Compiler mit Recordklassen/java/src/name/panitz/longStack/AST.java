package name.panitz.longStack;

import java.io.*;
import java.util.*;
import java.util.function.BinaryOperator;


public sealed interface AST permits AST.IntLiteral, AST.Var, AST.Assign, AST.OpExpr, AST.IfExpr, AST.WhileExpr, AST.Sequence, AST.FunCall {


    Fun factorial = new Fun("fac", List.of("x"), new Sequence(List.of(new Assign(new Var("r"), new IntLiteral(1)), new WhileExpr(new Var("x"), new Sequence(List.of(new Assign(new Var("r"), new OpExpr(new Var("r"), BinOP.mult, new Var("x"))), new Assign(new Var("x"), new OpExpr(new Var("x"), BinOP.sub, new IntLiteral(1)))))), new Var("r"))));
    Fun factorialRek = new Fun("f", List.of("x"), new IfExpr(new OpExpr(new Var("x"), BinOP.eq, new IntLiteral(0)), new IntLiteral(1), new OpExpr(new Var("x"), BinOP.mult, new FunCall("f", List.of(new OpExpr(new Var("x"), BinOP.sub, new IntLiteral(1)))))));
    Fun minus = new Fun("minus", List.of("x", "y"), new OpExpr(new Var("x"), BinOP.sub, new Var("y")));
    Fun fib = new Fun("fib", List.of("x"), new IfExpr(new OpExpr(new IntLiteral(0), BinOP.eq, new Var("x")), new IntLiteral(0), new IfExpr(new OpExpr(new IntLiteral(1), BinOP.eq, new Var("x")), new IntLiteral(1), new OpExpr(new FunCall("fib", List.of(new OpExpr(new Var("x"), BinOP.sub, new IntLiteral(2)))), BinOP.add, new FunCall("fib", List.of(new OpExpr(new Var("x"), BinOP.sub, new IntLiteral(1))))))));
    String[] rs = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
    String[] help = {"<expr>         evaluate <expr>", "':q'           quits the interpreter", "':defs'        System.out.println();shows defined function names", "':s <funname>' prints function definition of <funname>", "':?            this help"};

    static String show(Fun fd) {
        var r = new ExWriter(new StringWriter());
        r.write("fun ");
        r.write(fd.name);
        r.write("(");
        var first = true;
        for (var arg : fd.args) {
            if (first) first = false;
            else r.write(", ");
            r.write(arg);
        }
        r.write(") = ");
        r.addIndent();
        r.nl();
        fd.body.show(r);
        return r.w.toString();
    }

    static void asm(Fun f, ExWriter r) {


        r.nl();
        r.lnwrite(".globl  " + f.name);
        if (!System.getProperty("os.name").toLowerCase().contains("win"))
            r.lnwrite(".type  " + f.name + ", @function");
        r.lnwrite(f.name + ":");
        r.addIndent();


        r.lnwrite("pushq %rbp");
        r.lnwrite("movq %rsp, %rbp");


        var registerArgs = Math.min(rs.length, f.args.size());
        var env = new HashMap<String, Integer>();


        var sp = -8;
        for (var i = 0; i < registerArgs; i++) {
            r.lnwrite("movq " + rs[i] + ", " + sp + "(%rbp)");
            env.put(f.args.get(i), sp);
            sp = sp - 8;
        }


        var vs = f.body.getVars();
        vs.removeAll(f.args);
        for (var v : vs) {
            env.put(v, sp);
            sp = sp - 8;
        }
        r.lnwrite("subq $" + (-sp) + ", %rsp");


        sp = 16;
        for (var i = registerArgs; i < f.args.size(); i++) {
            env.put(f.args.get(i), sp);
            sp += 8;
        }


        f.body.asm(Map.of(), env, r);


        r.lnwrite("movq %rbp, %rsp");
        r.lnwrite("popq %rbp");
        r.lnwrite("ret");
        r.subIndent();
    }

    static String asm(List<Fun> fs) {
        var r = new ExWriter(new StringWriter());
        fs.forEach(f -> asm(f, r));
        return r.w.toString();
    }

    static void printHelp() {
        for (var h : help) System.out.println(h);
    }

    static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("usage: java name.panitz.longStack.AST [-i] filename");
            System.out.println("  where -i starts interpreter " + "otherwise assembler code is generated");
            return;
        }


        var interpreter = args[0].equals("-i");
        var funDefs = LongStackParser.parseFunDefs(args[0].equals("-i") ? args[1] : args[0]);


        if (interpreter) {
            var in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("TUGS (Tiny Usable Great System) :? for help");


            while (true) {
                System.out.print("> ");
                var ln = in.readLine();


                if (ln.equals(":q")) break;


                if (ln.equals(":?")) {
                    printHelp();
                    continue;
                }


                if (ln.equals(":defs")) {
                    funDefs.parallelStream().forEach(fd -> System.out.println(fd.name));
                    continue;
                }
                var showFunction = ln.startsWith(":s ");
                try {


                    if (showFunction) {
                        var funname = ln.substring(2).trim();
                        Fun fundef = funDefs.stream().reduce(null, (r, fd) -> (r == null && fd.name.equals(funname)) ? fd : r, (fd1, fd2) -> fd1 == null ? fd2 : fd1);
                        if (fundef != null) System.out.println(show(fundef));
                        else System.out.println("unknown function: " + funname);


                    } else {
                        System.out.println(LongStackParser.parseExpr(ln).ev(funDefs));
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }


        } else {
            var out = new FileWriter(args[0].substring(0, args[0].lastIndexOf('.')) + ".s");
            var o = new ExWriter(out);
            for (var fun : funDefs) asm(fun, o);
            o.nl();
            out.close();
        }
    }

    default String whatAreYou() {
        return switch (this) {
            case Var v -> "Variable mit Namen " + v.name;
            case IntLiteral il -> "Literal mit Wert " + il.n;
            case FunCall fc -> "Aufruf der Funktion: " + fc.name;
            default -> "Irgend ein anderer Baumknoten";
        };
    }

    default String whatAreYou2() {
        return switch (this) {
            case Var(var name) -> "Variable mit Namen " + name;
            case IntLiteral(var n) -> "Literal mit Wert " + n;
            case FunCall(var name, var args) -> "Aufruf der Funktion: " + name;
            default -> "Irgend ein anderer Baumknoten";
        };
    }

    default String show() {
        var r = new ExWriter(new StringWriter());
        show(r);
        return r.w.toString();
    }

    default void show(ExWriter r) {
        switch (this) {
            case Var(var name) -> r.write(name);

            case IntLiteral(var n) -> r.write(String.valueOf(n));

            case Assign(Var v, AST right) -> {
                r.write(v.show());
                r.write(":=");
                r.write(right.show());
            }

            case OpExpr(AST left, BinOP op, AST right) -> {
                r.write(left.show());
                r.write(op.name);
                r.write(right.show());
            }

            case IfExpr(AST cond, AST alt1, AST alt2) -> {
                r.write("if ");
                r.write(cond.show());
                r.nl();
                r.write(" then ");
                r.write(alt1.show());
                r.nl();
                r.write(" else ");
                r.write(alt2.show());
            }

            case WhileExpr(AST cond, AST body) -> {
                r.write("while ");
                r.write(cond.show());
                r.nl();
                r.write("do");
                r.addIndent();
                r.nl();
                r.write(body.show());
            }

            case Sequence(List<AST> sts) -> {
                r.write("{");
                r.write(sts.get(0).show());
                r.nl();
                for (int i = 1; i < sts.size(); i++) {
                    r.write(";" + sts.get(i).show());
                    r.nl();
                }
                r.write("}");
            }

            case FunCall(String name, List<AST> args) -> {
                r.write(name);
                r.write("(");
                for (int i = 0; i < args.size() - 1; i++) {
                    r.write(args.get(i).show() + ", ");
                }
                r.write(args.get(args.size() - 1).show());
                r.write(")");
            }
            default -> r.write("not yet implemented. Show: " + this);
        }
    }

    default long ev() {
        return ev(List.of());
    }

    default long ev(List<Fun> funs) {
        HashMap<String, Fun> fs = new HashMap<>();
        funs.forEach(fun -> fs.put(fun.name, fun));
        return ev(fs, new HashMap<>());
    }

    default long ev(Map<String, Fun> fs, Map<String, Long> env) {
        return switch (this) {
            case IntLiteral il -> il.n;
            case Var v -> env.get(v.name);
            case OpExpr ae -> {
                long left = ae.left.ev(fs, env);
                long right = ae.right.ev(fs, env);
                yield ae.op.op.apply(left, right);
            }
            case IfExpr ie -> {
                boolean condition = ie.cond.ev(fs, env) != 0;
                yield condition ? ie.alt1.ev(fs, env) : ie.alt2.ev(fs, env);
            }
            case Assign as -> {
                long value = as.v.ev(fs, env);
                env.put(as.v.name, value);
                yield value;
            }
            case WhileExpr we -> {
                long result = 0;
                while (we.cond.ev(fs, env) != 0) {
                    result = we.body.ev(fs, env);
                }
                yield result;
            }
            case FunCall fc -> {
                Fun fun = fs.get(fc.name);
                List<Long> argValues = fc.args.stream()
                        .map(arg -> arg.ev(fs, env))
                        .toList();

                Map<String, Long> newEnv = new HashMap<>(env);
                for (int i = 0; i < fun.args.size(); i++) {
                    newEnv.put(fun.args.get(i), argValues.get(i));
                }

                yield fun.body.ev(fs, newEnv);
            }
            case Sequence sq -> {
                long result = 0;
                for (AST ast : sq.sts) {
                    result = ast.ev(fs, env);
                }
                yield result;
            }
        };
    }

    default Set<String> getVars() {
        var r = new TreeSet<String>();
        getVars(r);
        return r;
    }

    default void getVars(TreeSet<String> r) {
        switch (this){
            case Var v -> r.add(v.name);
            case Assign as -> {r.add(as.v.name);as.right.getVars(r);}
            case FunCall f -> f.args.forEach(x->x.getVars(r));
            case IfExpr ie -> {ie.cond.getVars(r); ie.alt1.getVars(r); ie.alt2.getVars(r);}
            case OpExpr oe -> {oe.left.getVars(r); oe.right.getVars(r);}
            case Sequence sq -> sq.sts.forEach(s->s.getVars(r));
            case WhileExpr we -> {we.cond.getVars(r); we.body.getVars(r);}
            default -> {}
        }
    }

    default String asm() {
        var r = new ExWriter(new StringWriter());
        asm(new HashMap<>(), new HashMap<>(), r);
        return r.w.toString();
    }

    default void asm(Map<String, Fun> fs, Map<String, Integer> e, ExWriter r) {
        switch (this) {
            case IntLiteral il -> r.lnwrite("movq $" + il.n + ", %rax");
            case Var v -> r.lnwrite("movq " + e.get(v.name) + "(%rbp), %rax");

            case Assign assign -> {
                assign.right.asm(fs, e, r);
                r.lnwrite("movq %rax, %rbx");
                assign.v.asm(fs, e, r);
                String varName = assign.v.name;
                if (fs.containsKey(varName)) {
                    // Accessing a function variable
                    r.lnwrite("movq %rax, " + varName);
                } else {
                    // Accessing a local variable
                    r.lnwrite("movq %rbx, (%rax)");
                }
            }

            case OpExpr op -> {
                op.left.asm(fs, e, r);
                r.lnwrite("pushq %rax");
                op.right.asm(fs, e, r);
                r.lnwrite("movq %rax, %rbx");
                r.lnwrite("popq %rax");
                switch (op.op) {
                    case add -> r.lnwrite("addq %rbx, %rax");
                    case sub -> r.lnwrite("subq %rbx, %rax");
                    case mult -> r.lnwrite("imulq %rbx, %rax");
                    case eq -> {
                        r.lnwrite("cmpq %rbx, %rax");
                        r.lnwrite("sete %al");
                        r.lnwrite("movzbq %al, %rax");
                    }
                }
            }
            case Sequence seq -> {
                for (var expr : seq.sts) {
                    expr.asm(fs, e, r);
                    if (expr != seq.sts.get(seq.sts.size() - 1)) {
                        r.lnwrite("popq %rax");
                    }
                }
            }
            case WhileExpr we -> {
                int startLabel = r.next();
                int endLabel = r.next();
                r.lnwrite("jmp L" + startLabel);
                r.lnwrite("L" + endLabel + ":");
                we.body.asm(fs, e, r);
                r.lnwrite("L" + startLabel + ":");
                we.cond.asm(fs, e, r);
                r.lnwrite("cmpq $0, %rax");
                r.lnwrite("jne L" + endLabel);
            }
            case IfExpr ie -> {
                int thenLabel = r.next();
                int elseLabel = r.next();
                ie.cond.asm(fs, e, r);
                r.lnwrite("cmpq $0, %rax");
                r.lnwrite("je L" + thenLabel);
                ie.alt2.asm(fs, e, r);
                r.lnwrite("jmp L" + elseLabel);
                r.lnwrite("L" + thenLabel + ":");
                ie.alt1.asm(fs, e, r);
                r.lnwrite("L" + elseLabel + ":");
            }
            case FunCall fc -> {
                for (int i = 0; i < Math.min(5, fc.args.size()); i++) {
                    fc.args.get(i).asm(fs, e, r);
                    r.lnwrite("movq %rax, " + rs[i]);
                }
                fc.args.stream().skip(rs.length).forEach(arg -> {
                    arg.asm(fs, e, r);
                    r.lnwrite("pushq %rax");
                });
                r.lnwrite("call " + fc.name);
            }
            default -> throw new IllegalStateException("Unexpected value: " + this);
        }
    }



    enum BinOP {
        add((x, y) -> x + y, "+"),
        sub((x, y) -> x - y, "-"),
        mult((x, y) -> x * y, "*"),
        eq((x, y) -> x == y ? 1L : 0L, "=");

        BinaryOperator<Long> op;
        String name;

        BinOP(BinaryOperator<Long> op, String name) {
            this.op = op;
            this.name = name;
        }
    }

    record IntLiteral(long n) implements AST {
    }

    record Var(String name) implements AST {
    }

    record Assign(Var v, AST right) implements AST {
    }

    record OpExpr(AST left, BinOP op, AST right) implements AST {
    }

    record IfExpr(AST cond, AST alt1, AST alt2) implements AST {
    }

    record WhileExpr(AST cond, AST body) implements AST {
    }

    record Sequence(List<AST> sts) implements AST {
    }

    record FunCall(String name, List<AST> args) implements AST {
    }

    record Fun(String name, List<String> args, AST body) {
    }

    class ExWriter {
        Writer w;
        String indent = "";
        int lbl = 0;

        public ExWriter(Writer w) {
            this.w = w;
        }

        void addIndent() {
            indent = indent + "  ";
        }

        void subIndent() {
            indent = indent.substring(2);
        }

        void nl() {
            write("\n" + indent);
        }

        int next() {
            return lbl++;
        }

        void lnwrite(Object o) {
            nl();
            write(o);
        }

        void write(Object o) {
            try {
                w.write(String.valueOf(o));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
