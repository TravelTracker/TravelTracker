package cmput301w15t07.TravelTracker.serverinterface;

public class Constants {
	
	public static final String CONNECTION_URL = "http://cmput301.softwareprocess.es:8080/";
	public static final String INDEX = "cmput301w15t07";
	
	
	public enum Type {
		USER("User"),
		CLAIM("Claim"),
		ITEM("Item"),
		TAG("Tag");
		
		private final String name;
		
		private Type(String s){
			name = s;
		}
		
		/**
		 * This method returns the Status instance corresponding to the passed string.
		 * @param text The text to search for.
		 * @return The matching Document Type.
		 */
		public static Type fromString(String text) {
		    if (text != null) {
		      for (Type t : Type.values()) {
		        if (text.equalsIgnoreCase(t.toString())) {
		          return t;
		        }
		      }
		    }
		    return null;
		 }
		
		public String toString(){
			return name;
		}
	}
}
