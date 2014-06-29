package se.mydns.kupo.auctionwatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * GUI for the TradeWatcher
 */
class WatchListFrame implements Runnable {
    private final String slash = FileSystems.getDefault().getSeparator();
    private final Image trayImage = Toolkit.getDefaultToolkit().getImage("." + slash +"res" + slash + "eq.gif");
    private JFrame frame;
    private List statusBar;
    private List itemList;
    private List wtbList;
    private List matchList;
    private TrayIcon trayIcon;

    public WatchListFrame() {
        run();
    }


    public void run() {
        setupWindow();
        setupSystemTray();
    }

    private void setupSystemTray() {
        /** System tray **/
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
            JOptionPane.showMessageDialog(frame, "Could not get system tray. Popup notification will be unavailable.", "System Tray Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setupWindow() {
        /** New shiny window **/
        frame = new JFrame("Project 1999 Trade Watcher");
        frame.setPreferredSize(new Dimension(600, 350));
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("." + slash + "res" + slash + "eq.png"));
        frame.setLocationByPlatform(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /** Layout **/
        BorderLayout layout = new BorderLayout(5,5);
        frame.setLayout(layout);

        /** Options menu **/
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem optionsMenuItem = new JMenuItem("Options");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        menu.add(optionsMenuItem);
        menu.add(exitMenuItem);

        /** Panels for the borderlayout **/
        JTabbedPane tabs = new JTabbedPane();
        Panel topPanel = new Panel(new BorderLayout());
        Panel bottomPanel = new Panel();
        Panel leftPanel = new Panel();
        leftPanel.setPreferredSize(new Dimension(200,350));
        Panel rightPanel = new Panel();

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setJMenuBar(menuBar);

        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new BorderLayout());

        /** Lists for items and matches **/
        itemList = new List();
        wtbList = new List();
//        leftPanel.add(itemList, BorderLayout.CENTER);
//        itemList.setPreferredSize(leftPanel.getPreferredSize());
        matchList = new List();
        rightPanel.add(matchList, BorderLayout.CENTER);
        matchList.setPreferredSize(rightPanel.getPreferredSize());
        tabs.addTab("WTS", itemList);
        tabs.addTab("WTB", wtbList);
        leftPanel.add(tabs, BorderLayout.CENTER);

        /** Status bar **/
        statusBar = new List();
        statusBar.setPreferredSize(bottomPanel.getPreferredSize());
        bottomPanel.add(statusBar, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public void addStatus(String message) {
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

        statusBar.add("[" + dateFormatter.format(date) + "] " + message);
    }

    public void addSellItem(String item) {
        itemList.add("[WTS] " + item);
    }

    public void addBuyItem(String item) {
        itemList.add("[WTB] " + item);
    }

    public void addMatch(String match) {
        matchList.add(match);
    }

    public void notify(String auction) {
        trayIcon.displayMessage("Match found!", auction, TrayIcon.MessageType.INFO);
    }
}

