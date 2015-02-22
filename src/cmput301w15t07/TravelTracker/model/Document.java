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

import java.util.UUID;

/**
* Model object corresponding to storage documents.  Used to track cache/remote
* synchronization status.
* 
* @author kdbanman
*
*/
public abstract class Document {

	// A new document may be synced, but dirty is a safer default.
	private boolean dirty = true;
	
	private UUID docID;
	
	public Document(UUID docID) {
		this.docID = docID;
	}
	
	/**
	 * @return Document/object identifier.
	 */
	public UUID getUUID() {
		return docID;
	}
	
	/** 
	 * @return Whether or not the Document has been synced.
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * Flag document as modified and not yet synchronized.
	 */
	public void setDirty() {
		dirty = true;
	}
	
	/**
	 * Flag document as synchronized.
	 */
	public void setClean() {
		dirty = false;
	}
}
