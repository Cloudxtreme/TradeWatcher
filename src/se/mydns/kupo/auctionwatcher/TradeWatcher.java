package se.mydns.kupo.auctionwatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public TradeWatcher() {
        setup();
        parse();
        match();
    }

    private void match() {
        matches.clear();
        boolean matchFound;
        while(continueMatching) {
            matchFound = false;
            System.out.println("Matching patterns");
            matches = matcher.checkSelling(auctions);
            if(!matches.isEmpty()) {
                System.out.println("Buyers matching your criteria:");
                printMatches();
                matches.clear();
                matchFound = true;
            }

            matches = matcher.checkShopping(auctions);
            if(!matches.isEmpty()) {
                System.out.println("Sales matching your criteria:");
                printMatches();
                matches.clear();
                matchFound = true;
            }
            if(!matchFound)
                System.out.println("No matches found.");
            System.out.println("Sleeping for 30 seconds.");
            try { Thread.sleep(30000); } catch (InterruptedException e) { e.printStackTrace(); }
            getAuctionFeed();
            parse();
        }

    }

    private void printMatches() {
        for(HashMap<String, String> match : matches) {
            System.out.println(match.get("Time") + " - " + match.get("Seller") + " - " + match.get("Auction"));
        }
    }

    private void parse() {
        auctions.clear();
        System.out.println("Parsing html");
        auctions = parser.parse(lines);
    }

    private void setup() {
        populateMatcher();
        getAuctionFeed();
    }

    private void populateMatcher() {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(".\\res\\sell.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Adding " + line + " to sell list");
                matcher.addSellingPattern(line);
            }
            br.close();

            br = new BufferedReader(new FileReader(".\\res\\buy.txt"));
            while ((line = br.readLine()) != null) {
                System.out.println("Adding " + line + " to buy list");
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
            System.out.println("Getting auction feed...");
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
