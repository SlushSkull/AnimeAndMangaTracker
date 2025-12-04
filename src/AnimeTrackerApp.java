import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

// Abstract Show class
abstract class Show {
    protected String id;
    protected String title;
    protected String imageUrl;
    
    public Show(String id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    
    public abstract String getType();
    public abstract String toFileString();
}

// Anime class
class Anime extends Show {
    private int totalEpisodes;
    private int watchedEpisodes;
    
    public Anime(String id, String title, String imageUrl, int totalEpisodes) {
        super(id, title, imageUrl);
        this.totalEpisodes = totalEpisodes;
        this.watchedEpisodes = 0;
    }
    
    public int getTotalEpisodes() { return totalEpisodes; }
    public int getWatchedEpisodes() { return watchedEpisodes; }
    public void setWatchedEpisodes(int episodes) { this.watchedEpisodes = episodes; }
    
    @Override
    public String getType() { return "ANIME"; }
    
    @Override
    public String toFileString() {
        return id + "|" + title + "|" + imageUrl + "|" + totalEpisodes;
    }
    
    public static Anime fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Anime(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
    }
}

// Manga class
class Manga extends Show {
    private int totalChapters;
    private int readChapters;
    
    public Manga(String id, String title, String imageUrl, int totalChapters) {
        super(id, title, imageUrl);
        this.totalChapters = totalChapters;
        this.readChapters = 0;
    }
    
    public int getTotalChapters() { return totalChapters; }
    public int getReadChapters() { return readChapters; }
    public void setReadChapters(int chapters) { this.readChapters = chapters; }
    
    @Override
    public String getType() { return "MANGA"; }
    
    @Override
    public String toFileString() {
        return id + "|" + title + "|" + imageUrl + "|" + totalChapters;
    }
    
    public static Manga fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Manga(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
    }
}

// UserShowEntry class to hold status and progress
class UserShowEntry {
    private String status;
    private int progress;
    private String showId;
    
    public UserShowEntry(String status, int progress) {
        this.status = status;
        this.progress = progress;
    }
    
    public String getStatus() { return status; }
    public int getProgress() { return progress; }
    public String getShowId() { return showId; }
    public void setShowId(String showId) { this.showId = showId; }
}

// Theme class
class Theme {
    public static final Color PRIMARY = new Color(41, 128, 185);
    public static final Color SECONDARY = new Color(52, 73, 94);
    public static final Color BACKGROUND = new Color(236, 240, 241);
    public static final Color TEXT = new Color(44, 62, 80);
    public static final Color ACCENT = new Color(231, 76, 60);
}

// TrackerAPI class
class TrackerAPI {
    private static final String ANIME_FILE = "anime_database.txt";
    private static final String MANGA_FILE = "manga_database.txt";
    private static final String USERS_DIR = "users/";
    
    public TrackerAPI() {
        new File(USERS_DIR).mkdirs();
    }
    
    // Anime database methods
    public void addAnime(Anime anime) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ANIME_FILE, true))) {
            writer.write(anime.toFileString());
            writer.newLine();
        }
    }
    
    public List<Anime> getAllAnime() {
        List<Anime> animeList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ANIME_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                animeList.add(Anime.fromFileString(line));
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return animeList;
    }
    
    // Manga database methods
    public void addManga(Manga manga) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MANGA_FILE, true))) {
            writer.write(manga.toFileString());
            writer.newLine();
        }
    }
    
    public List<Manga> getAllManga() {
        List<Manga> mangaList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MANGA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                mangaList.add(Manga.fromFileString(line));
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return mangaList;
    }
    
    // User methods
    public boolean createUser(String username) {
        File userFile = new File(USERS_DIR + username + ".txt");
        if (userFile.exists()) {
            return false;
        }
        try {
            userFile.createNewFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public boolean userExists(String username) {
        return new File(USERS_DIR + username + ".txt").exists();
    }
    
    public void addToUserList(String username, String showId, String status, String type, int progress) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_DIR + username + ".txt", true))) {
            writer.write(type + "|" + showId + "|" + status + "|" + progress);
            writer.newLine();
        }
    }
    
    public void updateUserEntry(String username, String showId, String type, String newStatus, int newProgress) throws IOException {
        File userFile = new File(USERS_DIR + username + ".txt");
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(type) && parts[1].equals(showId)) {
                    lines.add(type + "|" + showId + "|" + newStatus + "|" + newProgress);
                } else {
                    lines.add(line);
                }
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    public UserShowEntry getUserShowEntry(String username, String showId, String type) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_DIR + username + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(type) && parts[1].equals(showId)) {
                    return new UserShowEntry(parts[2], Integer.parseInt(parts[3]));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return null;
    }
    
    public Map<String, List<UserShowEntry>> getUserAnime(String username) {
        Map<String, List<UserShowEntry>> animeByStatus = new HashMap<>();
        animeByStatus.put("Watching", new ArrayList<>());
        animeByStatus.put("Completed", new ArrayList<>());
        animeByStatus.put("Plan to Watch", new ArrayList<>());
        animeByStatus.put("Dropped", new ArrayList<>());
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_DIR + username + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("ANIME")) {
                    UserShowEntry entry = new UserShowEntry(parts[2], Integer.parseInt(parts[3]));
                    entry.setShowId(parts[1]);
                    animeByStatus.get(parts[2]).add(entry);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return animeByStatus;
    }
    
    public Map<String, List<UserShowEntry>> getUserManga(String username) {
        Map<String, List<UserShowEntry>> mangaByStatus = new HashMap<>();
        mangaByStatus.put("Reading", new ArrayList<>());
        mangaByStatus.put("Completed", new ArrayList<>());
        mangaByStatus.put("Plan to Read", new ArrayList<>());
        mangaByStatus.put("Dropped", new ArrayList<>());
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_DIR + username + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("MANGA")) {
                    UserShowEntry entry = new UserShowEntry(parts[2], Integer.parseInt(parts[3]));
                    entry.setShowId(parts[1]);
                    mangaByStatus.get(parts[2]).add(entry);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return mangaByStatus;
    }
    
    public void removeFromUserList(String username, String showId, String type) throws IOException {
        File userFile = new File(USERS_DIR + username + ".txt");
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (!(parts[0].equals(type) && parts[1].equals(showId))) {
                    lines.add(line);
                }
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}

// Main Application
public class AnimeTrackerApp extends JFrame {
    private TrackerAPI api;
    private String currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public AnimeTrackerApp() {
        api = new TrackerAPI();
        setTitle("Anime and Manga Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createMainMenu(), "MAIN_MENU");
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createAdminPanel(), "ADMIN");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "MAIN_MENU");
    }
    
    private JPanel createMainMenu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND);
        
        JLabel titleLabel = new JLabel("Anime and Manga Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Theme.PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Theme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton loginBtn = createStyledButton("Sign-in / Login");
        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        
        JButton adminBtn = createStyledButton("Admin Menu");
        adminBtn.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN"));
        
        JButton settingsBtn = createStyledButton("Settings");
        settingsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Settings coming soon!"));
        
        buttonPanel.add(loginBtn, gbc);
        buttonPanel.add(adminBtn, gbc);
        buttonPanel.add(settingsBtn, gbc);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND);
        
        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Theme.PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Theme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton createAccountBtn = createStyledButton("Create New Account");
        createAccountBtn.addActionListener(e -> createAccount());
        
        JButton loginBtn = createStyledButton("Login");
        loginBtn.addActionListener(e -> login());
        
        JButton backBtn = createStyledButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        
        centerPanel.add(createAccountBtn, gbc);
        centerPanel.add(loginBtn, gbc);
        centerPanel.add(backBtn, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void createAccount() {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        if (username != null && !username.trim().isEmpty()) {
            if (api.createUser(username)) {
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                currentUser = username;
                showUserDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            }
        }
    }
    
    private void login() {
        String username = JOptionPane.showInputDialog(this, "Enter username:");
        if (username != null && !username.trim().isEmpty()) {
            if (api.userExists(username)) {
                currentUser = username;
                showUserDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "User not found!");
            }
        }
    }
    
    private void showUserDashboard() {
        JPanel dashboard = createUserDashboard();
        mainPanel.add(dashboard, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    
    private JPanel createUserDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Theme.PRIMARY);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(welcomeLabel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Anime tabs
        tabbedPane.addTab("Watching", createAnimeStatusPanel("Watching"));
        tabbedPane.addTab("Completed Anime", createAnimeStatusPanel("Completed"));
        tabbedPane.addTab("Plan to Watch", createAnimeStatusPanel("Plan to Watch"));
        tabbedPane.addTab("Dropped Anime", createAnimeStatusPanel("Dropped"));
        
        // Manga tabs
        tabbedPane.addTab("Reading", createMangaStatusPanel("Reading"));
        tabbedPane.addTab("Completed Manga", createMangaStatusPanel("Completed"));
        tabbedPane.addTab("Plan to Read", createMangaStatusPanel("Plan to Read"));
        tabbedPane.addTab("Dropped Manga", createMangaStatusPanel("Dropped"));
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(Theme.BACKGROUND);
        
        JButton addAnimeBtn = createStyledButton("Add Anime");
        addAnimeBtn.addActionListener(e -> addAnimeToList());
        
        JButton addMangaBtn = createStyledButton("Add Manga");
        addMangaBtn.addActionListener(e -> addMangaToList());
        
        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "MAIN_MENU");
        });
        
        bottomPanel.add(addAnimeBtn);
        bottomPanel.add(addMangaBtn);
        bottomPanel.add(logoutBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createAnimeStatusPanel(String status) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        
        Map<String, List<UserShowEntry>> userAnime = api.getUserAnime(currentUser);
        List<Anime> allAnime = api.getAllAnime();
        
        for (UserShowEntry entry : userAnime.get(status)) {
            for (Anime anime : allAnime) {
                if (anime.getId().equals(entry.getShowId())) {
                    listModel.addElement(anime.getTitle() + " - " + entry.getProgress() + "/" + 
                                       anime.getTotalEpisodes() + " eps (ID: " + anime.getId() + ")");
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        
        JButton editBtn = createStyledButton("Edit Selected");
        editBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                editAnime(id, status);
            }
        });
        
        JButton removeBtn = createStyledButton("Remove Selected");
        removeBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                try {
                    api.removeFromUserList(currentUser, id, "ANIME");
                    listModel.removeElement(selected);
                    JOptionPane.showMessageDialog(this, "Removed successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error removing anime!");
                }
            }
        });
        
        btnPanel.add(editBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void editAnime(String animeId, String currentStatus) {
        Anime anime = null;
        for (Anime a : api.getAllAnime()) {
            if (a.getId().equals(animeId)) {
                anime = a;
                break;
            }
        }
        
        if (anime == null) return;
        
        UserShowEntry entry = api.getUserShowEntry(currentUser, animeId, "ANIME");
        
        JPanel editPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel titleLabel = new JLabel("Editing: " + anime.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JSpinner episodeSpinner = new JSpinner(new SpinnerNumberModel(
            entry.getProgress(), 0, anime.getTotalEpisodes(), 1));
        
        String[] statuses = {"Watching", "Completed", "Plan to Watch", "Dropped"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(currentStatus);
        
        editPanel.add(titleLabel);
        editPanel.add(new JLabel(""));
        editPanel.add(new JLabel("Episodes Watched:"));
        editPanel.add(episodeSpinner);
        editPanel.add(new JLabel("Status:"));
        editPanel.add(statusCombo);
        
        int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Anime Progress",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int newProgress = (Integer) episodeSpinner.getValue();
                String newStatus = (String) statusCombo.getSelectedItem();
                
                // Auto-complete if watched all episodes
                if (newProgress == anime.getTotalEpisodes() && !newStatus.equals("Completed")) {
                    int choice = JOptionPane.showConfirmDialog(this, 
                        "You've watched all episodes. Mark as Completed?", 
                        "Complete Anime", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        newStatus = "Completed";
                    }
                }
                
                api.updateUserEntry(currentUser, animeId, "ANIME", newStatus, newProgress);
                JOptionPane.showMessageDialog(this, "Updated successfully!");
                showUserDashboard(); // Refresh the dashboard
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error updating anime!");
            }
        }
    }
    
    private JPanel createMangaStatusPanel(String status) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        
        Map<String, List<UserShowEntry>> userManga = api.getUserManga(currentUser);
        List<Manga> allManga = api.getAllManga();
        
        for (UserShowEntry entry : userManga.get(status)) {
            for (Manga manga : allManga) {
                if (manga.getId().equals(entry.getShowId())) {
                    listModel.addElement(manga.getTitle() + " - " + entry.getProgress() + "/" + 
                                       manga.getTotalChapters() + " chs (ID: " + manga.getId() + ")");
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        
        JButton editBtn = createStyledButton("Edit Selected");
        editBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                editManga(id, status);
            }
        });
        
        JButton removeBtn = createStyledButton("Remove Selected");
        removeBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                String id = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                try {
                    api.removeFromUserList(currentUser, id, "MANGA");
                    listModel.removeElement(selected);
                    JOptionPane.showMessageDialog(this, "Removed successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error removing manga!");
                }
            }
        });
        
        btnPanel.add(editBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void editManga(String mangaId, String currentStatus) {
        Manga manga = null;
        for (Manga m : api.getAllManga()) {
            if (m.getId().equals(mangaId)) {
                manga = m;
                break;
            }
        }
        
        if (manga == null) return;
        
        UserShowEntry entry = api.getUserShowEntry(currentUser, mangaId, "MANGA");
        
        JPanel editPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel titleLabel = new JLabel("Editing: " + manga.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JSpinner chapterSpinner = new JSpinner(new SpinnerNumberModel(
            entry.getProgress(), 0, manga.getTotalChapters(), 1));
        
        String[] statuses = {"Reading", "Completed", "Plan to Read", "Dropped"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(currentStatus);
        
        editPanel.add(titleLabel);
        editPanel.add(new JLabel(""));
        editPanel.add(new JLabel("Chapters Read:"));
        editPanel.add(chapterSpinner);
        editPanel.add(new JLabel("Status:"));
        editPanel.add(statusCombo);
        
        int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Manga Progress",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int newProgress = (Integer) chapterSpinner.getValue();
                String newStatus = (String) statusCombo.getSelectedItem();
                
                // Auto-complete if read all chapters
                if (newProgress == manga.getTotalChapters() && !newStatus.equals("Completed")) {
                    int choice = JOptionPane.showConfirmDialog(this, 
                        "You've read all chapters. Mark as Completed?", 
                        "Complete Manga", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        newStatus = "Completed";
                    }
                }
                
                api.updateUserEntry(currentUser, mangaId, "MANGA", newStatus, newProgress);
                JOptionPane.showMessageDialog(this, "Updated successfully!");
                showUserDashboard(); // Refresh the dashboard
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error updating manga!");
            }
        }
    }
    
    private void addAnimeToList() {
        List<Anime> allAnime = api.getAllAnime();
        if (allAnime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No anime in database! Ask admin to add anime first.");
            return;
        }
        
        String[] animeNames = allAnime.stream()
            .map(a -> a.getTitle() + " (ID: " + a.getId() + ")")
            .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(this, "Select Anime:",
            "Add Anime", JOptionPane.QUESTION_MESSAGE, null, animeNames, animeNames[0]);
        
        if (selected != null) {
            String[] statuses = {"Watching", "Completed", "Plan to Watch", "Dropped"};
            String status = (String) JOptionPane.showInputDialog(this, "Select Status:",
                "Add Anime", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
            
            if (status != null) {
                String id = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                try {
                    api.addToUserList(currentUser, id, status, "ANIME", 0);
                    JOptionPane.showMessageDialog(this, "Anime added successfully!");
                    showUserDashboard();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding anime!");
                }
            }
        }
    }
    
    private void addMangaToList() {
        List<Manga> allManga = api.getAllManga();
        if (allManga.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No manga in database! Ask admin to add manga first.");
            return;
        }
        
        String[] mangaNames = allManga.stream()
            .map(m -> m.getTitle() + " (ID: " + m.getId() + ")")
            .toArray(String[]::new);
        
        String selected = (String) JOptionPane.showInputDialog(this, "Select Manga:",
            "Add Manga", JOptionPane.QUESTION_MESSAGE, null, mangaNames, mangaNames[0]);
        
        if (selected != null) {
            String[] statuses = {"Reading", "Completed", "Plan to Read", "Dropped"};
            String status = (String) JOptionPane.showInputDialog(this, "Select Status:",
                "Add Manga", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
            
            if (status != null) {
                String id = selected.substring(selected.indexOf("ID: ") + 4, selected.length() - 1);
                try {
                    api.addToUserList(currentUser, id, status, "MANGA", 0);
                    JOptionPane.showMessageDialog(this, "Manga added successfully!");
                    showUserDashboard();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding manga!");
                }
            }
        }
    }
    
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND);
        
        JLabel titleLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Theme.ACCENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Theme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton addAnimeBtn = createStyledButton("Add Anime to Database");
        addAnimeBtn.addActionListener(e -> addAnimeToDatabase());
        
        JButton addMangaBtn = createStyledButton("Add Manga to Database");
        addMangaBtn.addActionListener(e -> addMangaToDatabase());
        
        JButton backBtn = createStyledButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        
        centerPanel.add(addAnimeBtn, gbc);
        centerPanel.add(addMangaBtn, gbc);
        centerPanel.add(backBtn, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void addAnimeToDatabase() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField episodesField = new JTextField();
        JTextField imageUrlField = new JTextField();
        
        inputPanel.add(new JLabel("Anime ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Total Episodes:"));
        inputPanel.add(episodesField);
        inputPanel.add(new JLabel("Image URL:"));
        inputPanel.add(imageUrlField);
        
        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Anime",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText();
                String title = titleField.getText();
                int episodes = Integer.parseInt(episodesField.getText());
                String imageUrl = imageUrlField.getText();
                
                Anime anime = new Anime(id, title, imageUrl, episodes);
                api.addAnime(anime);
                JOptionPane.showMessageDialog(this, "Anime added to database!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding anime: " + ex.getMessage());
            }
        }
    }
    
    private void addMangaToDatabase() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField chaptersField = new JTextField();
        JTextField imageUrlField = new JTextField();
        
        inputPanel.add(new JLabel("Manga ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Total Chapters:"));
        inputPanel.add(chaptersField);
        inputPanel.add(new JLabel("Image URL:"));
        inputPanel.add(imageUrlField);
        
        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Manga",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText();
                String title = titleField.getText();
                int chapters = Integer.parseInt(chaptersField.getText());
                String imageUrl = imageUrlField.getText();
                
                Manga manga = new Manga(id, title, imageUrl, chapters);
                api.addManga(manga);
                JOptionPane.showMessageDialog(this, "Manga added to database!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding manga: " + ex.getMessage());
            }
        }
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Theme.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Theme.SECONDARY);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.PRIMARY);
            }
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AnimeTrackerApp app = new AnimeTrackerApp();
            app.setVisible(true);
        });
    }
}