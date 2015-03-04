package cmput301w15t07.TravelTracker.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.Observable;

public class DummyDataSource extends Observable<DummyDataSource> implements DataSource {

	private HashMap<UUID, Claim> claims;
	private HashMap<UUID, User> users;
	private HashMap<UUID, Item> items;
	private HashMap<UUID, Tag> tags;

	public DummyDataSource() {
		claims = new HashMap<UUID, Claim>();
		users = new HashMap<UUID, User>();
		items = new HashMap<UUID, Item>();
		tags = new HashMap<UUID, Tag>();
		
	}
	@Override
	public void addUser(ResultCallback<User> callback) {
		User user = new Claimant(UUID.randomUUID());
		
		users.put(user.getUUID(), user);
		
		//TODO add self as observer
		
		callback.onResult(user);
	}
	
	@Override
	public void addClaim(User user, ResultCallback<Claim> callback) {
		Claim claim = new Claim(UUID.randomUUID());
		
		// TODO associate claim with user
		// TODO possibly add self as observer
		
		callback.onResult(claim);
	}

	@Override
	public void addItem(Claim claim, ResultCallback<Item> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTag(User user, ResultCallback<Tag> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUser(UUID id, ResultCallback<User> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteClaim(UUID id, ResultCallback<Claim> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteItem(UUID id, ResultCallback<Item> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTag(UUID id, ResultCallback<Tag> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getUser(UUID id, ResultCallback<User> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getClaim(UUID id, ResultCallback<Claim> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getItem(UUID id, ResultCallback<Item> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getTag(UUID id, ResultCallback<Tag> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllClaims(UUID id, ResultCallback<Collection<Claim>> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllItems(UUID id, ResultCallback<Collection<Item>> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllTags(UUID id, ResultCallback<Collection<Tag>> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyDataChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Document> getDirtyDocuments() {
		// TODO Auto-generated method stub
		return null;
	}

}
