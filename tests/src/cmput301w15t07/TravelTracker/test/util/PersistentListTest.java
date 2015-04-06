package cmput301w15t07.TravelTracker.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import android.content.Context;
import android.test.InstrumentationTestCase;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Geolocation;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.ItemCategory;
import cmput301w15t07.TravelTracker.model.ItemCurrency;
import cmput301w15t07.TravelTracker.model.Receipt;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.serverinterface.FileSystemHelper;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import cmput301w15t07.TravelTracker.util.PersistentList;

public class PersistentListTest  extends InstrumentationTestCase {

	final String TEST_FILENAME = "TEST_FILENAME";
	
	FileSystemHelper fs;
	Context ctx;
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
	ArrayList<Claim> claim2;

	ArrayList<Item> items;
	ArrayList<Item> item2;

	ArrayList<Tag> tags;
	ArrayList<Tag> tag1;
	ArrayList<Tag> tag2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		ds = new InMemoryDataSource();
		ctx = getInstrumentation().getTargetContext().getApplicationContext();
		fs = new FileSystemHelper(ctx);
		fs.purgeFileSystem();
		
		u1 = DataSourceUtils.addUser("Billington", ds);
		u2 = DataSourceUtils.addUser("Stevula", ds);
		
		c1 = DataSourceUtils.addEmptyClaim(u1, ds);
		c1.addComment("test comment");
		c1.setApprover(u2.getUUID());
		c2 = DataSourceUtils.addEmptyClaim(u1, ds);
		
		i1 = DataSourceUtils.addEmptyItem(c1, ds);
		i1.setComplete(true);
		i1.setCurrency(ItemCurrency.CNY);
		i1.setDescription("no words");
		i1.setGeolocation(new Geolocation(100.3, 97.3));
		i1.setCategory(ItemCategory.AIR_FARE);
		i1.setReceipt(new Receipt());
		i2 = DataSourceUtils.addEmptyItem(c1, ds);
		
		t1 = DataSourceUtils.addEmptyTag(u1, ds);
		t1.setTitle("Noodles.");
		t2 = DataSourceUtils.addEmptyTag(u1, ds);

		users = new ArrayList<User>(Arrays.asList(u1, u2));
		justSteve = new ArrayList<User>(Arrays.asList(u2));

		claims = new ArrayList<Claim>(Arrays.asList(c1, c2));
		claim2 = new ArrayList<Claim>(Arrays.asList(c2));

		items = new ArrayList<Item>(Arrays.asList(i1, i2));
		item2 = new ArrayList<Item>(Arrays.asList(i2));

		tags = new ArrayList<Tag>(Arrays.asList(t1, t2));
		tag1 = new ArrayList<Tag>(Arrays.asList(t1));
		tag2 = new ArrayList<Tag>(Arrays.asList(t2));

		c1.setTags(new ArrayList<UUID>(Arrays.asList(t1.getUUID(), t2.getUUID())));
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		fs.purgeFileSystem();
		ctx.deleteFile(TEST_FILENAME);
	}
	
	public void testUUIDs() {
		PersistentList<UUID> list = new PersistentList<UUID>(TEST_FILENAME, ctx, UUID.class);
		list.add(u1.getUUID());
		list.add(u2.getUUID());
		list.add(c1.getUUID());
		
		PersistentList<UUID> copy = new PersistentList<UUID>(TEST_FILENAME, ctx, UUID.class);
		assertEquals(3, list.size());
		assertEquals(3, copy.size());
		assertEquals(list, copy);
		
		copy.clear();
		assertEquals(0, copy.size());
		
	}
	
	public void testTags() {
		PersistentList<Tag> list = new PersistentList<Tag>(TEST_FILENAME, ctx, Tag.class);
		list.add(t1);
		list.add(t2);
		
		PersistentList<Tag> copy = new PersistentList<Tag>(TEST_FILENAME, ctx, Tag.class);
		assertEquals(2, list.size());
		assertEquals(2, copy.size());
		assertEquals(list, copy);
		
		copy.clear();
		assertEquals(0, copy.size());
		
	}
}
