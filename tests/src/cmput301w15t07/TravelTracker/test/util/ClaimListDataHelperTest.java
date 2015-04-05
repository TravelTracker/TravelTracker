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

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Status;
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
		Claim claim2 = DataSourceUtils.addEmptyClaim(user1, ds);
		Claim claim3 = DataSourceUtils.addEmptyClaim(user1, ds);
		Claim claim4 = DataSourceUtils.addEmptyClaim(user1, ds);
		
		Claim claim5 = DataSourceUtils.addEmptyClaim(user2, ds);
		
		claim1.setStatus(Status.IN_PROGRESS);
		claim2.setStatus(Status.RETURNED);
		claim3.setStatus(Status.APPROVED);
		claim4.setStatus(Status.SUBMITTED);
		
		helper.getInitialData(idcb, new UserData(user1.getUUID(), name1, UserRole.CLAIMANT), ds);
		data = DataSourceUtils.getData(idcb);
		
		assertTrue("Claimant is getting claims that doesn't belong to them", data.getClaims().size() == 4);
		
	}
	
	public void testNoSubmittedClaimsApprover(){
		User user1 = DataSourceUtils.addUser(name1, ds);
		User user2 = DataSourceUtils.addUser(name2, ds);
		
		Claim claim1 = DataSourceUtils.addEmptyClaim(user1, ds);
		Claim claim2 = DataSourceUtils.addEmptyClaim(user2, ds);
		Claim claim3 = DataSourceUtils.addEmptyClaim(user2, ds);
		
		claim1.setStatus(Status.IN_PROGRESS);
		claim2.setStatus(Status.RETURNED);
		claim3.setStatus(Status.APPROVED);
		
		helper.getInitialData(idcb, new UserData(user1.getUUID(), name1, UserRole.APPROVER), ds);
		data = DataSourceUtils.getData(idcb);
		
		assertTrue("Approver receiving non-submitted claims", data.getClaims().size() == 0);
		
	}
	
	public void testSubmittedClaimsApprover(){
		User user1 = DataSourceUtils.addUser(name1, ds);
		User user2 = DataSourceUtils.addUser(name2, ds);
		User user3 = DataSourceUtils.addUser(name3, ds);
		
		Claim claim1 = DataSourceUtils.addEmptyClaim(user1, ds);
		Claim claim2 = DataSourceUtils.addEmptyClaim(user2, ds);
		
		claim1.setStatus(Status.SUBMITTED);
		claim2.setStatus(Status.SUBMITTED);
		
		helper.getInitialData(idcb, new UserData(user3.getUUID(), user3.getUserName(), UserRole.APPROVER), ds);
		data = DataSourceUtils.getData(idcb);
		
		assertTrue("Approver receiving non-submitted claims", data.getClaims().size() == 2);
	}
	
	public void testApproverCantSeeOwnClaims(){
		User user1 = DataSourceUtils.addUser(name1, ds);
		User user2 = DataSourceUtils.addUser(name2, ds);
		
		Claim claim1 = DataSourceUtils.addEmptyClaim(user1, ds);
		Claim claim2 = DataSourceUtils.addEmptyClaim(user2, ds);
		
		claim1.setStatus(Status.SUBMITTED);
		claim2.setStatus(Status.SUBMITTED);
		
		helper.getInitialData(idcb, new UserData(user1.getUUID(), name1, UserRole.APPROVER), ds);
		data = DataSourceUtils.getData(idcb);
		
		assertTrue("Approver receiving non-submitted claims", data.getClaims().size() == 1);
	}
	
}
