package core.data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.campaigns.Campaign;
import core.records.Impression;

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
	private LocalDateTime dataStartDate;
	private LocalDateTime dataEndDate;
	
	// the time granularity of this dataprocessor
	private int timeGranularityInSeconds;
	
	// bounce logic
	private int bounceMinimumPagesViewed;
	private int bounceMinimumSecondsOnPage;
	
	// the list of filters to work with
	private List<DataFilter> filters;
	
	
	
	
	// ==== Constructor ====
	
	public DataProcessor(Campaign campaign) {
		this.campaign = campaign;
	}
	

	// ==== Accessors ====
	
	public final Campaign getCampaign() {
		return campaign;
	}
	
	public final void setCampaign(Campaign campaign) {
		final LocalDateTime campaignStartDate = campaign.getStartDate();
		final LocalDateTime campaignEndDate = campaign.getEndDate();
		
		if (dataStartDate == null || dataStartDate.isBefore(campaignStartDate))
			dataStartDate = campaignStartDate;
		
		if (dataEndDate == null || dataEndDate.isAfter(campaignEndDate))
			dataEndDate = campaignEndDate;
		
		this.campaign = campaign;
	}
	
	
	public final LocalDateTime getDataStartDate() {
		return dataStartDate;
	}
	
	public final void setDataStartDate(LocalDateTime dataStartDate) {
		// check if the input start date happens before the campaign start date
		if (dataStartDate.isBefore(campaign.getStartDate()))
			throw new IllegalArgumentException("cannot set data start date before campaign starts");
		
		// update the values if is valid
		this.dataStartDate = dataStartDate;
	}
	
	
	public final LocalDateTime getDataEndDate() {
		return dataEndDate;
	}
	
	public final void setDataEndDAte(LocalDateTime dataEndDate) {
		// check input is not after campaign end date
		if (dataEndDate.isAfter(campaign.getEndDate()))
			throw new IllegalArgumentException("cannot set data end date to after campaign ends");
		
		// update if valid
		this.dataEndDate = dataEndDate;
	}
	
	
	public final int getTimeGranularityInSeconds() {
		return timeGranularityInSeconds;
	}
	
	public final void setTimeGranularityInSeconds(int timeGranularityInSeconds) {
		// check time granularity is at least 1
		if (timeGranularityInSeconds < 1)
			throw new IllegalArgumentException("cannot have time granularity below 1 second");
		
		// store the time difference to compute min/max bounds
		final long timeDifference = ChronoUnit.SECONDS.between(dataStartDate, dataEndDate);
		
		// we want a minimum number of data points
		// if time granularity is larger than this number, then we won't have enough nodes
		if (timeGranularityInSeconds  > timeDifference / MINIMUM_NUMBER_OF_NODES)
			timeGranularityInSeconds = (int) (timeDifference / MINIMUM_NUMBER_OF_NODES);
		
		// converse to above logic
		if (timeGranularityInSeconds < timeDifference / MAXIMUM_NUMBER_OF_NODES)
			timeGranularityInSeconds = (int) (timeDifference / MAXIMUM_NUMBER_OF_NODES);
		
		// TODO remove, just throwing error if we reach this unreachable state
		if (timeGranularityInSeconds < 1 || timeGranularityInSeconds >=  timeDifference)
			throw new IllegalArgumentException("something happened in " + this.getClass().getName());
		
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
	
	
	public final Map<LocalDateTime, Integer> numberOfImpressions() {
		final Map<LocalDateTime, Integer> impressionsMap = new HashMap<LocalDateTime, Integer>();
		int count = 0;
		for (Impression impression : campaign.getImpressions()) {
			count++;
		}
		// TODO read logic here
		System.out.println(count);
		
		return impressionsMap;
	}
	
	public final Map<LocalDateTime, Integer> numberOfClicks() {
		final Map<LocalDateTime, Integer> clicksMap = new HashMap<LocalDateTime, Integer>();
		
		// TODO read logic here
		
		return clicksMap;
	}
	
	public final Map<LocalDateTime, Integer> numberOfUniques() {
		final Map<LocalDateTime, Integer> uniquesMap = new HashMap<LocalDateTime, Integer>();
		
		// TODO read logic here
		
		return uniquesMap;
	}

	public final Map<LocalDateTime, Integer> numberOfBounces() {
		final Map<LocalDateTime, Integer> bouncesMap = new HashMap<LocalDateTime, Integer>();
		
		// TODO read logic here
		
		return bouncesMap;
	}
	
	public final Map<LocalDateTime, Integer> numberOfConversions() {
		final Map<LocalDateTime, Integer> conversionsMap = new HashMap<LocalDateTime, Integer>();
		
		// TODO read logic here
		
		return conversionsMap;
	}
	
	public final Map<LocalDateTime, Double> totalCost() {
		final Map<LocalDateTime, Double> costMap = new HashMap<LocalDateTime, Double>();
		
		// TODO read logic here
		
		return costMap;
	}
	
	public final Map<LocalDateTime, Double> CTR() {
		return null;
	}
	
	public final Map<LocalDateTime, Double> CPA() {
		return null;
	}
	
	public final Map<LocalDateTime, Double> CPC() {
		return null;
	}
	
	public final Map<LocalDateTime, Double> CPM() {
		return null;
	}
	
	public final Map<LocalDateTime, Integer> bounceRate() {
		return null;
	}
}
