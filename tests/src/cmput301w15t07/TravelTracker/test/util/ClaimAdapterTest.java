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

import cmput301w15t07.TravelTracker.model.GeneratedDataSource;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.AllCallbacks;
import cmput301w15t07.TravelTracker.util.ClaimAdapter;
import android.test.AndroidTestCase;

public class ClaimAdapterTest extends AndroidTestCase{
	
	GeneratedDataSource ds;
	ClaimAdapter adapter;
	AllCallbacks acb;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ds = new GeneratedDataSource();
		acb = new AllCallbacks(ds);
	}
	
	public void testCLaimsSortedClaimant(){
		adapter = new ClaimAdapter(getContext(), UserRole.CLAIMANT);
		adapter.rebuildList(acb.getClaims(), acb.getItems(), acb.getUsers(), acb.getUser(), null);
		
        for (int i = 1; i < adapter.getCount(); i++){
            assertTrue(adapter.getItem(i).getStartDate().compareTo(adapter.getItem(i-1).getStartDate()) < 1);
        }
	}
	
	public void testCLaimsSortedApprover(){
		adapter = new ClaimAdapter(getContext(), UserRole.APPROVER);
		adapter.rebuildList(acb.getClaims(), acb.getItems(), acb.getUsers(), acb.getUser(), null);
		
		for (int i = 1; i < adapter.getCount(); i++){
		    assertTrue(adapter.getItem(i).getStartDate().compareTo(adapter.getItem(i-1).getStartDate()) > -1);
		}
	}
	
	public void testAdapterMappingDS(){
		adapter = new ClaimAdapter(getContext(), UserRole.APPROVER);
		adapter.rebuildList(acb.getClaims(), acb.getItems(), acb.getUsers(), acb.getUser(), null);
		
		assertEquals(acb.getClaims().size(), adapter.getCount());
	}
	
}
