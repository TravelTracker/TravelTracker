package cmput301w15t07.TravelTracker.test.util;

import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper.InitialData;
import android.test.AndroidTestCase;

public class ClaimListDataHelperTest extends AndroidTestCase {

	DataSource ds;
	ClaimsListDataHelper helper;
	InitialData data;
	SynchronizedResultCallback<InitialData> idcb;
	
	String name1 = "Joe";
	String name2 = "Bob";
	String name3 = "Alice";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ds = new InMemoryDataSource();
		helper = new ClaimsListDataHelper();
		idcb = new SynchronizedResultCallback<InitialData>();
		
	}
	
	public void testRightUsers(){
		User user1 = DataSourceUtils.addUser(name1, ds);
		User user2 = DataSourceUtils.addUser(name2, ds);
		User user3 = DataSourceUtils.addUser(name3, ds);
		
		
		helper.getInitialData(idcb, new UserData(user1.getUUID(), name1, UserRole.CLAIMANT), ds);
		data = DataSourceUtils.getData(idcb);
		assertTrue(data.getUsers().size() == 3);
	}
	
	public void testClaimsForClaimant(){
		User user1 = DataSourceUtils.addUser(name1, ds);
		User user2 = DataSourceUtils.addUser(name2, ds);
		
		Claim claim1 = DataSourceUtils.addEmptyClaim(user1, ds);
		Claim claim2 = DataSourceUtils.addEmptyClaim(user2, ds);
		
		helper.getInitialData(idcb, new UserData(user1.getUUID(), name1, UserRole.CLAIMANT), ds);
		data = DataSourceUtils.getData(idcb);
		
		assertTrue("Claimant is getting claims that doesn't belong to them", data.getClaims().size() == 1);
		
	}
	
}
