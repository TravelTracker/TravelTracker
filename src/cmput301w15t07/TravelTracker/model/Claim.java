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
import java.util.UUID;

/**
 * Model object for Claim made by Users acting as Claimants.
 * 
 * @author kdbanman
 * @author Braedy Kuzma
 *
 */
public class Claim {
	/**
	 * The name or title of the claim.
	 */
	private String name;
	
	/**
	 * The UUID of the claim.
	 */
	private UUID id;
	
	/**
	 * The list of tags associated with this claim.
	 */
	private TagList tags;
	
	/**
	 * The date the claim starts at.
	 */
	private Date startDate;
	
	/**
	 * The date the claim ends at.
	 */
	private Date endDate;
	
	/**
	 * The list of destinations associated with this claim.
	 */
	private DestinationList destinations;
	
	/**
	 * The list of items associated with this claim.
	 */
	private ItemList items;
	
	/**
	 * The list of comments placed on this claim.
	 */
	private ApproverCommentList comments;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public UUID getId() {
		return id;
	}

	public TagList getTags() {
		return tags;
	}

	public DestinationList getDestinations() {
		return destinations;
	}

	public ItemList getItems() {
		return items;
	}

	public ApproverCommentList getComments() {
		return comments;
	}
	
	
}
