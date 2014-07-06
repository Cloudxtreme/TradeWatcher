package se.mydns.kupo.auctionwatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keeps track of what should be looked for and does the actual matching.
 */
public class AuctionMatcher {

    private List<String> shoppingList = new ArrayList<>();
    private List<String> sellingList = new ArrayList<>();

    public AuctionMatcher() {
        shoppingList = Collections.synchronizedList(shoppingList);
        sellingList = Collections.synchronizedList(sellingList);
    }

    /**
     * Checks for duplicates and adds if none are found *
     */
    public void addSellingPattern(String pattern) {
        if (hasValue(sellingList, pattern) == -1) {
            sellingList.add(pattern);
//            System.out.println("[Matcher] Adding selling pattern " + pattern);
        }

    }

    /**
     * tries to remove a pattern and returns true is successful *
     */
    public boolean delSellingPattern(String pattern) {
        int index = hasValue(sellingList, pattern);
        if (index != -1) {
            sellingList.remove(index);
//            System.out.println("[Matcher] Removing selling pattern " + pattern);
            return true;
        }
        return false;
    }

    /**
     * Checks for duplicates and adds if none are found *
     */
    public void addShoppingPattern(String pattern) {
        if (hasValue(shoppingList, pattern) == -1)
            shoppingList.add(pattern);
//            System.out.println("[Matcher] Adding shopping pattern " + pattern);
    }

    /**
     * tries to remove a pattern and returns true is successful *
     */
    public boolean delShoppingPattern(String pattern) {
        int index = hasValue(shoppingList, pattern);
        if (index != -1) {
            shoppingList.remove(index);
//            System.out.println("[Matcher] Removing shopping pattern " + pattern);
            return true;
        }
        return false;
    }

    /**
     * Checks for pattern and returns index if found. returns -1 if not found. *
     */
    private int hasValue(List<String> list, String pattern) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).matches(pattern)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<HashMap<String, String>> checkWTS(ArrayList<HashMap<String, String>> feed) {
        ArrayList<HashMap<String, String>> newMatches = new ArrayList<>();

        // Loop over all the auctions
        for (HashMap<String, String> auc : feed) {
            for (String p : sellingList) {
                // For each auction. check against all the patterns
                Pattern pattern = Pattern.compile(".*(WTB|Buy).*" + p + ".*", Pattern.CASE_INSENSITIVE);
                String line = auc.get("Auction");
                if (line != null) {
                    Matcher m = pattern.matcher(line);
                    if (m.find()) {
                        auc.put("Match", p);
                        HashMap<String,String> match = (HashMap<String, String>) auc.clone();
                        newMatches.add(match);
                    }
                }
            }
        }
        return newMatches;
    }

    public ArrayList<HashMap<String, String>> checkWTB(ArrayList<HashMap<String, String>> feed) {
        ArrayList<HashMap<String, String>> newMatches = new ArrayList<>();

        // Loop over all the auctions
        for (HashMap<String, String> auc : feed) {
            for (String p : shoppingList) {
                // For each auction. check against all the patterns
                Pattern pattern = Pattern.compile(".*(WTS|Sell).*" + p + ".*", Pattern.CASE_INSENSITIVE);
                String line = auc.get("Auction");
                if (line != null) {
                    Matcher m = pattern.matcher(line);
                    if (m.find()) {
                        auc.put("Match", p);
                        HashMap<String,String> match = (HashMap<String, String>) auc.clone();
                        newMatches.add(match);
                    }
                }
            }
        }
        return newMatches;
    }
}
