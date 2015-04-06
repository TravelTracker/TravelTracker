package cmput301w15t07.TravelTracker.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

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

public class ModelMergeTest extends InstrumentationTestCase {

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
		fs = new FileSystemHelper(getInstrumentation().getTargetContext().getApplicationContext());
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
	}
	
	public void testMergeTag() throws Exception {
		assertFalse(t1.mergeAttributesFrom(t1));
		assertEquals(t1.getUser(), u1.getUUID());
		
		fs.<Tag>saveDocuments(tag1);
		Tag[] u1Tags = new Tag[1];
		fs.getTags(u1.getUUID()).toArray(u1Tags);
		Tag t1Copy = u1Tags[0];
		
		assertEquals(t1.getUser(), t1Copy.getUser());
		assertEquals(t1.getTitle(), t1Copy.getTitle());
		
		t1Copy.setTitle("Different");
		assertTrue(t1.mergeAttributesFrom(t1Copy));
		assertFalse(t1.mergeAttributesFrom(t1Copy));
		
		t1Copy.setUser(UUID.randomUUID());
		assertTrue(t1.mergeAttributesFrom(t1Copy));
		assertFalse(t1.mergeAttributesFrom(t1Copy));
		
		assertFalse(t1.mergeAttributesFrom(t1));
		assertFalse(t2.mergeAttributesFrom(t2));
		assertFalse(t1.mergeAttributesFrom(t2));
	}
}
