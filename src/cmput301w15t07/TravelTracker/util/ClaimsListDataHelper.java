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

import java.util.ArrayList;
import java.util.Collection;

import android.util.SparseArray;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.MultiCallback;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;

/**
 * Contains helper functions for Claim lists.
 * 
 * @author ryant26
 */
public class ClaimsListDataHelper {
	
	private static final int CLAIMS_ID = 0;
	private static final int ITEMS_ID = 1;
	private static final int USER_ID = 2;
	private static final int USERS_ID = 3;
	private static final int TAGS_ID = 4;
	
	private UserRole userRole;
	private ResultCallback<InitialData> callback;
	
	/**
	 * This method is used to retrieve Claims, Items, and the current User object
	 * for use in the claimsList activity
	 * 
	 * @param callback The callback for when data is retrieved.
	 * @param userData The current user's data.
	 * @param ds The data source.
	 */
	public void getInitialData(ResultCallback<InitialData> callback, UserData userData, DataSource ds){
		this.userRole = userData.getRole();
		this.callback = callback;
		MultiCallback mc = new MultiCallback(new initalDataCallback());
		ds.getAllClaims(mc.<Collection<Claim>>createCallback(CLAIMS_ID));
		ds.getAllItems(mc.<Collection<Item>>createCallback(ITEMS_ID));
		ds.getUser(userData.getUUID(), mc.<User>createCallback(USER_ID));
		ds.getAllUsers(mc.<Collection<User>>createCallback(USERS_ID));
		ds.getAllTags(mc.<Collection<Tag>>createCallback(TAGS_ID));
		mc.ready();
	}
	
	private InitialData buildInitialData(SparseArray<Object> array, UserRole role){
		InitialData data = new InitialData();
		data.setUser((User)array.get(USER_ID));
		data.setClaims(getClaimsForUser((Collection<Claim>)array.get(CLAIMS_ID), data.getUser(), role));
		data.setItems(getItemsForClaims(data.getClaims(), (Collection<Item>) array.get(ITEMS_ID)));
		data.setUsers((Collection<User>)array.get(USERS_ID));
		data.setTags((Collection<Tag>)array.get(TAGS_ID));
		
		return data;
	}
	
	private static ArrayList<Claim> getClaimsForUser(Collection<Claim> claims, User user, UserRole role){
		ArrayList<Claim> outClaims = new ArrayList<Claim>();
		
		
		for (Claim c : claims){
			if (role.equals(UserRole.APPROVER)) {
				if (!c.getUser().equals(user.getUUID())){
					if (c.getStatus().equals(Status.SUBMITTED)){
						outClaims.add(c);
					}
				}
			} else if (role.equals(UserRole.CLAIMANT)){
				if (c.getUser().equals(user.getUUID())){
					outClaims.add(c);
				}
			}
		}
		
		return outClaims;
	}
	
	private static ArrayList<Item> getItemsForClaims(Collection<Claim> claims, Collection<Item> items){
		ArrayList<Item> outItems = new ArrayList<Item>();
		
		for (Item i: items){
			for (Claim c : claims){
				if (i.getClaim().equals(c.getUUID())){
					outItems.add(i);
					break;
				}
			}
		}
		return outItems;
	}
	
	public class InitialData {
		private User user;
		private Collection<Claim> claims;
		private Collection<Item> items;
		private Collection<User> users;
		private Collection<Tag> tags;
		
		public User getUser() {
			return user;
		}
		public Collection<Claim> getClaims() {
			return claims;
		}
		public Collection<Item> getItems() {
			return items;
		}
		public Collection<User> getUsers() {
			return users;
		}
		public Collection<Tag> getTags() {
			return tags;
		}
		
		private void setUser(User user) {
			this.user = user;
		}
		private void setClaims(Collection<Claim> claims) {
			this.claims = claims;
		}
		private void setItems(Collection<Item> items) {
			this.items = items;
		}
		private void setUsers(Collection<User> users){
			this.users = users;
		}
		private void setTags(Collection<Tag> tags){
			this.tags = tags;
		}
	}
	
	class initalDataCallback implements ResultCallback<SparseArray<Object>>{
		
		@Override
		public void onResult(SparseArray<Object> result) {
			callback.onResult(buildInitialData(result, userRole));
		}

		@Override
		public void onError(String message) {
			callback.onError(message);
		}
		
	}
}