package se.mydns.kupo.auctionwatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oskurot on 2014-06-22.
 */
public class AuctionParser {
    private ArrayList<HashMap<String, String>> auctionLines = new ArrayList<>();
//    private ArrayList<ArrayList<String>> auctionItems = new ArrayList<>();
    private String[] aucLines;

    public ArrayList parse(ArrayList<String> lines) {
        for(String line : lines) {
            if(line.contains("<div class='auc'>")) {
                aucLines = line.split("div class='auc'>");
            }
        }

        for(String item : aucLines) {
            // temp stores time, seller, auction line
//            ArrayList<String> temp = new ArrayList<>();
            HashMap<String, String> tempHash = new HashMap<>();

            /** Time of auction **/
            Pattern tp = Pattern.compile("(\\[<b class='aud'>).*?(</b>])");
            Matcher tm = tp.matcher(item);
            if(tm.find())
//                temp.add(tm.group(0).replaceAll("\\[<b class='aud'>", "").replaceAll("</b>]", ""));
                tempHash.put("Time", tm.group(0).replaceAll("\\[<b class='aud'>", "").replaceAll("</b>]", ""));

            /** Seller **/
            Pattern sp = Pattern.compile("(<b class='slr'>).*?(</b>)");
            Matcher sm = sp.matcher(item);
            if(sm.find())
//                temp.add(sm.group(0).replaceAll("<b class='slr'>", "").replaceAll("</b>", ""));
                tempHash.put("Seller", sm.group(0).replaceAll("<b class='slr'>", "").replaceAll("</b>", ""));

            /** Auction **/
            Pattern ap = Pattern.compile("(<b class='aul'>).*?(</b>)");
            Matcher am = ap.matcher(item);
            if(am.find())
//                temp.add(am.group(0).replaceAll("<b class='aul'>", "").replaceAll("</b>", ""));
                tempHash.put("Auction", am.group(0).replaceAll("<b class='aul'>", "").replaceAll("</b>", ""));

//            auctionItems.add(temp);
            auctionLines.add(tempHash);
        }
        return auctionLines;
    }
}
