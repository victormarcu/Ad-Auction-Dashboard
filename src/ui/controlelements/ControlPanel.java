package ui.controlelements;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Controller;
import core.Model;
import core.campaigns.Campaign;

public class ControlPanel extends JPanel implements Observer, ActionListener, ChangeListener, ItemListener {

	// ==== Constants ====
	
	private static final long serialVersionUID = 824395947852730145L;
	
	// ==== Properties ====	
	
	private final Model model;
	
	// Add controls here
	/* private final JSpinner ...
	 * private final JTextField ...
	 * private ...
	 */
	JTabbedPane controlTabbedPane;
	private GeneralTab generalTab;
	private FilterTab filterTab;
	
	ProgressBox modelProgress;
	
	// ==== Constructor ====
	
	public ControlPanel(Model model) {
		
		// Register as an Observer of Model
		this.model = model;
		model.addObserver(this);
		
		// Add Borders for visual appeal!!!
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(280, 600));
		setBorder(BorderFactory.createCompoundBorder(
		new MatteBorder(0, 1, 0, 0, Color.DARK_GRAY),
		new EmptyBorder(0, 10, 10, 10)));
		
		// Add Listener Hooks here
		/* x.addChangeListener(this);
		 * y.addActionListener(this);
		 * z.addItemListener(this);
		 */
		
		// Initialise UI Eleements Here
		
		controlTabbedPane = new JTabbedPane();
		
		generalTab = new GeneralTab(model);
		filterTab = new FilterTab(model);
		
		controlTabbedPane.addTab("General", null, generalTab, "Inspect Campaigns");
		controlTabbedPane.addTab("Filter", null, filterTab, "Modify Chart Filters/Metrics");

		modelProgress = new ProgressBox(model);
		
		// Add Settings here
		/* addSetting(component, title, help text)
		 * 
		 */
		
		// Add UI Elements Here
		add(controlTabbedPane, BorderLayout.CENTER);
		add(modelProgress,BorderLayout.SOUTH);
	}
	
	// ==== Private Helper Methods ====
	
	private void addSetting(JComponent control, String title, String text) {
		JLabel label = new JLabel(title);
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		// setting maximum size to comply with BoxLayout
		control.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		control.setMaximumSize(new Dimension(300, control.getPreferredSize().height));
		
		// use cheap "<html>" to enable line-wrapping
		JLabel help = new JLabel("<html>" + text + "</html>");
		help.setFont(label.getFont().deriveFont(Font.ITALIC, 10));
	
		add(label);
		add(Box.createRigidArea(new Dimension(0, 3)));
		add(control);
		add(Box.createRigidArea(new Dimension(0, 2)));
		add(help);
		add(Box.createRigidArea(new Dimension(0, 15)));
	}
	
	// ==== Observer Implementation ====

	@Override
	public void update(Observable o, Object arg) {
		if (o == model) {
			// TODO What happens when Model updates the controls?
			
			// We would like to update the existing values in the ControlPanel to reflect
			// the new state of the Model
			Campaign[] listData = new Campaign[model.getCampaigns().size()];
//			generalTab.setCampaignListData(model.getCampaigns().toArray(listData));
		}
	}
	
	
	// ==== ActionListener Implementation ====
	
	// This method triggers whenever a control is manipulated
	// Use this to alter the Model by calling its appropriate Models

	@Override
	public void actionPerformed(ActionEvent e) {
//		final Object source = e.getSource();
		
		// TODO Add if-statements to determine appropriate actions
		
	}
	
	
	// ==== ItemListener Implementation ====

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	// ==== ChangeListener Implementation ====

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}



}