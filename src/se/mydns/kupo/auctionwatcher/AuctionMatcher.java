package se.mydns.kupo.auctionwatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keeps track of what should be looked for and does the actual matching.
 */
public class AuctionMatcher {
    private final ArrayList<String> shoppingList = new ArrayList<>();
    private final ArrayList<String> sellingList = new ArrayList<>();
//    private ArrayList<ArrayList<String>> auctionFeed = new ArrayList<>();

    /** Checks for duplicates and adds if none are found **/
    public void addSellingPattern(String pattern) {
        if(hasValue(sellingList, pattern) == -1)
            sellingList.add(pattern);
    }

    /** tries to remove a pattern and returns true is successful **/
    public boolean delSellingPattern(String pattern) {
        int index = hasValue(sellingList, pattern);
        if(index != -1) {
            sellingList.remove(index);
            return true;
        }
        return false;
    }

    /** Checks for duplicates and adds if none are found **/
    public void addShoppingPattern(String pattern) {
        if(hasValue(shoppingList, pattern) == -1)
            shoppingList.add(pattern);
    }

    /** tries to remove a pattern and returns true is successful **/
    public boolean delShoppingPattern(String pattern) {
        int index = hasValue(shoppingList, pattern);
        if(index != -1) {
            shoppingList.remove(index);
            return true;
        }
        return false;
    }

    /** Checks for pattern and returns index if found. returns -1 if not found. **/
    private int hasValue(ArrayList<String> list, String pattern) {
        for(int i=0;i<list.size();i++) {
            if (list.get(i).matches(pattern)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<HashMap<String,String>> checkSelling(ArrayList<HashMap<String,String>> feed) {
        ArrayList<HashMap<String,String>> matches = new ArrayList<>();

        for(HashMap<String,String> auc : feed) {
            for(String p : sellingList) {
                Pattern pattern = Pattern.compile(".*(WTB|Buy).*" + p, Pattern.CASE_INSENSITIVE);
                String line = auc.get("Auction");
                if (line != null) {
                    Matcher m = pattern.matcher(line);
                    if (m.find()) {
                        matches.add(auc);
                    }
                }
            }
        }
        return matches;
    }

    public ArrayList checkShopping(ArrayList<HashMap<String,String>> feed) {
        ArrayList<HashMap<String,String>> matches = new ArrayList<>();

        for(HashMap<String,String> auc : feed) {
            for(String p : shoppingList) {
                Pattern pattern = Pattern.compile(".*(WTS|Sell).*" + p, Pattern.CASE_INSENSITIVE);
                String line = auc.get("Auction");
                if (line != null) {
                    Matcher m = pattern.matcher(line);
                    if (m.find()) {
                        if(!matches.contains(auc))
                            matches.add(auc);
                    }
                }
            }
        }
        return matches;
    }
}
