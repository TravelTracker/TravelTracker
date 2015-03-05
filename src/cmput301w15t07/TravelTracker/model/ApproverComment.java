package cmput301w15t07.TravelTracker.model;

import java.util.Date;


/**
 * Model class for the Approver Comment.
 * 
 * Setters are not exposed - object attributes are set at construction time.
 * This is so that mutations must be made at the Claim for observer notification
 * and cache dirtying.
 * 
 * @author ryant26
 *
 */
public class ApproverComment {
	private Approver approver;
	private String comment;
	private Date date;
	
	public ApproverComment(Approver approver, String comment, Date date) {
		this.approver = approver;
		this.comment = comment;
		this.date = date;
	}
	
	public Approver getApprover() {
		return approver;
	}

	public String getComment() {
		return comment;
	}

	public Date getDate() {
		return date;
	}

	
}
