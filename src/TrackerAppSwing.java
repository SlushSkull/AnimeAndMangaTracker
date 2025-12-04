import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/*
 Single-file Java Swing application implementing the Anime & Manga Tracker described.
 - Master lists: anime_db.txt, manga_db.txt (format: id|title|imgURL|total|type)
 - Accounts: accounts/<username>.txt
   per-line: A|mediaID|currentProgress|status|rating  or  M|mediaID|currentProgress|status|rating

 How to compile/run:
  javac TrackerAppSwing.java
  java TrackerAppSwing

 Requires Java 8+
*/

// ----- Data model classes -----
abstract class Entry {
    protected String title;
    protected String imgURL;
    protected double rating;
    protected int currentProgress;
    protected int totalProgress;
    protected int mediaID;
    protected String status;
    protected String type;

    public Entry(String title, String imgURL, double rating, int currentProgress, int totalProgress, int mediaID, String status, String type) {
        this.title = title;
        this.imgURL = imgURL;
        this.rating = rating;
        this.currentProgress = currentProgress;
        this.totalProgress = totalProgress;
        this.mediaID = mediaID;
        this.status = status;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getImgURL() { return imgURL; }
    public double getRating() { return rating; }
    public int getCurrentProgress() { return currentProgress; }
    public int getTotalProgress() { return totalProgress; }
    public int getMediaID() { return mediaID; }
    public String getState() { return status; }
    public String getType() { return type; }

    public void setTitle(String s) { title = s; }
    public void setImgURL(String s) { imgURL = s; }
    public void setRating(double r) { rating = r; }
    public void setCurrentProgress(int p) { currentProgress = p; }
    public void setTotalProgress(int t) { totalProgress = t; }
    public void setMediaID(int id) { mediaID = id; }
    public void setState(String s) { status = s; }
    public void setType(String s) { type = s; }

    @Override
    public String toString() {
        return String.format("%d - %s (%d/%d) [%s]", mediaID, title, currentProgress, totalProgress, status);
    }
}

class AnimeEntry extends Entry {
    public AnimeEntry(String title, String imgURL, double rating, int currentEpisode, int totalEpisodes, int mediaID, String animeType, String status) {
        super(title, imgURL, rating, currentEpisode, totalEpisodes, mediaID, status, animeType);
    }
}

class MangaEntry extends Entry {
    public MangaEntry(String title, String imgURL, double rating, int currentChapter, int totalChapters, int mediaID, String mangaType, String status) {
        super(title, imgURL, rating, currentChapter, totalChapters, mediaID, status, mangaType);
    }
}

// ----- Main Swing Application -----
public class TrackerAppSwing extends JFrame {
    private static final String ANIME_DB = "anime_db.txt";
    private static final String MANGA_DB = "manga_db.txt";
    private static final String ACCOUNTS_DIR = "accounts";

    private Map<Integer, AnimeEntry> animeMaster = new TreeMap<>();
    private Map<Integer, MangaEntry> mangaMaster = new TreeMap<>();

    // UI cards
    private CardLayout cards = new CardLayout();
    private JPanel cardPanel = new JPanel(cards);

    // main menu components
    private JLabel titleLabel = new JLabel("Anime and Manga Tracker", SwingConstants.CENTER);

    // login/create
    private JTextField usernameField = new JTextField(20);

    // admin fields
    private JTextField adminTitleField = new JTextField(20);
    private JTextField adminImgField = new JTextField(20);
    private JTextField adminTotalField = new JTextField(6);
    private JTextField adminTypeField = new JTextField(10);
    private JList<String> masterAnimeList;
    private JList<String> masterMangaList;
    private DefaultListModel<String> masterAnimeModel = new DefaultListModel<>();
    private DefaultListModel<String> masterMangaModel = new DefaultListModel<>();

    // user session
    private String currentUser = null;
    private DefaultListModel<String> userAnimeModel = new DefaultListModel<>();
    private DefaultListModel<String> userMangaModel = new DefaultListModel<>();
    private JList<String> userAnimeList;
    private JList<String> userMangaList;
    private Map<Integer, AnimeEntry> userAnime = new TreeMap<>();
    private Map<Integer, MangaEntry> userManga = new TreeMap<>();

    public TrackerAppSwing() {
        super("Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // ensure accounts dir
        new File(ACCOUNTS_DIR).mkdirs();

        loadMasterAnime();
        loadMasterManga();

        initUI();
    }

    private void initUI() {
        cardPanel.add(buildMainMenu(), "MAIN");
        cardPanel.add(buildLoginPanel(), "LOGIN");
        cardPanel.add(buildAdminPanel(), "ADMIN");
        cardPanel.add(buildUserPanel(), "USER");

        add(cardPanel);
        showMain();
    }

    // ----- Panels -----
    private JPanel buildMainMenu() {
        JPanel p = new JPanel(new BorderLayout());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        p.add(titleLabel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton signInBtn = new JButton("Sign-in / Login");
        signInBtn.addActionListener(e -> cards.show(cardPanel, "LOGIN"));
        center.add(signInBtn, gbc);

        gbc.gridy++;
        JButton adminBtn = new JButton("Admin Menu");
        adminBtn.addActionListener(e -> { refreshMasterLists(); cards.show(cardPanel, "ADMIN"); });
        center.add(adminBtn, gbc);

        gbc.gridy++;
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        center.add(exitBtn, gbc);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("Sign-in / Login", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(lbl, BorderLayout.NORTH);

        JPanel mid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.gridx = 0; gbc.gridy = 0;
        mid.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        mid.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JPanel btnRow = new JPanel();
        JButton createBtn = new JButton("Create New Account");
        createBtn.addActionListener(e -> createAccountAction());
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> loginAction());
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> showMain());
        btnRow.add(createBtn); btnRow.add(loginBtn); btnRow.add(backBtn);
        mid.add(btnRow, gbc);

        p.add(mid, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAdminPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("Admin Menu", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,2));

        // Left: add form
        JPanel left = new JPanel(new GridBagLayout());
        left.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; left.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; left.add(adminTitleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; left.add(new JLabel("Image URL:"), gbc);
        gbc.gridx = 1; left.add(adminImgField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; left.add(new JLabel("Total Episodes/Chapters:"), gbc);
        gbc.gridx = 1; left.add(adminTotalField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; left.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; left.add(adminTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel addBtns = new JPanel();
        JButton addAnimeBtn = new JButton("Add Anime");
        addAnimeBtn.addActionListener(e -> addAnimeAdminAction());
        JButton addMangaBtn = new JButton("Add Manga");
        addMangaBtn.addActionListener(e -> addMangaAdminAction());
        addBtns.add(addAnimeBtn); addBtns.add(addMangaBtn);
        left.add(addBtns, gbc);

        // Right: master lists
        JPanel right = new JPanel(new BorderLayout());
        masterAnimeList = new JList<>(masterAnimeModel);
        masterMangaList = new JList<>(masterMangaModel);
        JPanel lists = new JPanel(new GridLayout(2,1));
        lists.add(new JScrollPane(masterAnimeList));
        lists.add(new JScrollPane(masterMangaList));
        right.add(lists, BorderLayout.CENTER);

        center.add(left); center.add(right);

        p.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton back = new JButton("Back"); back.addActionListener(e -> showMain());
        bottom.add(back);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildUserPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("User Collection", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,2));

        // Left: Anime
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(new EmptyBorder(6,6,6,6));
        userAnimeList = new JList<>(userAnimeModel);
        left.add(new JLabel("Anime"), BorderLayout.NORTH);
        left.add(new JScrollPane(userAnimeList), BorderLayout.CENTER);
        JPanel leftBtns = new JPanel();
        JButton addAnimeBtn = new JButton("Add from Master"); addAnimeBtn.addActionListener(e -> addFromMaster(true));
        JButton removeAnimeBtn = new JButton("Remove"); removeAnimeBtn.addActionListener(e -> removeSelected(true));
        JButton updateAnimeBtn = new JButton("Update"); updateAnimeBtn.addActionListener(e -> updateSelected(true));
        leftBtns.add(addAnimeBtn); leftBtns.add(removeAnimeBtn); leftBtns.add(updateAnimeBtn);
        left.add(leftBtns, BorderLayout.SOUTH);

        // Right: Manga
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(new EmptyBorder(6,6,6,6));
        userMangaList = new JList<>(userMangaModel);
        right.add(new JLabel("Manga"), BorderLayout.NORTH);
        right.add(new JScrollPane(userMangaList), BorderLayout.CENTER);
        JPanel rightBtns = new JPanel();
        JButton addMangaBtn = new JButton("Add from Master"); addMangaBtn.addActionListener(e -> addFromMaster(false));
        JButton removeMangaBtn = new JButton("Remove"); removeMangaBtn.addActionListener(e -> removeSelected(false));
        JButton updateMangaBtn = new JButton("Update"); updateMangaBtn.addActionListener(e -> updateSelected(false));
        rightBtns.add(addMangaBtn); rightBtns.add(removeMangaBtn); rightBtns.add(updateMangaBtn);
        right.add(rightBtns, BorderLayout.SOUTH);

        center.add(left); center.add(right);
        p.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton logout = new JButton("Logout"); logout.addActionListener(e -> { saveUserCollection(currentUser); currentUser = null; showMain(); });
        bottom.add(logout);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    // ----- Actions -----
    private void createAccountAction() {
        String u = usernameField.getText().trim();
        if (u.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter username."); return; }
        File f = new File(ACCOUNTS_DIR, u + ".txt");
        if (f.exists()) { JOptionPane.showMessageDialog(this, "Account already exists."); return; }
        try { f.createNewFile(); JOptionPane.showMessageDialog(this, "Account created. Logging in..."); loginUser(u); }
        catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage()); }
    }

    private void loginAction() {
        String u = usernameField.getText().trim();
        if (u.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter username."); return; }
        File f = new File(ACCOUNTS_DIR, u + ".txt");
        if (!f.exists()) { int r = JOptionPane.showConfirmDialog(this, "Account doesn't exist. Create it?", "Create?", JOptionPane.YES_NO_OPTION); if (r==JOptionPane.YES_OPTION) { createAccountAction(); } return; }
        loginUser(u);
    }

    private void loginUser(String u) {
        currentUser = u;
        loadUserCollection(u);
        refreshUserLists();
        cards.show(cardPanel, "USER");
    }

    private void addAnimeAdminAction() {
        String title = adminTitleField.getText().trim();
        if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter title."); return; }
        String img = adminImgField.getText().trim();
        int total = parseIntOrDefault(adminTotalField.getText().trim(), 0);
        String type = adminTypeField.getText().trim();
        int id = nextAnimeID();
        AnimeEntry e = new AnimeEntry(title, img, 0.0, 0, total, id, type.isEmpty() ? "TV" : type, "N/A");
        animeMaster.put(id, e);
        saveMasterAnime();
        JOptionPane.showMessageDialog(this, "Added anime with ID " + id);
        refreshMasterLists();
    }

    private void addMangaAdminAction() {
        String title = adminTitleField.getText().trim();
        if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter title."); return; }
        String img = adminImgField.getText().trim();
        int total = parseIntOrDefault(adminTotalField.getText().trim(), 0);
        String type = adminTypeField.getText().trim();
        int id = nextMangaID();
        MangaEntry m = new MangaEntry(title, img, 0.0, 0, total, id, type.isEmpty() ? "Manga" : type, "N/A");
        mangaMaster.put(id, m);
        saveMasterManga();
        JOptionPane.showMessageDialog(this, "Added manga with ID " + id);
        refreshMasterLists();
    }

    private void addFromMaster(boolean isAnime) {
        if (currentUser == null) return;
        String input = JOptionPane.showInputDialog(this, "Enter ID to add (or leave blank to pick from master):");
        if (input == null) return;
        if (input.trim().isEmpty()) {
            // show a selection dialog
            if (isAnime) {
                String sel = (String) JOptionPane.showInputDialog(this, "Select Anime:", "Select", JOptionPane.PLAIN_MESSAGE, null, masterAnimeModel.toArray(), null);
                if (sel == null) return;
                int id = Integer.parseInt(sel.split(" - ")[0]);
                addAnimeToUserById(id);
            } else {
                String sel = (String) JOptionPane.showInputDialog(this, "Select Manga:", "Select", JOptionPane.PLAIN_MESSAGE, null, masterMangaModel.toArray(), null);
                if (sel == null) return;
                int id = Integer.parseInt(sel.split(" - ")[0]);
                addMangaToUserById(id);
            }
        } else {
            try {
                int id = Integer.parseInt(input.trim());
                if (isAnime) addAnimeToUserById(id); else addMangaToUserById(id);
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid id."); }
        }
    }

    private void addAnimeToUserById(int id) {
        if (!animeMaster.containsKey(id)) { JOptionPane.showMessageDialog(this, "No such anime id in master."); return; }
        if (userAnime.containsKey(id)) { JOptionPane.showMessageDialog(this, "Already in your collection."); return; }
        AnimeEntry m = animeMaster.get(id);
        AnimeEntry copy = new AnimeEntry(m.getTitle(), m.getImgURL(), 0.0, 0, m.getTotalProgress(), id, m.getType(), "Watching");
        userAnime.put(id, copy);
        saveUserCollection(currentUser);
        refreshUserLists();
    }

    private void addMangaToUserById(int id) {
        if (!mangaMaster.containsKey(id)) { JOptionPane.showMessageDialog(this, "No such manga id in master."); return; }
        if (userManga.containsKey(id)) { JOptionPane.showMessageDialog(this, "Already in your collection."); return; }
        MangaEntry m = mangaMaster.get(id);
        MangaEntry copy = new MangaEntry(m.getTitle(), m.getImgURL(), 0.0, 0, m.getTotalProgress(), id, m.getType(), "Reading");
        userManga.put(id, copy);
        saveUserCollection(currentUser);
        refreshUserLists();
    }

    private void removeSelected(boolean isAnime) {
        if (currentUser == null) return;
        if (isAnime) {
            String sel = userAnimeList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select an anime first."); return; }
            int id = Integer.parseInt(sel.split(" - ")[0]);
            userAnime.remove(id);
        } else {
            String sel = userMangaList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a manga first."); return; }
            int id = Integer.parseInt(sel.split(" - ")[0]);
            userManga.remove(id);
        }
        saveUserCollection(currentUser);
        refreshUserLists();
    }

    private void updateSelected(boolean isAnime) {
        if (currentUser == null) return;
        if (isAnime) {
            String sel = userAnimeList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select an anime first."); return; }
            int id = Integer.parseInt(sel.split(" - ")[0]);
            AnimeEntry e = userAnime.get(id);
            updateEntryDialog(e, true);
        } else {
            String sel = userMangaList.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a manga first."); return; }
            int id = Integer.parseInt(sel.split(" - ")[0]);
            MangaEntry m = userManga.get(id);
            updateEntryDialog(m, false);
        }
        saveUserCollection(currentUser);
        refreshUserLists();
    }

    private void updateEntryDialog(Entry e, boolean isAnime) {
        JTextField progField = new JTextField(String.valueOf(e.getCurrentProgress()));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Watching","Completed","Reading"});
        statusBox.setSelectedItem(e.getState());
        JTextField ratingField = new JTextField(String.valueOf(e.getRating()));
        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel("Progress (number):")); panel.add(progField);
        panel.add(new JLabel("Status:")); panel.add(statusBox);
        panel.add(new JLabel("Rating (0-10):")); panel.add(ratingField);
        int res = JOptionPane.showConfirmDialog(this, panel, "Update", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int prog = Integer.parseInt(progField.getText().trim());
                if (prog < 0) prog = 0;
                if (prog > e.getTotalProgress()) prog = e.getTotalProgress();
                e.setCurrentProgress(prog);
            } catch (NumberFormatException ex) { /* ignore */ }
            String st = (String) statusBox.getSelectedItem();
            if (st != null) {
                if (st.equalsIgnoreCase("Completed")) {
                    e.setState("Completed");
                    e.setCurrentProgress(e.getTotalProgress());
                } else e.setState(st);
            }
            try { double r = Double.parseDouble(ratingField.getText().trim()); if (r < 0) r = 0; if (r > 10) r = 10; e.setRating(r); } catch (Exception ignored) {}
        }
    }

    // ----- Persistence -----
    private void loadMasterAnime() {
        animeMaster.clear();
        File f = new File(ANIME_DB);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1); if (parts.length < 5) continue;
                int id = Integer.parseInt(parts[0]); String title = parts[1]; String img = parts[2]; int total = Integer.parseInt(parts[3]); String type = parts[4];
                animeMaster.put(id, new AnimeEntry(title, img, 0.0, 0, total, id, type, "N/A"));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveMasterAnime() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ANIME_DB))) {
            for (int id : animeMaster.keySet()) {
                AnimeEntry a = animeMaster.get(id);
                bw.write(String.format("%d|%s|%s|%d|%s\n", id, escape(a.getTitle()), escape(a.getImgURL()), a.getTotalProgress(), escape(a.getType())));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadMasterManga() {
        mangaMaster.clear();
        File f = new File(MANGA_DB);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1); if (parts.length < 5) continue;
                int id = Integer.parseInt(parts[0]); String title = parts[1]; String img = parts[2]; int total = Integer.parseInt(parts[3]); String type = parts[4];
                mangaMaster.put(id, new MangaEntry(title, img, 0.0, 0, total, id, type, "N/A"));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveMasterManga() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MANGA_DB))) {
            for (int id : mangaMaster.keySet()) {
                MangaEntry m = mangaMaster.get(id);
                bw.write(String.format("%d|%s|%s|%d|%s\n", id, escape(m.getTitle()), escape(m.getImgURL()), m.getTotalProgress(), escape(m.getType())));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadUserCollection(String username) {
        userAnime.clear(); userManga.clear();
        File f = new File(ACCOUNTS_DIR, username + ".txt");
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1); if (parts.length < 5) continue;
                String kind = parts[0]; int id = Integer.parseInt(parts[1]); int prog = Integer.parseInt(parts[2]); String status = parts[3]; double rating = 0.0; try { rating = Double.parseDouble(parts[4]); } catch (Exception ignored) {}
                if (kind.equals("A")) {
                    if (animeMaster.containsKey(id)) {
                        AnimeEntry m = animeMaster.get(id);
                        userAnime.put(id, new AnimeEntry(m.getTitle(), m.getImgURL(), rating, prog, m.getTotalProgress(), id, m.getType(), status));
                    } else userAnime.put(id, new AnimeEntry("Unknown","",rating,prog,prog,id,"Unknown",status));
                } else if (kind.equals("M")) {
                    if (mangaMaster.containsKey(id)) {
                        MangaEntry mm = mangaMaster.get(id);
                        userManga.put(id, new MangaEntry(mm.getTitle(), mm.getImgURL(), rating, prog, mm.getTotalProgress(), id, mm.getType(), status));
                    } else userManga.put(id, new MangaEntry("Unknown","",rating,prog,prog,id,"Unknown",status));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveUserCollection(String username) {
        File f = new File(ACCOUNTS_DIR, username + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (int id : userAnime.keySet()) {
                AnimeEntry a = userAnime.get(id);
                bw.write(String.format("A|%d|%d|%s|%.1f\n", id, a.getCurrentProgress(), escape(a.getState()), a.getRating()));
            }
            for (int id : userManga.keySet()) {
                MangaEntry m = userManga.get(id);
                bw.write(String.format("M|%d|%d|%s|%.1f\n", id, m.getCurrentProgress(), escape(m.getState()), m.getRating()));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ----- Utilities -----
    private void refreshMasterLists() {
        masterAnimeModel.clear(); masterMangaModel.clear();
        for (int id : animeMaster.keySet()) {
            AnimeEntry a = animeMaster.get(id);
            masterAnimeModel.addElement(String.format("%d - %s", id, a.getTitle()));
        }
        for (int id : mangaMaster.keySet()) {
            MangaEntry m = mangaMaster.get(id);
            masterMangaModel.addElement(String.format("%d - %s", id, m.getTitle()));
        }
    }

    private void refreshUserLists() {
        userAnimeModel.clear(); userMangaModel.clear();
        for (int id : userAnime.keySet()) userAnimeModel.addElement(userAnime.get(id).toString());
        for (int id : userManga.keySet()) userMangaModel.addElement(userManga.get(id).toString());
    }

    private void showMain() { cards.show(cardPanel, "MAIN"); }

    private int nextAnimeID() { int max=0; for (int id: animeMaster.keySet()) if (id>max) max=id; return max+1; }
    private int nextMangaID() { int max=0; for (int id: mangaMaster.keySet()) if (id>max) max=id; return max+1; }

    private int parseIntOrDefault(String s, int def) { try { return Integer.parseInt(s); } catch (Exception e) { return def; } }

    private String escape(String s) { if (s==null) return ""; return s.replace("\n"," ").replace("\r"," "); }

    // ----- startup file loading helpers -----
    private void loadMasterMangaAndAnime() { loadMasterAnime(); loadMasterManga(); }

    // Initial load helpers called in constructor
    private void loadMasterMangaQuiet() { loadMasterManga(); }

    // wrapper for loading user collection into user maps
    private void loadUserCollectionQuiet(String username) { loadUserCollection(username); }

    // ----- main -----
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrackerAppSwing app = new TrackerAppSwing();
            app.setVisible(true);
        });
    }
}
