package cmput301w15t07.TravelTracker.test.util;

import java.util.ArrayList;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.serverinterface.ElasticSearchHelper;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import android.test.AndroidTestCase;

public class ElasticSearchHelperTest extends AndroidTestCase{
	ElasticSearchHelper es;
	DataSource ds;
	User user1;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		es = new ElasticSearchHelper();
		ds = new InMemoryDataSource();
		user1 = DataSourceUtils.addUser("Bob", ds);
	}
	
	public void testAddClaims() throws Exception{
		Claim claim1 = DataSourceUtils.addEmptyClaim(user1, ds);
		Claim claim2 = DataSourceUtils.addEmptyClaim(user1, ds);
		
		ArrayList<Claim> claims = new ArrayList<Claim>();
		claims.add(claim1);
		claims.add(claim2);
		
		es.saveDocuments(claims);
		Thread.sleep(1000);
		assertEquals(claims.size(), es.getClaims(user1.getUUID()).size());
		
		es.deleteDocuments(claims);
		Thread.sleep(1000);		
		assertEquals(0, es.getClaims(user1.getUUID()).size());
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
		es.closeConnection();
	}
	
	
}
