package cmput301w15t07.TravelTracker.testutils;

import java.util.UUID;

import android.test.AndroidTestCase;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;


public class DataSourceUtils extends AndroidTestCase{
	
	/**
	 * Simply adds a user to the passed data source
	 * @param name - user name
	 * @param ds - datasource
	 * @return User object
	 */
	public static User addUser(String name, DataSource ds){
		SynchronizedResultCallback<User> userCB = new SynchronizedResultCallback<User>();
		ds.addUser(userCB);
		return getData(userCB);
	}
	
	/**
	 * Convenience method to get some a CLAIMANT UserData obj
	 * @param name
	 * @return UserData obj
	 */
	public static UserData getUserDataClaimant(String name){
		return getUserData(name, UserRole.CLAIMANT);
	}
	
	/**
	 * Convenience method to get an APPROVER UserData obj
	 * @param name
	 * @return UserData obj
	 */
	public static UserData getUserDataApprover(String name){
		return getUserData(name, UserRole.APPROVER);
	}
	
	public static Claim addEmptyClaim(User user, DataSource ds){
		SynchronizedResultCallback<Claim> claimCB = new SynchronizedResultCallback<Claim>();
		ds.addClaim(user, claimCB);
		return getData(claimCB);
	}
	
	
	/**
	 * Convenience method to get the data out of a synchronized Result callback. Asserts
	 * that waiting for result was successful. Handles the interrupted exception (Will fail the test).
	 * @param callback
	 * @return data
	 */
	public static <T> T getData(SynchronizedResultCallback<T> callback) {
		try{
			boolean success = callback.waitForResult();
			assertTrue("callback waiting failed", success);
			return callback.getResult();
		} catch (InterruptedException e){
			fail("failed to get data from data source for class: " + callback.toString());
		}
		return null;
	}
	
	private static UserData getUserData(String name, UserRole role){
		return new UserData(UUID.randomUUID(), "name", role);
	}
}
