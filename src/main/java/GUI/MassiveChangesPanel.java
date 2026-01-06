package GUI;

import service.ScenarioCashflowService;
import service.ScenarioForeisService;
import service.CashFlowService;
import service.ForeisService;
import dao.Foreis;
import dao.CashFlow;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel για την εφαρμογή μαζικών αλλαγών στον προϋπολογισμό.
 * Επιτρέπει την ποσοστιαία μεταβολή εσόδων, εξόδων και φορέων.
 */
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
    private JTable previewTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private JPanel summaryPanel;

    // Color constants matching other panels
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color DARKER_BG = new Color(8, 15, 30);
    private static final Color CARD_BG = new Color(30, 41, 59);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);
    private static final Color TABLE_HEADER_BG = new Color(30, 41, 59);
    private static final Color TABLE_ROW_ALT = new Color(20, 30, 48);
    private static final Color SUCCESS_GREEN = new Color(16, 185, 129);
    private static final Color WARNING_ORANGE = new Color(245, 158, 11);

    public MassiveChangesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.cashflowService = new CashFlowService();
        this.foreisService = new ForeisService();
        this.scenarioCashflowService = new ScenarioCashflowService();
        this.scenarioForeisService = new ScenarioForeisService();

        setLayout(new BorderLayout());
        setBackground(DARK_BG);

        createUI();
    }

    /**
     * Δημιουργία του UI
     */
    private void createUI() {
        JPanel mainContent = new JPanel(new BorderLayout(0, 15));
        mainContent.setBackground(DARK_BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainContent.add(headerPanel, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = createControlPanel();
        mainContent.add(controlPanel, BorderLayout.CENTER);

        // Bottom Panel with buttons
        JPanel bottomPanel = createBottomPanel();
        mainContent.add(bottomPanel, BorderLayout.SOUTH);

        add(mainContent);
    }

    /**
     * Δημιουργία header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Σενάρια Μεταβολής Προϋπολογισμού");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel yearLabel = new JLabel("Έτος: " + mainFrame.getSelectedYear());
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        yearLabel.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(DARK_BG);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(yearLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        return headerPanel;
    }

    /**
     * Δημιουργία control panel
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout(0, 15));
        controlPanel.setBackground(DARK_BG);

        // Settings Card
        JPanel settingsCard = createSettingsCard();
        controlPanel.add(settingsCard, BorderLayout.NORTH);

        // Preview Panel
        JPanel previewPanel = createPreviewPanel();
        controlPanel.add(previewPanel, BorderLayout.CENTER);

        return controlPanel;
    }

    /**
     * Δημιουργία settings card
     */
    private JPanel createSettingsCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Category Selection
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        categoryPanel.setBackground(CARD_BG);

        JLabel categoryLabel = new JLabel("Κατηγορία:");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryLabel.setForeground(TEXT_SECONDARY);

        categoryCombo = new JComboBox<>(new String[]{"Έσοδα", "Έξοδα", "Φορείς"});
        styleComboBox(categoryCombo);
        categoryCombo.addActionListener(e -> updatePreview());

        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryCombo);

        // Slider Panel
        JPanel sliderPanel = createSliderPanel();

        // Add to card
        card.add(categoryPanel);
        card.add(Box.createVerticalStrut(12));
        card.add(sliderPanel);

        return card;
    }

    /**
     * Δημιουργία slider panel
     */
    private JPanel createSliderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);

        JLabel sliderTitle = new JLabel("Ποσοστό Μεταβολής:");
        sliderTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sliderTitle.setForeground(TEXT_PRIMARY);
        sliderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        percentageSlider = new JSlider(-45, 45, 0);
        percentageSlider.setMajorTickSpacing(15);
        percentageSlider.setMinorTickSpacing(5);
        percentageSlider.setPaintTicks(true);
        percentageSlider.setPaintLabels(true);
        percentageSlider.setBackground(CARD_BG);
        percentageSlider.setForeground(TEXT_SECONDARY);
        percentageSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        percentageSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        percentageSlider.addChangeListener(e -> {
            updatePercentageLabel();
            updatePreview();
        });

        percentageLabel = new JLabel("0%", SwingConstants.CENTER);
        percentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        percentageLabel.setForeground(ACCENT_BLUE);
        percentageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(sliderTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(percentageSlider);
        panel.add(Box.createVerticalStrut(5));
        panel.add(percentageLabel);

        return panel;
    }

    /**
     * Δημιουργία preview panel
     */
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(DARK_BG);

        // Preview Label
        JLabel previewLabel = new JLabel("Προεπισκόπηση Αλλαγών");
        previewLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        previewLabel.setForeground(TEXT_PRIMARY);
        previewLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Summary Cards
        summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(DARK_BG);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        updateSummaryCards(0, 0, 0);

        // Center panel with label and cards
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(DARK_BG);
        centerPanel.add(previewLabel, BorderLayout.NORTH);
        centerPanel.add(summaryPanel, BorderLayout.CENTER);

        // Table
        createTable();
        tableScrollPane = new JScrollPane(previewTable);
        tableScrollPane.setBackground(DARKER_BG);
        tableScrollPane.getViewport().setBackground(DARKER_BG);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tableScrollPane.setPreferredSize(new Dimension(0, 400));

        panel.add(centerPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Δημιουργία πίνακα
     */
    private void createTable() {
        String[] columns = {"Όνομα", "Τρέχον Ποσό (€)", "Νέο Ποσό (€)", "Αλλαγή (€)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        previewTable = new JTable(tableModel);
        styleTable(previewTable);
    }

    /**
     * Styling του πίνακα
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);
        table.setBackground(DARKER_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBackground(TABLE_HEADER_BG);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, BORDER_COLOR));
                label.setOpaque(true);
                return label;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                setBackground(r % 2 == 0 ? DARKER_BG : TABLE_ROW_ALT);
                setForeground(TEXT_PRIMARY);
                
                if (c == 0) {
                    setHorizontalAlignment(LEFT);
                } else {
                    setHorizontalAlignment(RIGHT);
                }
                
                return comp;
            }
        });
    }

    /**
     * Styling του combo box
     */
    private void styleComboBox(JComboBox<?> combo) {
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = super.createArrowButton();
                b.setBackground(TABLE_ROW_ALT);
                b.setForeground(TEXT_PRIMARY);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                popup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                return popup;
            }
        });
        
        combo.setBackground(TABLE_ROW_ALT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        combo.setPreferredSize(new Dimension(140, 38));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                l.setForeground(TEXT_PRIMARY);
                l.setBackground(isSelected ? ACCENT_BLUE : TABLE_ROW_ALT);
                l.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                l.setOpaque(true);
                return l;
            }
        });
    }

    /**
     * Δημιουργία bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(DARK_BG);

        JButton backButton = new JButton("← Προηγούμενο");
        backButton.setUI(new BasicButtonUI());
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setPreferredSize(new Dimension(160, 38));
        backButton.setBackground(ACCENT_BLUE);
        backButton.setForeground(Color.WHITE);
        backButton.setOpaque(true);
        backButton.setBorder(BorderFactory.createEmptyBorder());
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showPanel(MainFrame.ACTION_SELECTION));

        leftPanel.add(backButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(DARK_BG);

        JButton applyButton = new JButton("✓ Εφαρμογή Σεναρίου");
        applyButton.setUI(new BasicButtonUI());
        applyButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        applyButton.setPreferredSize(new Dimension(200, 38));
        applyButton.setBackground(SUCCESS_GREEN);
        applyButton.setForeground(Color.WHITE);
        applyButton.setOpaque(true);
        applyButton.setBorder(BorderFactory.createEmptyBorder());
        applyButton.setFocusPainted(false);
        applyButton.addActionListener(e -> applyScenario());

        rightPanel.add(applyButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Ενημέρωση του label ποσοστού
     */
    private void updatePercentageLabel() {
        int value = percentageSlider.getValue();
        percentageLabel.setText(String.format("%+d%%", value));
        
        if (value > 0) {
            percentageLabel.setForeground(SUCCESS_GREEN);
        } else if (value < 0) {
            percentageLabel.setForeground(new Color(239, 68, 68));
        } else {
            percentageLabel.setForeground(ACCENT_BLUE);
        }
    }

    /**
     * Ενημέρωση προεπισκόπησης
     */
    private void updatePreview() {
        tableModel.setRowCount(0);
        
        int year = Integer.parseInt(mainFrame.getSelectedYear());
        double percentage = percentageSlider.getValue();
        String category = (String) categoryCombo.getSelectedItem();

        double totalCurrent = 0;
        double totalNew = 0;

        try {
            if ("Έσοδα".equals(category)) {
                List<CashFlow> items = cashflowService.getCashflows(year, "Έσοδο");
                for (CashFlow cf : items) {
                    double currentAmount = cf.getAmount();
                    double newAmount = currentAmount + (currentAmount * percentage / 100);
                    double change = newAmount - currentAmount;
                    
                    tableModel.addRow(new Object[]{
                        cf.getName(),
                        String.format("%,.2f", currentAmount),
                        String.format("%,.2f", newAmount),
                        String.format("%+,.2f", change)
                    });
                    
                    totalCurrent += currentAmount;
                    totalNew += newAmount;
                }
            } else if ("Έξοδα".equals(category)) {
                List<CashFlow> items = cashflowService.getCashflows(year, "Έξοδο");
                for (CashFlow cf : items) {
                    double currentAmount = cf.getAmount();
                    double newAmount = currentAmount + (currentAmount * percentage / 100);
                    double change = newAmount - currentAmount;
                    
                    tableModel.addRow(new Object[]{
                        cf.getName(),
                        String.format("%,.2f", currentAmount),
                        String.format("%,.2f", newAmount),
                        String.format("%+,.2f", change)
                    });
                    
                    totalCurrent += currentAmount;
                    totalNew += newAmount;
                }
            } else if ("Φορείς".equals(category)) {
                List<Foreis> allForeis = new ArrayList<>();
                allForeis.addAll(foreisService.getForeisByYearAndType(year, "Κεντρική Διοίκηση"));
                allForeis.addAll(foreisService.getForeisByYearAndType(year, "Υπουργείο"));
                allForeis.addAll(foreisService.getForeisByYearAndType(year, "Αποκεντρωμένη Διοίκηση"));
                
                for (Foreis f : allForeis) {
                    double currentAmount = f.getTotal();
                    double newAmount = currentAmount + (currentAmount * percentage / 100);
                    double change = newAmount - currentAmount;
                    
                    tableModel.addRow(new Object[]{
                        f.getName(),
                        String.format("%,.2f", currentAmount),
                        String.format("%,.2f", newAmount),
                        String.format("%+,.2f", change)
                    });
                    
                    totalCurrent += currentAmount;
                    totalNew += newAmount;
                }
            }
            
            updateSummaryCards(totalCurrent, totalNew, totalNew - totalCurrent);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ενημέρωση summary cards
     */
    private void updateSummaryCards(double current, double newAmount, double change) {
        summaryPanel.removeAll();
        
        summaryPanel.add(createSummaryCard("Τρέχον Σύνολο", String.format("€%,.2f", current), TEXT_PRIMARY));
        summaryPanel.add(createSummaryCard("Νέο Σύνολο", String.format("€%,.2f", newAmount), TEXT_PRIMARY));
        
        Color changeColor = change > 0 ? SUCCESS_GREEN : (change < 0 ? new Color(239, 68, 68) : TEXT_SECONDARY);
        summaryPanel.add(createSummaryCard("Αλλαγή", String.format("%+,.2f €", change), changeColor));
        
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    /**
     * Δημιουργία summary card
     */
    private JPanel createSummaryCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(valueColor);

        card.add(titleLabel);
        card.add(valueLabel);

        return card;
    }

    /**
     * Εφαρμογή σεναρίου
     */
    private void applyScenario() {
        int year = Integer.parseInt(mainFrame.getSelectedYear());
        double percentage = percentageSlider.getValue();
        String category = (String) categoryCombo.getSelectedItem();

        if (percentage == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Το ποσοστό μεταβολής είναι 0%. Δεν θα γίνει καμία αλλαγή.",
                "Προειδοποίηση",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            String.format("Είστε σίγουροι ότι θέλετε να εφαρμόσετε μεταβολή %+.1f%% στην κατηγορία '%s';\n\n" +
                         "Αυτή η ενέργεια θα αλλάξει όλες τις εγγραφές της κατηγορίας.",
                         percentage, category),
            "Επιβεβαίωση Εφαρμογής",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int totalUpdates = 0;

        try {
            if ("Έσοδα".equals(category)) {
                totalUpdates = scenarioCashflowService.updateCashflowWithModifiedAmount(year, "Έσοδο", percentage);
            } else if ("Έξοδα".equals(category)) {
                totalUpdates = scenarioCashflowService.updateCashflowWithModifiedAmount(year, "Έξοδο", percentage);
            } else if ("Φορείς".equals(category)) {
                String[] types = {"Κεντρική Διοίκηση", "Υπουργείο", "Αποκεντρωμένη Διοίκηση"};
                for (String t : types) {
                    totalUpdates += scenarioForeisService.updateForeisWithModifiedBudget(year, t, "RegularBudget", percentage);
                    totalUpdates += scenarioForeisService.updateForeisWithModifiedBudget(year, t, "PublicInvBudget", percentage);
                }
            }

            JOptionPane.showMessageDialog(
                this,
                String.format("Το σενάριο εφαρμόστηκε επιτυχώς!\n\n" +
                             "Ενημερώθηκαν %d εγγραφές\n" +
                             "Ποσοστό αλλαγής: %+.1f%%",
                             totalUpdates, percentage),
                "Επιτυχής Εφαρμογή",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Reset slider και ανανέωση προεπισκόπησης
            percentageSlider.setValue(0);
            updatePreview();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Σφάλμα κατά την εφαρμογή του σεναρίου:\n" + ex.getMessage(),
                "Σφάλμα",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
}