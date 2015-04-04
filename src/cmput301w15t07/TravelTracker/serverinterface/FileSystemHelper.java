package cmput301w15t07.TravelTracker.serverinterface;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.util.GsonIOManager;
import cmput301w15t07.TravelTracker.util.PersistentList;

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
 * An interface to the local file system to cache data when server connection is lost.
 * 
 * @author kdbanman
 */
public class FileSystemHelper implements ServerHelper {

	private static final String USERS_FILENAME = "cached_users.json";
	private static final String CLAIMS_FILENAME = "cached_claims.json";
	private static final String ITEMS_FILENAME = "cached_items.json";
	private static final String TAGS_FILENAME = "cached_tags.json";
	
	private Context ctx;
	
	private HashMap<Class<? extends Document>, PersistentList<UUID>> savedDocs;
	
	/**
	 * 
	 * @param ctx The application context for File IO and Toast warnings.
	 */
	public FileSystemHelper(Context ctx) {
		this.ctx = ctx;
		
		savedDocs = new HashMap<Class<? extends Document>, PersistentList<UUID>>();
		
		savedDocs.put(User.class, new PersistentList<UUID>(USERS_FILENAME, ctx));
		savedDocs.put(Claim.class, new PersistentList<UUID>(CLAIMS_FILENAME, ctx));
		savedDocs.put(Item.class, new PersistentList<UUID>(ITEMS_FILENAME, ctx));
		savedDocs.put(Tag.class, new PersistentList<UUID>(TAGS_FILENAME, ctx));
	}

	@Override
	public <T extends Document> void deleteDocuments(Collection<T> documents)
			throws Exception {
		for (Document doc : documents) {
			deleteDocument(doc);
		}
		
	}

	@Override
	public Collection<Claim> getClaims(UUID user) throws Exception {
		Collection<Claim> claims = this.<Claim>loadAll(savedDocs.get(Claim.class));
		ArrayList<Claim> userClaims = new ArrayList<Claim>();
		for (Claim claim : claims) {
			if (claim.getUser().equals(user))
				userClaims.add(claim);
		}
		return userClaims;
	}

	@Override
	public Collection<Item> getExpenses(UUID claim) throws Exception {
		Collection<Item> items = this.<Item>loadAll(savedDocs.get(Item.class));
		ArrayList<Item> claimItems = new ArrayList<Item>();
		for (Item item : items) {
			if (item.getClaim().equals(claim))
				claimItems.add(item);
		}
		return claimItems;
	}

	@Override
	public Collection<Tag> getTags(UUID user) throws Exception {
		Collection<Tag> tags = this.<Tag>loadAll(savedDocs.get(Tag.class));
		ArrayList<Tag> userTags = new ArrayList<Tag>();
		for (Tag tag : tags) {
			if (tag.getUser().equals(user))
				userTags.add(tag);
		}
		return userTags;
	}

	@Override
	public User getUser(String name) throws Exception {
		Collection<User> users = this.<User>loadAll(savedDocs.get(User.class));
		for (User user : users) {
			if (user.getUserName().equals(name))
				return user;
		}
		warn("Could not find user " + name);
		return null;
	}

	@Override
	public <T extends Document> void saveDocuments(Collection<T> documents)
			throws Exception {
		for (Document doc : documents) {
			saveDocument(doc);
		}
	}
	
	
	private <T extends Document> Collection<T> loadAll(Collection<UUID> documents) {
		ArrayList<T> docs = new ArrayList<T>();
		
		for (UUID id : documents) {
			T doc = this.<T>loadDoc(id.toString());
			if (doc != null)
				docs.add(doc);
		}
		return docs;
	}
	
	private <T extends Document> T loadDoc(String filename) {
		GsonIOManager gson = new GsonIOManager(ctx);
		try {
			return gson.<T>load(filename);
		} catch (FileNotFoundException e) {
			warn("Could not find cached " + filename + " to load.");
		} catch (JsonSyntaxException e) {
			warn("Cached " + filename + " is incorrect type.");
		}
		return null;
	}

	/**
	 * save document to a file named the uuid and add the UUID to the saved list.
	 * 
	 * @param doc document to save.
	 */
	private <T extends Document> void saveDocument(T doc) {
		GsonIOManager gson = new GsonIOManager(ctx);
		gson.<T>save(doc, doc.getUUID().toString());
		savedDocs.get(doc.getClass()).add(doc.getUUID());
	}
	
	/**
	 * delete document file named the_uuid.json and remove the UUID from the saved list.
	 * 
	 * @param doc document to delete.
	 */
	private <T extends Document> void deleteDocument(T doc) {	
		if (savedDocs.get(doc.getClass()).remove(doc.getUUID())) {
			if (!ctx.deleteFile(doc.getUUID().toString()))
				warn("Could not find cached " + doc.getUUID().toString() + " to delete.");
		} else {
			Log.i("FileSystemHelper", "delete called on nonexistent document " + doc.getUUID().toString());
		}
	}
	
	private void warn(String msg) {
		Log.e("FileSystemHelper", msg);
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}
}
