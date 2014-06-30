package se.mydns.kupo.auctionwatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses ahungrys html page and puts everything in a list
 */
public class AuctionParser {
    private final ArrayList<HashMap<String, String>> auctionLines = new ArrayList<>();
    private String[] aucLines;

    public ArrayList<HashMap<String, String>> parse(ArrayList<String> lines) {
        for(String line : lines) {
            if(line.contains("<div class='auc'>")) {
                aucLines = line.split("div class='auc'>");
            }
        }

        if(aucLines == null)
            return auctionLines;

        for(String item : aucLines) {
            // temp stores time, seller, auction line
            HashMap<String, String> tempHash = new HashMap<>();

            /** Time of auction **/
            Pattern tp = Pattern.compile("(\\[<b class='aud'>).*?(</b>])");
            Matcher tm = tp.matcher(item);
            if(tm.find())
                tempHash.put("Time", tm.group(0).replaceAll("\\[<b class='aud'>", "").replaceAll("</b>]", ""));

            /** Seller **/
            Pattern sp = Pattern.compile("(<b class='slr'>).*?(</b>)");
            Matcher sm = sp.matcher(item);
            if(sm.find())
                tempHash.put("Seller", sm.group(0).replaceAll("<b class='slr'>", "").replaceAll("</b>", ""));

            /** Auction **/
            Pattern ap = Pattern.compile("(<b class='aul'>).*?(</b>)");
            Matcher am = ap.matcher(item);
            if(am.find())
                tempHash.put("Auction", am.group(0).replaceAll("<b class='aul'>", "").replaceAll("</b>", ""));

            auctionLines.add(tempHash);
        }
        return auctionLines;
    }
}
