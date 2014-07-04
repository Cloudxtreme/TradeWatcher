package se.mydns.kupo.auctionwatcher;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GUI for the TradeWatcher
 */
class TradeWatcherFrame implements Runnable, ActionListener {
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
    private MediaPlayer mediaPlayer;
    private JTabbedPane tabs;
    private AuctionMatcher matcher;

    public TradeWatcherFrame(AuctionMatcher matcher) {
        this.matcher = matcher;
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

        /** Notification Audio **/
//        String soundFile = "" + slash + "res" + slash + "notify.mp3";
//        Media notification = new Media(soundFile);
//        mediaPlayer = new MediaPlayer(notification);

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

        /** Matches panel **/
        matchList = new JList(matchData);
        rightPanel.add(matchList, BorderLayout.CENTER);
        Label matchLabel = new Label("Matches:");
        rightPanel.add(matchLabel, BorderLayout.NORTH);
        matchList.setPreferredSize(rightPanel.getPreferredSize());

        /** buy/sell tabs and lists **/
        tabs = new JTabbedPane();
        wtsList = new JList(wtsData);
        wtsList.setName("WTS");
        wtsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wtbList = new JList(wtbData);
        wtbList.setName("WTB");
        wtbList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabs.addTab("WTS", wtsList);
        tabs.addTab("WTB", wtbList);


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

    public void addWtsItem(String item) {
        wtsData.addElement(item);
    }

    public void delWtsItem(String item) {
        for(int i = 0; i < wtsData.size(); i++) {
            String data = wtsData.getElementAt(i);
            if (data.equals(item)) {
                wtsData.removeElement(data);
            }
        }
    }

    public void delWtbItem(String item) {
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

    public void notify(String auction) {
        trayIcon.displayMessage("Match found!", auction, TrayIcon.MessageType.INFO);
    }

    public void updateMatches(java.util.List<HashMap<String, String>> matches) {
        Date date = new Date();
        matchData.clear();
        for(HashMap<String,String> match : matches) {
            matchData.addElement("[" + dateFormatter.format(date) + "] " + "Auctioneer: " + match.get("Seller") + " - " + match.get("Auction"));
        }
        for(Component comp : tabs.getComponents()) {
            comp.update(frame.getGraphics());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Component tab = tabs.getSelectedComponent();

        if(command.equals("Add")) {
            String item = JOptionPane.showInputDialog(frame, "Enter item name", "New item", JOptionPane.QUESTION_MESSAGE);


            if(tab.getName().equals("WTS")) {
                if(!item.isEmpty()) {
                    matcher.addSellingPattern(item);
                    addWtsItem(item);
                }
            } else {
                if(!item.isEmpty()) {
                    matcher.addShoppingPattern(item);
                    addWtbItem(item);
                }
            }
        } else {

            if(tab.getName().equals("WTS")) {
                Object selected = wtsList.getSelectedValue();

                if(selected != null) {
                    matcher.delSellingPattern((String) selected);
                    delWtsItem((String) selected);
                }
            } else {
                Object selected = wtbList.getSelectedValue();

                if(selected != null) {
                    matcher.delShoppingPattern((String) selected);
                    delWtbItem((String) selected);
                }
            }
        }
    }
}

