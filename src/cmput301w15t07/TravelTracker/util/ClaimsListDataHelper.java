package cmput301w15t07.TravelTracker.util;

import java.util.ArrayList;
import java.util.Collection;

import android.util.SparseArray;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.MultiCallback;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;

public class ClaimsListDataHelper {
	
	private static final int CLAIMS_ID = 0;
	private static final int ITEMS_ID = 0;
	private static final int USER_ID = 0;
	
	private UserRole userRole;
	private ResultCallback<InitialData> callback;
	
	
	public void getInitialData(ResultCallback<InitialData> callback, UserRole userRole){
		this.userRole = userRole;
		this.callback = callback;
		new MultiCallback(new initalDataCallback());
	}
	
	private InitialData buildInitialData(SparseArray<Object> array, UserRole role){
		InitialData data = new InitialData();
		data.setUser((User)array.get(USER_ID));
		data.setClaims(getClaimsForUser((Collection<Claim>)array.get(CLAIMS_ID), data.getUser(), role));
		data.setItems(getItemsForClaims(data.getClaims(), (Collection<Item>) array.get(ITEMS_ID)));
		return data;
	}
	
	private static ArrayList<Claim> getClaimsForUser(Collection<Claim> claims, User user, UserRole role){
		ArrayList<Claim> outClaims = new ArrayList<Claim>();
		
		
		for (Claim c : claims){
			if (role.equals(UserRole.APPROVER)) {
				if (!c.getUser().equals(user.getUUID())){
					outClaims.add(c);
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
	
	class InitialData {
		private User user;
		private Collection<Claim> claims;
		private Collection<Item> items;
		
		public User getUser() {
			return user;
		}
		public Collection<Claim> getClaims() {
			return claims;
		}
		public Collection<Item> getItems() {
			return items;
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