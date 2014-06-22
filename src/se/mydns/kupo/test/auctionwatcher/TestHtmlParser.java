package se.mydns.kupo.test.auctionwatcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.mydns.kupo.auctionwatcher.AuctionParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by oskurot on 22/06/14.
 */
public class TestHtmlParser {
    private ArrayList<String> lines = new ArrayList<>();

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
        ArrayList<ArrayList<String>> list = parser.parse(lines);

        for(ArrayList item : list) {
            if (item.isEmpty())
                continue;

//            System.out.print("Time: " + item.get(0));
//            System.out.print(", Seller: " + item.get(1));
//            System.out.print(", Auction: " + item.get(2));
//            System.out.println();

        }
        Assert.assertFalse(list.isEmpty());
    }
}
