package view;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import core.campaigns.Campaign;
import core.data.DataFilter;
import core.data.DataProcessor;
import core.data.Metric;
import core.users.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import util.DateRangeCallback;
import view.animation.PieChartScaleAnimation;

public class ChartOverviewController {
	
	// ==== Constants ====
	
	private static final ObservableList<Metric> METRICS = FXCollections.observableArrayList(Arrays.asList(Metric.values()));
	
	
	// ==== FXML Properties ====
	
	@FXML
	private AreaChart<Number, Number> areaChart;
	
	@FXML
	private ChoiceBox<Metric> metricsBox;
	
	@FXML
	private ChoiceBox<Campaign> campaignsBox;
	
	@FXML
	private DatePicker startDate;
	
	@FXML 
	private DatePicker endDate;
	
	@FXML
	private ChoiceBox<String> timeGranularity;
	
	// ==== Begin Filter Stuff ====
	
	@FXML
    private ListView<FilterListItem> filterList;
	
	private ObservableList<FilterListItem> filterListItems = FXCollections.observableArrayList();
	
	@FXML
	private Button addFilterBTN;
	
	@FXML
	private Button removeFilterBTN;
	
	@FXML
	private CheckBox filterMale;
	
	@FXML
	private CheckBox filterFemale;
	
	@FXML
	private CheckBox filterBelow25;
	
	@FXML
	private CheckBox filter25to34;
	
	@FXML
	private CheckBox filter35to44;
	
	@FXML
	private CheckBox filter45to54;
	
	@FXML
	private CheckBox filterAbove54;
	
	@FXML
	private CheckBox filterLow;
	
	@FXML
	private CheckBox filterMedium;
	
	@FXML
	private CheckBox filterHigh;
	
	@FXML
	private CheckBox filterNews;
	
	@FXML
	private CheckBox filterShopping;
	
	@FXML
	private CheckBox filterSocialMedia;
	
	@FXML
	private CheckBox filterBlog;
	
	@FXML
	private CheckBox filterHobbies;
	
	@FXML
	private CheckBox filterTravel;

	// ==== End Filter Stuff ====
	
	// ==== Begin User Stuff ====
	
	@FXML
	private PieChart genderChart;
	
	@FXML
	private PieChart ageChart;
	
	@FXML
	private PieChart incomeChart;
	
	@FXML
	private PieChart contextChart;
	
	// ==== End User Stuff ====
	
	// ==== Begin Bar Goodies ====
	
	@FXML
	private Label impressionsLabel;
	
	@FXML
	private Label clicksLabel;
	
	@FXML
	private Label uniquesLabel;
	
	@FXML
	private Label costLabel;
	
	@FXML
	private Label cpaLabel;
	
	@FXML
	private Label ctrLabel;
	
	@FXML
	private Label bounceRateLabel;
	
	// ==== End Bar Goodies
	
	// ==== Begin Bounces ====
	
	@FXML
	private TextField bounceViews;
	
	@FXML
	private TextField bounceTime;
	
	@FXML
	private BarChart<String, Number> histogram;
	
	// ==== End Bounces ====
	
	private CheckBox[][] filterGroups = new CheckBox[3][];
		
	private DataProcessor dataProcessor;
	
	private boolean isReady;
	
	
	// ==== Constructor ====
	
	public ChartOverviewController() {
		filterGroups = new CheckBox[][] {
			{filterMale, filterFemale},
			{filterBelow25, filter25to34, filter35to44, filter45to54, filterAbove54},
			{filterLow, filterMedium, filterHigh},
			{filterNews, filterShopping, filterSocialMedia, filterBlog, filterHobbies, filterTravel}};
		
	}
	
	
	// ==== Accessors ====
	
	public void setCampaigns(ObservableList<Campaign> campaigns) {
		campaignsBox.setItems(campaigns);

		campaignsBox.setTooltip(new Tooltip("Select a campaign"));
	}
	
	public void setDataProcessor(DataProcessor dataProcessor) {
		this.dataProcessor = dataProcessor;
		
		if(dataProcessor == null)
			return;
		
		// temporary hack
		isReady = false;
		
		//Setup filter list
		filterListItems.clear();
		
		for(DataFilter f : dataProcessor.getAllDataFilters()) {
			final FilterListItem fli = new FilterListItem(f.toString(), filterListItems.size());
			filterListItems.add(fli);
		}	
		
		filterList.getSelectionModel().clearAndSelect(0);	

		refreshData();
	}
	
	@FXML
	private void initialize() {
		// update Metrics box with current metrics
		metricsBox.setItems(METRICS);
		metricsBox.setTooltip(new Tooltip("Select a metric for your chart"));
		
		// listener to update DataProcessor metric
		metricsBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Metric>() {
			@Override
			public void changed(ObservableValue<? extends Metric> observable, Metric oldValue, Metric newValue) {

		    	if (!isReady)
		    		return;
		    	
				dataProcessor.setMetric(newValue);
				areaChart.getYAxis().setLabel(newValue.toString());
				refreshData();
			}
		});

		areaChart.getYAxis().setLabel(metricsBox.getItems().get(0).toString());
		
		// campaign change listener
		campaignsBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Campaign>() {
			@Override
			public void changed(ObservableValue<? extends Campaign> observable, Campaign oldValue, Campaign newValue) {
		    	if (!isReady)
		    		return;
				
		    	if (newValue == null)
		    		return;
		    	
				dataProcessor.setCampaign(newValue);
				refreshData();
			}
		});

		// locks to integers only
		timeGranularity.setItems(FXCollections.observableArrayList("Weeks", "Days", "Hours"));
		timeGranularity.getSelectionModel().clearAndSelect(1);
		timeGranularity.setTooltip(new Tooltip("Set the time granularity of the chart"));

		timeGranularity.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				final int i = timeGranularity.getSelectionModel().getSelectedIndex();
				
				if (i == 0)
					dataProcessor.setTimeGranularityInSeconds(60 * 60 * 24 * 7);
				else if (i == 1)
					dataProcessor.setTimeGranularityInSeconds(60 * 60 * 24);
				else if (i == 2)
					dataProcessor.setTimeGranularityInSeconds(60 * 60);
				
				areaChart.getXAxis().setLabel(newValue.toString());
				
				refreshData();
			}
		});

		areaChart.getXAxis().setLabel(timeGranularity.getSelectionModel().getSelectedItem());
		
		// locks to integers only
		bounceViews.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (!isReady)
		    		return;
		    	
		        if (!newValue.isEmpty() && newValue.matches("\\d*")) {
		            int value = Integer.parseInt(newValue);

		            dataProcessor.setBounceMinimumPagesViewed(value);
		            refreshData();
		        } else {
		        	bounceViews.setText(oldValue);
		        }
		    }
		});
		

		// locks to integers only
		bounceTime.textProperty().addListener(new ChangeListener<String>() {
		    @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	if (!isReady)
		    		return;
		    	
		        if (!newValue.isEmpty() && newValue.matches("\\d*")) {
		            int value = Integer.parseInt(newValue);

		            dataProcessor.setBounceMinimumSecondsOnPage(value);
		            refreshData();
		        } else {
		        	bounceTime.setText(oldValue);
		        }
		    }

		});
		

		//filter change
		filterList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FilterListItem>() {
			@Override
			public void changed(ObservableValue<? extends FilterListItem> i, FilterListItem arg1, FilterListItem arg2) {
				if (!isReady)
					return;
				
				refreshData();
			}
		});
		
		// stuff?
		filterList.setItems(filterListItems);

		//Tooltips
		addFilterBTN.setTooltip(new Tooltip("Add a new filter"));
		removeFilterBTN.setTooltip(new Tooltip("Remove selected filter"));
		filterList.setTooltip(new Tooltip("Filters currently applied to this chart"));
		
		impressionsLabel.setTooltip(new Tooltip("Total number of ads shown to users"));
		clicksLabel.setTooltip(new Tooltip("Total number of ads clicked"));
		uniquesLabel.setTooltip(new Tooltip("Total number of unique users that click on an ad"));
		costLabel.setTooltip(new Tooltip("Total cost of the campaign"));
		cpaLabel.setTooltip(new Tooltip("Cost-per-acquisition: Average cost of each acquisition"));
		ctrLabel.setTooltip(new Tooltip("Click-through-rate: The average number of clicks per impression"));
		bounceRateLabel.setTooltip(new Tooltip("The average percent of bounces per click"));

		bounceViews.setTooltip(new Tooltip("Number of pages viewed"));
		bounceTime.setTooltip(new Tooltip("Number of seconds spent on page"));
	}
	

	private void refreshData() {
		isReady = false;

		drawChart();
		drawUsers();
		drawHistogram();
		updateCampaigns();
		updateStats();
		updateDates();
		updateFilter();
		updateMetric();
		updateBounce();
		
		isReady = true;
	}
	
	private void updateBounce() {
		bounceTime.setText(Integer.toString(dataProcessor.getBounceMinimumSecondsOnPage()));
		bounceViews.setText(Integer.toString(dataProcessor.getBounceMinimumPagesViewed()));		
	}
	
	private void updateCampaigns() {
		campaignsBox.getSelectionModel().select(dataProcessor.getCampaign());
		
	}
	
	private void updateStats() {
		dataProcessor.computeTotals(filterList.getSelectionModel().getSelectedIndex());
		
		final NumberFormat numberFormatter = NumberFormat.getInstance();
		final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
		
		final String impressions = numberFormatter.format(dataProcessor.numberOfImpressions);
		final String clicks = numberFormatter.format(dataProcessor.numberOfClicks);
		final String uniques = numberFormatter.format(dataProcessor.numberOfUniques);
		final String cost = currencyFormatter.format((dataProcessor.costOfImpressions + dataProcessor.costOfClicks) / 100);
		final String cpa = currencyFormatter.format((dataProcessor.costOfImpressions + dataProcessor.costOfClicks) / 100 / dataProcessor.numberOfAcquisitions);
		final String ctr = numberFormatter.format(dataProcessor.numberOfClicks / (double) dataProcessor.numberOfImpressions);
		final String bounceRate = percentFormatter.format((double) dataProcessor.numberOfBounces / dataProcessor.numberOfClicks);
		
		impressionsLabel.setText(impressions);
		clicksLabel.setText(clicks);
		uniquesLabel.setText(uniques);
		costLabel.setText(cost);
		cpaLabel.setText(cpa);
		ctrLabel.setText(ctr);
		bounceRateLabel.setText(bounceRate);
	}
		
	private void drawUsers() {
		final EnumMap<User, Integer> users = dataProcessor.getContextData(filterList.getSelectionModel().getSelectedIndex());
		
		if (users == null)
			System.exit(0);
		
		final ObservableList<PieChart.Data> genderData = genderChart.getData();
		final ObservableList<PieChart.Data> ageData = ageChart.getData();
		final ObservableList<PieChart.Data> incomeData = incomeChart.getData();
		final ObservableList<PieChart.Data> contextData = contextChart.getData();
				
		genderData.clear();
		genderData.add(new PieChart.Data(User.GENDER_MALE.toString(), users.get(User.GENDER_MALE)));
		genderData.add(new PieChart.Data(User.GENDER_FEMALE.toString(), users.get(User.GENDER_FEMALE)));
		addPieChartAnimation(genderChart);
		
		ageData.clear();
		ageData.add(new PieChart.Data(User.AGE_BELOW_25.toString(), users.get(User.AGE_BELOW_25)));
		ageData.add(new PieChart.Data(User.AGE_25_TO_34.toString(), users.get(User.AGE_25_TO_34)));
		ageData.add(new PieChart.Data(User.AGE_35_TO_44.toString(), users.get(User.AGE_35_TO_44)));
		ageData.add(new PieChart.Data(User.AGE_45_TO_54.toString(), users.get(User.AGE_45_TO_54)));
		ageData.add(new PieChart.Data(User.AGE_ABOVE_54.toString(), users.get(User.AGE_ABOVE_54)));
		addPieChartAnimation(ageChart);
		
		incomeData.clear();
		incomeData.add(new PieChart.Data(User.INCOME_LOW.toString(), users.get(User.INCOME_LOW)));
		incomeData.add(new PieChart.Data(User.INCOME_MEDIUM.toString(), users.get(User.INCOME_MEDIUM)));
		incomeData.add(new PieChart.Data(User.INCOME_HIGH.toString(), users.get(User.INCOME_HIGH)));
		addPieChartAnimation(incomeChart);
		
		contextData.clear();
		contextData.add(new PieChart.Data(User.CONTEXT_NEWS.toString(), users.get(User.CONTEXT_NEWS)));
		contextData.add(new PieChart.Data(User.CONTEXT_SHOPPING.toString(), users.get(User.CONTEXT_SHOPPING)));
		contextData.add(new PieChart.Data(User.CONTEXT_SOCIAL_MEDIA.toString(), users.get(User.CONTEXT_SOCIAL_MEDIA)));
		contextData.add(new PieChart.Data(User.CONTEXT_BLOG.toString(), users.get(User.CONTEXT_BLOG)));
		contextData.add(new PieChart.Data(User.CONTEXT_HOBBIES.toString(), users.get(User.CONTEXT_HOBBIES)));
		contextData.add(new PieChart.Data(User.CONTEXT_TRAVEL.toString(), users.get(User.CONTEXT_TRAVEL)));
		addPieChartAnimation(contextChart);
	}
	
	private void addPieChartAnimation(PieChart chart) {
		for (final PieChart.Data d : chart.getData()) {
			d.getNode().setOnMouseEntered(new PieChartScaleAnimation(chart, d, true));
			d.getNode().setOnMouseExited(new PieChartScaleAnimation(chart, d, false));

			Tooltip.install(d.getNode(), new Tooltip(d.getName() + ": " + ((int)d.getPieValue())));
		}
	}
	
	private void addAreaChartTooltips(AreaChart<Number, Number> chart) {
		for (XYChart.Series<Number, Number> s : chart.getData()) {
			for (XYChart.Data<Number, Number> d : s.getData()) {
				// Tooltips
				Tooltip.install(d.getNode(),
						new Tooltip(d.getXValue() + "\n" + chart.getYAxis().getLabel() + ": " + d.getYValue()));

				// Adding class on hover
				d.getNode().setOnMouseEntered(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						d.getNode().getStyleClass().add("onHover");
					}
				});

				// Removing class on exit
				d.getNode().setOnMouseExited(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						d.getNode().getStyleClass().remove("onHover");
					}
				});
			}
		}
	}
	
	private void updateDates() {
		LocalDate startLocalDate = dataProcessor.getCampaign().getStartDateTime().toLocalDate();
		LocalDate endLocalDate = dataProcessor.getCampaign().getEndDateTime().toLocalDate();

		startDate.setDayCellFactory(new DateRangeCallback(true, startLocalDate, endLocalDate.minusDays(1), dataProcessor.getDataEndDateTime().toLocalDate()));
		startDate.setValue(dataProcessor.getDataStartDateTime().toLocalDate());
		startDate.setTooltip(new Tooltip("Set the start date of your campaign"));
		
		endDate.setDayCellFactory(new DateRangeCallback(false, startLocalDate.plusDays(1),endLocalDate, dataProcessor.getDataStartDateTime().toLocalDate()));
		endDate.setValue(dataProcessor.getDataEndDateTime().toLocalDate().minusDays(1));
		endDate.setTooltip(new Tooltip("Set the end date of your campaign"));
	}
	
	private void updateMetric() {
		metricsBox.getSelectionModel().select(dataProcessor.getMetric());
	}
	
	private void updateFilter() {		
		int dataFilterIndex = filterList.getSelectionModel().getSelectedIndex();
		
		filterMale.setSelected(dataProcessor.getFilterValue(User.GENDER_MALE, dataFilterIndex));
		filterFemale.setSelected(dataProcessor.getFilterValue(User.GENDER_FEMALE, dataFilterIndex));
		
		filterBelow25.setSelected(dataProcessor.getFilterValue(User.AGE_BELOW_25, dataFilterIndex));
		filter25to34.setSelected(dataProcessor.getFilterValue(User.AGE_25_TO_34, dataFilterIndex));
		filter35to44.setSelected(dataProcessor.getFilterValue(User.AGE_35_TO_44, dataFilterIndex));
		filter45to54.setSelected(dataProcessor.getFilterValue(User.AGE_45_TO_54, dataFilterIndex));
		filterAbove54.setSelected(dataProcessor.getFilterValue(User.AGE_ABOVE_54, dataFilterIndex));
		
		filterLow.setSelected(dataProcessor.getFilterValue(User.INCOME_LOW, dataFilterIndex));
		filterMedium.setSelected(dataProcessor.getFilterValue(User.INCOME_MEDIUM, dataFilterIndex));
		filterHigh.setSelected(dataProcessor.getFilterValue(User.INCOME_HIGH, dataFilterIndex));
		
		filterNews.setSelected(dataProcessor.getFilterValue(User.CONTEXT_NEWS, dataFilterIndex));
		filterShopping.setSelected(dataProcessor.getFilterValue(User.CONTEXT_SHOPPING, dataFilterIndex));
		filterSocialMedia.setSelected(dataProcessor.getFilterValue(User.CONTEXT_SOCIAL_MEDIA, dataFilterIndex));
		filterBlog.setSelected(dataProcessor.getFilterValue(User.CONTEXT_BLOG, dataFilterIndex));
		filterHobbies.setSelected(dataProcessor.getFilterValue(User.CONTEXT_HOBBIES, dataFilterIndex));
		filterTravel.setSelected(dataProcessor.getFilterValue(User.CONTEXT_TRAVEL, dataFilterIndex));
		
		//Refreshes the list view string of selected element
		//TODO Can this be done a better way?
		dataProcessor.getAllDataFilters().add(dataFilterIndex, dataProcessor.getDataFilter(dataFilterIndex));
		dataProcessor.getAllDataFilters().remove(dataFilterIndex);
	}
	
	@FXML
	private void handleAddFilter() {
		DataFilter dataFilter = new DataFilter();
		dataProcessor.addDataFilter(dataFilter);

		// refresh the list?
		filterListItems.add(new FilterListItem(dataFilter.toString(), dataProcessor.getAllDataFilters().size() - 1));
		filterList.getSelectionModel().clearAndSelect(dataProcessor.getAllDataFilters().size() - 1);
	}
	
	@FXML
	private void handleRemoveFilter() {
		if (dataProcessor.getAllDataFilters().size() > 1) {
			final int index = filterList.getSelectionModel().getSelectedIndex();
			dataProcessor.getAllDataFilters().remove(index);
			filterListItems.remove(index);
		}else
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Cannot remove last filter");
			alert.setHeaderText("Error removing filter");
			alert.setContentText("You cannot remove the last filter from a chart.");
			alert.showAndWait();
		}
	}
	
	@FXML
	private void handleFilter() {
//		fixDeselectedFilters();
		int dataFilterIndex = filterList.getSelectionModel().getSelectedIndex();
		
		dataProcessor.setFilterValue(User.GENDER_MALE, filterMale.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.GENDER_FEMALE, filterFemale.isSelected(), dataFilterIndex);
		
		dataProcessor.setFilterValue(User.AGE_BELOW_25, filterBelow25.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.AGE_25_TO_34, filter25to34.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.AGE_35_TO_44, filter35to44.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.AGE_45_TO_54, filter45to54.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.AGE_ABOVE_54, filterAbove54.isSelected(), dataFilterIndex);
		
		dataProcessor.setFilterValue(User.INCOME_LOW, filterLow.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.INCOME_MEDIUM, filterMedium.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.INCOME_HIGH, filterHigh.isSelected(), dataFilterIndex);

		dataProcessor.setFilterValue(User.CONTEXT_NEWS, filterNews.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.CONTEXT_SHOPPING, filterShopping.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.CONTEXT_SOCIAL_MEDIA, filterSocialMedia.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.CONTEXT_BLOG, filterBlog.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.CONTEXT_HOBBIES, filterHobbies.isSelected(), dataFilterIndex);
		dataProcessor.setFilterValue(User.CONTEXT_TRAVEL, filterTravel.isSelected(), dataFilterIndex);
		
		filterListItems.get(dataFilterIndex).setText(dataProcessor.getDataFilter(dataFilterIndex).toString());
		
		refreshData();
	}
	
	@FXML
	private void handleStartDate() {
		final LocalDate date = startDate.getValue();
		final LocalTime time = LocalTime.of(0, 0);

		dataProcessor.setDataStartDateTime(LocalDateTime.of(date, time));
		
		refreshData();
	}
	
	@FXML
	private void handleEndDate() {
		final LocalDate date = endDate.getValue().plusDays(1);
		final LocalTime time = LocalTime.of(0, 0);
		
		dataProcessor.setDataEndDateTime(LocalDateTime.of(date, time));
		
		refreshData();
	}
	
	@FXML
	private void handleMetrics() {
		dataProcessor.setMetric(metricsBox.getValue());
	}
	
	private void drawHistogram()
	{
		histogram.getData().clear();
		
		for(int dataFilterIndex=0; dataFilterIndex < dataProcessor.getAllDataFilters().size(); dataFilterIndex++)
		{
			XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
			series.setName(dataProcessor.getDataFilter(dataFilterIndex).toString());
			
			List<Integer> list = dataProcessor.getClickCostFrequency(dataFilterIndex);
			for(int cost=0; cost<list.size(); cost++)
			{
				series.getData().add(new XYChart.Data<String, Number>(""+(cost+1), list.get(cost)));
			}
			histogram.getData().add(series);
		}
	}
	
	private void drawChart() {
		areaChart.getData().clear();
				
		for(int dataFilterIndex=0; dataFilterIndex < dataProcessor.getAllDataFilters().size(); dataFilterIndex++)
		{
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			series.setName(dataProcessor.getDataFilter(dataFilterIndex).toString());
						
			List<? extends Number> list = dataProcessor.getData(dataFilterIndex);
			int counter = 0;
			for (Number n : list) {
				series.getData().add(new XYChart.Data<Number, Number>(counter++, n));
			}
			
			areaChart.getData().add(series);
		}
		
		addAreaChartTooltips(areaChart);
	}

	//TODO code review
	private void fixDeselectedFilters() {
		
		for (CheckBox[] group : filterGroups) {
			boolean noneSelected = true;
			
			for (CheckBox checkbox : group) {
				if (checkbox.isSelected()) {
					noneSelected = false;
					break;
				}
			}
			
			if (noneSelected) {
				for (CheckBox checkbox : group) {
					checkbox.setSelected(true);
				}
			}
		}
		
//		if(!filterMale.isSelected() && !filterFemale.isSelected()){
//			filterMale.setSelected(true);
//			filterFemale.setSelected(true);
//		}
//
//		if(!filterBelow25.isSelected() && !filter25to34.isSelected() && !filter35to44.isSelected() &&
//				!filter45to54.isSelected() && !filterAbove54.isSelected()){
//			filterBelow25.setSelected(true);
//			filter25to34.setSelected(true);
//			filter35to44.setSelected(true);
//			filter45to54.setSelected(true);
//			filterAbove54.setSelected(true);
//		}
//
//		if(!filterLow.isSelected() && !filterMedium.isSelected() && !filterHigh.isSelected()){
//			filterLow.setSelected(true);
//			filterMedium.setSelected(true);
//			filterHigh.setSelected(true);
//		}
//
//		if(!filterNews.isSelected() && !filterShopping.isSelected() && !filterSocialMedia.isSelected() &&
//				!filterBlog.isSelected() && !filterHobbies.isSelected() && !filterTravel.isSelected()){
//			filterNews.setSelected(true);
//			filterShopping.setSelected(true);
//			filterBlog.setSelected(true);
//			filterSocialMedia.setSelected(true);
//			filterHobbies.setSelected(true);
//			filterTravel.setSelected(true);
//		}
	}

}
