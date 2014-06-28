package se.mydns.kupo.test.auctionwatcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.mydns.kupo.auctionwatcher.AuctionParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oskurot on 22/06/14.
 */
public class TestHtmlParser {
    private final ArrayList<String> lines = new ArrayList<>();

    @Before
    public void setUp() {
        try {
        BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\oskurot\\IdeaProjects\\P99AuctionWatchList\\res\\sample-html.html"));

            String inputLine;
            while((inputLine = in.readLine()) != null)  {
                lines.add(inputLine);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    public void testHtmlParser() {
        AuctionParser parser = new AuctionParser();
        ArrayList<HashMap<String, String>> list = parser.parse(lines);

        for(HashMap<String,String> item : list) {
            if (item.isEmpty())
                continue;

            System.out.print("Time: " + item.get("Time") +
            ", Seller: " + item.get("Seller") +
            ", Auction: " + item.get("Auction"));
            System.out.println();

        }
        Assert.assertFalse(list.isEmpty());
    }

}
