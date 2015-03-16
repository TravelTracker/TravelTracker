package cmput301w15t07.TravelTracker.model;

/*
 *   Copyright 2015 Kirby Banman,
 *                  Stuart Bildfell,
 *                  Elliot Colp,
 *                  Christian Ellinger,
 *                  Braedy Kuzma,
 *                  Ryan Thornhill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.util.Date;

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
	private String comment;
	private Date date;
	
	public ApproverComment(String comment, Date date) {
		this.comment = comment;
		this.date = date;
	}

	/**
	 * Get the comment string.
	 * @return The comment string.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the date on which the comment was made.
	 * @return The comment date.
	 */
	public Date getDate() {
		return date;
	}

	
}
