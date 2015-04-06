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

package cmput301w15t07.TravelTracker.testutils;

import java.util.Collection;

import android.test.AndroidTestCase;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;

/**
 * This is a convenience class for testing to get all of the data you might need from a datasource
 *  for a test without messing around with synchronized call backs. 
 * 
 * Please add to it as you see fit!
 * @author ryan
 *
 */
public class AllCallbacks extends AndroidTestCase{
    public SynchronizedResultCallback<User> userCallback = new SynchronizedResultCallback<User>();
    public SynchronizedResultCallback<Collection<Claim>> claimCallback = new SynchronizedResultCallback<Collection<Claim>>();
    public SynchronizedResultCallback<Collection<Item>> itemCallback = new SynchronizedResultCallback<Collection<Item>>();
    public SynchronizedResultCallback<Collection<User>> usersCallback = new SynchronizedResultCallback<Collection<User>>();
    public SynchronizedResultCallback<Collection<Tag>> tagCallback = new SynchronizedResultCallback<Collection<Tag>>();
    
    /**
     *  Constructor for this class 
     * @param ds - The datasource you want to use
     */
    public AllCallbacks(DataSource ds){
        ds.getAllClaims(claimCallback);
        ds.getAllItems(itemCallback);
        ds.addUser(userCallback);
        ds.getAllTags(tagCallback);
        ds.getAllUsers(usersCallback);
    }
    
    
    public User getUser(){
        return DataSourceUtils.getData(userCallback);
    }
    
    public Collection<User> getUsers(){
        return DataSourceUtils.getData(usersCallback);
    }
    
    public Collection<Claim> getClaims(){
        return DataSourceUtils.getData(claimCallback);
    }
    
    public Collection<Item> getItems(){
        return DataSourceUtils.getData(itemCallback);
    }
    
    public Collection<Tag> getTags(){
        return DataSourceUtils.getData(tagCallback);
    }
    
}

