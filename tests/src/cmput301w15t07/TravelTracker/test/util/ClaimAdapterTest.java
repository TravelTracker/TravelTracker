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
		adapter.rebuildList(acb.getClaims(), acb.getItems(), acb.getUsers());
		 for (int i = 1; i < adapter.getCount(); i++){
			 assertTrue(adapter.getItem(i).getStartDate().compareTo(adapter.getItem(i-1).getStartDate()) < 1);
		 }
	}
	
	public void testCLaimsSortedApprover(){
		adapter = new ClaimAdapter(getContext(), UserRole.APPROVER);
		adapter.rebuildList(acb.getClaims(), acb.getItems(), acb.getUsers());
		 for (int i = 1; i < adapter.getCount(); i++){
			 assertTrue(adapter.getItem(i).getStartDate().compareTo(adapter.getItem(i-1).getStartDate()) > -1);
		 }
	}
	
	public void testAdapterMappingDS(){
		adapter = new ClaimAdapter(getContext(), UserRole.APPROVER);
		adapter.rebuildList(acb.getClaims(), acb.getItems(), acb.getUsers());
		assertEquals(acb.getClaims().size(), adapter.getCount());
	}
	
}
