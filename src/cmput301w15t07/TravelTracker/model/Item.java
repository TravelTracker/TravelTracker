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

import java.util.Currency;
import java.util.Date;
import java.util.UUID;

/**
 * Model object for individual expense Items made by Users acting as Claimants.
 * Expense Items belong to a Claim.
 * 
 * @author kdbanman
 * @author Braedy Kuzma
 *
 */
public class Item {

	/**
	 * The UUID of the item.
	 */
	private UUID id;
	
	/**
	 * The description of the item.
	 */
	private String description;
	
	/**
	 * The category that this item falls into.
	 */
	private ItemCategory category;
	
	/**
	 * The date the expense was incurred on.
	 */
	private Date date;
	
	/**
	 * The amount of currency exchanged in the item.
	 */
	private float amount;
	
	/**
	 * The currency that was exchanged in the item.
	 */
	private Currency currency;
	
	/**
	 * The item receipt.
	 * Can only be "got", changing the image in the receipt can be done
	 * through the receipt (i.e. receipt.setPhoto())
	 * @see Receipt
	 */
	private Receipt receipt;
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ItemCategory getCategory() {
		return category;
	}
	
	public void setCategory(ItemCategory category) {
		this.category = category;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public float getAmount() {
		return amount;
	}
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	public Receipt getReceipt() {
		return receipt;
	}
}
