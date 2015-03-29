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
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ElasticSearchHelper implements ServerHelper{
	private JestClient conn;
	
	
	private static final String CONNECTIONURL = "http://cmput301.softwareprocess.es:8080/";
	private static final String INDEX = "cmput301w15t07";
	
	public ElasticSearchHelper(){
		JestClientFactory factory = new JestClientFactory();
		factory.setDroidClientConfig(new DroidClientConfig.Builder(CONNECTIONURL).build());
		
		conn = factory.getObject();
	}

	@Override
	public void deleteDocuments(Collection<Document> documents) throws Exception {
		Builder bulkBuilder = new Bulk.Builder();
		bulkBuilder.defaultIndex(INDEX);
		
		for (Document d : documents){
			bulkBuilder.addAction(new Delete.Builder(d.getUUID().toString()).index(INDEX).build());
		}
		
		conn.execute(bulkBuilder.build());
	}

	@Override
	public Collection<Claim> getClaims(UUID user) {
		// TODO Auto-generated method stub
		return null;
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
	public void saveDocuments(Collection<Document> documents) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("deprecation")
	private <T> Collection<T> runSearch(Search search, Class<T> t) throws Exception{
		SearchResult result = conn.execute(search);
		return result.getSourceAsObjectList(t);
	}
}
