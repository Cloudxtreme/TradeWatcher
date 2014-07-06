package se.mydns.kupo.auctionwatcher;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * GUI for the TradeWatcher
 */
class TradeWatcherFrame implements Runnable, ActionListener {
    private final String slash = FileSystems.getDefault().getSeparator();
    private final Image trayImage = Toolkit.getDefaultToolkit().getImage("." + slash +"res" + slash + "eq.gif");
    private JFrame frame;
    private DefaultListModel<String> statusData = new DefaultListModel<>();
    private JList<ArrayList<String>> statusBar;
    private DefaultListModel<String> wtsData = new DefaultListModel<>();
    private JList<ArrayList<String>> wtsList;
    private DefaultListModel<String> wtbData = new DefaultListModel<>();
    private JList<ArrayList<String>> wtbList;
    private DefaultListModel<String> matchData = new DefaultListModel<>();
    private TrayIcon trayIcon;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    private MediaPlayer mediaPlayer;
    private JTabbedPane tabs;
    private AuctionMatcher matcher;
    private JScrollPane statusScrollPane;
    private AudioStream as;

    public TradeWatcherFrame(AuctionMatcher matcher) {

        this.matcher = matcher;
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
                    if(frame.getState() == JFrame.ICONIFIED) {
                        frame.setState(JFrame.NORMAL);
                    } else {
                        frame.setState(JFrame.ICONIFIED);
                    }
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

//        /** Notification Audio **/
//        Media media = new Media("file:///c|\\Users\\oskurot\\IdeaProjects\\P99AuctionWatchList\\res\\notify.mp3");
        try {

            String notificationFile = "." + slash + "res" + slash + "notify.wav";
            InputStream is = new FileInputStream(notificationFile);
            as = new AudioStream(is);
        } catch (Exception e) { System.out.println("Could not load notification audio."); e.printStackTrace(); }
//        mediaPlayer = new MediaPlayer(media);

        /** Layout **/
        BorderLayout layout = new BorderLayout(5,5);
        frame.setLayout(layout);

        /** Menubar **/
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic('f');
        menuBar.add(menu);
        JMenuItem optionsMenuItem = new JMenuItem("Options");
        JCheckBoxMenuItem options = new JCheckBoxMenuItem("Audio notification");

        optionsMenuItem.setMnemonic('o');
        optionsMenuItem.addActionListener(this);
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic('x');
        exitMenuItem.addActionListener(this);
        menu.add(optionsMenuItem);
        menu.add(exitMenuItem);
        menu.add(options);

        /** Panels for the borderlayout **/
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(200,50));
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200,350));
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(400,350));

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setJMenuBar(menuBar);

        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new BorderLayout());

        /** Matches panel **/
        JList<ArrayList<String>> matchList = new JList(matchData);
        JScrollPane matchPane = new JScrollPane(matchList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(matchPane, BorderLayout.CENTER);
        JLabel matchLabel = new JLabel("Matches:");
        rightPanel.add(matchLabel, BorderLayout.NORTH);
        matchPane.setPreferredSize(rightPanel.getPreferredSize());

        /** buy/sell tabs and lists **/
        tabs = new JTabbedPane();

        wtsList = new JList(wtsData);
        JScrollPane wtsPane = new JScrollPane(wtsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wtsPane.setName("WTS");
        wtsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabs.addTab("WTS", wtsPane);

        wtbList = new JList(wtbData);
        JScrollPane wtbPane = new JScrollPane(wtbList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wtbPane.setPreferredSize(rightPanel.getPreferredSize());
        wtbPane.setName("WTB");
        wtbList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabs.addTab("WTB", wtbPane);

        JPopupMenu pop = new JPopupMenu();
        JMenuItem addItem = new JMenuItem("Add");
        JMenuItem removeItem = new JMenuItem("Remove");
        addItem.addActionListener(this);
        removeItem.addActionListener(this);
        pop.add(addItem);
        pop.add(removeItem);
        wtsList.setComponentPopupMenu(pop);
        wtbList.setComponentPopupMenu(pop);

        leftPanel.add(tabs, BorderLayout.CENTER);

        /** Status bar **/
        statusBar = new JList(statusData);
        statusScrollPane = new JScrollPane(statusBar, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        statusScrollPane.setPreferredSize(bottomPanel.getPreferredSize());
        bottomPanel.add(statusScrollPane, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public void addStatus(String message) {
        Date date = new Date();
        int newIndex = statusData.size();
        statusData.addElement("[" + dateFormatter.format(date) + "] " + message);
//        statusBar.setSelectedIndex(newIndex);
//        statusBar.ensureIndexIsVisible(newIndex);
//        statusScrollPane.revalidate();
    }

    public void addWtsItem(String item) {
        wtsData.addElement(item);
    }

    void delWtsItem(String item) {
        for(int i = 0; i < wtsData.size(); i++) {
            String data = wtsData.getElementAt(i);
            if (data.equals(item)) {
                wtsData.removeElement(data);
            }
        }
    }

    void delWtbItem(String item) {
        for(int i = 0; i < wtbData.size(); i++) {
            String data = wtbData.getElementAt(i);
            if (data.equals(item)) {
                wtbData.removeElement(data);
            }
        }
    }

    public void addWtbItem(String item) {
        wtbData.addElement(item);
    }

    public void notify(HashMap<String, String> match) {
        trayIcon.displayMessage("Match found!", "Auctioneer: " + match.get("Seller") + " - Match: " + match.get("Match"), TrayIcon.MessageType.INFO);
        AudioPlayer.player.start(as);
    }

    public void updateMatches(java.util.List<HashMap<String, String>> matches) {
        Date date = new Date();
        String dateString = "[" + dateFormatter.format(date) + "] ";
        matchData.clear();
        for(HashMap<String,String> match : matches) {
            String pattern = match.get("Match");
            String name = match.get("Seller");
            String auction = match.get("Auction");

            matchData.addElement(dateString + "Auctioneer: " + name + " - " + pattern + " - " + auction);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Component tab = tabs.getSelectedComponent();

        switch (command) {
            case "Add":
                String item = JOptionPane.showInputDialog(frame, "Enter item name", "New item", JOptionPane.QUESTION_MESSAGE);


                if (tab.getName().equals("WTS")) {
                    if (!item.isEmpty()) {
                        matcher.addSellingPattern(item);
                        addWtsItem(item);
                    }
                } else {
                    if (item != null) {
                        matcher.addShoppingPattern(item);
                        addWtbItem(item);
                    }
                }
                break;
            case "Remove":

                if (tab.getName().equals("WTS")) {
                    Object selected = wtsList.getSelectedValue();

                    if (selected != null) {
                        matcher.delSellingPattern((String) selected);
                        delWtsItem((String) selected);
                    }
                } else {
                    Object selected = wtbList.getSelectedValue();

                    if (selected != null) {
                        matcher.delShoppingPattern((String) selected);
                        delWtbItem((String) selected);
                    }
                }
                break;
            case "Exit":
                System.exit(0);
        }
    }
}

