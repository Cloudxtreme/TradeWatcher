package se.mydns.kupo.auctionwatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GUI for the TradeWatcher
 */
class WatchListFrame implements Runnable {
    private Logger log = Logger.getLogger(TradeWatcher.class.getName());
    private final String slash = FileSystems.getDefault().getSeparator();
    private final Image trayImage = Toolkit.getDefaultToolkit().getImage("." + slash +"res" + slash + "eq.gif");
    private JFrame frame;
    private List statusBar;
    private DefaultListModel<String> wtsData = new DefaultListModel<>();
    private JList<ArrayList<String>> wtsList;
    private DefaultListModel<String> wtbData = new DefaultListModel<>();
    private JList<ArrayList<String>> wtbList;
    private DefaultListModel<String> matchData = new DefaultListModel<>();
    private JList<ArrayList<String>> matchList;
    private TrayIcon trayIcon;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

    public WatchListFrame() {
        run();
    }


    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {
            log.log(Level.INFO, "Couldn't set system look and feel.");
        }
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

        /** Menubar **/
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem optionsMenuItem = new JMenuItem("Options");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        menu.add(optionsMenuItem);
        menu.add(exitMenuItem);

        /** Panels for the borderlayout **/
        JPanel bottomPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200,350));
        JPanel rightPanel = new JPanel();

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setJMenuBar(menuBar);

        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new BorderLayout());

        /** Lists for items and matches **/
        JTabbedPane tabs = new JTabbedPane();
        wtsList = new JList();
        wtbList = new JList(wtbData);
        matchList = new JList(matchData);
        rightPanel.add(matchList, BorderLayout.CENTER);
        matchList.setPreferredSize(rightPanel.getPreferredSize());
        tabs.addTab("WTS", wtsList);
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
        statusBar.add("[" + dateFormatter.format(date) + "] " + message);
        statusBar.select(statusBar.getItemCount() -1);
    }

    public void addSellItem(String item) {
        wtsData.addElement(item);
    }

    public void delSellItem(String item) {
        for(int i = 0; i < wtsData.size(); i++) {
            String data = wtsData.getElementAt(i);
            if (data.equals(item)) {
                wtsData.removeElement(data);
            }
        }
    }

    public void delBuyItem(String item) {
        for(int i = 0; i < wtbData.size(); i++) {
            String data = wtbData.getElementAt(i);
            if (data.equals(item)) {
                wtbData.removeElement(data);
            }
        }
    }

    public void addBuyItem(String item) {
        wtbData.addElement(item);
    }

//    public void addMatch(HashMap<String, String> match) {
//        Date date = new Date();
//        matchData.addElement("[" + dateFormatter.format(date) + "] " + "Seller: " + match.get("Seller") + " - " + match.get("Auction"));
//    }

    public void notify(String auction) {
        trayIcon.displayMessage("Match found!", auction, TrayIcon.MessageType.INFO);
    }

    public void updateMatches(java.util.List<HashMap<String, String>> matches) {
        Date date = new Date();
        matchData.clear();
        for(HashMap<String,String> match : matches) {
            matchData.addElement("[" + dateFormatter.format(date) + "] " + "Seller: " + match.get("Seller") + " - " + match.get("Auction"));
        }
    }
}

