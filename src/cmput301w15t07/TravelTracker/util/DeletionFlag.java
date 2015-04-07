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

package cmput301w15t07.TravelTracker.util;

import java.util.Date;

import cmput301w15t07.TravelTracker.model.Document;

public class DeletionFlag<T extends Document> {
	
	private Date date;
	private T toDelete;

    public DeletionFlag(Date date, T toDelete) {
		this.date = date;
		this.toDelete = toDelete;
	}
	
	public DeletionFlag(T toDelete) {
		this(new Date(), toDelete);
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the document to be deleted.  may not be a reference, but will satisfy .equals()
	 */
	public T getToDelete() {
		return toDelete;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((toDelete == null) ? 0 : toDelete.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DeletionFlag))
			return false;
		DeletionFlag<?> other = (DeletionFlag<?>) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (toDelete == null) {
			if (other.toDelete != null)
				return false;
		} else if (!toDelete.equals(other.toDelete))
			return false;
		return true;
	}

}
