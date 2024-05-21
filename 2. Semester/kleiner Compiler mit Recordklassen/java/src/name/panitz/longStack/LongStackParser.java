package name.panitz.longStack;

import name.panitz.parser.Parser;
import name.panitz.parser.Parser.Pair;
import name.panitz.parser.Tokenizer;
import name.panitz.parser.Tokenizer.Token;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import static name.panitz.longStack.AST.*;
import static name.panitz.longStack.LongStackParser.Tok.*;
import static name.panitz.parser.Parser.result;
import static name.panitz.parser.Parser.token;

public class LongStackParser {
    public static List<Fun> parseFunDefs(String fn) throws Exception {
        var tokenizer = new LSTokenizer();
        tokenizer.setReader(new java.io.FileReader(fn));
        var ts = tokenizer.asList();
        var parse = fundefs().get().parse(ts);
        if (parse.isEmpty()) throw new Exception("cannot parse: " + ts);
        if (parse.size() > 1) throw new Exception("multiple parses for: " + ts);
        if (!parse.get(0).fst.isEmpty())
            throw new Exception
                    ("cannot parse complete: " + fn + " unused token: " + parse.get(0).fst);
        return parse.get(0).snd;
    }


    public static AST parseExpr(String s) {
        var tokenizer = new LSTokenizer();
        tokenizer.setInputString(s);
        var ts = tokenizer.asList();
        List<Pair<List<Token<Tok>>, AST>> parse = (expr().get()).parse(ts);
        if (parse.isEmpty()) throw new RuntimeException("cannot parse: " + s);
        if (parse.size() > 1) throw new RuntimeException("multiple parses for: " + s);
        if (!parse.get(0).fst.isEmpty())
            throw new RuntimeException("cannot parse complete: " + s + " unused token: " + parse.get(0).fst);
        return parse.get(0).snd;
    }

    public static Supplier<Parser<Token<Tok>, List<Fun>>> fundefs() {
        return () -> fundef().get().zeroToN();
    }

    static Supplier<Parser<Token<Tok>, Fun>> fundef() {
        return () ->
                token(funT)
                        .seq(() -> token(identT).map(x -> x.image))
                        .seq(() -> token(lparT))
                        .seq(params())
                        .seq(() -> token(rparT))
                        .seq(() -> token(eqT))
                        .seq(expr())
                        .map(x -> new Fun(x.fst.fst.fst.fst.fst.snd, x.fst.fst.fst.snd, x.snd));
    }

    static Supplier<Parser<Token<Tok>, AST>> expr() {
        return () -> whileExpr().get()
                .alt(ifExpr())
                .alt(assignExpr())
                .alt(cmpExpr())
                .alt(block());
    }

    static Supplier<Parser<Token<Tok>, AST>> ident() {
        return
                () -> token(identT).map(x -> new Var(x.image));
    }

    static Supplier<Parser<Token<Tok>, AST>> number() {
        return
                () -> token(intConstantT).map(x -> new IntLiteral(Integer.parseInt(x.image)));
    }

    static Supplier<Parser<Token<Tok>, AST>> assignExpr() {
        return () ->
                ident().get().seq(() -> token(assignT)).seq(expr())
                        .map(x -> (AST) new Assign(((Var) x.fst.fst), x.snd));
    }

    static Supplier<Parser<Token<Tok>, AST>> cmpExpr() {
        return () ->
                addExpr().get().seq(() -> token(eqT)).seq(addExpr())
                        .map(x -> (AST) new OpExpr(x.fst.fst, BinOP.eq, x.snd))
                        .alt(addExpr());
    }

    static Supplier<Parser<Token<Tok>, AST>> addExpr() {
        return () ->
                multExpr().get()
                        .seq(() -> addOp().get().seq(multExpr()).zeroToN())
                        .map(x -> {
                            AST r = x.fst;
                            for (var snd : x.snd) {
                                r = snd.fst.apply(r, snd.snd);
                            }
                            return r;
                        });
    }

    static Supplier<Parser<Token<Tok>, BinaryOperator<AST>>> addOp() {
        BinaryOperator<AST> aop = (AST l, AST r) -> (AST) new OpExpr(l, BinOP.add, r);
        BinaryOperator<AST> sop = (AST l, AST r) -> (AST) new OpExpr(l, BinOP.sub, r);
        return () -> token(plusT).map(x -> aop).alt(() -> token(minusT).map(x -> sop));
    }


    static Supplier<Parser<Token<Tok>, AST>> multExpr() {
        return () ->
                atom().get()
                        .seq(() -> token(multT).seq(atom()).zeroToN())
                        .map(x -> {
                            AST r = x.fst;
                            for (var snd : x.snd) {
                                r = new OpExpr(r, BinOP.mult, snd.snd);
                            }
                            return r;
                        });
    }

    static Supplier<Parser<Token<Tok>, AST>> atom() {
        return () ->
                funCall().get()
                        .alt(ident())
                        .alt(number())
                        .alt(() -> token(lparT).seq(expr()).seq(() -> token(rparT)).map(x -> x.fst.snd))
                ;
    }

    static Supplier<Parser<Token<Tok>, AST>> funCall() {
        return () ->
                ident().get()
                        .seq(() -> token(lparT))
                        .seq(args())
                        .seq(() -> token(rparT))
                        .map(x -> new FunCall(((Var) x.fst.fst.fst).name(), x.fst.snd));
    }

    static Supplier<Parser<Token<Tok>, List<AST>>> args() {
        return () ->
                expr().get()
                        .seq(() -> token(commaT)
                                .seq(expr()).map(x -> x.snd).zeroToN())
                        .map(x -> {
                            var r = x.snd;
                            r.add(0, x.fst);
                            return r;
                        })
                        .alt(() -> result(List.of()));
    }

    static Supplier<Parser<Token<Tok>, List<String>>> params() {
        return () ->
                ident().get()
                        .seq(() -> token(commaT)
                                .seq(ident()).map(x -> ((Var) x.snd).name()).zeroToN())
                        .map(x -> {
                            var r = x.snd;
                            r.add(0, ((Var) x.fst).name());
                            return r;
                        })
                        .alt(() -> result(new java.util.LinkedList<String>()));
    }

    static Supplier<Parser<Token<Tok>, AST>> whileExpr() {
        return () ->
                token(whileT)
                        .seq(expr())
                        .seq(() -> token(doT))
                        .seq(expr())
                        .map(x -> new WhileExpr(x.fst.fst.snd, x.snd));
    }

    static Supplier<Parser<Token<Tok>, AST>> ifExpr() {
        return () ->
                token(ifT)
                        .seq(expr())
                        .seq(() -> token(thenT))
                        .seq(expr())
                        .seq(() -> token(elseT))
                        .seq(expr())
                        .map(x -> new IfExpr(x.fst.fst.fst.fst.snd, x.fst.fst.snd, x.snd));
    }

    static Supplier<Parser<Token<Tok>, AST>> block() {
        return () ->
                token(lbraceT)
                        .seq(
                                () -> expr().get().seq(
                                                () -> token(semicolonT).seq(expr()).map(x -> x.snd).zeroToN())
                                        .map(x -> {
                                            var r = x.snd;
                                            r.add(0, x.fst);
                                            return r;
                                        })
                        ).seq(() -> token(rbraceT)).map(x -> new Sequence(x.fst.snd));
    }

    public static void main(String[] args) {
        var tokenizer = new LSTokenizer();
        var fac = "fun f(x)= if x=0 then 1 else x*f(x-1) \n ";
        var fib = "fun fib(x)= if x=0 then 0 else if x=1 then 1 else fib(x-1)+fib(x-2)\n\n";
        var not = "fun not(x)=if x then 0 else 1\n\n";
        var frec = "fun fac(x)= {r:=1; while (not(x=0)) {r:=r*x;x:=x-1};r}";

        tokenizer.setInputString(fac + fib + not + frec);
        var ts = tokenizer.asList();
        System.out.println(ts);

        List<Pair<List<Token<Tok>>, List<Fun>>> parse = (fundefs().get()).parse(ts);
        System.out.println(parse);
        System.out.println(asm(parse.get(0).snd));

        tokenizer = new LSTokenizer();
        tokenizer.setInputString("f(1+1*3-1+2)");
        ts = tokenizer.asList();
        List<Pair<List<Token<Tok>>, AST>> parse1 = (expr().get()).parse(ts);
        System.out.println(parse1.get(0).snd.ev(parse.get(0).snd));

        tokenizer = new LSTokenizer();
        tokenizer.setInputString("fib(10)");
        ts = tokenizer.asList();
        parse1 = (expr().get()).parse(ts);
        System.out.println(parse1.get(0).snd.ev(parse.get(0).snd));

        tokenizer = new LSTokenizer();
        tokenizer.setInputString("fac(5)");
        ts = tokenizer.asList();
        parse1 = (expr().get()).parse(ts);
        System.out.println(parse1.get(0).snd.ev(parse.get(0).snd));
    }


    public enum Tok {
        ifT("if\\W"), elseT("else\\W"), thenT("then\\W"), whileT("while\\W"), funT("fun\\W"), doT("do\\W"), lparT("\\(."), rparT("\\)."), lbraceT("\\{."), rbraceT("\\}."), commaT(",."), semicolonT(";."), assignT(":=."), eqT("=."), plusT("\\+."), minusT("-."), multT("\\*."), divT("/."), modT("%."), identT("[\\_a-zA-Z]\\w*\\W"), intConstantT("(?:\\d+\\.?|\\.\\d)\\d*(?:[Ee][-+]?\\d+)?.");
        public String regEx;

        Tok(String regEx) {
            this.regEx = regEx;
        }
    }

    public static class LSTokenizer extends Tokenizer<Tok> {
        LSTokenizer() {
            for (Tok tok : Tok.values()) {
                put(tok.regEx, tok);
            }
        }
    }
}
