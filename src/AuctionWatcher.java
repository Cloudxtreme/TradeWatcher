import sun.net.www.http.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;

/**
 * Created by oskurot on 2014-06-22.
 */
public class AuctionWatcher {

    private StringBuilder blob = new StringBuilder();

    public AuctionWatcher() {
        setup();
        parse();
    }

    private void parse() {
        String html = blob.toString();

        System.out.println(html);
    }

    private void setup() {
        URL ahungry = null;
        try {

            ahungry = new URL("http://ahungry.com/eqauctions/?");
            HttpURLConnection connection = (HttpURLConnection) ahungry.openConnection();

            connection.setRequestMethod("POST");

            connection.setDoInput(true); // We want to use this connection for both input and output
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//            BufferedReader in = new BufferedReader(new InputStreamReader(ahungry.openStream()));
//
//            String inputLine;
//            while((inputLine = in.readLine()) != null)  {
//                blob.append(inputLine);
//            }
//
//            in.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String args[]) {
        new AuctionWatcher();
    }
}
