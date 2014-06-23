package se.mydns.kupo.auctionwatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oskurot on 2014-06-23.
 */
public class AuctionMatcher {
    private ArrayList<String> shoppingList = new ArrayList<>();
    private ArrayList<String> sellingList = new ArrayList<>();
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

    public ArrayList checkSelling(ArrayList<HashMap<String,String>> feed) {
        ArrayList<HashMap<String,String>> matches = new ArrayList<>();

        for(HashMap<String,String> auc : feed) {
            for(String p : sellingList) {
                Pattern pattern = Pattern.compile(".*WTB.*" + p);
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
                Pattern pattern = Pattern.compile(".*WTS.*" + p);
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
}
