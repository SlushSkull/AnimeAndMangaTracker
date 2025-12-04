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
    
    public void addToUserList(String username, String showId, String status, String type) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_DIR + username + ".txt", true))) {
            writer.write(type + "|" + showId + "|" + status);
            writer.newLine();
        }
    }
    
    public Map<String, List<String>> getUserAnime(String username) {
        Map<String, List<String>> animeByStatus = new HashMap<>();
        animeByStatus.put("Watching", new ArrayList<>());
        animeByStatus.put("Completed", new ArrayList<>());
        animeByStatus.put("Plan to Watch", new ArrayList<>());
        animeByStatus.put("Dropped", new ArrayList<>());
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_DIR + username + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("ANIME")) {
                    animeByStatus.get(parts[2]).add(parts[1]);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return animeByStatus;
    }
    
    public Map<String, List<String>> getUserManga(String username) {
        Map<String, List<String>> mangaByStatus = new HashMap<>();
        mangaByStatus.put("Reading", new ArrayList<>());
        mangaByStatus.put("Completed", new ArrayList<>());
        mangaByStatus.put("Plan to Read", new ArrayList<>());
        mangaByStatus.put("Dropped", new ArrayList<>());
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_DIR + username + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals("MANGA")) {
                    mangaByStatus.get(parts[2]).add(parts[1]);
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
        
        Map<String, List<String>> userAnime = api.getUserAnime(currentUser);
        List<Anime> allAnime = api.getAllAnime();
        
        for (String animeId : userAnime.get(status)) {
            for (Anime anime : allAnime) {
                if (anime.getId().equals(animeId)) {
                    listModel.addElement(anime.getTitle() + " (ID: " + anime.getId() + ")");
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMangaStatusPanel(String status) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        
        Map<String, List<String>> userManga = api.getUserManga(currentUser);
        List<Manga> allManga = api.getAllManga();
        
        for (String mangaId : userManga.get(status)) {
            for (Manga manga : allManga) {
                if (manga.getId().equals(mangaId)) {
                    listModel.addElement(manga.getTitle() + " (ID: " + manga.getId() + ")");
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(list);
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
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
                    api.addToUserList(currentUser, id, status, "ANIME");
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
                    api.addToUserList(currentUser, id, status, "MANGA");
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