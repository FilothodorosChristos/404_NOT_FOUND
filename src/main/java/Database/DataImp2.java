package database;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

public class DataImp2 {

    private static final String URL = "jdbc:sqlite:budgetDB.db";
    private static final int[] YEARS = {23, 24, 25};
    private static final String[] CASHFLOW_TYPES = {"Esoda", "Exoda"};

    public static void importer() {

        // Καθαρισμός πινάκων
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM cashflows;");
            stmt.executeUpdate("DELETE FROM foreis;");
            System.out.println("Οι πίνακες καθαρίστηκαν.");

        } catch (SQLException e) {
            System.out.println("Σφάλμα κατά το καθάρισμα των πινάκων: " + e.getMessage());
            return;
        }

        // Εισαγωγή foreis
        for (int year : YEARS) {
            String filename = "B" + year + "Foreis.csv";
            try {
                insertForeisFromCsv(filename);
                System.out.println("Εισήχθησαν δεδομένα από " + filename);
            } catch (Exception e) {
                System.out.println("Πρόβλημα με " + filename + ": " + e.getMessage());
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
                    System.out.println("Πρόβλημα με " + filename + ": " + e.getMessage());
                }
            }
        }
    }

    private static void insertForeisFromCsv(String filename) throws Exception {
        InputStream is = DataImporter.class.getResourceAsStream("/data/" + filename);
        if (is == null) throw new Exception("Δεν βρέθηκε το αρχείο resources/data/" + filename);

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO foreis (foreas_id, year_id, type, name, regular_budget, public_inv_budget, total) VALUES (?, ?, ?, ?, ?, ?, ?)");
             Scanner scanner = new Scanner(is)) {

            conn.setAutoCommit(false);
            if (scanner.hasNextLine()) scanner.nextLine(); // Παράβλεψη header

            int lineNo = 1;
            while (scanner.hasNextLine()) {
                lineNo++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);

                // Debug: εμφανίζουμε κάθε γραμμή
                System.out.println("Foreis line " + lineNo + ": " + line);

                if (parts.length < 6) {
                    System.out.println("  -> Παράλειψη: λιγότερα από 7 πεδία");
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
                    System.out.println("  -> Παράλειψη: αριθμητικό σφάλμα στο line " + lineNo + " (" + nfe.getMessage() + ")");
                }
            }

            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new Exception("Σφάλμα στον πίνακα foreis: " + e.getMessage());
        }
    }

    private static void insertCashflowsFromCsv(String filename, String type) throws Exception {
        InputStream is = DataImporter.class.getResourceAsStream("/data/" + filename);
        if (is == null) throw new Exception("Δεν βρέθηκε το αρχείο resources/data/" + filename);

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO cashflows (year_id, type, name, amount) VALUES (?, ?, ?, ?)");
             Scanner scanner = new Scanner(is)) {

            conn.setAutoCommit(false);
            if (scanner.hasNextLine()) scanner.nextLine(); // Παράβλεψη header

            int lineNo = 1;
            while (scanner.hasNextLine()) {
                lineNo++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);

                // Debug: εμφανίζουμε κάθε γραμμή
                System.out.println("Cashflows line " + lineNo + ": " + line);

                if (parts.length < 3) {
                    System.out.println("  -> Παράλειψη: λιγότερα από 3 πεδία");
                    continue;
                }

                try {
                    pstmt.setInt(1, Integer.parseInt(parts[0].trim()));
                    pstmt.setString(2, type);
                    pstmt.setString(3, parts[1].trim());
                    pstmt.setDouble(4, Double.parseDouble(parts[2].trim()));
                    pstmt.addBatch();
                } catch (NumberFormatException nfe) {
                    System.out.println("  -> Παράλειψη: αριθμητικό σφάλμα στο line " + lineNo + " (" + nfe.getMessage() + ")");
                }
            }

            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new Exception("Σφάλμα στον πίνακα cashflows: " + e.getMessage());
        }
    }
}
