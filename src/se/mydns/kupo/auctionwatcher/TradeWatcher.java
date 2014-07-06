package se.mydns.kupo.auctionwatcher;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for TradeWatcher
 */
class TradeWatcher {
    private final ArrayList<String> lines = new ArrayList<>();
    private ArrayList<HashMap<String, String>> auctions = new ArrayList<>();
    private final AuctionMatcher matcher = new AuctionMatcher();
    private final AuctionParser parser = new AuctionParser();
    private ArrayList<HashMap<String, String>> newMatches = new ArrayList<>();
    private List<HashMap<String, String>> matches = new CopyOnWriteArrayList<>();
    private TradeWatcherFrame frame;
    private Logger log = Logger.getLogger(TradeWatcher.class.getName());

    private TradeWatcher() {
        setup();
        parse();
        match();
    }

    private void match() {
        newMatches.clear();
        boolean paus = false;
        while (!paus) {
            getAuctionFeed();
            parse();
            frame.addStatus("Matching patterns.");

            newMatches = matcher.checkAuctions(auctions);
            addMatches();

            // Sleep until next check
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private void addMatches() {
        if (!newMatches.isEmpty()) {

            for (HashMap<String, String> newMatch : newMatches) {
                boolean gotMatch = false;
                if (matches.isEmpty()) {
                    matches.add(newMatch);
                    frame.notify(newMatch);
                } else {
                    for (HashMap<String, String> oldMatch : matches) {
                        if (newMatch.get("Seller").equals(oldMatch.get("Seller")) &&
                                newMatch.get("Match").equals(oldMatch.get("Match"))) {
                            matches.remove(oldMatch);
                            matches.add(newMatch);
                            gotMatch = true;
                        }


                    }
                    if (!gotMatch) {
                        matches.add(newMatch);
                        frame.notify(newMatch);
                    }
                }
            }

            frame.updateMatches(matches);
        }
    }

    private void parse() {
        auctions.clear();
        auctions = parser.parse(lines);
    }

    private void setup() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {
            log.log(Level.INFO, "Couldn't set system look and feel.");
        }
        frame = new TradeWatcherFrame(matcher);
        populateMatcher();
    }

    private void populateMatcher() {
        frame.addStatus("Populating matcher patterns.");
        String slash = FileSystems.getDefault().getSeparator();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("." + slash + "res" + slash + "sell.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                frame.addWtsItem(line);
                matcher.addSellingPattern(line);
            }
            br.close();

            br = new BufferedReader(new FileReader("." + slash + "res" + slash + "buy.txt"));
            while ((line = br.readLine()) != null) {
                frame.addWtbItem(line);
                matcher.addShoppingPattern(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAuctionFeed() {
        URL ahungry;
        lines.clear();
        try {
            frame.addStatus("Getting auction feed...");
            ahungry = new URL("http://ahungry.com/eqauctions/?");
            HttpURLConnection connection = (HttpURLConnection) ahungry.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoInput(true); // We want to use this connection for both input and output
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.getResponseMessage();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                lines.add(inputLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new TradeWatcher();
    }
}
