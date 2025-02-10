import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntUnaryOperator;

public class main {
    static String filename = "file1";
    private static IntUnaryOperator toEven = i -> i << 1;
    private static IntUnaryOperator toOdd = i -> (i << 1) + 1;
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws IOException {

        // Встретив в задании слово "цифры", я понял, что речь идйт от символьном
        // представлении числа, а не о байтовом
        // как в java.io.DataInputStream и java.io.DataOutputStream, К тому же я понял,
        // что числа надо разделять, чтобы
        // "хвост" одного числа не слипся с началом другого, порождая фантомные данные.
        // В случае бинарного формата для этого можно было бы исопльзовать тот факт, что
        // длина байтового представления
        // числа формата int всегда постоянна и равна 4.

        Thread odds = new Thread(newWriter(toOdd, "-odd"));
        Thread evens = new Thread(newWriter(toEven, "-even"));
        Thread reader = new Thread(newReader());
        reader.start();
        odds.start();
        sleep(100);
        evens.start();
    }

    private static Runnable newWriter(IntUnaryOperator intUnaryOperator, String disc) {
        return () -> {
            try (Writer ps = new OutputStreamWriter(new FileOutputStream(filename, true));
                    Writer ps1 = new OutputStreamWriter(new FileOutputStream(filename + disc, true))) {
                Random random = new Random();
                while (true) {
//                    ps.println нельзя использовать так как он делает две хзаписи в файл, числа могут слепляться
                    // пауза закомментирована
//                    sleep(200);
                    String str = intUnaryOperator.applyAsInt(random.nextInt()) + "\n";
                    ps1.write(str);
                    synchronized (lock) {
                        ps.write(str);
                    }
//                    ps.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Runnable newReader() {
        byte[] bb = new byte[1024];
        return () -> {
            // Дождаться пока файл начнёт существовать, иначе Reader упадёт с ошибкой, что
            // файл не найден.
            while (!new File(filename).exists())
                // промежуток времени выбран произвольно
                sleep(100);
            try (FileInputStream is = new FileInputStream(filename)) {
                try {
                    // Самое простое: всё что записано в файл, переписывать в консоль через буфер
                    // произвольного размера
                    while (true) {
                        int count = is.read(bb);
                        if (count > 0) {
                            System.out.write(bb, 0, count);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        };

    }

    static void sleep(long ms) {
        // надо гарантированно проспать время, досыпая в случае прерывания
        long ms1 = System.currentTimeMillis() + ms;
        for (long i = ms; i > 0; i = trySleepTill(ms1))
            ;
    }

    static long trySleepTill(long ms1) {
        try {
            // Может, хотя и не должен по спецификации, проснуться раньше времени, надо это
            // контролировать
            Thread.sleep(ms1 - System.currentTimeMillis());
            // вернуть недоспанное время
            return ms1 - System.currentTimeMillis();
        } catch (InterruptedException e) {
            // Если прервали - вернуть недоспанное время
            return ms1 - System.currentTimeMillis();
        }
    }
}
