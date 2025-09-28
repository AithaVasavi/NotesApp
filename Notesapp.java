package notesapp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Notesapp {
    private static final String DATA_DIR = "data";
    private static final String NOTES_FILE = DATA_DIR + File.separator + "notes.txt";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ensureDataDirectory();
        System.out.println("Welcome to Java Notes App");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> listNotes();
                case "2" -> addNoteInteractive();
                case "3" -> deleteNoteInteractive();
                case "4" -> searchNotesInteractive();
                case "5" -> {
                    System.out.println("Exiting. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Enter 1-5.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n--- Notes Menu ---");
        System.out.println("1. List notes");
        System.out.println("2. Add a note");
        System.out.println("3. Delete a note");
        System.out.println("4. Search notes");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    // Ensure the data directory exists
    private static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean ok = dir.mkdirs();
            if (!ok) {
                System.out.println("Warning: Could not create data directory. Files may fail to save.");
            }
        }
        // Ensure file exists
        File notesFile = new File(NOTES_FILE);
        try {
            if (!notesFile.exists()) {
                notesFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating notes file: " + e.getMessage());
        }
    }

    // List notes with line numbers
    private static void listNotes() {
        List<String> lines = readAllNotes();
        if (lines.isEmpty()) {
            System.out.println("No notes found.");
            return;
        }
        System.out.println("\n--- Your Notes ---");
        for (int i = 0; i < lines.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, lines.get(i));
        }
    }

    // Interactive add
    private static void addNoteInteractive() {
        System.out.println("Enter your note (single line). Press Enter to save:");
        String note = sc.nextLine().trim();
        if (note.isEmpty()) {
            System.out.println("Empty note not saved.");
            return;
        }
        boolean success = appendNoteToFile(note);
        if (success) System.out.println("Note saved.");
        else System.out.println("Failed to save note.");
    }

    // Append single note (uses FileWriter in append mode)
    private static boolean appendNoteToFile(String note) {
        try (FileWriter fw = new FileWriter(NOTES_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(note);
            bw.newLine();
            bw.flush();
            return true;
        } catch (IOException e) {
            System.out.println("IOException while writing note: " + e.getMessage());
            return false;
        }
    }

    // Read all notes into list
    private static List<String> readAllNotes() {
        List<String> lines = new ArrayList<>();
        File file = new File(NOTES_FILE);
        if (!file.exists()) return lines;

        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("IOException while reading notes: " + e.getMessage());
        }
        return lines;
    }

    // Interactive delete by index
    private static void deleteNoteInteractive() {
        List<String> lines = readAllNotes();
        if (lines.isEmpty()) {
            System.out.println("No notes to delete.");
            return;
        }
        listNotes();
        System.out.print("Enter note number to delete (or 0 to cancel): ");
        String input = sc.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        if (idx == 0) {
            System.out.println("Delete cancelled.");
            return;
        }
        if (idx < 1 || idx > lines.size()) {
            System.out.println("Note number out of range.");
            return;
        }
        String removed = lines.remove(idx - 1);
        boolean ok = overwriteNotesFile(lines);
        if (ok) System.out.println("Deleted: " + removed);
        else System.out.println("Failed to delete note.");
    }

    // Overwrite file with new list (used for delete)
    private static boolean overwriteNotesFile(List<String> lines) {
        try (FileWriter fw = new FileWriter(NOTES_FILE, false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            bw.flush();
            return true;
        } catch (IOException e) {
            System.out.println("IOException while overwriting notes: " + e.getMessage());
            return false;
        }
    }

    // Interactive search
    private static void searchNotesInteractive() {
        System.out.print("Enter keyword to search: ");
        String keyword = sc.nextLine().trim().toLowerCase();
        if (keyword.isEmpty()) {
            System.out.println("Empty keyword.");
            return;
        }
        List<String> lines = readAllNotes();
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            String note = lines.get(i);
            if (note.toLowerCase().contains(keyword)) {
                if (!found) {
                    System.out.println("\nMatching notes:");
                    found = true;
                }
                System.out.printf("%d. %s%n", i + 1, note);
            }
        }
        if (!found) System.out.println("No matching notes found.");
    }
}
