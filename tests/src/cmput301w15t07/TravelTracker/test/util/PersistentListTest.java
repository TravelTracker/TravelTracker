/*
 *   Copyright 2015 Kirby Banman,
 *                  Stuart Bildfell,
 *                  Elliot Colp,
 *                  Christian Ellinger,
 *                  Braedy Kuzma,
 *                  Ryan Thornhill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cmput301w15t07.TravelTracker.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

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
import cmput301w15t07.TravelTracker.util.DeletionFlag;
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
		ctx.deleteFile(TEST_FILENAME);
		
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
	
	public void testClaims() {
		PersistentList<Claim> list = new PersistentList<Claim>(TEST_FILENAME, ctx, Claim.class);
		list.add(c1);
		list.add(c2);
		
		PersistentList<Claim> copy = new PersistentList<Claim>(TEST_FILENAME, ctx, (new TypeToken<Claim>(){}).getType());
		assertEquals(2, list.size());
		assertEquals(2, copy.size());
		assertEquals(list, copy);
		
		copy.clear();
		assertEquals(0, copy.size());
		
	}
	
	public void testArrayList() {
		PersistentList<ArrayList<Claim>> list = new PersistentList<ArrayList<Claim>>(TEST_FILENAME, ctx, (new TypeToken<ArrayList<Claim>>(){}).getType());
		list.add(claims);
		list.add(claim2);
		
		PersistentList<ArrayList<Claim>> copy = new PersistentList<ArrayList<Claim>>(TEST_FILENAME, ctx, (new TypeToken<ArrayList<Claim>>(){}).getType());
		assertEquals(2, list.size());
		assertEquals(2, copy.size());
		assertTrue(list.equals(copy));
		
		copy.clear();
		assertEquals(0, copy.size());
		
	}
	
	public void testDeletionFlag() {
		PersistentList<DeletionFlag<Claim>> list = new PersistentList<DeletionFlag<Claim>>(TEST_FILENAME, ctx, (new TypeToken<DeletionFlag<Claim>>(){}).getType());
		list.add(new DeletionFlag<Claim>(c1));
		list.add(new DeletionFlag<Claim>(c2));
		
		PersistentList<DeletionFlag<Claim>> copy = new PersistentList<DeletionFlag<Claim>>(TEST_FILENAME, ctx, (new TypeToken<DeletionFlag<Claim>>(){}).getType());
		assertEquals(2, list.size());
		assertEquals(2, copy.size());
		assertTrue(list.equals(copy));
		
		copy.clear();
		assertEquals(0, copy.size());
		
	}
}
