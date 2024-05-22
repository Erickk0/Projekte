package name.panitz.parser;

import name.panitz.parser.Parser.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("unchecked")
public class Tokenizer<T> implements Iterator<Tokenizer.Token<T>>, Iterable<Tokenizer.Token<T>>,
        Cloneable {

    List<Pair<Pattern, T>> regEx = new LinkedList<>();
    int l = 0;
    int c = 0;
    private String[] lines = null;

    private String inputString = null;
    private BufferedReader reader;

    protected void put(String pattern, T tok) {
        regEx.add(new Pair<>(Pattern.compile("^(" + pattern + ")"), tok));
    }

    public void setReader(Reader read) {
        reader = new BufferedReader(read);
        StringBuffer buf = new StringBuffer();
        try {
            int c = 0;
            boolean take = true;
            do {
                c = reader.read();

                if (c >= 0) {
                    char ca = (char) c;
                    if (!take && ca == '*') {
                        int c2 = reader.read();
                        if (c2 == '/') {
                            take = true;
                        }
                    } else if (take && ca == '/') {
                        int c2 = reader.read();
                        if (c2 == '*') {
                            take = false;
                        } else {
                            buf.append(ca);
                            buf.append((char) c2);
                        }
                    } else if (take) {
                        buf.append(ca);
                    }
                }

            } while (c >= 0);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setInputString(buf.toString());
    }


    // private boolean headTaken = false;
    // private Token<T> theHead = null;

    public void setInputString(String ip) {
        inputString = ip.trim();
        lines = inputString.split("\\n");
        inputString = "";
    }

    @Override
    public boolean hasNext() {
        if (null == inputString || inputString.trim().isEmpty()) {
            do {
                inputString = l < lines.length ? (lines[l].trim() + " ") : null;
                l++;
                c = 0;
            } while (null != inputString && inputString.trim().isEmpty());
        }
        return null != inputString && !inputString.trim().isEmpty();
    }

    @Override
    public Token<T> next() {
        /*
         * if (headTaken) { headTaken = false; return theHead; }
         */

        if (hasNext()) {

            for (Pair<Pattern, T> t : regEx) {
                Matcher m = t.fst.matcher(inputString);
                if (m.find()) {
                    String tok = m.group();
                    c += tok.length() - 1;
                    inputString = inputString.substring(tok.length() - 1).trim() + " ";//m.replaceFirst("").trim();
                    return new Token<>(l, c, tok.substring(0, tok.length() - 1), t.snd);
                }
            }
            System.out.println(inputString);
        }
        throw new RuntimeException("Unexpected character in input: (" + l + "," + c + ")"
                + inputString + "\n" + lines[l + 1] + "\n" + lines[l + 2] + "\n" + lines[l + 3] + "\n" + lines[l + 4] + "\n" + lines[l + 5] + "\n" + lines[l + 6] + "\n" + lines[l + 7] + "\n" + lines[l + 8]);
    }

    /*
     * public Token<T> head() { if (headTaken) return theHead; theHead = next();
     * headTaken = true; return theHead; }
     */

    @Override
    public Iterator<Token<T>> iterator() {
        try {
            return (Iterator<Token<T>>) this.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public List<Token<T>> asList() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(this, 0), false)
                .collect(Collectors.toList());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return inputString;
    }

    public static class Token<T> {
        public String image;
        public T token;
        int l;
        int c;

        public Token(int l, int c, String image, T token) {
            super();
            this.l = l;
            this.c = c;
            this.image = image;
            this.token = token;
        }

        @Override
        public String toString() {
            return "(" + l + "," + c + ") " + token + " \"" + image + "\"";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Token && ((Token<T>) obj).token == token;
        }
    }
}