package cmput301w15t07.TravelTracker.model;

import java.util.Date;
import java.util.UUID;


/**
 * Model class for the Approver Comment.
 * 
 * Setters are not exposed - object attributes are set at construction time.
 * This is so that mutations must be made at the Claim for observer notification
 * and cache dirtying.
 * 
 * @author ryant26,
 * 		   colp
 *
 */
public class ApproverComment {
	private UUID approver;
	private String comment;
	private Date date;
	
	public ApproverComment(UUID approver, String comment, Date date) {
		this.approver = approver;
		this.comment = comment;
		this.date = date;
	}
	
	public UUID getApprover() {
		return approver;
	}

	public String getComment() {
		return comment;
	}

	public Date getDate() {
		return date;
	}

	
}
