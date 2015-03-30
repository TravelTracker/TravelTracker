package cmput301w15t07.TravelTracker.serverinterface;

import java.util.Collection;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;

import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
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
		String query = "{\n" +
						"	\"query\" : {\n" +
						"		\"match\" : {\n" +
						"			\"user\" : \"" + user.toString() + "\" \n" +
						"		}\n" +
						"	}\n" +
						"}";
		
		Search search = new Search.Builder(query)
		.addIndex(Constants.INDEX)
		.addType(Constants.Type.CLAIM.toString()).build();
		
		return runSearch(search, Claim.class);
	}

	@Override
	public Collection<Item> getExpenses(UUID claim) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Tag> getTags(UUID user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUser(String name) {
		// TODO Auto-generated method stub
		return null;
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
	
	@SuppressWarnings("deprecation")
	private <T> Collection<T> runSearch(Search search, Class<T> t) throws Exception{
		SearchResult result = conn.execute(search);
		return result.getSourceAsObjectList(t);
	}
	
	public void closeConnection(){
		conn.shutdownClient();
	}
}
