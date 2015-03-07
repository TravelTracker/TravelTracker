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
 * @author kdbanman
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
	
	/**
	 * Package protected constructor, intended for use only by DataSource.
	 * 
	 * @param docID UUID document identifier
	 */
	Item(UUID docID) {
		super(docID);
	}
	
	public UUID getClaim() {
		return this.claim;
	}
	public void setClaim(UUID claim) {
		this.claim = claim;
		this.updateObservers(this);
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
		this.updateObservers(this);
	}
	
	public ItemCategory getCategory() {
		return category;
	}
	public void setCategory(ItemCategory category) {
		this.category = category;
		this.updateObservers(this);
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
		this.updateObservers(this);
	}
	
	public Float getAmount() {
		return amount;
	}
	public void setAmmunt(Float amount) {
		this.amount = amount;
		this.updateObservers(this);
	}
	
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
		this.updateObservers(this);
	}
	
	public Receipt getReceipt() {
		return receipt;
	}
	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
		this.updateObservers(this);
	}
	
}
