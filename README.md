
Anime and Manga Tracker
=======================

A small Java Swing desktop app to track anime and manga for users. It stores a central database of anime and manga and a per-user file containing their personal lists (status, progress, and rating). This repository is a student project (Object Oriented Programming course).

**Features**
- Add anime and manga to a central database (admin panel).
- Per-user lists stored in `users/<username>.txt` with entry fields: TYPE|SHOW_ID|STATUS|PROGRESS|RATING
- Status categories for Anime: `Watching`, `Completed`, `Plan to Watch`, `Dropped`.
- Status categories for Manga: `Reading`, `Completed`, `Plan to Read`, `Dropped`.
- Per-entry integer rating (0–10); `-1` means unrated.
- Auto-generated IDs (UUIDs) for Anime and Manga when added to the database.
- Validation when editing progress (cannot set watched/read > total).
- When an entry is marked `Completed`, progress is auto-filled to the total episodes/chapters.
- Simple asynchronous image loading with a placeholder when no image is available.

**Repository layout**
- `src/` — Java source, main app is `AnimeTrackerApp.java`.
- `anime_database.txt` — Central anime DB: each line `ID|Title|ImageUrl|TotalEpisodes`.
- `manga_database.txt` — Central manga DB: each line `ID|Title|ImageUrl|TotalChapters`.
- `users/` — Per-user files named `<username>.txt`.

**User file format**
Each line in `users/<username>.txt` represents a list entry using pipe separators:
```
TYPE|SHOW_ID|STATUS|PROGRESS|RATING
```
- `TYPE` is `ANIME` or `MANGA`.
- `SHOW_ID` is the UUID stored in the respective database file.
- `STATUS` is one of the allowed statuses (see Features).
- `PROGRESS` is an integer (episodes watched or chapters read).
- `RATING` is an integer 0–10, or `-1` if not rated.

The app will attempt to auto-fix older user files where the `SHOW_ID` contains a title instead of an ID by matching titles against the DB and rewriting the user file.

**Build and run (Windows PowerShell)**
```powershell
cd 'AnimeAndMangaTracker\src'
javac *.java
java AnimeTrackerApp
```
If compilation fails because other .java files are missing from the `src` folder, run `javac` from the project root or include all sources.

**Admin usage**
- Open the app and click `Admin Menu`.
- Use `Add Anime to Database` or `Add Manga to Database` to add new entries. IDs are auto-generated and shown in the confirmation dialog.

**User usage**
- Create or login with a username (files are created in `users/`).
- Add anime/manga to your personal list via the dashboard.
- Click an item card to edit progress, status, and rating.

**Behavior notes / Known quirks**
- Titles are displayed in selection dialogs (UUIDs are hidden); the app maps titles back to IDs when adding to a user list.
- The app currently resolves some malformed user files automatically; if you prefer manual fixes, let me know and I can change it to a read-only resolution.
- Image loading uses `ImageIO` and `URL` — you may see a lint warning about `URL(String)` being deprecated on newer JDKs. It is non-blocking.

**Extending the project**
- Add persistent storage (SQLite) instead of text files for scalability.
- Add user authentication instead of plain username files.
- Improve image caching and error handling.
- Add sorting/filtering by rating or status in the UI.

**Contact / Credits**
Student project by the repository owner. Open an issue or message the owner for questions or help.
