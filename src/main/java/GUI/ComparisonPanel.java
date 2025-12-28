package GUI;

import dto.CashFlowCompareDto;
import dto.ForeasCompareDto;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ComparisonPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ComparisonService comparisonService;
    
    private JComboBox<Integer> year1Combo; 
    private JComboBox<Integer> year2Combo;
    private JComboBox<String> dataTypeCombo;
    private JPanel resultsPanel, cardsPanel;
    private int selectedYear;
    
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color DARKER_BG = new Color(8, 15, 30);
    private static final Color CARD_BG = new Color(30, 41, 59);
    private static final Color SUCCESS_GREEN = new Color(16, 185, 129);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);
    private static final Color TABLE_HEADER = new Color(30, 41, 59);
    private static final Color TABLE_ROW_ALT = new Color(20, 30, 48);

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00 €");
    private final DecimalFormat percentFormat = new DecimalFormat("+#,##0.00%;-#,##0.00%");

    private static class ComparisonResult {
        final DefaultTableModel model;
        final double sum1;
        final double sum2;
        final boolean hasData;

        ComparisonResult(DefaultTableModel model, double sum1, double sum2, boolean hasData) {
            this.model = model;
            this.sum1 = sum1;
            this.sum2 = sum2;
            this.hasData = hasData;
        }
    }

    public ComparisonPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.comparisonService = new ComparisonService();
        setLayout(new BorderLayout(0, 10));
        setBackground(DARK_BG);
        initComponents();
    }
    
    public void setSelectedYear(int year) {
        this.selectedYear = year;
        if (year1Combo != null) {
            year1Combo.setSelectedItem(year);
            performComparison();
        }
    }

    private void initComponents() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(DARK_BG);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        
        JLabel titleLabel = new JLabel("Συγκριτική Ανάλυση Δεδομένων", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controls.setBackground(DARK_BG);
        
        JLabel yearBaseLabel = new JLabel("Έτος Βάσης:");
        yearBaseLabel.setForeground(TEXT_SECONDARY);
        yearBaseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        year1Combo = new JComboBox<>();
        styleComboBox(year1Combo);
        populateYears(year1Combo); 
        
        JLabel compareLabel = new JLabel("Σύγκριση με:");
        compareLabel.setForeground(TEXT_SECONDARY);
        compareLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        year2Combo = new JComboBox<>();
        styleComboBox(year2Combo);
        populateYears(year2Combo);
        
        JLabel categoryLabel = new JLabel("Κατηγορία:");
        categoryLabel.setForeground(TEXT_SECONDARY);
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        dataTypeCombo = new JComboBox<>(new String[]{"Φορείς", "Έσοδα", "Έξοδα"});
        styleComboBox(dataTypeCombo);
        
        JButton compareBtn = new JButton("🔄 ΑΝΑΝΕΩΣΗ ΣΥΓΚΡΙΣΗΣ");
        compareBtn.setBackground(ACCENT_BLUE); 
        compareBtn.setForeground(Color.WHITE); 
        compareBtn.setFocusPainted(false); 
        compareBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        compareBtn.setPreferredSize(new Dimension(220, 38));
        compareBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); 
        compareBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        compareBtn.setOpaque(true);
        compareBtn.setContentAreaFilled(true);
        compareBtn.setBorderPainted(true);
        compareBtn.addActionListener(e -> performComparison());

        controls.add(yearBaseLabel); 
        controls.add(year1Combo); 
        controls.add(compareLabel); 
        controls.add(year2Combo);
        controls.add(categoryLabel); 
        controls.add(dataTypeCombo);
        controls.add(compareBtn);
        headerPanel.add(controls, BorderLayout.CENTER);

        cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(DARK_BG);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));
        
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(cardsPanel, BorderLayout.SOUTH);

        resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(DARKER_BG);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));

        add(topContainer, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("← Προηγούμενο");
        backBtn.setBackground(ACCENT_BLUE); 
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setPreferredSize(new Dimension(160, 38));
        backBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); 
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setOpaque(true);
        backBtn.setContentAreaFilled(true);
        backBtn.setBorderPainted(true);
        backBtn.addActionListener(e -> mainFrame.showPanel(MainFrame.ACTION_SELECTION));
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(DARK_BG);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 25, 15, 25));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void performComparison() {
        int y1 = (int) year1Combo.getSelectedItem();
        int y2 = (int) year2Combo.getSelectedItem();
        
        if (y1 == y2) {
            JOptionPane.showMessageDialog(this, 
                "Δεν μπορείτε να συγκρίνετε το ίδιο έτος με τον εαυτό του.", 
                "Μη έγκυρη Σύγκριση", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String type = (String) dataTypeCombo.getSelectedItem();
        resultsPanel.removeAll();
        JLabel loadingLabel = new JLabel("Ανάκτηση δεδομένων...", SwingConstants.CENTER);
        loadingLabel.setForeground(TEXT_SECONDARY);
        resultsPanel.add(loadingLabel, BorderLayout.CENTER);
        resultsPanel.revalidate();
        resultsPanel.repaint();

        SwingWorker<ComparisonResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ComparisonResult doInBackground() throws Exception {
                double s1 = 0, s2 = 0;
                DefaultTableModel model = null;
                boolean dataFound = false;

                if ("Φορείς".equals(type)) {
                    List<ForeasCompareDto> data = comparisonService.compareForeis(y1, y2);
                    if (data != null && !data.isEmpty()) {
                        for(ForeasCompareDto d : data) { s1 += d.getTotalYear1(); s2 += d.getTotalYear2(); }
                        model = createForeisModel(data, y1, y2);
                        dataFound = true;
                    }
                } else {
                    String dbType = "Έσοδα".equals(type) ? "Έσοδο" : "Έξοδο";
                    List<CashFlowCompareDto> data = comparisonService.compareCashFlows(y1, y2, dbType);
                    if (data != null && !data.isEmpty()) {
                        for(CashFlowCompareDto d : data) { s1 += d.getAmountYear1(); s2 += d.getAmountYear2(); }
                        model = createCashFlowModel(data, y1, y2);
                        dataFound = true;
                    }
                }
                return new ComparisonResult(model, s1, s2, dataFound);
            }

            @Override
            protected void done() {
                try {
                    ComparisonResult res = get();
                    resultsPanel.removeAll();
                    if (res.hasData) {
                        JTable table = setupTable(res.model);
                        JScrollPane scrollPane = new JScrollPane(table);
                        scrollPane.getViewport().setBackground(DARKER_BG);
                        resultsPanel.add(scrollPane, BorderLayout.CENTER);
                    } else {
                        resultsPanel.add(new JLabel("Δεν βρέθηκαν δεδομένα.", SwingConstants.CENTER));
                    }
                    updateSummaryCards(res.sum1, res.sum2, res.sum2 - res.sum1, y1, y2);
                    resultsPanel.revalidate();
                    resultsPanel.repaint();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void updateSummaryCards(double t1, double t2, double diff, int y1, int y2) {
        cardsPanel.removeAll();
        String winnerTitle = (t2 > t1) ? "Υψηλότερο Έτος: " + y2 + " ↑" : (t1 > t2) ? "Υψηλότερο Έτος: " + y1 + " ↓" : "Ισοπαλία";
        Color winnerColor = (t2 > t1) ? SUCCESS_GREEN : (t1 > t2) ? DANGER_RED : TEXT_SECONDARY;
        cardsPanel.add(createCard("Προηγούμενο (" + y1 + ")", decimalFormat.format(t1), TEXT_PRIMARY));
        cardsPanel.add(createCard("Σύγκριση (" + y2 + ")", decimalFormat.format(t2), TEXT_PRIMARY));
        cardsPanel.add(createCard(winnerTitle, "Διαφορά: " + decimalFormat.format(Math.abs(diff)), winnerColor));
        cardsPanel.revalidate();
    }

    private JPanel createCard(String title, String val, Color valueColor) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        JLabel t = new JLabel(title); t.setForeground(TEXT_SECONDARY);
        JLabel v = new JLabel(val); v.setFont(new Font("Segoe UI", Font.BOLD, 18)); v.setForeground(valueColor);
        card.add(t); card.add(v);
        return card;
    }

    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setRowHeight(45);
        table.setBackground(DARKER_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setHorizontalAlignment(c == 0 ? LEFT : RIGHT);
                if (c >= 3 && v != null) {
                    String str = v.toString();
                    if (str.startsWith("+")) setForeground(SUCCESS_GREEN);
                    else if (str.startsWith("-")) setForeground(DANGER_RED);
                    else setForeground(TEXT_PRIMARY);
                } else {
                    setForeground(TEXT_PRIMARY);
                }
                setBackground(r % 2 == 0 ? DARKER_BG : TABLE_ROW_ALT);
                return comp;
            }
        });
        return table;
    }

    private DefaultTableModel createCashFlowModel(List<CashFlowCompareDto> data, int y1, int y2) {
        String[] cols = {"Περιγραφή", "Έτος " + y1, "Έτος " + y2, "Διαφορά", "Τάση %"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (CashFlowCompareDto d : data) {
            double diff = d.getAmountYear2() - d.getAmountYear1();
            double p = (d.getAmountYear1() != 0) ? (diff / d.getAmountYear1()) : 0;
            model.addRow(new Object[]{d.getName(), decimalFormat.format(d.getAmountYear1()), 
                decimalFormat.format(d.getAmountYear2()), decimalFormat.format(diff), percentFormat.format(p)});
        }
        return model;
    }

    private DefaultTableModel createForeisModel(List<ForeasCompareDto> data, int y1, int y2) {
        String[] cols = {"Φορέας", "Έτος " + y1, "Έτος " + y2, "Διαφορά", "Μεταβολή %"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (ForeasCompareDto d : data) {
            model.addRow(new Object[]{d.getName(), decimalFormat.format(d.getTotalYear1()), 
                decimalFormat.format(d.getTotalYear2()), decimalFormat.format(d.getTotalDiff()), 
                d.getTotalPercentChange() != null ? percentFormat.format(d.getTotalPercentChange()/100) : "0.00%"});
        }
        return model;
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(new Color(51, 65, 85));
        combo.setForeground(TEXT_PRIMARY);
        combo.setPreferredSize(new Dimension(120, 38));
    }

    private void populateYears(JComboBox<Integer> combo) {
        int currentYear = Year.now().getValue();
        combo.removeAllItems();
        for (int i = 2021; i <= currentYear + 1; i++) {
            combo.addItem(i);
        }
        if (combo == year2Combo) {
            combo.setSelectedItem((selectedYear != currentYear) ? currentYear : currentYear - 1);
        } else if (selectedYear != 0) {
            combo.setSelectedItem(selectedYear);
        }
    }
}