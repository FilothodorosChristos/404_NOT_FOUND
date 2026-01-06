package GUI;

import service.CashFlowService;
import service.ForeisService;
import dao.CashFlow;
import dao.Foreis;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MassiveChangesPanel extends JPanel {

    private final MainFrame mainFrame;
    private final CashFlowService cashflowService;
    private final ForeisService foreisService;

    private JComboBox<String> categoryCombo;
    private JSlider percentageSlider;
    private JLabel percentageLabel;

    private JTable cashflowTable;
    private JTable foreisTable;

    private static final Color BG = new Color(15, 23, 42);
    private static final Color CARD = new Color(30, 41, 59);
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color TEXT = new Color(248, 250, 252);

    public MassiveChangesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.cashflowService = new CashFlowService();
        this.foreisService = new ForeisService();

        setLayout(new BorderLayout());
        setBackground(BG);

        createUI();
        loadTables();
    }

    private void createUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // === Title ===
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
        content.add(Box.createVerticalStrut(20));

        // === Card με Controls ===
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Κατηγορία
        JLabel catLabel = new JLabel("Κατηγορία:");
        catLabel.setForeground(TEXT);

        categoryCombo = new JComboBox<>(new String[]{
                "Όλα", "Έσοδα", "Έξοδα", "Φορείς"
        });

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

        percentageLabel = new JLabel("0 %");
        percentageLabel.setForeground(TEXT);

        percentageSlider.addChangeListener(e ->
                percentageLabel.setText(percentageSlider.getValue() + " %")
        );

        JButton applyButton = new JButton("Εφαρμογή Σεναρίου");
        applyButton.setUI(new BasicButtonUI());
        applyButton.setBackground(ACCENT);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        applyButton.addActionListener(e -> {
            applyScenario();
            loadTables(); // refresh
        });

        card.add(catLabel);
        card.add(categoryCombo);
        card.add(Box.createVerticalStrut(15));
        card.add(sliderTitle);
        card.add(percentageSlider);
        card.add(percentageLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(applyButton);

        content.add(card);
        content.add(Box.createVerticalStrut(20));

        // === Πίνακες ===
        cashflowTable = new JTable();
        foreisTable = new JTable();

        JScrollPane cashflowScroll = new JScrollPane(cashflowTable);
        JScrollPane foreisScroll = new JScrollPane(foreisTable);

        cashflowScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        foreisScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(new JLabel("Cashflows:"));
        content.add(cashflowScroll);
        content.add(Box.createVerticalStrut(15));
        content.add(new JLabel("Φορείς:"));
        content.add(foreisScroll);
        content.add(Box.createVerticalStrut(15));

        // === Back button ===
        JButton back = new JButton("← Πίσω");
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e ->
                mainFrame.showPanel(MainFrame.ACTION_SELECTION)
        );

        content.add(back);

        add(content, BorderLayout.CENTER);
    }

    private void loadTables() {
        int year = Integer.parseInt(mainFrame.getSelectedYear());

        // --- Cashflow Table ---
        try {
            List<CashFlow> cashflows = cashflowService.getCashflows(year, "Έσοδο");
            cashflows.addAll(cashflowService.getCashflows(year, "Έξοδο"));
            String[] columns = {"Τύπος", "Όνομα", "Ποσό"};
            Object[][] data = new Object[cashflows.size()][3];
            for (int i = 0; i < cashflows.size(); i++) {
                CashFlow cf = cashflows.get(i);
                data[i][0] = cf.getType();
                data[i][1] = cf.getName();
                data[i][2] = cf.getAmount();
            }
            cashflowTable.setModel(new DefaultTableModel(data, columns));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα φόρτωσης cashflows: " + e.getMessage());
        }

        // --- Foreis Table ---
        try {
            List<Foreis> foreisList = foreisService.getForeisByYearAndType(year, "RegularBudget");
            String[] columns = {"Υπουργείο", "RegularBudget", "PublicInvBudget", "Total"};
            Object[][] data = new Object[foreisList.size()][4];
            for (int i = 0; i < foreisList.size(); i++) {
                Foreis f = foreisList.get(i);
                data[i][0] = f.getName();
                data[i][1] = f.getRegularBudget();
                data[i][2] = f.getPublicInvBudget();
                data[i][3] = f.getTotal();
            }
            foreisTable.setModel(new DefaultTableModel(data, columns));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα φόρτωσης φορέων: " + e.getMessage());
        }
    }

    private void applyScenario() {
        int year = Integer.parseInt(mainFrame.getSelectedYear());
        double percentage = percentageSlider.getValue();
        String category = (String) categoryCombo.getSelectedItem();
        int totalUpdates = 0;

        try {
            if ("Όλα".equals(category) || "Έσοδα".equals(category)) {
                for (CashFlow cf : cashflowService.getCashflows(year, "Έσοδο")) {
                    double newAmount = cf.getAmount() * (1 + percentage / 100.0);
                    cf.setAmount(newAmount);
                    cashflowService.updateCashflow(cf);
                    totalUpdates++;
                }
            }

            if ("Όλα".equals(category) || "Έξοδα".equals(category)) {
                for (CashFlow cf : cashflowService.getCashflows(year, "Έξοδο")) {
                    double newAmount = cf.getAmount() * (1 + percentage / 100.0);
                    cf.setAmount(newAmount);
                    cashflowService.updateCashflow(cf);
                    totalUpdates++;
                }
            }

            if ("Όλα".equals(category) || "Φορείς".equals(category)) {
                List<Foreis> foreisList = foreisService.getForeisByYearAndType(year, "RegularBudget");
                for (Foreis f : foreisList) {
                    double newRegular = f.getRegularBudget() * (1 + percentage / 100.0);
                    double newPublic = f.getPublicInvBudget() * (1 + percentage / 100.0);
                    f.setRegularBudget(newRegular);
                    f.setPublicInvBudget(newPublic);
                    f.setTotal(newRegular + newPublic);
                    foreisService.updateForeis(f);
                    totalUpdates++;
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Το σενάριο εφαρμόστηκε επιτυχώς.\nΕνημερώθηκαν εγγραφές: " + totalUpdates,
                    "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE
            );

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
