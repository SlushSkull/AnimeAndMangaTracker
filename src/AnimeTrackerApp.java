import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.List;

// Refactor note: core models, API and utilities have been moved to separate files in `src/`.
// - Show, Anime, Manga, UserShowEntry -> Show.java, Anime.java, Manga.java, UserShowEntry.java
// - ImageLoader -> ImageLoader.java
// - TrackerAPI -> TrackerAPI.java
// - Theme -> Theme.java

// Main Application
public class AnimeTrackerApp extends JFrame {
    TrackerAPI api;
    String currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public AnimeTrackerApp() {
        api = new TrackerAPI();
        setTitle("Anime and Manga Tracker");
        setSize(900, 700);
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
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Theme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton loginBtn = UIHelpers.createStyledButton("Sign-in / Login");
        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        
        JButton adminBtn = UIHelpers.createStyledButton("Admin Menu");
        adminBtn.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN"));
        
        JButton exitBtn = UIHelpers.createStyledButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        
        gbc.gridy = 0; buttonPanel.add(loginBtn, gbc);
        gbc.gridy = 1; buttonPanel.add(adminBtn, gbc);
        gbc.gridy = 2; buttonPanel.add(exitBtn, gbc);
        
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
        
        JButton createAccountBtn = UIHelpers.createStyledButton("Create New Account");
        createAccountBtn.addActionListener(e -> createAccount());
        
        JButton loginBtn = UIHelpers.createStyledButton("Login");
        loginBtn.addActionListener(e -> login());
        
        JButton backBtn = UIHelpers.createStyledButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        
        gbc.gridy = 0; centerPanel.add(createAccountBtn, gbc);
        gbc.gridy = 1; centerPanel.add(loginBtn, gbc);
        gbc.gridy = 2; centerPanel.add(backBtn, gbc);
        
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
    
    public void showUserDashboard() {
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
        
        JButton addAnimeBtn = UIHelpers.createStyledButton("Add Anime");
        addAnimeBtn.addActionListener(e -> AddToListActions.addAnimeToList(this));
        
        JButton addMangaBtn = UIHelpers.createStyledButton("Add Manga");
        addMangaBtn.addActionListener(e -> AddToListActions.addMangaToList(this));
        
        JButton logoutBtn = UIHelpers.createStyledButton("Logout");
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
        
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        Map<String, List<UserShowEntry>> userAnime = api.getUserAnime(currentUser);
        List<Anime> allAnime = api.getAllAnime();
        
        List<UserShowEntry> entries = userAnime.get(status);
        if (entries != null) {
            for (UserShowEntry entry : entries) {
                for (Anime anime : allAnime) {
                        if (anime.getId().equals(entry.getShowId())) {
                            JPanel card = UIHelpers.createAnimeCard(anime, entry, status, this);
                            gridPanel.add(card);
                        }
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Anime card rendering moved to `UIHelpers.createAnimeCard(...)`.
    
    // Edit dialog logic for anime moved to `EditDialogs.editAnime(...)`.
    
    private JPanel createMangaStatusPanel(String status) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<String, List<UserShowEntry>> userManga = api.getUserManga(currentUser);
        List<Manga> allManga = api.getAllManga();

        List<UserShowEntry> entries = userManga.get(status);
        if (entries != null) {
            for (UserShowEntry entry : entries) {
                for (Manga manga : allManga) {
                        if (manga.getId().equals(entry.getShowId())) {
                            JPanel card = UIHelpers.createMangaCard(manga, entry, status, this);
                            gridPanel.add(card);
                        }
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Manga card rendering moved to `UIHelpers.createMangaCard(...)`.
    
    // Edit dialog logic for manga moved to `EditDialogs.editManga(...)`.
    
    // addAnimeToList moved to `AddToListActions.addAnimeToList(...)`.
    
    // addMangaToList moved to `AddToListActions.addMangaToList(...)`.
    
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
        
        JButton addAnimeBtn = UIHelpers.createStyledButton("Add Anime to Database");
        addAnimeBtn.addActionListener(e -> AdminActions.addAnimeToDatabase(this));
        
        JButton addMangaBtn = UIHelpers.createStyledButton("Add Manga to Database");
        addMangaBtn.addActionListener(e -> AdminActions.addMangaToDatabase(this));
        
        JButton backBtn = UIHelpers.createStyledButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));
        
        gbc.gridy = 0; centerPanel.add(addAnimeBtn, gbc);
        gbc.gridy = 1; centerPanel.add(addMangaBtn, gbc);
        gbc.gridy = 2; centerPanel.add(backBtn, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    // addAnimeToDatabase moved to `AdminActions.addAnimeToDatabase(...)`.
    
    // addMangaToDatabase moved to `AdminActions.addMangaToDatabase(...)`.
    
    // Local UI helper methods moved to `UIHelpers`.
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AnimeTrackerApp app = new AnimeTrackerApp();
            app.setVisible(true);
        });
    }
}
