package cmput301w15t07.TravelTracker.model;

import java.util.Currency;
import java.util.Date;
import java.util.UUID;

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

/**
 * Model object for individual expense Items made by Users acting as Claimants.
 * Expense Items belong to a Claim.
 * 
 * @author kdbanman, braedy
 *
 */
public class Item extends Document {
	private UUID claim;
	private String description;
	private ItemCategory category;
	private Date date;
	private Float amount;
	private Currency currency;
	private Receipt receipt;
	private boolean isComplete;

	/**
	 * Package protected constructor, intended for use only by DataSource.
	 * 
	 * @param docID UUID document identifier
	 */
	Item(UUID docID) {
		super(docID);
		
		claim = null;
		
		// Empty description
		description = "";
		
		category = null;
		
		date = new Date();
		
		// Start at 0 of unknown currency
		amount = 0.f;
		currency = null;
		
		receipt = null;
		
		isComplete = true;
		
	}
	
	/**
	 * Get the claim to which this belongs.
	 * @return The claim's UUID.
	 */
	public UUID getClaim() {
		return this.claim;
	}
	
	/**
	 * Set the claim to which this belongs.
	 * @param claim The claim's UUID.
	 */
	public void setClaim(UUID claim) {
		this.claim = claim;
		this.updateObservers(this);
	}
	
	/**
	 * Get the item description.
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the item description.
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
		this.updateObservers(this);
	}
	
	/**
	 * Get the item's category.
	 * @return The category.
	 */
	public ItemCategory getCategory() {
		return category;
	}
	
	/**
	 * Set the item's category.
	 * @param category The category.
	 */
	public void setCategory(ItemCategory category) {
		this.category = category;
		this.updateObservers(this);
	}
	
	/**
	 * Get the date on which the expense item occurred.
	 * @return The date.
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Set the date on which the expense item occurred.
	 * @param date The date.
	 */
	public void setDate(Date date) {
		this.date = date;
		this.updateObservers(this);
	}
	
	/**
	 * Get the expense amount.
	 * @return The amount.
	 */
	public Float getAmount() {
		return amount;
	}
	
	/**
	 * Set the expense amount.
	 * @param amount The amount.
	 */
	public void setAmount(Float amount) {
		this.amount = amount;
		this.updateObservers(this);
	}
	
	/**
	 * Get the currency type of the expense.
	 * @return The currency.
	 */
	public Currency getCurrency() {
		return currency;
	}
	
	/**
	 * Set the currency type of the expense.
	 * @param currency The currency.
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
		this.updateObservers(this);
	}
	
	/**
	 * Get the attached Receipt.
	 * @return The receipt.
	 */
	public Receipt getReceipt() {
		return receipt;
	}
	
	/**
	 * Set the attached Receipt.
	 * @param receipt The receipt.
	 */
	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
		this.updateObservers(this);
	}
	
	/**
	 * Get the status of the expense item.
	 * @return The status.
	 */
	public boolean isComplete() {
		return isComplete;
	}
	
	/**
	 * Set the status of the expense item.
	 * @param isComplete The status.
	 */
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
		this.updateObservers(this);
	}
}
