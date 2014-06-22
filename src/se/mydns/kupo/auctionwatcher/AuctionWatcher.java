package se.mydns.kupo.auctionwatcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by oskurot on 2014-06-22.
 */
public class AuctionWatcher {

    private ArrayList<String> lines = new ArrayList<>();

    public AuctionWatcher() {
        setup();
        parse();
    }

    private void parse() {


    }

    private void setup() {
        URL ahungry = null;
        try {

            ahungry = new URL("http://ahungry.com/eqauctions/?");
            HttpURLConnection connection = (HttpURLConnection) ahungry.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoInput(true); // We want to use this connection for both input and output
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            String resp = connection.getResponseMessage();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            while((inputLine = in.readLine()) != null)  {
                lines.add(inputLine);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String args[]) {
        new AuctionWatcher();
    }
}
