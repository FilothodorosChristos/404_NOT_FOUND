package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * WelcomePanel displays the initial welcome screen of the application.
 * Shows the GoverLens logo, welcome message, and navigation to project selection.
 */
public class WelcomePanel extends JPanel {
	
	/** Reference to the main application frame. */
	private final MainFrame mainFrame;
	
	/** White color for text. */
	private static final Color TEXT_WHITE = Color.WHITE;
	
	/** Font used for main title. */
	private static final Font TITLE_FONT = new Font("Tahoma", Font.PLAIN, 50);
	
	/** Font used for subtitles. */
	private static final Font SUBTITLE_FONT = new Font("Tahoma", Font.PLAIN, 24);
	
	/**
	 * Constructs a WelcomePanel with the specified MainFrame reference.
	 *
	 * @param mainFrame the main application frame
	 */
	public WelcomePanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(new BorderLayout());
		createUI();
	}
	
	/**
	 * Creates and initializes the user interface components.
	 * Sets up the logo background, title, subtitle, and navigation button.
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
				
				/**Background */
				if (mainFrame.getBackgroundImage() != null) {
					g.drawImage(mainFrame.getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
				}
				
				/**circular logo */
				if (mainFrame.getLogoImage() != null) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					int diameter = (int) (Math.min(getWidth(), getHeight()) * 0.3);
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
		
		/** center panel */
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);
		centerPanel.add(Box.createVerticalStrut(30));
		centerPanel.add(createLabel("Καλώς ήρθατε στην GmoverLens! ", TITLE_FONT));
		
		logoBackgroundPanel.add(centerPanel, BorderLayout.CENTER);
		
		/**bottom section */
		JPanel bottomSection = new JPanel();
		bottomSection.setLayout(new BoxLayout(bottomSection, BoxLayout.Y_AXIS));
		bottomSection.setOpaque(false);
		bottomSection.add(Box.createVerticalStrut(80));
		bottomSection.add(createLabel("________________________________________________________", SUBTITLE_FONT));
		bottomSection.add(Box.createVerticalStrut(40));
		bottomSection.add(createLabel("Περισσότερη διαφάνεια , λιγότερη δυσκολία", SUBTITLE_FONT));
		bottomSection.add(Box.createVerticalStrut(10));
		
		bottomSection.add(createLabel("Μάθε που πηγαίνει κάθε ευρώ και ", SUBTITLE_FONT));
		bottomSection.add(Box.createVerticalStrut(10));
		bottomSection.add(createLabel(" επεξεργάσου τα δεδομένα σαν υπουργός .", SUBTITLE_FONT));
		bottomSection.add(Box.createVerticalStrut(80));
		
		
		JButton nextButton = new JButton("Ξεκινήστε!");
		nextButton.setPreferredSize(new Dimension(250, 60));
		nextButton.addActionListener(e -> mainFrame.showPanel(MainFrame.PROJECT_SELECTION));
		

		JPanel navigationPanel = new JPanel(new BorderLayout());
		navigationPanel.setOpaque(false);
		navigationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JPanel nextButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		nextButtonPanel.setOpaque(false);
		nextButtonPanel.add(nextButton);
		navigationPanel.add(nextButtonPanel, BorderLayout.CENTER);
		
		JPanel southWrapper = new JPanel(new BorderLayout());
		southWrapper.setOpaque(false);
		southWrapper.add(bottomSection, BorderLayout.CENTER);
		southWrapper.add(navigationPanel, BorderLayout.SOUTH);
		
		logoBackgroundPanel.add(southWrapper, BorderLayout.SOUTH);
		add(logoBackgroundPanel);
	}
	
	/**
	 * Creates a centered JLabel with the specified text and font.
	 *
	 * @param text the text to display
	 * @param font the font to use
	 * @return the configured JLabel
	 */
	private JLabel createLabel(String text, Font font) {
		JLabel label = new JLabel(text);
		label.setFont(font);
		label.setForeground(TEXT_WHITE);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		return label;
	}
}