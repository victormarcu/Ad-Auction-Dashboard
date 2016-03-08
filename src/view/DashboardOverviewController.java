package view;

import java.io.File;
import java.io.IOException;
import java.util.List;

import core.Model;
import core.campaigns.Campaign;
import core.data.DataProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class DashboardOverviewController {
	
	@FXML
	private Accordion campaignAccordion;
	
	@FXML
	private Button addCampaignButton;
	
	@FXML
	private Button removeCampaignButton;
	
	@FXML
	private TabPane chartsTabPane;
	
	@FXML
	private Tab addChartTab;
	
	@FXML
	private ProgressBar progress;
	
	private Stage mainStage;
	private Model model;
	
	BorderPane campaignOverview;
	ChartOverviewController chartController;
	
	
	// ==== Constructor ====
	
	public DashboardOverviewController() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("ChartOverview.fxml"));
		try {
			campaignOverview = (BorderPane) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chartController = loader.getController();
	}
	
	@FXML
	private void initialize() {
		campaignAccordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
			@Override
			public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue, TitledPane newValue) {
		        if (oldValue != null) 
		        	oldValue.setCollapsible(true);
		        
				if (newValue != null) {
					final int index = campaignAccordion.getPanes().indexOf(newValue);
					model.currentCampaign.set(model.campaigns.get(index));
					
					newValue.setCollapsible(false);
				} else {
					model.currentCampaign.set(null);
				}
			}			
		});
		
		progress.managedProperty().bind(progress.visibleProperty());
		
		addCampaignButton.managedProperty().bind(progress.visibleProperty().not());
		addCampaignButton.visibleProperty().bind(progress.visibleProperty().not());
		
		removeCampaignButton.managedProperty().bind(progress.visibleProperty().not());
		removeCampaignButton.visibleProperty().bind(progress.visibleProperty().not());
		
		progress.setVisible(false);		
		
		chartsTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {			
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				// make addChartTab unselectable
				if (newValue == addChartTab) {
					// select old value if one exists
					if (chartsTabPane.getTabs().size() != 1) {
						chartsTabPane.getSelectionModel().select(oldValue);
						model.addChart();
					}
					
					return;
				}
				
				oldValue.setContent(null);				
				newValue.setContent(campaignOverview);
				
				final int index = chartsTabPane.getTabs().indexOf(newValue);
				
				if (index != -1)
					model.currentProcessor.set(model.dataProcessors.get(index));
				else
					model.currentProcessor.set(null);
			}		
		});
	}
	
	@FXML
	private void handleAddCampaign() {
		DirectoryChooser dc = new DirectoryChooser();
		File campaignDirectory = dc.showDialog(mainStage);
		
		if (campaignDirectory != null)
			model.addCampaign(campaignDirectory);
	}
	
	@FXML
	private void handleRemoveCampaign() {	
		model.removeCampaign();
	}
	
	@FXML
	private void handleAddChart() {
		model.addChart();
	}
	
	public void setStageAndModel(Stage stage, Model model) {
		this.mainStage = stage;
		this.model = model;
		
		model.busy.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				progress.setVisible(newValue);
			}			
		});
		
		model.currentProcessor.addListener(new ChangeListener<DataProcessor>() {
			@Override
			public void changed(ObservableValue<? extends DataProcessor> observable, DataProcessor oldValue, DataProcessor newValue) {
				chartController.setDataProcessor(newValue);				
			}			
		});

		model.campaigns.addListener(new ListChangeListener<Campaign>() {
			@Override
			public void onChanged(Change<? extends Campaign> c) {
				while (c.next()) {
					TitledPane campaignOverview = null;
					
					campaignAccordion.getPanes().clear();

					for (Campaign campaign : model.campaigns) {
						try {
							// Load person overview.
							FXMLLoader loader = new FXMLLoader();
							loader.setLocation(this.getClass().getResource("CampaignOverview.fxml"));
							campaignOverview = (TitledPane) loader.load();

							CampaignOverviewController controller = loader.getController();

							controller.setCampaign(campaign);

							campaignAccordion.getPanes().add(campaignOverview);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					campaignAccordion.setExpandedPane(campaignOverview);
				}
			}
		});
		
		model.dataProcessors.addListener(new ListChangeListener<DataProcessor>() {
			@Override
			public void onChanged(Change<? extends DataProcessor> c) {
				final List<Tab> tabsList = chartsTabPane.getTabs();
				
				while (c.next()) {
					if (c.wasAdded()) {
						for (DataProcessor dataProcessor : c.getAddedSubList()) {
							TextField textField = new TextField();
							Label label = new Label("Chart");
							Tab tab = new Tab();

							tab.setGraphic(label);

							label.setOnMouseClicked(new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent event) {
									if (event.getClickCount() == 2) {
										textField.setText(label.getText());
										tab.setGraphic(textField);
										textField.selectAll();
										textField.requestFocus();
									}
								}
							});

							textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
								@Override
								public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
									if (!newValue) {
										if (!textField.getText().isEmpty())
											label.setText(textField.getText());

										tab.setGraphic(label);
									}
								}
							});

							textField.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									if (!textField.getText().isEmpty())
										label.setText(textField.getText());

									tab.setGraphic(label);
								}
							});

							tab.setOnCloseRequest(new EventHandler<Event>() {
								@Override
								public void handle(Event event) {
									final int index = chartsTabPane.getTabs().indexOf(event.getSource());
									model.removeChart(index);
								}
							});

							tabsList.add(tabsList.size() - 1, tab);
						}
						
						chartsTabPane.getSelectionModel().clearAndSelect(tabsList.size() - 2);
					}

				}
			}		
		});
		
		chartController.setCampaigns(model.campaigns);
	}
}
