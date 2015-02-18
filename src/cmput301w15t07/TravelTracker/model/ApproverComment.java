package cmput301w15t07.TravelTracker.model;

import java.util.Date;


/**
 * Model class for the Approver Comment.
 * 
 * @author ryant26
 *
 */
public class ApproverComment {
	Approver approver;
	String comment;
	Date date;
	
	public Approver getApprover() {
		return approver;
	}
	public void setApprover(Approver approver) {
		this.approver = approver;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
