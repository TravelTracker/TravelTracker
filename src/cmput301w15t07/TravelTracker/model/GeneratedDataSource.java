package cmput301w15t07.TravelTracker.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import android.util.Log;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;

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
 * Mock data source extending InMemoryDataSource that creates data upon User creation.
 * Most useful in testing UI.
 * 
 * @author braedy
 *
 */
public class GeneratedDataSource extends InMemoryDataSource {
	
	/** 
	 * Overrides addUser to generate starting data for a new User.
	 * @see cmput301w15t07.TravelTracker.model.InMemoryDataSource#addUser(cmput301w15t07.TravelTracker.serverinterface.ResultCallback)
	 */
	@Override
	public void addUser(ResultCallback<User> callback) {

		Random r = new Random();

		User user = new User(UUID.randomUUID());
		user.addObserver(this);
		internalAddUser(user);
		
		// Add ten random tags
		for (int i = 0; i < 10; ++i) {
		    Tag t = new Tag(UUID.randomUUID());
		    
		    // Set data
		    t.setUser(user.getUUID());
		    t.setTitle(getRandomString(r, 5, 10));
		    
		    internalAddTag(t);
		}
		
		// Want 10 random claims
		for (int i = 0; i < 10; ++i) {
			// Create claim and set data
			Claim claim = new Claim(UUID.randomUUID());
			claim.setUser(user.getUUID());
			
			// Random start time (up to 10 days ago)
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -r.nextInt(10));
			claim.setStartDate(calendar.getTime());
			
			// Random end time (up to 10 days from now)
			calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, r.nextInt(10));
			claim.setEndDate(calendar.getTime());
			
			// Set status
			claim.setStatus(
					Status.values()[r.nextInt(Status.values().length)]);
			
			// Set approver if status is not in progress
			if (claim.getStatus() != Status.IN_PROGRESS) {
				claim.setApprover(user.getUUID());
			}
			
			// Set tags
			ArrayList<UUID> tagIDs = new ArrayList<UUID>(tags.keySet());
		    int tagCount = r.nextInt(5);
		    if (tagCount > 0) {
		        ArrayList<UUID> tags = new ArrayList<UUID>();
		        
    		    for (int j = 0; j < tagCount; ++j) {
    		        int tagIndex = r.nextInt(tagIDs.size());
    		        tags.add(tagIDs.get(tagIndex));
    		    }
    		    
    		    claim.setTags(tags);
		    }
			
			internalAddClaim(claim);
			
			// With 10 items each
			for (int j = 0; j < 10; ++j) {
				
				Item item = new Item(UUID.randomUUID(), claim.getUUID());
				
				item.setAmount(r.nextFloat()*(10+r.nextInt(6))*r.nextInt(4)); // Set amount
				
				// Set currency
				Currency curr = null;
				for(int k = 0; k < 100; ++k) {
					try {
						 curr = Currency.getInstance(
								 Locale.getAvailableLocales()[r.nextInt(Locale.getAvailableLocales().length)]);
					}
					catch (IllegalArgumentException e) {
						// Some locales aren't supported, just try again
						continue;
					}
					
					// Success!
					break;
				}
				
				// If we make it through on iterations..
				if (curr == null)  {
					Log.w("GeneratedDataSource", "Couldn't get a good currency, defaulting to CAD");
					curr = Currency.getInstance(Locale.CANADA);
				}
				
				item.setCurrency(curr);

				// Random time (10 days before today to 10 after)
				calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, r.nextInt(20) - 10);
				
				item.setDate(calendar.getTime());
				item.setDescription(getRandomString(r, 20, 76)); // Description 20-75
	            
	            switch(r.nextInt(2)) {
	            case 0:
	                item.setComplete(true);
	                break;
	            case 1:
	                item.setComplete(false);
	                break;
	            default:
	                item.setComplete(true);
	            }
				
				// Set receipt, can't generate a receipt right now
				//item.setReceipt(receipt);
				internalAddItem(item);
			}
			
			//Add some destinations
			for (int k = 0; k < (new Random()).nextInt(5); k++){
				claim.getDestinations().add(new Destination(getRandomString(new Random(), 5, 10), "A test Reason"));
			}
			
			// Add some comments (from the same user for simplicity's sake, though this is impossible)
			for (int l = 0; l < 5; l++) {
				calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, r.nextInt(20) - 10);
				claim.getComments().add(new ApproverComment(getRandomString(r, 50, 200), calendar.getTime()));
			}
		}
		
		callback.onResult(user);
	}

	
	/**
	 * Generate a random string of chars A-Z,a-z,0-9
	 * 
	 * http://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
	 * 
	 * @param minLength Min length of the string generated.
	 * @param maxLength Max length of the string generated
	 * @return Random string of length [minLength, maxLength)
	 */
	private String getRandomString(Random r, int minLength, int maxLength) {
		// Start chars. Two spaces for more probability of spaces. Does it matter really? No.
        final String CHARS = "  ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder str = new StringBuilder();
        int length = minLength + r.nextInt(maxLength-minLength);
        while (str.length() < length) {
            int index = r.nextInt(CHARS.length());
            str.append(CHARS.charAt(index));
        }
        
        return str.toString();
    }
}
