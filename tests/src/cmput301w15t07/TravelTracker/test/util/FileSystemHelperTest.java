package cmput301w15t07.TravelTracker.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

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
	
	ArrayList<User> users;
	ArrayList<User> justSteve;

	ArrayList<Claim> claims;
	ArrayList<Claim> justClaim2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		ds = new InMemoryDataSource();
		fs = new FileSystemHelper(getInstrumentation().getTargetContext().getApplicationContext());
		fs.purgeFileSystem();
		
		u1 = DataSourceUtils.addUser("Billington", ds);
		u2 = DataSourceUtils.addUser("Stevula", ds);
		c1 = DataSourceUtils.addEmptyClaim(u1, ds);
		c2 = DataSourceUtils.addEmptyClaim(u1, ds);
		i1 = DataSourceUtils.addEmptyItem(c1, ds);
		i2 = DataSourceUtils.addEmptyItem(c1, ds);
		t1 = DataSourceUtils.addEmptyTag(u1, ds);
		t2 = DataSourceUtils.addEmptyTag(u1, ds);

		users = new ArrayList<User>(Arrays.asList(u1, u2));
		justSteve = new ArrayList<User>(Arrays.asList(u2));

		claims = new ArrayList<Claim>(Arrays.asList(c1, c2));
		justClaim2 = new ArrayList<Claim>(Arrays.asList(c2));
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		fs.purgeFileSystem();
	}
	
	public void testUsers() throws Exception {
		
		fs.deleteDocuments(users);
		assertNull(fs.getUser("Billington"));
		assertNull(fs.getUser("Stevula"));
		assertNull(fs.getUser("Nobody"));

		fs.<User>saveDocuments(users);
		assertEquals(u1, fs.getUser("Billington"));
		assertEquals(u2, fs.getUser("Stevula"));
		assertNull(fs.getUser("Nobody"));
		
		fs.deleteDocuments(justSteve);
		assertNull(fs.getUser("Stevula"));
		assertEquals(u1, fs.getUser("Billington"));
		assertNull(fs.getUser("Nobody"));
		
		fs.deleteDocuments(users);
		assertNull(fs.getUser("Billington"));
		assertNull(fs.getUser("Stevula"));
		assertNull(fs.getUser("Nobody"));
	}
	
	public void testClaims() throws Exception {

		fs.<User>saveDocuments(users);
		
		fs.deleteDocuments(claims);
		assertEquals(0, fs.getClaims(u1.getUUID()).size());
		assertEquals(0, fs.getClaims(u2.getUUID()).size());
		assertEquals(0, fs.getClaims(UUID.randomUUID()).size());

		fs.<Claim>saveDocuments(claims);
		assertTrue(fs.getClaims(u1.getUUID()).contains(c1));
		assertTrue(fs.getClaims(u1.getUUID()).contains(c2));
		assertEquals(0, fs.getClaims(u2.getUUID()).size());
		assertEquals(0, fs.getClaims(UUID.randomUUID()).size());
		
		fs.deleteDocuments(justClaim2);
		assertTrue(fs.getClaims(u1.getUUID()).contains(c1));
		assertTrue(!fs.getClaims(u1.getUUID()).contains(c2));
		assertEquals(0, fs.getClaims(u2.getUUID()).size());
		assertEquals(0, fs.getClaims(UUID.randomUUID()).size());
		
		fs.deleteDocuments(claims);
		assertTrue(!fs.getClaims(u1.getUUID()).contains(c1));
		assertTrue(!fs.getClaims(u1.getUUID()).contains(c2));
		assertEquals(0, fs.getClaims(u2.getUUID()).size());
		assertEquals(0, fs.getClaims(UUID.randomUUID()).size());
	}
	
	public void testAddItem() {
		
	}
	
	public void testAddTag() {
		
	}
	
	public void testAddMixed() {
		
	}
}
