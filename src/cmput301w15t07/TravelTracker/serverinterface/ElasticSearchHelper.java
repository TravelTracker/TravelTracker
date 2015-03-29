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

import io.searchbox.*;
import io.searchbox.client.JestClient;

public class ElasticSearchHelper implements ServerHelper{
	private JestClient conn;
	
	
	private static final String connectionURL = "http://cmput301.softwareprocess.es:8080/cmput301w15t07/";
	
	public ElasticSearchHelper(){
		JestClientFactory factory = new JestClientFactory();
		factory.setDroidClientConfig(new DroidClientConfig.Builder(connectionURL).build());
		
		conn = factory.getObject();
	}

	@Override
	public void deleteDocuments(Collection<Document> documents) {
		// TODO Auto-generated method stub
		
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
}
