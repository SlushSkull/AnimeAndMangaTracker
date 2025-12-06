import java.io.*;
import java.util.*;

public class TrackerAPI {
    private static final String ANIME_FILE = "anime_database.txt";
    private static final String MANGA_FILE = "manga_database.txt";
    private static final String USERS_DIR = "users/";
    
    public TrackerAPI() {
        new File(USERS_DIR).mkdirs();
    }

    // Return a new unique Anime ID (UUID-based)
    public String generateAnimeId() {
        return java.util.UUID.randomUUID().toString();
    }
    // Return a new unique Manga ID (UUID-based)
    public String generateMangaId() {
        return java.util.UUID.randomUUID().toString();
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
                if (line.trim().isEmpty()) continue;
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
                if (line.trim().isEmpty()) continue;
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
    
    /**
     * Adds an entry to the user's file.
     * Returns true if added, false if a duplicate was detected.
     */
    public boolean addToUserList(String username, String showId, String status, String type, int progress) throws IOException {
        return addToUserList(username, showId, status, type, progress, -1);
    }

    public boolean addToUserList(String username, String showId, String status, String type, int progress, int rating) throws IOException {
        File userFile = new File(USERS_DIR + username + ".txt");
        // ensure file exists
        if (!userFile.exists()) userFile.createNewFile();
        // check duplicates
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].equals(type) && parts[1].equals(showId)) {
                    return false; // duplicate
                }
            }
        }
        // append (include rating)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            writer.write(type + "|" + showId + "|" + status + "|" + progress + "|" + rating);
            writer.newLine();
        }
        return true;
    }
    
    public void updateUserEntry(String username, String showId, String type, String newStatus, int newProgress) throws IOException {
        updateUserEntry(username, showId, type, newStatus, newProgress, -1);
    }

    public void updateUserEntry(String username, String showId, String type, String newStatus, int newProgress, int newRating) throws IOException {
        File userFile = new File(USERS_DIR + username + ".txt");
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts[0].equals(type) && parts[1].equals(showId)) {
                    lines.add(type + "|" + showId + "|" + newStatus + "|" + newProgress + "|" + newRating);
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
                    int prog = Integer.parseInt(parts[3]);
                    int rating = -1;
                    if (parts.length >= 5) {
                        try { rating = Integer.parseInt(parts[4]); } catch (NumberFormatException nf) { rating = -1; }
                    }
                    UserShowEntry e = new UserShowEntry(parts[2], prog, rating);
                    e.setShowId(parts[1]);
                    return e;
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
        // Read all lines first so we can attempt to correct malformed showIds (e.g., title instead of ID)
        File userFile = new File(USERS_DIR + username + ".txt");
        if (!userFile.exists()) return animeByStatus;

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            return animeByStatus;
        }

        List<String> outLines = new ArrayList<>();
        boolean changed = false;
        List<Anime> allAnime = getAllAnime();

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4 && "ANIME".equals(parts[0])) {
                String storedId = parts[1];
                String status = parts[2];
                int prog = 0;
                try { prog = Integer.parseInt(parts[3]); } catch (NumberFormatException nf) { prog = 0; }
                int rating = -1;
                if (parts.length >= 5) {
                    try { rating = Integer.parseInt(parts[4]); } catch (NumberFormatException nf) { rating = -1; }
                }

                // If storedId doesn't match any anime ID, try to resolve by title
                boolean idMatches = false;
                for (Anime a : allAnime) {
                    if (a.getId().equals(storedId)) { idMatches = true; break; }
                }

                String resolvedId = storedId;
                if (!idMatches) {
                    String candidate = storedId.trim();
                    for (Anime a : allAnime) {
                        String t = a.getTitle() == null ? "" : a.getTitle().trim();
                        if (t.equalsIgnoreCase(candidate) || t.equalsIgnoreCase("The " + candidate) || t.toLowerCase().contains(candidate.toLowerCase())) {
                            resolvedId = a.getId();
                            idMatches = true;
                            break;
                        }
                    }
                }

                UserShowEntry entry = new UserShowEntry(status, prog, rating);
                entry.setShowId(resolvedId);
                List<UserShowEntry> list = animeByStatus.get(status);
                if (list != null) list.add(entry);

                if (!resolvedId.equals(storedId)) {
                    String newLine = String.join("|", "ANIME", resolvedId, status, String.valueOf(prog), String.valueOf(rating));
                    outLines.add(newLine);
                    changed = true;
                } else {
                    outLines.add(line);
                }
            } else {
                outLines.add(line);
            }
        }

        if (changed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
                for (String ol : outLines) {
                    writer.write(ol);
                    writer.newLine();
                }
            } catch (IOException e) {
                // ignore write failures for now
            }
        }

        return animeByStatus;
    }
    
    public Map<String, List<UserShowEntry>> getUserManga(String username) {
        Map<String, List<UserShowEntry>> mangaByStatus = new HashMap<>();
        mangaByStatus.put("Reading", new ArrayList<>());
        mangaByStatus.put("Completed", new ArrayList<>());
        mangaByStatus.put("Plan to Read", new ArrayList<>());
        mangaByStatus.put("Dropped", new ArrayList<>());
        // Read all lines first so we can attempt to correct malformed showIds (e.g., title instead of ID)
        File userFile = new File(USERS_DIR + username + ".txt");
        if (!userFile.exists()) return mangaByStatus;

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            return mangaByStatus;
        }

        List<String> outLines = new ArrayList<>();
        boolean changed = false;
        List<Manga> allManga = getAllManga();

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4 && "MANGA".equals(parts[0])) {
                String storedId = parts[1];
                String status = parts[2];
                int prog = 0;
                try { prog = Integer.parseInt(parts[3]); } catch (NumberFormatException nf) { prog = 0; }
                int rating = -1;
                if (parts.length >= 5) {
                    try { rating = Integer.parseInt(parts[4]); } catch (NumberFormatException nf) { rating = -1; }
                }

                // If storedId doesn't match any manga ID, try to resolve by title
                boolean idMatches = false;
                for (Manga m : allManga) {
                    if (m.getId().equals(storedId)) { idMatches = true; break; }
                }

                String resolvedId = storedId;
                if (!idMatches) {
                    String candidate = storedId.trim();
                    // Try matching by title (exact, case-insensitive, or contains)
                    for (Manga m : allManga) {
                        String t = m.getTitle() == null ? "" : m.getTitle().trim();
                        if (t.equalsIgnoreCase(candidate) || t.equalsIgnoreCase("The " + candidate) || t.toLowerCase().contains(candidate.toLowerCase())) {
                            resolvedId = m.getId();
                            idMatches = true;
                            break;
                        }
                    }
                }

                // Build entry using resolvedId
                UserShowEntry entry = new UserShowEntry(status, prog, rating);
                entry.setShowId(resolvedId);
                List<UserShowEntry> list = mangaByStatus.get(status);
                if (list != null) list.add(entry);

                // If we resolved to a different id, rewrite the line to persist fix
                if (!resolvedId.equals(storedId)) {
                    String newLine = String.join("|", "MANGA", resolvedId, status, String.valueOf(prog), String.valueOf(rating));
                    outLines.add(newLine);
                    changed = true;
                } else {
                    outLines.add(line);
                }
            } else {
                outLines.add(line);
            }
        }

        if (changed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
                for (String ol : outLines) {
                    writer.write(ol);
                    writer.newLine();
                }
            } catch (IOException e) {
                // ignore write failures for now
            }
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
