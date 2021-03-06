package core.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import core.campaigns.Campaign;
import core.tables.ClicksTable;
import core.tables.CostTable;
import core.tables.ImpressionsTable;
import core.tables.LogTable;
import core.users.User;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import util.DateProcessor;

// TODO should we rename this to DataProcessor instead?
// Chart sounds like something the view should be handling
public class DataProcessor {
	
	// ==== Constants ====
	
	// number of nodes used to compute time granularity bounds
	public final static int MINIMUM_NUMBER_OF_NODES = 5;
	public final static int MAXIMUM_NUMBER_OF_NODES = 20;
	
	// ==== Properties ====
	
	// the campaign we are working on for this chart
	private Campaign campaign;
	
	// the start and end dates of this dataprocessor
	private long dataStartDate;
	private long dataEndDate;
	
	// the filter to filter the metrics by
	//private final ObservableList<DataFilter> dataFilters = FXCollections.observableArrayList();
	private final List<DataFilter> dataFilters = new ArrayList<DataFilter>();
	
	// the metric that the chart is handling
	private Metric metric;
	
	// the time granularity of this dataprocessor
	private int timeGranularityInSeconds;
	
	// bounce logic
	private int bounceMinimumPagesViewed;
	private int bounceMinimumSecondsOnPage;
	
	public int numberOfImpressions;
	public int numberOfClicks;
	public int numberOfUniques;
	public int numberOfAcquisitions;
	public int numberOfBounces;
	
	public double costOfImpressions;
	public double costOfClicks;
	
	private LogTable logTable;
	private ClicksTable clickTable;
	private ImpressionsTable impressionTable;
	
//	// TODO: misc stats (useful for checks)
//	private int dataReturnSize;
	
	
	// ==== Constructor ====
	
	/**
	 * Constructor taking another DataProcessor, this will clone
	 * the properties of the DataProcessor
	 * 
	 * @param dataProcessor - the DataProcessor to clone by
	 */
	public DataProcessor(DataProcessor dataProcessor) {
		// sets the campaign
		campaign = dataProcessor.campaign;
		
		// assume validated dates
		dataStartDate = dataProcessor.dataStartDate;
		dataEndDate = dataProcessor.dataEndDate;
		
		// new DataFilter with same parameters, different objects
		// as user can modify this filter
		addAllDataFilters(getAllDataFilters());
		
		// same metrics
		metric = dataProcessor.metric;
		
		// time granularity
		timeGranularityInSeconds = dataProcessor.timeGranularityInSeconds;
		
		// bounce criteria
		bounceMinimumPagesViewed = dataProcessor.bounceMinimumPagesViewed;
		bounceMinimumSecondsOnPage = dataProcessor.bounceMinimumSecondsOnPage;		
	}
	
	public DataProcessor(Campaign campaign) {
		// set campaign SHOULD compute start and end dates
		setCampaign(campaign);	
		
		// create a new DataFilter (all enabled)
		addDataFilter(new DataFilter());
		
		// set default metric
		metric = Metric.NUMBER_OF_IMPRESSIONS;
		
		// default time granularity - daily
		timeGranularityInSeconds = 60 * 60 * 24;
		
		// set bounce criteria
		bounceMinimumPagesViewed = 1;
		bounceMinimumSecondsOnPage = 30;	
	}
	

	// ==== Accessors ====
	
	public final Campaign getCampaign() {
		return campaign;
	}
	
	// TODO: adjust for time granularity here as well
	public final void setCampaign(Campaign campaign) {
		this.campaign = Objects.requireNonNull(campaign);
		
		final long campaignStartDate = DateProcessor.toEpochSeconds(campaign.getStartDateTime());
		final long campaignEndDate = DateProcessor.toEpochSeconds(campaign.getEndDateTime());
		
		if (dataStartDate < campaignStartDate || dataStartDate > campaignEndDate)
			dataStartDate = campaignStartDate;
		
		if (dataEndDate < campaignStartDate || dataEndDate > campaignEndDate)
			dataEndDate = campaignEndDate;
		
		logTable = campaign.getServers();
		clickTable = campaign.getClicks();
		impressionTable = campaign.getImpressions();
	}
	
	public final Metric getMetric() {
		return metric;
	}
	
	/**
	 * sets the current dataProcessor's metric
	 * 
	 * @param metric - the metric to process on
	 */
	public final void setMetric(Metric metric) {
		this.metric = Objects.requireNonNull(metric);
	}
	
	/**
	 * Computes a list of the frequency of each click cost delimited
	 * by one pence each. 
	 * @param dataFilterIndex DataFilter to apply for this data set
	 * @return List of click cost frequencies for the given data filter index
	 */
	public final List<Integer> getClickCostFrequency(int dataFilterIndex)
	{
		final DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		//Assume max cost per click is 16 (based off of data set provided)
		int max_cost = 16;
		int[] freqs = new int[max_cost];
		int val;
		
		for (int i = 0; i < clickTable.size(); i++)
		{
			final int dateTime = clickTable.getDateTime(i);
			
			//Ignore values before start date
			if (dateTime < dataStartDate)
				continue;
			//Terminate once end date is reached
			if (dateTime > dataEndDate)
				break;
			
			//Test filter against datum
			if (!dataFilter.test(clickTable.getUserData(i)))
				continue;
			
			//Round value up to nearest pence as so to bucket the costs for histogram
			val = (int) Math.ceil(clickTable.getCost(i));
			//Ignore zero values
			if(val == 0) continue;
			if(val >=  max_cost)
			{
				//If click cost is above the max cost, array is not big enough and must be resized
				freqs = resizeIntArray(freqs);
				max_cost = freqs.length;	
			}
			//Increase frequency of relevant bucket
			freqs[val]++;
		}
		
		//Return array in List format, trimmed to size
		ArrayList<Integer> clickFrequency = new ArrayList<Integer>();
		for(int i=0; i<freqs.length; i++)
		{
			if(i > max_cost / 2 && freqs[i] == 0)
				break;
			clickFrequency.add(freqs[i]);
		}
		clickFrequency.trimToSize();
		
		return clickFrequency;
	}
	
	/**
	 * Resizes an integer array by doubling its length and copying
	 * elements over
	 * @param arr Original array
	 * @return New array with double length
	 */
	private final int[] resizeIntArray(int[] arr)
	{
		int[] new_arr = new int[arr.length * 2];
		for(int i=0; i<arr.length; i++)
			new_arr[i] = arr[i];
		return new_arr;
	}
	
	/**
	 * method to get DataProcessor's Data depending on
	 * the metric selected
	 * 
	 * @return a list of numbers with data
	 */
	public final List<? extends Number> getData(int dataFilterIndex) {
		List<? extends Number> returnList;
		
		final long time = System.currentTimeMillis();
		
		switch (metric) {
		case NUMBER_OF_IMPRESSIONS:
			returnList = numberOfImpressions(dataFilterIndex);
			break;
		case NUMBER_OF_CLICKS:
			returnList = numberOfClicks(dataFilterIndex);
			break;
		case NUMBER_OF_UNIQUES:
			returnList = numberOfUniques(dataFilterIndex);
			break;
		case NUMBER_OF_BOUNCES:
			returnList = numberOfBounces(dataFilterIndex);
			break;
		case NUMBER_OF_CONVERSIONS:
			returnList = numberOfConversions(dataFilterIndex);
			break;
		case TOTAL_COST:
			returnList = totalCost(dataFilterIndex);
			break;
		case CLICK_THROUGH_RATE:
			returnList = clickThroughRate(dataFilterIndex);
			break;
		case COST_PER_ACQUISITION:
			returnList = costPerAcquisition(dataFilterIndex);
			break;
		case COST_PER_CLICK:
			returnList = costPerClick(dataFilterIndex);
			break;
		case COST_PER_THOUSAND_IMPRESSIONS:
			returnList = costPerThousandImpressions(dataFilterIndex);
			break;
		case BOUNCE_RATE:
			returnList = bounceRate(dataFilterIndex);
			break;
		default:
			returnList = null;
		}
		
		if (returnList != null) {
			System.out.println("Metric Type:\t" + metric.toString());
			System.out.println("Data Size:\t" + returnList.size());
			System.out.println("Time Taken:\t" + (System.currentTimeMillis() - time));
			System.out.println("--------------------------------------");
		}		
		return returnList;
	}
	
	
	public final void computeTotals(int dataFilterIndex)
	{
		DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		costOfImpressions = 0;
		numberOfImpressions = 0;		
		for (int i = 0; i < impressionTable.size(); i++) {
			final int dateTime = impressionTable.getDateTime(i);
			
			if (dateTime < dataStartDate)
				continue;
			
			if (dateTime > dataEndDate)
				break;
			
			if (dataFilter.test(impressionTable.getUserData(i))) {
				costOfImpressions += impressionTable.getCost(i);
				numberOfImpressions++;
			}
		}
		
		TLongSet usersSet = new TLongHashSet();
		numberOfClicks = 0;
		costOfClicks = 0;
		for (int i = 0; i < clickTable.size(); i++) {
			final int dateTime = clickTable.getDateTime(i);
			
			if (dateTime < dataStartDate)
				continue;
			
			if (dateTime > dataEndDate)
				break;
			
			if (dataFilter.test(clickTable.getUserData(i))) {
				usersSet.add(clickTable.getUserID(i));
				costOfClicks += clickTable.getCost(i);
				numberOfClicks++;
			}
		}
		numberOfUniques = usersSet.size();

		numberOfAcquisitions = 0;
		numberOfBounces = 0;
		for (int i = 0; i < logTable.size(); i++) {
			final int dateTime = clickTable.getDateTime(i);
			
			if (dateTime < dataStartDate)
				continue;
			
			if (dateTime > dataEndDate)
				break;
			
			if (dataFilter.test(clickTable.getUserData(i))) {
				if (logTable.getConversion(i)) {
					numberOfAcquisitions++;
					continue;
				}
				
				if (logTable.getPagesViewed(i) > bounceMinimumPagesViewed)
					continue;
				
				final long exitDateTime = logTable.getExitDateTime(i);
				
				if (exitDateTime == DateProcessor.DATE_NULL)
					continue;
				
				if (exitDateTime - dateTime > bounceMinimumSecondsOnPage)
					continue;
				
				numberOfBounces++;
			}
		}
	}
	
	public final LocalDateTime getDataStartDateTime() {
		return DateProcessor.toLocalDateTime(dataStartDate);
	}
	
	public final void setDataStartDateTime(LocalDateTime dateTime) {
		final long campaignStartDate = DateProcessor.toEpochSeconds(campaign.getStartDateTime());
		final long campaignEndDate = DateProcessor.toEpochSeconds(campaign.getEndDateTime());
		
		final long newDateTime = DateProcessor.toEpochSeconds(dateTime);
		
//		if (newDateTime < campaignStartDate)
//			return;

		if (newDateTime >= campaignEndDate)
			return;
		
		if (newDateTime >= dataEndDate)
			return;
		
		// update the values if is valid
		dataStartDate = newDateTime;
	}
	
	
	public final LocalDateTime getDataEndDateTime() {
		return DateProcessor.toLocalDateTime(dataEndDate);
	}
	
	public final void setDataEndDateTime(LocalDateTime dateTime) {
		final long campaignStartDate = DateProcessor.toEpochSeconds(campaign.getStartDateTime());
		final long campaignEndDate = DateProcessor.toEpochSeconds(campaign.getEndDateTime());
		
		// increment date by one day		
		final long newDateTime = DateProcessor.toEpochSeconds(dateTime);
		
//		if (newDateTime > campaignEndDate)
//			return;
		
		if (newDateTime <= campaignStartDate)
			return;
		
		if (newDateTime <= dataStartDate)
			return;
		
		// update if valid
		dataEndDate = newDateTime;
	}
	
	
	public final int getTimeGranularityInSeconds() {
		return timeGranularityInSeconds;
	}
	
	public final void setDataPoints(int dataPoints) {
		final long timeDifference = dataEndDate - dataStartDate;		
		timeGranularityInSeconds = (int) (timeDifference / (dataPoints + 1));
	}
	
	public final void setTimeGranularityInSeconds(int timeGranularityInSeconds) {
		// check time granularity is at least 1
		if (timeGranularityInSeconds < 3600)
			throw new IllegalArgumentException("cannot have time granularity below 1 hour");
		
		// time granularity 		
		this.timeGranularityInSeconds = timeGranularityInSeconds;
	}
	
	
	public final int getBounceMinimumPagesViewed() {
		return bounceMinimumPagesViewed;
	}
	
	public final void setBounceMinimumPagesViewed(int bounceMinimumPagesViewed) {
		if (bounceMinimumPagesViewed < 1)
			throw new IllegalArgumentException("cannot have less than 1 page view");
		
		this.bounceMinimumPagesViewed = bounceMinimumPagesViewed;
	}
	
	public final int getBounceMinimumSecondsOnPage() {
		return bounceMinimumSecondsOnPage;
	}
	
	public final void setBounceMinimumSecondsOnPage(int bounceMinimumSecondsOnPage) {
		if (bounceMinimumSecondsOnPage < 1)
			throw new IllegalArgumentException("cannot spend less than 1 second on page");
		
		this.bounceMinimumSecondsOnPage = bounceMinimumSecondsOnPage;
	}
	
	public final boolean getFilterValue(User field) {
		return getFilterValue(field, 0);
	}
	
	public final boolean getFilterValue(User field, int dataFilterIndex) {
		return dataFilters.get(dataFilterIndex).getField(field);
	}
	
	public final void setFilterValue(User field, boolean value, int dataFilterIndex) {
		dataFilters.get(dataFilterIndex).setField(field, value);
	}
	
	
	// ==== Compute Metrics ====	
	
	private final List<Integer> numberOfImpressions(int dataFilterIndex) {
		return numberOfRecord(impressionTable, dataFilterIndex);
	}
	
	private final List<Integer> numberOfClicks(int dataFilterIndex) {
		return numberOfRecord(clickTable, dataFilterIndex);
	}
	
	private final List<Integer> numberOfRecord(CostTable costTable, int dataFilterIndex) {
		final ArrayList<Integer> recordList = new ArrayList<Integer>();
		final DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		// initialise current date as startDate
		long currentDate = dataStartDate;
		long nextDate = currentDate + timeGranularityInSeconds;
		
		// reset counter
		int counter = 0;
		
		outerLoop:
		for (int i = 0; i < costTable.size(); i++) {
			final int dateTime = costTable.getDateTime(i);
						
			// we ignore the impression if the date is before the current date
			if (dateTime < currentDate)
				continue;
			
			// add new mapping if after time granularity separator
			while (dateTime > nextDate) {
				if (nextDate == dataEndDate)
					break outerLoop;
				
				recordList.add(counter);
				
				counter = 0;
				
				currentDate = nextDate;
				nextDate = currentDate + timeGranularityInSeconds;
				
				if (nextDate > dataEndDate)
					nextDate = dataEndDate;
			}
			
			if (dataFilter.test(costTable.getUserData(i))) {
				counter++;
			}
		}
		
		// add last entry
		recordList.add(counter);

		// pack
		recordList.trimToSize();
				
		return recordList;
	}
	
	// The number of unique users that click on an ad during the course of a campaign.
	private final List<Integer> numberOfUniques(int dataFilterIndex) {
		final ArrayList<Integer> uniquesList = new ArrayList<Integer>();
		final ClicksTable costTable = clickTable;
		final DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		// 330ms vs 668ms
		final TLongSet usersSet = new TLongHashSet();
		
		// initialise current date as startDate
		long currentDate = dataStartDate;
		long nextDate = currentDate + timeGranularityInSeconds;
				
		outerLoop:
		for (int i = 0; i < costTable.size(); i++) {
			final int dateTime = costTable.getDateTime(i);

			// we ignore the impression if the date is before the current date
			if (dateTime < currentDate)
				continue;

			// add new mapping if after time granularity separator
			while (dateTime > nextDate) {
				if (nextDate == dataEndDate)
					break outerLoop;

				uniquesList.add(usersSet.size());

				usersSet.clear();

				currentDate = nextDate;
				nextDate = currentDate + timeGranularityInSeconds;

				if (nextDate > dataEndDate)
					nextDate = dataEndDate;
			}

			if (dataFilter.test(costTable.getUserData(i))) {
				usersSet.add(costTable.getUserID(i));
			}
		}
		
		// add last entry
		uniquesList.add(usersSet.size());

		// pack
		uniquesList.trimToSize();
				
		return uniquesList;
	}
	
	/*
	 * A user clicks on an ad, but then fails to interact with the website
	 * (typically detected when a user navigates away from the website after a
	 * short time, or when only a single page has been viewed).
	 */
	private final List<Integer> numberOfBounces(int dataFilterIndex) {
		final ArrayList<Integer> bouncesList = new ArrayList<Integer>();
		final DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		int numberOfBounces = 0;
		
		// initialise current date as startDate
		long currentDate = dataStartDate;
		long nextDate = currentDate + timeGranularityInSeconds;
				
		outerLoop:
		for (int i = 0; i < logTable.size(); i++) {
			final int dateTime = logTable.getDateTime(i);
						
			// we ignore the impression if the date is before the current date
			if (dateTime < currentDate)
				continue;
			
			// add new mapping if after time granularity separator
			while (dateTime > nextDate) {
				if (nextDate == dataEndDate)
					break outerLoop;
				
				bouncesList.add(numberOfBounces);
				
				numberOfBounces = 0;
				
				currentDate = nextDate;
				nextDate = currentDate + timeGranularityInSeconds;
				
				if (nextDate > dataEndDate)
					nextDate = dataEndDate;
			}
			
			if (dataFilter.test(clickTable.getUserData(i))) {				
				if (logTable.getPagesViewed(i) > bounceMinimumPagesViewed)
					continue;
				
				final long exitDateTime = logTable.getExitDateTime(i);
				
				if (exitDateTime == DateProcessor.DATE_NULL)
					continue;
				
				if (exitDateTime - dateTime > bounceMinimumSecondsOnPage) {
					continue;
				}
				
				numberOfBounces++;
				
//				if (campaign.getServers().getPagesViewed(i) <= bounceMinimumPagesViewed &&
//						exitDateTime != DateProcessor.DATE_NULL &&
//						exitDateTime - dateTime < bounceMinimumSecondsOnPage) {
//					numberOfBounces++;
//					System.out.println(exitDateTime - dateTime);
//				}
			}
		}
		
		// add last entry
		bouncesList.add(numberOfBounces);

		// pack
		bouncesList.trimToSize();
		
		return bouncesList;
	}
	
	private final List<Integer> numberOfConversions(int dataFilterIndex) {
		final ArrayList<Integer> conversionsList = new ArrayList<Integer>();
		final DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		int numberOfConversions = 0;
		
		// initialise current date as startDate
		long currentDate = dataStartDate;
		long nextDate = currentDate + timeGranularityInSeconds;
				
		outerLoop:
		for (int i = 0; i < logTable.size(); i++) {
			final int dateTime = logTable.getDateTime(i);
						
			// we ignore the impression if the date is before the current date
			if (dateTime < currentDate)
				continue;
			
			// add new mapping if after time granularity separator
			while (dateTime > nextDate) {
				if (nextDate == dataEndDate)
					break outerLoop;
				
				conversionsList.add(numberOfConversions);
				
				numberOfConversions = 0;
				
				currentDate = nextDate;
				nextDate = currentDate + timeGranularityInSeconds;
				
				if (nextDate > dataEndDate)
					nextDate = dataEndDate;
			}
			
			// try to short circuit as expr1 is a direct boolean evaluation
			if (logTable.getConversion(i) && dataFilter.test(clickTable.getUserData(i))) {
				numberOfConversions++;
			}
		}
		
		// add last entry
		conversionsList.add(numberOfConversions);

		// pack
		conversionsList.trimToSize();
		
		return conversionsList;
	}
	
	private final List<Double> costOfRecord(CostTable table, int dataFilterIndex) {
		final ArrayList<Double> costList = new ArrayList<Double>();
		final DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		double costOfImpressions = 0;
		
		// initialise current date as startDate
		long currentDate = dataStartDate;
		long nextDate = currentDate + timeGranularityInSeconds;
		
		outerLoop:
		for (int i = 0; i < table.size(); i++) {
			final int dateTime = table.getDateTime(i);
			
			// we ignore the impression if the date is before the current date
			if (dateTime < currentDate)
				continue;
			
			// add new mapping if after time granularity separator
			while (dateTime > nextDate) {
				if (nextDate == dataEndDate)
					break outerLoop;
				
				costList.add(costOfImpressions);
				
				costOfImpressions = 0;
				
				currentDate = nextDate;
				nextDate = currentDate + timeGranularityInSeconds;
				
				if (nextDate > dataEndDate)
					nextDate = dataEndDate;
			}
			
			if (dataFilter.test(table.getUserData(i))) {
				costOfImpressions += table.getCost(i);
			}
		}
		
		// add last entry
		costList.add(costOfImpressions);

		// pack
		costList.trimToSize();
				
		return costList;
	}
	
	// I'm assuming this is cost of impression and click
	private final List<Double> totalCost(int dataFilterIndex) {
		final List<Double> impressionsCost = costOfRecord(impressionTable, dataFilterIndex);
		final List<Double> clicksCost = costOfRecord(clickTable, dataFilterIndex);
				
		final ArrayList<Double> costList = new ArrayList<Double>(impressionsCost.size());
		
		for (int i = 0; i < impressionsCost.size(); i++)
			costList.add(impressionsCost.get(i) + clicksCost.get(i));

		return costList;
	}
	
	// average clicks per impression
	private final List<Double> clickThroughRate(int dataFilterIndex) {
		final List<Integer> impressionsList = numberOfImpressions(dataFilterIndex);
		final List<Integer> clicksList = numberOfClicks(dataFilterIndex);
		
		final ArrayList<Double> clickThroughRate = new ArrayList<Double>(impressionsList.size());
		
		for (int i = 0; i < impressionsList.size(); i++)
			clickThroughRate.add((double) clicksList.get(i) / (double) impressionsList.get(i));
		
		return clickThroughRate;
	}
	
	
	// The average amount of money spent on an advertising campaign
	// for each acquisition (i.e., conversion).
	private final List<Double> costPerAcquisition(int dataFilterIndex) {
		final List<Integer> conversionList = numberOfConversions(dataFilterIndex);
		final List<Double> costList = totalCost(dataFilterIndex);
		
		final ArrayList<Double> costPerAcquisition = new ArrayList<Double>(conversionList.size());
		
		for (int i = 0; i < conversionList.size(); i++) {
			double metric = costList.get(i) / conversionList.get(i);
			
			if (metric == Double.POSITIVE_INFINITY)
				metric = 0;
			
			costPerAcquisition.add(metric);
		}
		
		return costPerAcquisition;
	}
	
	// The average amount of money spent on an advertising campaign for each click.
	private final List<Double> costPerClick(int dataFilterIndex) {
		final List<Integer> clickList = numberOfClicks(dataFilterIndex);
		final List<Double> costList = totalCost(dataFilterIndex);
				
		final ArrayList<Double> costPerClick = new ArrayList<Double>(clickList.size());
		
		for (int i = 0; i < clickList.size(); i++) {
			if (clickList.get(i) == 0)
				costPerClick.add(0d);
			else
				costPerClick.add(costList.get(i) / clickList.get(i));
		}
		
		return costPerClick;
	}
	
	// The average amount of money spent on an advertising campaign for every one thousand impressions.
	private final List<Double> costPerThousandImpressions(int dataFilterIndex) {
		final List<Integer> impressionsList = numberOfImpressions(dataFilterIndex);
		final List<Double> costsList = totalCost(dataFilterIndex);
		
		final ArrayList<Double> costPerThousandImpressions = new ArrayList<Double>(impressionsList.size());
		
		for (int i = 0; i < impressionsList.size(); i++)
			costPerThousandImpressions.add(costsList.get(i) * 1000 / impressionsList.get(i));
		
		return costPerThousandImpressions;
	}
	
	// The average number of bounces per click.
	private final List<Double> bounceRate(int dataFilterIndex) {
		final List<Integer> bouncesList = numberOfBounces(dataFilterIndex);
		final List<Integer> clicksList = numberOfClicks(dataFilterIndex);
		
		final ArrayList<Double> bounceRates = new ArrayList<Double>(bouncesList.size());
		
		for (int i = 0; i < bouncesList.size(); i++) {
			if (clicksList.get(i) == 0)
				bounceRates.add(0d);
			else
				bounceRates.add((double) bouncesList.get(i) / clicksList.get(i));
		}
		
		return bounceRates;
	}
	
	// Clicks are more valuable than impressions, always
	public final EnumMap<User, Integer> getContextData(int dataFilterIndex) {
		final EnumMap<User, Integer> enumMap = new EnumMap<User, Integer>(User.class);
		final DataFilter dataFilter = dataFilters.get(dataFilterIndex);
		
		final int[] values = new int[User.values().length];
		
		for (int i = 0; i < clickTable.size(); i++) {
			final short userData = clickTable.getUserData(i);
			
			if (!dataFilter.test(userData))
				continue;
			
			for (User u : User.values()) {
				if (User.checkFlag(userData, u)) {
					values[u.ordinal()]++;
				}
			}
		}
		
		for (User u : User.values()) {
			enumMap.put(u, values[u.ordinal()]);
		}
		
		return enumMap;
	}
	
	public final void addDataFilter(DataFilter dataFilter)
	{
		dataFilters.add(dataFilter);
	}
	
	public final void addAllDataFilters(List<DataFilter> dataFilters)
	{
		dataFilters.addAll(dataFilters);
	}
	
	public final List<DataFilter> getAllDataFilters()
	{
		return dataFilters;
	}
	
	public final DataFilter getDataFilter(int dataFilterIndex)
	{
		return dataFilters.get(dataFilterIndex);
	}
}
