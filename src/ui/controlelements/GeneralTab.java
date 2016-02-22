package ui.controlelements;


import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import core.Model;
import core.campaigns.Campaign;

import ui.controlelements.ControlPanelBox;

import javax.swing.*;

import core.Model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Filter;


/**
 * Created by james on 17/02/16.
 */


public class GeneralTab extends ControlPanelBox {

    public enum FilterType {
        GENDER, AGE, INCOME, CONTEXT
    }

    private JButton removeCampaignBTN = new JButton("-");
    private JButton addCampaignBTN = new JButton("+");

    String[] arr = {"Campaign 1", "Campaign 2"};
    private JList<String> campaignList = new JList<String>(arr);
    JLabel noImpressionsLabel = new JLabel("######");
    JLabel startDateLabel = new JLabel("######");
    JLabel endDateLabel = new JLabel("######");
    JLabel totalClicksLabel =  new JLabel("######");
    JLabel totalCostLabel = new JLabel("#####");
    JLabel campaignDirectoryLabel = new JLabel("######");

    private DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public GeneralTab(Model model){
    	super(model);

        Box addsubPanel = new Box(BoxLayout.X_AXIS);
        addsubPanel.add(addCampaignBTN);
        addsubPanel.add(removeCampaignBTN);
        //campaign metrics

        addSetting(campaignList,"Campaigns","");
        addSetting(addsubPanel,"","");
        addSetting(noImpressionsLabel, "Impressions", " " );
        addSetting(startDateLabel, "Start Date", " " );
        addSetting(endDateLabel, "End Date", "" );
        addSetting(totalClicksLabel, "Total Clicks", " " );
        addSetting(totalCostLabel, "Total Cost", " ");
        addSetting(campaignDirectoryLabel, "Campaign Directory", " " );

    }
    
    public void setCampaignListData(Campaign[] listData) {
//    	campaignList.setListData(listData);
//    	campaignList.setSelectedIndex(campaignList.getModel().getSize()-1);
    }
    

	@Override
	public void update(Observable o, Object arg) {

        if(campaignList.getSelectedValue() == null)
            return;
//
//        Campaign campaign = campaignList.getSelectedValue();
//
//        noImpressionsLabel.setText(""+campaign.getNumberOfImpressions());
//        startDateLabel.setText(campaign.getStartDate().format(dateTimeFormatter));
//        endDateLabel.setText(campaign.getEndDate().format(dateTimeFormatter));
//        totalClicksLabel.setText(""+campaign.getNumberOfClicks());
//        totalCostLabel.setText(("£"+new DecimalFormat("#.##").format(campaign.getTotalCostOfCampaign())));
//        campaignDirectoryLabel.setText(campaign.getDirectoryPath());

	}

    class GeneralTabController implements ActionListener,
            ChangeListener, ItemListener {

        public GeneralTabController(Model model){

        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }

        @Override
        public void stateChanged(ChangeEvent e) {

        }

        @Override
        public void itemStateChanged(ItemEvent e) {

        }
    }


}
