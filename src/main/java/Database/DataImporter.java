package database;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class DataImporter {

    private static String URL = "jdbc:sqlite:budgetDB.db";
    private static final int[] YEARS = {23, 24, 25};
    private static final String[] CASHFLOW_TYPES = {"Esoda", "Exoda"};

    //μέθοδος setter για τα τεστ
    public static void setURL(String url) {
        URL = url;
    }

    public static void importer() {
        DatabaseSetup.resetTables(); //καθαρισμός πινάκων πριν απο το γέμισμα

        // Εισαγωγή foreis
        for (int year : YEARS) {
            String filename = "B" + year + "Foreis.csv";
            try {
                insertForeisFromCsv(filename);
                System.out.println("Εισήχθησαν δεδομένα από " + filename);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        // Εισαγωγή cashflows
        for (int year : YEARS) {
            for (String type : CASHFLOW_TYPES) {
                String filename = "B" + year + type + ".csv";
                try {
                    insertCashflowsFromCsv(filename, type);
                    System.out.println("Εισήχθησαν δεδομένα από " + filename);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    static void insertForeisFromCsv(String filename) throws Exception {
        InputStream is = DataImporter.class.getResourceAsStream("/data/" + filename);
        if (is == null) throw new FileNotFoundException("Δεν βρέθηκε το αρχείο resources/data/" + filename);

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO foreis (foreas_id, year_id, type, name, regular_budget, public_inv_budget, total) VALUES (?, ?, ?, ?, ?, ?, ?)");
             Scanner scanner = new Scanner(is, "UTF-8")) { // <-- UTF-8

            conn.setAutoCommit(false);
            if (scanner.hasNextLine()) scanner.nextLine(); // Παράβλεψη header

            int lineNo = 1;
            while (scanner.hasNextLine()) {
                lineNo++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";", -1); // <-- διαχωριστικό ;
                if (parts.length < 6) {
                    System.err.println("Foreis line " + lineNo + ": " + line + " -> Παράλειψη: λιγότερα από 6 πεδία");
                    continue;
                }

                try {
                    pstmt.setInt(1, Integer.parseInt(parts[0].trim()));
                    pstmt.setInt(2, Integer.parseInt(parts[1].trim()));
                    pstmt.setString(3, parts[2].trim());
                    pstmt.setString(4, parts[3].trim());
                    pstmt.setDouble(5, Double.parseDouble(parts[4].trim()));
                    pstmt.setDouble(6, Double.parseDouble(parts[5].trim()));
                    pstmt.setDouble(7, Double.parseDouble(parts[6].trim()));
                    pstmt.addBatch();
                } catch (NumberFormatException nfe) {
                    // παραλείπουμε γραμμή με λάθος αριθμό
                }
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new Exception("Σφάλμα κατά την εισαγωγή στον πίνακα foreis: " + e.getMessage());
        }
    }

    static void insertCashflowsFromCsv(String filename, String type) throws Exception {
        InputStream is = DataImporter.class.getResourceAsStream("/data/" + filename);
        if (is == null) throw new FileNotFoundException("Δεν βρέθηκε το αρχείο resources/data/" + filename);

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO cashflows (year_id, type, name, amount) VALUES (?, ?, ?, ?)");
             Scanner scanner = new Scanner(is, "UTF-8")) { // <-- UTF-8

            conn.setAutoCommit(false);
            if (scanner.hasNextLine()) scanner.nextLine(); // Παράβλεψη header

            int lineNo = 1;
            while (scanner.hasNextLine()) {
                lineNo++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";", -1); // <-- διαχωριστικό ;
                if (parts.length < 3) {
                    System.err.println("Cashflows line " + lineNo + ": " + line + " -> Παράλειψη: λιγότερα από 3 πεδία");
                    continue;
                }

                try {
                    pstmt.setInt(1, Integer.parseInt(parts[0].trim())); // year_id
                    pstmt.setString(2, parts[1].trim());               // type από CSV (Έσοδο / Έξοδο)
                    pstmt.setString(3, parts[2].trim());               // name
                    pstmt.setDouble(4, Double.parseDouble(parts[3].trim())); // amount

                    pstmt.addBatch();
                } catch (NumberFormatException nfe) {
                    System.err.println("Παράλειψη γραμμής με λάθος αριθμό");
                }
            }

            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new Exception("Σφάλμα κατά την εισαγωγή στον πίνακα cashflows: " + e.getMessage());
        }
    }
}
