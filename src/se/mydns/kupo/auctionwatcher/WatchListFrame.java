package se.mydns.kupo.auctionwatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by oskurot on 2014-06-23.
 */
public class WatchListFrame implements Runnable {
    private Image trayImage = Toolkit.getDefaultToolkit().getImage(".\\res\\eq.gif");

    public WatchListFrame() {
        run();
    }


    public void run() {
        JFrame frame = new JFrame("Project 1999 Auction Watcher");

        frame.setPreferredSize(new Dimension(200,100));
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\eq.png"));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        TrayIcon trayIcon = null;
        if(SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                }
            };

            PopupMenu popup = new PopupMenu();
            MenuItem item = new MenuItem("A MenuItem");
            popup.add(item);

            trayIcon = new TrayIcon(trayImage, "ECLurker", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(listener);


            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Tray unavailable");
        }

    }

    public static void main(String[] args) {
        new WatchListFrame();
    }
}

