package cmput301w15t07.TravelTracker.serverinterface;

/**
 * Enum for results of a data merge from the ElasticSearch server.
 * 
 * @author kdbanman
 */
public enum MergeResult {
	CHANGED,
	OVERRIDDEN,
	NOT_FOUND;
}
