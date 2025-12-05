package GUI;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * ProjectSelectionPanel allows the user to choose between continuing
 * an existing project or starting a new simulation.
 */
public class ProjectSelectionPanel extends JPanel {
	
	/** Reference to the main application frame. */
	private final MainFrame mainFrame;
	
	/** Navy-blue color used in UI. */
	private static final Color NAVY_BLUE = new Color(0, 0, 128);
	
	/** White color for text. */
	private static final Color TEXT_WHITE = Color.WHITE;
	
	/** Font used for main title. */
	private static final Font TITLE_FONT = new Font("Tahoma", Font.PLAIN, 50);
	
	/** Font used for buttons. */
	private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 24);
	
	/**
	 * Constructs a ProjectSelectionPanel with the specified MainFrame reference.
	 *
	 * @param mainFrame the main application frame
	 */
	public ProjectSelectionPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new BorderLayout());
		createUI();
	}
	
	/**
	 * Creates and initializes the user interface components.
	 * Sets up the logo background, title, action buttons, and navigation.
	 */
	private void createUI() {
		JPanel logoBackgroundPanel = new JPanel(new BorderLayout()) {
			/**
			 * Paints the background image and circular logo.
			 *
			 * @param g the Graphics context
			 */
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				/**Background image */
				if (mainFrame.getBackgroundImage() != null) {
					g.drawImage(mainFrame.getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
				}
				
				/** Circular logo */
				if (mainFrame.getLogoImage() != null) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					int diameter = (int) (Math.min(getWidth(), getHeight()) * 0.4);
					int x = (getWidth() - diameter) / 2;
					int y = (getHeight() - diameter) / 3;
					
					Shape clip = new Ellipse2D.Double(x, y, diameter, diameter);
					g2.setClip(clip);
					g2.drawImage(mainFrame.getLogoImage(), x, y, diameter, diameter, this);
					
					g2.setClip(null);
					g2.setStroke(new BasicStroke(4));
					g2.setColor(Color.DARK_GRAY);
					g2.drawOval(x, y, diameter, diameter);
					g2.dispose();
				}
			}
		};
		
		/**Title */
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.setOpaque(false);
		titlePanel.add(Box.createVerticalStrut(40));
		
		JLabel titleLabel = new JLabel("Καλώς ήρθατε στην GoverLens! ");
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setForeground(TEXT_WHITE);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titlePanel.add(titleLabel);
		
		logoBackgroundPanel.add(titlePanel, BorderLayout.CENTER);
		
		/**Buttons */
		JButton oldProjectBtn = createStyledButton("Συνέχεια Προσομοίωσης");
		JButton newProjectBtn = createStyledButton("Καινούργια Προσομοίωση");
		
		oldProjectBtn.addActionListener(e -> mainFrame.showPanel(MainFrame.YEAR_SELECTION));
		newProjectBtn.addActionListener(e -> mainFrame.showPanel(MainFrame.YEAR_SELECTION));
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.setOpaque(false);
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(oldProjectBtn);
		buttonsPanel.add(Box.createHorizontalStrut(40));
		buttonsPanel.add(newProjectBtn);
		buttonsPanel.add(Box.createHorizontalGlue());
		
		/**previous button */
		JButton prevButton = new JButton("< Προηγούμενο");
		prevButton.setPreferredSize(new Dimension(150, 40));
		prevButton.addActionListener(e -> mainFrame.showPanel(MainFrame.WELCOME));
		
		JPanel prevButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		prevButtonPanel.setOpaque(false);
		prevButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		prevButtonPanel.add(prevButton);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setOpaque(false);
		southPanel.add(buttonsPanel, BorderLayout.NORTH);
		southPanel.add(prevButtonPanel, BorderLayout.SOUTH);
		
		logoBackgroundPanel.add(southPanel, BorderLayout.SOUTH);
		add(logoBackgroundPanel);
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
		button.setPreferredSize(new Dimension(350, 90));
		button.setMinimumSize(new Dimension(350, 90));
		button.setMaximumSize(new Dimension(350, 90));
		button.setBorder(new RoundedBorder(15));
		button.setFocusPainted(false);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		return button;
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
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
			g2.dispose();
		}
	}
}