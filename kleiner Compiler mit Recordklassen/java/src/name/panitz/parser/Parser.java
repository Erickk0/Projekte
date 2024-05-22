package name.panitz.parser;

import name.panitz.parser.Tokenizer.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Parser<T, R> {
    static <T> Parser<Token<T>, Token<T>> token(T t) {
        return (toks) -> {
            List<Pair<List<Token<T>>, Token<T>>> result = new ArrayList<>();
            if (!toks.isEmpty() && toks.get(0).token == t) {
                var tok = toks.get(0);
                result.add(new Pair<>(toks.subList(1, toks.size()), tok));
            }
            return result;
        };
    }

    static <T, R> Parser<T, R> result(R r) {
        return (toks) -> List.of(new Pair<>(toks, r));
    }

    List<Pair<List<T>, R>> parse(List<T> toks);

    default Parser<T, R> alt(Supplier<Parser<T, R>> that) {
        return (toks) -> {
            List<Pair<List<T>, R>> result = parse(toks);
            if (result.isEmpty())
                result.addAll(that.get().parse(toks));
            return result;
        };
    }

    default <R2> Parser<T, Pair<R, R2>> seq(Supplier<Parser<T, R2>> that) {
        return (toks) -> {
            List<Pair<List<T>, R>> result1s = parse(toks);
            List<Pair<List<T>, Pair<R, R2>>> result = new ArrayList<>();
            for (Pair<List<T>, R> r1 : result1s) {
                List<Pair<List<T>, R2>> result2 = that.get().parse(r1.fst);
                for (var r2 : result2) {
                    result.add(new Pair<>(r2.fst, new Pair<>(r1.snd, r2.snd)));
                }
            }
            return result;
        };
    }

    default Parser<T, List<R>> zeroToN() {
        return (toks) -> {
            List<Pair<List<T>, R>> result1s = parse(toks);
            if (result1s.isEmpty()) return List.of(new Pair<>(toks, new LinkedList<>()));
            List<Pair<List<T>, List<R>>> result = new ArrayList<>();
            for (Pair<List<T>, R> r1 : result1s) {
                result.addAll(zeroToN().map((List<R> xs) -> {
                    xs.add(0, r1.snd);
                    return xs;
                }).parse(r1.fst));
            }
            return result;
        };
    }

    default <R2> Parser<T, R2> map(Function<R, R2> f) {
        return (toks) -> {
            List<Pair<List<T>, R>> result = parse(toks);
            return result.stream().map(p -> new Pair<>(p.fst, f.apply(p.snd))).collect(Collectors.toList());
        };
    }

    class Pair<A, B> {
        public A fst;
        public B snd;

        public Pair(A fst, B snd) {
            super();
            this.fst = fst;
            this.snd = snd;
        }

        @Override
        public String toString() {
            return "(" + fst + "," + snd + ")";
        }
    }
}