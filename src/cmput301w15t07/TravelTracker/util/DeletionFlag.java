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

public class DeletionFlag {
	
	private Date date;
	private Document toDelete;
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the document to be deleted.  may not be a reference, but will satisfy .equals()
	 */
	public Document getToDelete() {
		return toDelete;
	}

	public DeletionFlag(Date date, Document toDelete) {
		this.date = new Date();
		this.toDelete = toDelete;
	}
	
	public DeletionFlag(Document toDelete) {
		this(new Date(), toDelete);
	}
	
	/**
	 * Private no-args constructor to please GSON
	 */
	private DeletionFlag() {
		this(null, null);
	}

}
