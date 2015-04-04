package cmput301w15t07.TravelTracker.test.util;

import java.util.ArrayList;
import java.util.Arrays;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.serverinterface.FileSystemHelper;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import android.test.InstrumentationTestCase;

public class FileSystemHelperTest extends InstrumentationTestCase {

	FileSystemHelper fs;
	DataSource ds;
	
	User u1;
	User u2;
	Claim c1;
	Claim c2;
	Item i1;
	Item i2;
	Tag t1;
	Tag t2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		ds = new InMemoryDataSource();
		fs = new FileSystemHelper(getInstrumentation().getTargetContext().getApplicationContext());
		
		u1 = DataSourceUtils.addUser("Billington", ds);
		u2 = DataSourceUtils.addUser("Stevula", ds);
		c1 = DataSourceUtils.addEmptyClaim(u1, ds);
		c2 = DataSourceUtils.addEmptyClaim(u1, ds);
		i1 = DataSourceUtils.addEmptyItem(c1, ds);
		i2 = DataSourceUtils.addEmptyItem(c1, ds);
		t1 = DataSourceUtils.addEmptyTag(u1, ds);
		t2 = DataSourceUtils.addEmptyTag(u1, ds);
		
	}
	
	public void testAddUser() throws Exception {
		assertNull("user should not exist", fs.getUser("Billington"));
		
		ArrayList<User> users = new ArrayList<User>(Arrays.asList(u1));
		fs.<User>saveDocuments(users);
		
		assertEquals(u1, fs.getUser("Billington"));
	}
	
	public void testAddClaim() {
		
	}
	
	public void testAddItem() {
		
	}
	
	public void testAddTag() {
		
	}
	
	public void testAddMixed() {
		
	}
}
