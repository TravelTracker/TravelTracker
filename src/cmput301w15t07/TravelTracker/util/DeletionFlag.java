package cmput301w15t07.TravelTracker.util;

import java.util.Date;

import cmput301w15t07.TravelTracker.model.Document;

public class DeletionFlag {
	
	private Date date;
	private Document toDelete;
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the document to be deleted.  may not be a reference, but will satisfy .equals()
	 */
	public Document getToDelete() {
		return toDelete;
	}

	public DeletionFlag(Date date, Document toDelete) {
		this.date = new Date();
		this.toDelete = toDelete;
	}
	
	public DeletionFlag(Document toDelete) {
		this(new Date(), toDelete);
	}
	
	/**
	 * Private no-args constructor to please GSON
	 */
	private DeletionFlag() {
		this(null, null);
	}

}
