package qxmk.mk;

import java.io.*;

public class key {
    public static void wkey(String key) {
        final File file = new File("key");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(key);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void ckey() {
        final File file = new File("key");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static String rkey() {
        StringBuilder src = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("key"));
            StringBuilder sb;
            while (bufferedReader.ready()) {
                sb = new StringBuilder(bufferedReader.readLine());
                src.append(sb);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return src.toString();
    }
}
