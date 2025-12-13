package GUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * ActionSelectionPanel allows the user to select an action to perform:
 * View, Edit, Charts, or Comparison.
 */
public class ActionSelectionPanel extends JPanel {
	
	/** Reference to the main application frame. */
	private final MainFrame mainFrame;
	
	/** Navy-blue color used in UI. */
	private static final Color NAVY_BLUE = new Color(0, 0, 128);
	
	/** Font used for section headers. */
	private static final Font SECTION_TITLE_FONT = new Font("Tahoma", Font.PLAIN, 30);
	
	/** Font used for action buttons. */
	private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 24);
	
	/**
	 * Constructs an ActionSelectionPanel with the specified MainFrame reference.
	 *
	 * @param mainFrame the main application frame
	 */
	public ActionSelectionPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new BorderLayout());
		createUI();
	}

	/**
	 * Creates and initializes the user interface components.
	 * Sets up the background, title, action buttons, and navigation.
	 */
	private void createUI() {
		/** Background panel */
		JPanel backgroundPanel = new JPanel(new BorderLayout()) {
			/**
			 * Paints the background image.
			 *
			 * @param g the Graphics context
			 */
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (mainFrame.getBackgroundImage() != null) {
					g.drawImage(mainFrame.getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
				}
			}
		};
		
		/** center panel */
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);
		centerPanel.add(Box.createVerticalGlue());
		
		// Δημιουργία του ημιδιάφανου λευκού container
		JPanel whiteContainer = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				// Πολύ διάφανο λευκό φόντο
				g2d.setColor(new Color(255, 255, 255, 100)); // 40% opacity
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				
				// Ελαφρύ border
				g2d.setColor(new Color(255, 255, 255, 150));
				g2d.setStroke(new BasicStroke(1));
				g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);
			}
		};
		whiteContainer.setLayout(new BoxLayout(whiteContainer, BoxLayout.Y_AXIS));
		whiteContainer.setOpaque(false);
		whiteContainer.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
		whiteContainer.setMaximumSize(new Dimension(650, 500)); // Προσαρμοσμένο για 4 κουμπιά
		
		/**Title*/
		JLabel titleLabel = new JLabel("Παρακαλώ επιλέξτε διαδικασία:");
		titleLabel.setFont(SECTION_TITLE_FONT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		whiteContainer.add(titleLabel);
		whiteContainer.add(Box.createVerticalStrut(50));
		
		/**Action buttons */
		JButton viewBtn = createStyledButton("Προβολή");
		JButton editBtn = createStyledButton("Επεξεργασία");
		JButton diagramBtn = createStyledButton("Διαγράμματα");
		JButton compareBtn = createStyledButton("Σύγκριση");
		
		viewBtn.addActionListener(e -> handleAction("Προβολή"));
		editBtn.addActionListener(e -> handleAction("Επεξεργασία"));
		diagramBtn.addActionListener(e -> handleAction("Διαγράμματα"));
		compareBtn.addActionListener(e -> handleAction("Σύγκριση"));
		
		/** Button grid */
		JPanel buttonsGrid = new JPanel(new GridBagLayout());
		buttonsGrid.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15, 20, 15, 20);
		
		gbc.gridx = 0; gbc.gridy = 0;
		buttonsGrid.add(viewBtn, gbc);
		gbc.gridx = 1;
		buttonsGrid.add(editBtn, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		buttonsGrid.add(diagramBtn, gbc);
		gbc.gridx = 1;
		buttonsGrid.add(compareBtn, gbc);
		
		whiteContainer.add(buttonsGrid);
		
		centerPanel.add(whiteContainer);
		centerPanel.add(Box.createVerticalGlue());
		
		backgroundPanel.add(centerPanel, BorderLayout.CENTER);
		
		/**previous button */
		JButton prevButton = new JButton("< Προηγούμενο");
		prevButton.setPreferredSize(new Dimension(150, 40));
		prevButton.addActionListener(e -> mainFrame.showPanel(MainFrame.YEAR_SELECTION));
		
		JPanel prevButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		prevButtonPanel.setOpaque(false);
		prevButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		prevButtonPanel.add(prevButton);
		
		backgroundPanel.add(prevButtonPanel, BorderLayout.SOUTH);
		add(backgroundPanel);
	}
	
	/**
	 * Creates a styled button with specified text and predefined styling.
	 *
	 * @param text the text to display on the button
	 * @return the configured JButton
	 */
	private JButton createStyledButton(String text) {
		JButton button = new JButton(text);
		button.setForeground(NAVY_BLUE);
		button.setBackground(Color.WHITE);
		button.setFont(BUTTON_FONT);
		button.setPreferredSize(new Dimension(250, 60));
		button.setMinimumSize(new Dimension(250, 60));
		button.setMaximumSize(new Dimension(250, 60));
		button.setBorder(new RoundedBorder(30));
		button.setFocusPainted(false);
		
		// Hover effect
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(230, 240, 255));
			}
			
			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(Color.WHITE);
			}
		});
		
		return button;
	}
	
	/**
	 * Handles user action selection by displaying the appropriate panel or window.
	 *
	 * @param action the action string selected by the user
	 */
	private void handleAction(String action) {
		String selectedYear = mainFrame.getSelectedYear();
		
		if (action.equals("Διαγράμματα")) {
			FinanceChartPanel chartPanel = FinanceChartPanel.createPanel(selectedYear);
			
			JFrame chartFrame = new JFrame("Διαγράμματα Έτους " + selectedYear);
			chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			chartFrame.setSize(900, 700);
			chartFrame.setLocationRelativeTo(null);
			chartFrame.add(chartPanel);
			chartFrame.setVisible(true);
			
		} else if (action.equals("Προβολή")) {
			mainFrame.showBudgetView();
			
		} else if (action.equals("Επεξεργασία")) {
			// Εμφάνιση του DataEditorPanel μέσα στο MainFrame
			mainFrame.showDataEditor("cashflow");
			
		} else if (action.equals("Σύγκριση")) {
			JOptionPane.showMessageDialog(
				this,
				"Η λειτουργία 'Σύγκριση' δεν έχει υλοποιηθεί ακόμα.",
				"Υπό Κατασκευή",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	
	/**
	 * A custom border with rounded corners for Swing components.
	 */
	static class RoundedBorder extends AbstractBorder {
		private int radius;
		
		/**
		 * Constructs a RoundedBorder with the specified corner radius.
		 *
		 * @param radius the radius of the rounded corners
		 */
		public RoundedBorder(int radius) {
			this.radius = radius;
		}
		
		/**
		 * Paints the rounded border around the specified component.
		 *
		 * @param c the component for which this border is being painted
		 * @param g the Graphics context to use for painting
		 * @param x the x position of the border
		 * @param y the y position of the border
		 * @param width the width of the border
		 * @param height the height of the border
		 */
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(new Color(200, 200, 200)); // Ανοιχτό γκρι
			g2.setStroke(new BasicStroke(1)); // Λεπτή γραμμή
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
			g2.dispose();
		}
	}
}