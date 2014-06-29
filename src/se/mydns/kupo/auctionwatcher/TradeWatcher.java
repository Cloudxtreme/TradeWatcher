package se.mydns.kupo.auctionwatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main class for TradeWatcher
 */
class TradeWatcher {

    private final ArrayList<String> lines = new ArrayList<>();
    private ArrayList<HashMap<String,String>> auctions = new ArrayList<>();
    private final AuctionMatcher matcher = new AuctionMatcher();
    private final AuctionParser parser = new AuctionParser();
    private ArrayList<HashMap<String,String>> matches = new ArrayList<>();
    private WatchListFrame frame;

    private TradeWatcher() {
        setup();
        parse();
        match();
    }

    private void match() {
        matches.clear();
        boolean paus = true;
        while(!paus) {
            getAuctionFeed();
            parse();
            frame.addStatus("Matching patterns.");
            matches = matcher.checkSelling(auctions);
            if(!matches.isEmpty()) {
                addMatches();
                matches.clear();

            }

            matches = matcher.checkShopping(auctions);
            if(!matches.isEmpty()) {
                addMatches();
                matches.clear();
            }

            try { Thread.sleep(30000); } catch (InterruptedException e) { e.printStackTrace(); }
        }

    }

    private void addMatches() {
        for(HashMap<String, String> match : matches) {
            frame.addMatch(match.get("Time") + " - " + match.get("Seller") + " - " + match.get("Auction"));
            frame.notify(match.get("Auction"));
        }
    }

    private void parse() {
        auctions.clear();
        auctions = parser.parse(lines);
    }

    private void setup() {
        frame = new WatchListFrame();
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
                frame.addSellItem(line);
                matcher.addSellingPattern(line);
            }
            br.close();

            br = new BufferedReader(new FileReader("." + slash + "res" + slash + "buy.txt"));
            while ((line = br.readLine()) != null) {
                frame.addBuyItem(line);
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
            while((inputLine = in.readLine()) != null)  {
                lines.add(inputLine);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String args[]) {
        new TradeWatcher();
    }
}
