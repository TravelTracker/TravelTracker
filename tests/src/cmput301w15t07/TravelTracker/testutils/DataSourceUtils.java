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

package cmput301w15t07.TravelTracker.testutils;

import java.util.UUID;

import android.test.AndroidTestCase;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;

public class DataSourceUtils extends AndroidTestCase{
	
	/**
	 * Simply adds a user to the passed data source
	 * @param name - user name
	 * @param ds - datasource
	 * @return User object
	 */
	public static User addUser(String name, DataSource ds){
		SynchronizedResultCallback<User> userCB = new SynchronizedResultCallback<User>();
		ds.addUser(userCB);
		User user = getData(userCB);
		user.setUserName(name);
		return user;
	}
	
	/**
	 * Convenience method to get some a CLAIMANT UserData obj
	 * @param name
	 * @return UserData obj
	 */
	public static UserData getUserDataClaimant(String name){
		return getUserData(name, UserRole.CLAIMANT);
	}
	
	/**
	 * Convenience method to get an APPROVER UserData obj
	 * @param name
	 * @return UserData obj
	 */
	public static UserData getUserDataApprover(String name){
		return getUserData(name, UserRole.APPROVER);
	}
	
	/**
	 * Convenience method to add an empty claim to the data source
	 * 
	 * @param user the user that the claim belongs to
	 * @param ds the data source to add the claim to
	 * @return the newly added claim
	 */
	public static Claim addEmptyClaim(User user, DataSource ds){
		SynchronizedResultCallback<Claim> claimCB = new SynchronizedResultCallback<Claim>();
		ds.addClaim(user, claimCB);
		return getData(claimCB);
	}
	
	public static Item addEmptyItem(Claim claim, DataSource ds){
		SynchronizedResultCallback<Item> itemCB = new SynchronizedResultCallback<Item>();
		ds.addItem(claim, itemCB);
		return getData(itemCB);
	}
	
	public static Tag addEmptyTag(User user, DataSource ds){
		SynchronizedResultCallback<Tag> tagCB = new SynchronizedResultCallback<Tag>();
		ds.addTag(user, tagCB);
		return getData(tagCB);
	}
	
	public static void deleteClaim(Claim claim, DataSource ds){
		SynchronizedResultCallback<Void> claimCB = new SynchronizedResultCallback<Void>();
		ds.deleteClaim(claim.getUUID(), claimCB);
	}
	
	/**
	 * Convenience method to get the data out of a synchronized Result callback. Asserts
	 * that waiting for result was successful. Handles the interrupted exception (Will fail the test).
	 * @param callback
	 * @return data
	 */
	public static <T> T getData(SynchronizedResultCallback<T> callback) {
		try{
			boolean success = callback.waitForResult();
			assertTrue("callback waiting failed", success);
			return callback.getResult();
		} catch (InterruptedException e){
			fail("failed to get data from data source for class: " + callback.toString());
		}
		return null;
	}
	
	private static UserData getUserData(String name, UserRole role){
		return new UserData(UUID.randomUUID(), "name", role);
	}
}
