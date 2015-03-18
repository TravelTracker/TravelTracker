package cmput301w15t07.TravelTracker.model;

import android.graphics.Bitmap;

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
 * Model object for expense Item Receipt data.  (i.e. image)
 * 
 * Setters are not exposed - object attributes are set at construction time.
 * This is so that mutations must be made at the Claim for observer notification
 * and cache dirtying.
 * 
 * @author kdbanman
 *
 */
public class Receipt {
	private Bitmap photo;
	
	public Receipt(Bitmap photo) {
		this.photo = photo;
	}
	
	public Receipt() {
		this(null);  //TODO add a default Bitmap somehow
	}

	/**
	 * Get the photo of the receipt.
	 * @return The photo.
	 */
	public Bitmap getPhoto() {
		return photo;
	}

}
