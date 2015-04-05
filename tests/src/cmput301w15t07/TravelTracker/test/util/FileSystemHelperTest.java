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
import java.util.Date;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.Geolocation;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.ItemCategory;
import cmput301w15t07.TravelTracker.model.ItemCurrency;
import cmput301w15t07.TravelTracker.model.Receipt;
import cmput301w15t07.TravelTracker.model.Status;
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
	ArrayList<Claim> claim2;

	ArrayList<Item> items;
	ArrayList<Item> item2;

	ArrayList<Tag> tags;
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
		tag2 = new ArrayList<Tag>(Arrays.asList(t2));

		c1.setTags(new ArrayList<UUID>(Arrays.asList(t1.getUUID(), t2.getUUID())));
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
		
		fs.deleteDocuments(claim2);
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
	
	public void testItems() throws Exception {

		fs.<User>saveDocuments(users);
		fs.<Claim>saveDocuments(claims);
		
		fs.deleteDocuments(items);
		assertEquals(0, fs.getExpenses(c1.getUUID()).size());
		assertEquals(0, fs.getExpenses(c2.getUUID()).size());
		assertEquals(0, fs.getExpenses(u2.getUUID()).size());
		assertEquals(0, fs.getExpenses(UUID.randomUUID()).size());

		fs.<Item>saveDocuments(items);
		assertTrue(fs.getExpenses(c1.getUUID()).contains(i1));
		assertTrue(fs.getExpenses(c1.getUUID()).contains(i2));
		assertEquals(2, fs.getExpenses(c1.getUUID()).size());
		assertEquals(0, fs.getExpenses(c2.getUUID()).size());
		assertEquals(0, fs.getExpenses(UUID.randomUUID()).size());
		
		fs.deleteDocuments(item2);
		assertTrue(fs.getExpenses(c1.getUUID()).contains(i1));
		assertTrue(!fs.getExpenses(c1.getUUID()).contains(i2));
		assertEquals(0, fs.getExpenses(c2.getUUID()).size());
		assertEquals(0, fs.getExpenses(UUID.randomUUID()).size());
		
		fs.deleteDocuments(items);
		assertTrue(!fs.getExpenses(c1.getUUID()).contains(i1));
		assertTrue(!fs.getExpenses(c1.getUUID()).contains(i2));
		assertEquals(0, fs.getExpenses(c2.getUUID()).size());
		assertEquals(0, fs.getExpenses(UUID.randomUUID()).size());
	}
	
	public void testTags() throws Exception {

		fs.<User>saveDocuments(users);
		fs.<Claim>saveDocuments(claims);
		fs.<Item>saveDocuments(items);
		
		fs.deleteDocuments(tags);
		assertEquals(0, fs.getTags(u1.getUUID()).size());
		assertEquals(0, fs.getTags(u2.getUUID()).size());
		assertEquals(0, fs.getTags(u2.getUUID()).size());
		assertEquals(0, fs.getTags(UUID.randomUUID()).size());

		fs.<Tag>saveDocuments(tags);
		assertTrue(fs.getTags(u1.getUUID()).contains(t1));
		assertTrue(fs.getTags(u1.getUUID()).contains(t2));
		assertEquals(2, fs.getTags(u1.getUUID()).size());
		assertEquals(0, fs.getTags(u2.getUUID()).size());
		assertEquals(0, fs.getTags(UUID.randomUUID()).size());
		
		fs.deleteDocuments(tag2);
		assertTrue(fs.getTags(u1.getUUID()).contains(t1));
		assertTrue(!fs.getTags(u1.getUUID()).contains(t2));
		assertEquals(0, fs.getTags(u2.getUUID()).size());
		assertEquals(0, fs.getTags(UUID.randomUUID()).size());
		
		fs.deleteDocuments(tags);
		assertTrue(!fs.getTags(u1.getUUID()).contains(t1));
		assertTrue(!fs.getTags(u1.getUUID()).contains(t2));
		assertEquals(0, fs.getTags(u2.getUUID()).size());
		assertEquals(0, fs.getTags(UUID.randomUUID()).size());
	}
	
	public void testDeleteMixed() throws Exception {
		fs.<User>saveDocuments(users);
		fs.<Claim>saveDocuments(claims);
		fs.<Item>saveDocuments(items);
		fs.<Tag>saveDocuments(tags);
		
		assertEquals(2, fs.getClaims(u1.getUUID()).size());
		assertEquals(2, fs.getExpenses(c1.getUUID()).size());
		assertEquals(2, fs.getTags(u1.getUUID()).size());
		assertEquals(u1, fs.getUser("Billington"));
		assertEquals(u2, fs.getUser("Stevula"));
		
		fs.deleteDocuments(new ArrayList<Document>(Arrays.asList(u1, c1, t1, i1)));
		assertEquals(1, fs.getClaims(u1.getUUID()).size());
		assertEquals(1, fs.getExpenses(c1.getUUID()).size());
		assertEquals(1, fs.getTags(u1.getUUID()).size());
		assertNull(fs.getUser("Billington"));
		assertEquals(u2, fs.getUser("Stevula"));
		
		fs.deleteDocuments(new ArrayList<Document>(Arrays.asList(u1, c1, t1, i1)));
		assertEquals(1, fs.getClaims(u1.getUUID()).size());
		assertEquals(1, fs.getExpenses(c1.getUUID()).size());
		assertEquals(1, fs.getTags(u1.getUUID()).size());
		assertNull(fs.getUser("Billington"));
		assertEquals(u2, fs.getUser("Stevula"));
		
		fs.deleteDocuments(new ArrayList<Document>(Arrays.asList(u2, c2, t1, i1))); // mixed already deleted, not deleted
		assertEquals(0, fs.getClaims(u1.getUUID()).size());
		assertEquals(1, fs.getExpenses(c1.getUUID()).size());
		assertEquals(1, fs.getTags(u1.getUUID()).size());
		assertNull(fs.getUser("Billington"));
		assertNull(fs.getUser("Stevula"));
		
		fs.deleteDocuments(new ArrayList<Document>(Arrays.asList(u1, c1, t2, i2))); // all remaining
		assertEquals(0, fs.getClaims(u1.getUUID()).size());
		assertEquals(0, fs.getExpenses(c1.getUUID()).size());
		assertEquals(0, fs.getTags(u1.getUUID()).size());
		assertNull(fs.getUser("Billington"));
		assertNull(fs.getUser("Stevula"));
	}
	

	
	public void testEditClaim() throws Exception {
		

		User newUser = DataSourceUtils.addUser("newguy", ds);
		Claim originalClaim = DataSourceUtils.addEmptyClaim(newUser, ds);

		assertEquals(0, fs.getClaims(newUser.getUUID()).size());

		fs.<User>saveDocuments(new ArrayList<User>(Arrays.asList(newUser)));
		fs.<Claim>saveDocuments(new ArrayList<Claim>(Arrays.asList(originalClaim)));

		assertEquals(1, fs.getClaims(newUser.getUUID()).size());
		
		// retrieve saved claim for later comparison
		Claim[] claims = new Claim[1];
		fs.getClaims(newUser.getUUID()).toArray(claims);
		Claim referenceClaim = claims[0];
		
		assertEquals(referenceClaim, originalClaim);
		

		originalClaim.setStartDate(new Date());
		originalClaim.addComment("a comment");
		originalClaim.setApprover(u1.getUUID());
		originalClaim.setStatus(Status.RETURNED);
		originalClaim.setTags(new ArrayList<UUID>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID())));
		originalClaim.setDestinations(new ArrayList<Destination>(Arrays.asList(new Destination("loc", new Geolocation(72.3, 99.9), "because"))));
		originalClaim.setEndDate(new Date());
		
		assertFalse(originalClaim.equals(referenceClaim));
		assertEquals(referenceClaim.getUUID(), originalClaim.getUUID());
		assertEquals(referenceClaim.getUser(), originalClaim.getUser());
		assertEquals(referenceClaim.getUser(), newUser.getUUID());
		
		// saving original claim again should update the claim, so that the same retrieval process
		// as got referenceClaim should get an updated version.
		fs.<Claim>saveDocuments(new ArrayList<Claim>(Arrays.asList(originalClaim)));
		assertEquals(1, fs.getClaims(newUser.getUUID()).size());
		fs.getClaims(newUser.getUUID()).toArray(claims);
		Claim updatedClaim = claims[0];
		
		assertEquals(referenceClaim.getUser(), updatedClaim.getUser());
		assertEquals(updatedClaim.getUser(), newUser.getUUID());
		assertEquals(updatedClaim, originalClaim);
		
		fs.deleteDocuments(new ArrayList<Document>(Arrays.asList(originalClaim)));
		assertEquals(0, fs.getClaims(newUser.getUUID()).size());
	}
}
