package se.mydns.kupo.test.auctionwatcher;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by oskurot on 22/06/14.
 */
public class TestHtmlParser {
    private StringBuilder blob = new StringBuilder();

    @Before
    public void setUp() {
        try {
        BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\oskurot\\IdeaProjects\\P99AuctionWatchList\\res\\sample-html.html"));

            String inputLine;
            while((inputLine = in.readLine()) != null)  {
                blob.append(inputLine);
                blob.append("\n");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Test
    public void testHtmlParser() {
        System.out.println(blob.toString());
    }
}
