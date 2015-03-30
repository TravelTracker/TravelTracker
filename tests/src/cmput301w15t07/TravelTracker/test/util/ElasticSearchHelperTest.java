package cmput301w15t07.TravelTracker.test.util;

import java.util.ArrayList;

import cmput301w15t07.TravelTracker.activity.ExpenseItemInfoActivity;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.serverinterface.ElasticSearchHelper;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

public class ElasticSearchHelperTest extends AndroidTestCase{
	ElasticSearchHelper es;
	DataSource ds;
	User user1;
	Claim claim1;
	Claim claim2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		es = new ElasticSearchHelper();
		ds = new InMemoryDataSource();
		user1 = DataSourceUtils.addUser("Bob", ds);
		claim1 = DataSourceUtils.addEmptyClaim(user1, ds);
		claim2 = DataSourceUtils.addEmptyClaim(user1, ds);
	}
	
	@Suppress
	public void testAddClaims() throws Exception{
		ArrayList<Claim> claims = new ArrayList<Claim>();
		claims.add(claim1);
		claims.add(claim2);
		
		es.saveDocuments(claims);
		Thread.sleep(1000);
		assertEquals(claims.size(), es.getClaims(user1.getUUID()).size());
		
		cleanUp(claims);
		assertEquals(0, es.getClaims(user1.getUUID()).size());
	}
	
	@Suppress
	public void testAddExpense() throws Exception{
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(DataSourceUtils.addEmptyItem(claim1, ds));
		items.add(DataSourceUtils.addEmptyItem(claim1, ds));
		
		es.saveDocuments(items);
		Thread.sleep(1000);
		assertEquals(items.size(), es.getExpenses(claim1.getUUID()).size());
		
		cleanUp(items);
		assertEquals(0, es.getExpenses(claim1.getUUID()).size());
	}
	
	@Suppress
	public void testAAddUser() throws Exception {
		ArrayList<User> users = new ArrayList<User>();
		users.add(user1);
		
		
		es.saveDocuments(users);
		Thread.sleep(1000);
		assertTrue(user1.getUUID().equals(es.getUser(user1.getUserName()).getUUID()));
		
		cleanUp(users);
		//TODO test cleanup successful
	}
	
	@Suppress
	public void testAddTag() throws Exception {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(DataSourceUtils.addEmptyTag(user1, ds));
		
		es.saveDocuments(tags);
		Thread.sleep(1000);
		assertEquals(tags.size(), es.getTags(user1.getUUID()).size());
		
		cleanUp(tags);
		assertEquals(0, es.getTags(user1.getUUID()).size());
	}
	
	private <T extends Document> void cleanUp(ArrayList<T> models) throws Exception{
		es.deleteDocuments(models);
		Thread.sleep(1000);
	}
	
	@Override
	protected void tearDown() throws Exception{
		super.tearDown();
		es.closeConnection();
	}
	
	
}
