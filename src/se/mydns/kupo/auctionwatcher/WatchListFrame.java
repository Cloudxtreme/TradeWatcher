package se.mydns.kupo.auctionwatcher;

import javax.swing.*;
import java.awt.*;

/**
 * Created by oskurot on 2014-06-23.
 */
public class WatchListFrame implements Runnable {

    public WatchListFrame() {
        run();
    }


    public void run() {
        JFrame frame = new JFrame("Project 1999 Auction Watcher");

        frame.setPreferredSize(new Dimension(200,100));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }

    public static void main(String[] args) {
        new WatchListFrame();
    }
}

