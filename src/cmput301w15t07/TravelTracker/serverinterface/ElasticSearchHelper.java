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

package cmput301w15t07.TravelTracker.serverinterface;

import java.util.Collection;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.serverinterface.Constants.Type;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;

import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ElasticSearchHelper implements ServerHelper{
	private JestClient conn;
	
	public ElasticSearchHelper(){
		JestClientFactory factory = new JestClientFactory();
		factory.setDroidClientConfig(new DroidClientConfig.Builder(Constants.CONNECTION_URL).build());
		
		conn = factory.getObject();
	}

	@Override
	public <T extends Document> void deleteDocuments(Collection<T> documents) throws Exception {
		Builder bulkBuilder = new Bulk.Builder();
		bulkBuilder.defaultIndex(Constants.INDEX);
		
		for (Document d : documents){
			bulkBuilder.addAction(new Delete.Builder(d.getUUID().toString())
			.index(Constants.INDEX)
			.type(d.getType().toString()).build());
		}
		
		conn.execute(bulkBuilder.build());
	}

	@Override
	public Collection<Claim> getClaims(UUID user) throws Exception {
		String query = getQueryString("user", user.toString());
		Search search = getSearch(query, Constants.Type.CLAIM);
		return runSearch(search, Claim.class);
	}

	@Override
	public Collection<Item> getExpenses(UUID claim) throws Exception {
		String query = getQueryString("claim", claim.toString());
		Search search = getSearch(query, Constants.Type.ITEM);
		return runSearch(search, Item.class);
	}

	@Override
	public Collection<Tag> getTags(UUID user) throws Exception {
		String query = getQueryString("user", user.toString());
		Search search = getSearch(query, Constants.Type.TAG);
		return runSearch(search, Tag.class);
	}

	@Override
	public User getUser(String name) throws Exception {
		String query = getQueryString("userName", name);
		Search search = getSearch(query, Constants.Type.USER);
		User [] out = new User[1];
		runSearch(search, User.class).toArray(out);
		return out[0];
	}

	@Override
	public <T extends Document> void saveDocuments(Collection<T> documents) throws Exception {
		Builder bulkBuilder = new Bulk.Builder();
		for (Document d : documents){
			bulkBuilder.addAction(new Index.Builder(d)
			.index(Constants.INDEX)
			.type(d.getType().toString())
			.id(d.getUUID().toString()).build());
		}
		conn.execute(bulkBuilder.build());
	}
	
	public void closeConnection(){
		conn.shutdownClient();
	}
	
	private Search getSearch(String query, Type type){
		return new Search.Builder(query)
		.addIndex(Constants.INDEX)
		.addType(type.toString()).build();
	}
	
	private String getQueryString(String field, String value){
		String query = "{\n" +
				"	\"query\" : {\n" +
				"		\"match\" : {\n" +
				"			\""+ field + "\" : \"" + value + "\" \n" +
				"		}\n" +
				"	}\n" +
				"}";
		return query;
	}
	
	@SuppressWarnings("deprecation")
	private <T> Collection<T> runSearch(Search search, Class<T> t) throws Exception{
		SearchResult result = conn.execute(search);
		return result.getSourceAsObjectList(t);
	}
	
}
