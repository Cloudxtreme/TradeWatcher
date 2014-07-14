package se.mydns.kupo.auctionwatcher;

import java.io.*;
import java.nio.file.*;

/**
 * Created by oskurot on 2014-07-14.
 */
public class WatchListReaderWriter {
    private static String slash = FileSystems.getDefault().getSeparator();
    private static PrintWriter bw;
    private static BufferedReader br;
    private static File wtbFile = new File("." + slash + "res" + slash + "buy.txt");
    private static File wtsFile = new File("." + slash + "res" + slash + "sell.txt");
    private static File file;

    public static void addWtbItem(String item) {
        try {
            bw = new PrintWriter(new FileWriter(wtbFile, true));
            addItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addWtsItem(String item) {
        try {
            bw = new PrintWriter(new FileWriter(wtsFile, true));
            addItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addItem(String item) throws IOException {
            bw.println(item);
            bw.flush();
            bw.close();
    }

    public static void delWtsItem(String item) {
        try {
            file = wtsFile;
            br = new BufferedReader(new FileReader(file));
            deleteItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delWtbItem(String item) {
        try {
            file = wtbFile;
            br = new BufferedReader(new FileReader(file));
            deleteItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteItem(String item) throws Exception {
        File tempFile = new File("." + slash + "res" + slash + "temp.txt");
        PrintWriter output = new PrintWriter(new FileWriter(tempFile));

        item = item.trim();
        String currentLine;
        while((currentLine = br.readLine()) != null) {
            currentLine = currentLine.trim();
            if(currentLine.equals(item)) {
                continue;
            } else {
                output.println(currentLine);
            }
        }

        if (br != null) { br.close(); }
        if (bw != null) { bw.close(); }
        output.flush();
        output.close();
        Path orig = tempFile.toPath();
        Path dest = file.toPath();

        Files.move(orig, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
