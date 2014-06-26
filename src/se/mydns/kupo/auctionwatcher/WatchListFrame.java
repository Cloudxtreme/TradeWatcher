package se.mydns.kupo.auctionwatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI for the TradeWatcher
 */
public class WatchListFrame implements Runnable {
    private Image trayImage = Toolkit.getDefaultToolkit().getImage(".\\res\\eq.gif");
    private JFrame frame;
    public WatchListFrame() {
        run();
    }


    public void run() {
        /** New shiny window **/
        frame = new JFrame("Project 1999 Trade Watcher");
        frame.setPreferredSize(new Dimension(500, 350));
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\eq.png"));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** Layout **/
        frame.setLayout(new GridLayout(0,2));
        Panel leftPanel = new Panel();
        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.LINE_AXIS));
//        leftPanel.setBackground(Color.blue);

        Panel rightPanel = new Panel();
//        rightPanel.setBackground(Color.red);
        frame.add(leftPanel);
        frame.add(rightPanel);

        Label wtsLabel = new Label("WTS");
        leftPanel.add(wtsLabel);

        List wtsList = new List();
        wtsList.add("Cobalt Breastplate");
        wtsList.add("Impskin");
        leftPanel.add(wtsList);

        Label wtbLabel = new Label("WTB");
        rightPanel.add(wtbLabel);
        frame.pack();
        frame.setVisible(true);

        /** System tray **/
        TrayIcon trayIcon = null;
        if(SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(!frame.isVisible());
                }
            };

            trayIcon = new TrayIcon(trayImage, "Tradewatcher");
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

