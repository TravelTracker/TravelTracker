package cmput301w15t07.TravelTracker.test.util;

import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.GeneratedDataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper.InitialData;
import android.test.AndroidTestCase;

public class ClaimListDataHelperTest extends AndroidTestCase {

	DataSource ds;
	ClaimsListDataHelper helper;
	InitialData data;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ds = new GeneratedDataSource();
		helper = new ClaimsListDataHelper();
	}
	
	public void testRightUsers(){
		
	}
	
	private User addUser(String name){
		SynchronizedResultCallback<User> userCB = new SynchronizedResultCallback<User>();
		ds.addUser(userCB);
		User user = userCB.getResult();
		user.setUserName(name);
		return user;
	}
	
}
