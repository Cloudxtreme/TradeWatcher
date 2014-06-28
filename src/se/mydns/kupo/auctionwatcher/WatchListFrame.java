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
    private List statusBar;
    private List itemList;
    private List eventList;
    private SystemTray tray;
    TrayIcon trayIcon;

    public WatchListFrame() {
        run();
    }


    public void run() {
        /** New shiny window **/
        frame = new JFrame("Project 1999 Trade Watcher");
        frame.setPreferredSize(new Dimension(600, 350));
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\eq.png"));
//        frame.setLocationRelativeTo(null);
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        /** Layout **/
        BorderLayout layout = new BorderLayout(5,5);
        frame.setLayout(layout);

//        frame.validate();

        /** Panels for the borderlayout **/
        Panel topPanel = new Panel(new BorderLayout());
        Panel bottomPanel = new Panel();
        Panel leftPanel = new Panel();
        leftPanel.setPreferredSize(new Dimension(200,350));
        Panel rightPanel = new Panel();



//        frame.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new BorderLayout());


        /** Labels for WTB,WTS **/
        Label wtsLabel = new Label("WTS");
        topPanel.add(wtsLabel);
        Label wtbLabel = new Label("WTB");
        topPanel.add(wtbLabel);

        /** Lists for items and matches **/
        itemList = new List();
        leftPanel.add(itemList, BorderLayout.CENTER);
        itemList.setPreferredSize(leftPanel.getPreferredSize());
        eventList = new List();
        rightPanel.add(eventList, BorderLayout.CENTER);
        eventList.setPreferredSize(rightPanel.getPreferredSize());

        /** Status bar **/
        statusBar = new List();
        statusBar.setPreferredSize(bottomPanel.getPreferredSize());
        bottomPanel.add(statusBar, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        /** System tray **/
        if(SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();

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
            JOptionPane.showMessageDialog(frame, "Could not get system tray. Popup notification will be unavailable.", "System Tray Not Found",JOptionPane.OK_OPTION);
        }

    }

    public List getStatusBar() {
        return statusBar;
    }

    public List getItemList() {
        return itemList;
    }

    public List getMatchList() {
        return eventList;
    }

    public TrayIcon getSystemTray() {
        return trayIcon;
    }

//    public static void main(String[] args) {
//        new WatchListFrame();
//    }
}

