package se.mydns.kupo.auctionwatcher;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main class for TradeWatcher
 */
public class TradeWatcher {

    private ArrayList<String> lines = new ArrayList<>();
    private ArrayList<HashMap<String,String>> auctions = new ArrayList<>();
    private AuctionMatcher matcher = new AuctionMatcher();
    private AuctionParser parser = new AuctionParser();
    private boolean continueMatching = true;
    private ArrayList<HashMap<String,String>> matches = new ArrayList<>();
    private WatchListFrame frame;
    private List statusBar;
    private List itemList;
    private List matchList;
    private TrayIcon tray;

    public TradeWatcher() {
        setup();
        parse();
        match();
    }

    private void match() {
        matches.clear();

        while(continueMatching) {

            statusBar.add("Matching patterns");
            matches = matcher.checkSelling(auctions);
            if(!matches.isEmpty()) {
                printMatches();
                matches.clear();

            }

            matches = matcher.checkShopping(auctions);
            if(!matches.isEmpty()) {
                printMatches();
                matches.clear();
            }

            try { Thread.sleep(30000); } catch (InterruptedException e) { e.printStackTrace(); }

            getAuctionFeed();
            parse();
        }

    }

    private void printMatches() {
        for(HashMap<String, String> match : matches) {
            matchList.add(match.get("Time") + " - " + match.get("Seller") + " - " + match.get("Auction"));
            tray.displayMessage("Match found!", match.get("Auction"), TrayIcon.MessageType.INFO);

        }
    }

    private void parse() {
        auctions.clear();
        auctions = parser.parse(lines);
    }

    private void setup() {
        frame = new WatchListFrame();
        statusBar = frame.getStatusBar();
        itemList = frame.getItemList();
        matchList = frame.getMatchList();
        tray = frame.getSystemTray();
        populateMatcher();
        getAuctionFeed();
    }

    private void populateMatcher() {
        String slash = FileSystems.getDefault().getSeparator();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("." + slash + "res" + slash + "sell.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                itemList.add("[WTS] " + line);
                matcher.addSellingPattern(line);
            }
            br.close();

            br = new BufferedReader(new FileReader("." + slash + "res" + slash + "buy.txt"));
            while ((line = br.readLine()) != null) {
                itemList.add("[WTB] " + line);
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
            statusBar.add("Getting auction feed...");
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
