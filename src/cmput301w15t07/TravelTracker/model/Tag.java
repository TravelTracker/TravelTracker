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

package cmput301w15t07.TravelTracker.model;

import java.util.UUID;

import cmput301w15t07.TravelTracker.serverinterface.Constants.Type;

/**
 * Model object for Claim Tags for Claimant use.
 * 
 * @author kdbanman,
 *         therabidsquirel
 *
 */
public class Tag extends Document {
	private UUID user;
	private String title;
	
	/**
	 * Package protected constructor, intended for use only by DataSource.
	 * 
	 * @param docID UUID document identifier
	 */
	Tag(UUID docID, UUID userID) {
		super(docID);
		setType(Type.TAG);
		
		user = userID;
		title = "";
	}
	
	/**
	 * Private no-args constructor for GSON.
	 */
	@SuppressWarnings("unused")
	private Tag() {
		this(UUID.randomUUID(), null);
	}
	
	/**
	 * Get the user to which this belongs.
	 * @return The user's UUID.
	 */
	public UUID getUser() {
		return this.user;
	}
	
	/**
	 * Set the user to which this belongs.
	 * @param user The user's UUID.
	 */
	public void setUser(UUID user) {
		this.user = user;
		this.<Tag>hasChanged(this);
	}
	
	/**
	 * Get the tag's title.
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the tag's title.
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
		this.<Tag>hasChanged(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Tag))
			return false;
		Tag other = (Tag) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	protected boolean mergeFrom(Document sourceDoc) {
		if (!(sourceDoc instanceof Tag))
			return false;
		Tag sourceItem = (Tag) sourceDoc;
		boolean changed = false;
		
		if (this.title != null) {
			if (!this.title.equals(sourceItem.title)) {
				changed |= true;
				this.title = sourceItem.title;
			}
		} else {
			// attribute is null, if source is not null then set it
			if (!(sourceItem.title == null)) {
				changed |= true;
				this.title = sourceItem.title;
			}
		}
		
		if (this.user != null) {
			if (!this.user.equals(sourceItem.user)) {
				changed |= true;
				this.user = sourceItem.user;
			}
		} else {
			// attribute is null, if source is not null then set it
			if (!(sourceItem.user == null)) {
				changed |= true;
				this.user = sourceItem.user;
			}
		}
		
		return changed;
	}
}
