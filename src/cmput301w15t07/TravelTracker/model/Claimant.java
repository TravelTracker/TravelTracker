package cmput301w15t07.TravelTracker.model;

import java.util.ArrayList;

public class Claimant extends User{
	ArrayList<Tag> tags;

	public ArrayList<Tag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
	
}
