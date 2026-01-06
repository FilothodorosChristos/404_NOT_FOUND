package GUI;

import service.ScenarioCashflowService;
import service.ScenarioForeisService;
import service.CashFlowService;
import service.ForeisService;
import dao.Foreis;
import dao.CashFlow;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MassiveChangesPanel extends JPanel {

    @SuppressWarnings("EI_EXPOSE_REP2")
    private final MainFrame mainFrame;

    private final CashFlowService cashflowService;
    private final ForeisService foreisService;
    private final ScenarioCashflowService scenarioCashflowService;
    private final ScenarioForeisService scenarioForeisService;

    private JComboBox<String> categoryCombo;
    private JSlider percentageSlider;
    private JLabel percentageLabel;

    private JTable foreisTable;
    private JTable cashflowTable;

    private JScrollPane foreisScroll;
    private JScrollPane cashflowScroll;

    private static final Color BG = new Color(15, 23, 42);
    private static final Color CARD = new Color(30, 41, 59);
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color TEXT = new Color(248, 250, 252);

    public MassiveChangesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Κανονικά services για ανάκτηση δεδομένων
        this.cashflowService = new CashFlowService();
        this.foreisService = new ForeisService();

        // Scenario services για εφαρμογή ποσοστιαίων αλλαγών
        this.scenarioCashflowService = new ScenarioCashflowService();
        this.scenarioForeisService = new ScenarioForeisService();

        setLayout(new BorderLayout());
        setBackground(BG);

        createUI();
    }

    private void createUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Σενάρια Μεταβολής Προϋπολογισμού");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel yearLabel = new JLabel("Έτος: " + mainFrame.getSelectedYear());
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        yearLabel.setForeground(TEXT);
        yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(yearLabel);
        content.add(Box.createVerticalStrut(30));

        // === CARD ===
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Category
        JLabel catLabel = new JLabel("Κατηγορία:");
        catLabel.setForeground(TEXT);

        categoryCombo = new JComboBox<>(new String[]{
                "Έσοδα",
                "Έξοδα",
                "Φορείς"
        });
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Slider
        JLabel sliderTitle = new JLabel("Ποσοστό Μεταβολής:");
        sliderTitle.setForeground(TEXT);

        percentageSlider = new JSlider(-45, 45, 0);
        percentageSlider.setMajorTickSpacing(15);
        percentageSlider.setMinorTickSpacing(5);
        percentageSlider.setPaintTicks(true);
        percentageSlider.setPaintLabels(true);
        percentageSlider.setBackground(CARD);
        percentageSlider.setForeground(TEXT);
        percentageSlider.setAlignmentX(Component.LEFT_ALIGNMENT);

        percentageLabel = new JLabel("0 %");
        percentageLabel.setForeground(TEXT);

        percentageSlider.addChangeListener(e ->
                percentageLabel.setText(percentageSlider.getValue() + " %")
        );

        // Apply button
        JButton applyButton = new JButton("Εφαρμογή Σεναρίου");
        applyButton.setUI(new BasicButtonUI());
        applyButton.setBackground(ACCENT);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyButton.addActionListener(e -> applyScenario());

        // Δημιουργία πινάκων
        createCashflowTable();
        createForeisTable();

        cashflowScroll = new JScrollPane(cashflowTable);
        foreisScroll = new JScrollPane(foreisTable);
        cashflowScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        foreisScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Προσθήκη στοιχείων στο card
        card.add(catLabel);
        card.add(categoryCombo);
        card.add(Box.createVerticalStrut(20));
        card.add(sliderTitle);
        card.add(percentageSlider);
        card.add(percentageLabel);
        card.add(Box.createVerticalStrut(25));
        card.add(applyButton);
        card.add(Box.createVerticalStrut(20));
        card.add(new JLabel("Πίνακας Χρηματικών Ροών:") {{ setForeground(TEXT); }});
        card.add(cashflowScroll);
        card.add(Box.createVerticalStrut(15));
        card.add(new JLabel("Πίνακας Εξόδων Φορέων:") {{ setForeground(TEXT); }});
        card.add(foreisScroll);

        content.add(card);

        // Back button
        JButton back = new JButton("← Πίσω");
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> mainFrame.showPanel(MainFrame.ACTION_SELECTION));
        content.add(Box.createVerticalStrut(20));
        content.add(back);

        add(content);

        // Refresh πίνακες όταν αλλάζει κατηγορία
        categoryCombo.addActionListener(e -> refreshTables());
    }

    private void refreshTables() {
        createCashflowTable();
        cashflowScroll.setViewportView(cashflowTable);

        createForeisTable();
        foreisScroll.setViewportView(foreisTable);
    }

    private void createCashflowTable() {
        int year = Integer.parseInt(mainFrame.getSelectedYear());
        String category = (String) categoryCombo.getSelectedItem();

        List<CashFlow> list;
        if ("Έσοδα".equals(category)) {
            list = cashflowService.getCashflows(year, "Έσοδο");
        } else if ("Έξοδα".equals(category)) {
            list = cashflowService.getCashflows(year, "Έξοδο");
        } else {
            list = List.of();
        }

        String[] columns = {"ID", "Όνομα", "Ποσό"};
        Object[][] data = new Object[list.size()][3];

        for (int i = 0; i < list.size(); i++) {
            CashFlow cf = list.get(i);
            data[i][0] = cf.getId();
            data[i][1] = cf.getName();
            data[i][2] = cf.getAmount();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns);
        cashflowTable = new JTable(model);
        cashflowTable.setFillsViewportHeight(true);
        cashflowTable.setEnabled(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columns.length; i++) {
            cashflowTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        cashflowTable.setBackground(new Color(248, 250, 252));
        cashflowTable.setForeground(Color.BLACK);
        cashflowTable.setRowHeight(25);
    }

    private void createForeisTable() {
        int year = Integer.parseInt(mainFrame.getSelectedYear());

        // Εδώ χρησιμοποιούμε το ForeisService
        List<Foreis> list = new java.util.ArrayList<>();

        String[] types = {
            "Κεντρική Διοίκηση",
            "Υπουργείο",
            "Αποκεντρωμένη Διοίκηση"
        };

        for (String type : types) {
            list.addAll(foreisService.getForeisByYearAndType(year, type));
        }

        String[] columns = {"ID", "Όνομα", "Τύπος", "Τακτικός Προϋπολογισμός", "Π/Υ Δημ. Επενδύσεων", "Σύνολο"};
        Object[][] data = new Object[list.size()][6];

        for (int i = 0; i < list.size(); i++) {
            Foreis f = list.get(i);
            data[i][0] = f.getForeasId();
            data[i][1] = f.getName();
            data[i][2] = f.getType();
            data[i][3] = f.getRegularBudget();
            data[i][4] = f.getPublicInvBudget();
            data[i][5] = f.getTotal();
        }

        DefaultTableModel model = new DefaultTableModel(data, columns);
        foreisTable = new JTable(model);
        foreisTable.setFillsViewportHeight(true);
        foreisTable.setEnabled(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columns.length; i++) {
            foreisTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        foreisTable.setBackground(new Color(248, 250, 252));
        foreisTable.setForeground(Color.BLACK);
        foreisTable.setRowHeight(25);
    }

    private void applyScenario() {
        int year = Integer.parseInt(mainFrame.getSelectedYear());
        double percentage = percentageSlider.getValue();
        String category = (String) categoryCombo.getSelectedItem();

        int totalUpdates = 0;

        try {
            if ("Έσοδα".equals(category)) {
                totalUpdates += scenarioCashflowService.updateCashflowWithModifiedAmount(year, "Έσοδο", percentage);
            } else if ("Έξοδα".equals(category)) {
                totalUpdates += scenarioCashflowService.updateCashflowWithModifiedAmount(year, "Έξοδο", percentage);
            } else if ("Φορείς".equals(category)) {
                String[] types = {"Υπουργείο", "Κεντρική Διοίκηση", "Αποκεντρωμένη Διοίκηση"};
                for (String t : types) {
                    totalUpdates += scenarioForeisService.updateForeisWithModifiedBudget(year, t, "RegularBudget", percentage);
                    totalUpdates += scenarioForeisService.updateForeisWithModifiedBudget(year, t, "PublicInvBudget", percentage);
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Το σενάριο εφαρμόστηκε επιτυχώς.\nΕνημερώθηκαν εγγραφές: " + totalUpdates,
                    "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshTables();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
