import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class sub {

    public static void main(String[] args) throws IOException {
        @SuppressWarnings("unchecked")
        Predicate<String> preds[] = (Predicate<String>[]) new Predicate[] { (Predicate<String>) s -> true,
                (Predicate<String>) s -> s.length() > 10
                        || s.length() > 9 && s.charAt(0) > '1' && Long.valueOf(s) > Integer.MAX_VALUE,
                (Predicate<String>) s -> s.length() > 11
                        || s.length() > 10 && s.charAt(1) > '1' && Long.valueOf(s) < Integer.MIN_VALUE, };
        try (FileReader fileReader = new FileReader(main.filename);
                BufferedReader r = new BufferedReader(fileReader);
                Writer ps = new OutputStreamWriter(new FileOutputStream(main.filename + ".filtered", true));) {
            r.lines().map(s -> pair(s, s.isEmpty() || s.indexOf('-', 1) > 0 ? 0 : s.charAt(0) == '-' ? 2 : 1))
                    .filter(p -> preds[p.getValue()].test(p.getKey())).forEach(s -> {
                        try {
                            ps.write(s.getKey() + '\n');
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

        }
//for(String readLine )
//String readLine = r.readLine();
    }

//    static boolean check(long l) {
//        return l > Integer.MAX_VALUE || l < Integer.MIN_VALUE;
//    }

    static <A, B> Entry<A, B> pair(A a, B b) {
        return new AbstractMap.SimpleEntry(a, b);
    }
}
