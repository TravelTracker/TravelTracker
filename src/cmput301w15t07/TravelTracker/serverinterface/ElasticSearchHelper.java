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

package cmput301w15t07.TravelTracker.serverinterface;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import android.R.bool;
import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.serverinterface.Constants.Type;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;

import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ElasticSearchHelper implements ServerHelper{
    private JestClient conn;
    
    public ElasticSearchHelper(){
        JestClientFactory factory = new JestClientFactory();
        factory.setDroidClientConfig(new DroidClientConfig.Builder(Constants.CONNECTION_URL).build());
        
        conn = factory.getObject();
    }

    @Override
    public <T extends Document> void deleteDocuments(Collection<T> documents) throws Exception {
        final Builder bulkBuilder = new Bulk.Builder();
        bulkBuilder.defaultIndex(Constants.INDEX);
        
        for (Document d : documents){
            bulkBuilder.addAction(new Delete.Builder(d.getUUID().toString())
            .index(Constants.INDEX)
            .type(d.getType().toString()).build());
        }
        
        runESOperation(new ESOperation<Void>() {

            @Override
            public Void run() throws Exception {
                conn.execute(bulkBuilder.build());
                return null;
            }
        });
    }
    
    @Override
    public Collection<Claim> getAllClaims() throws Exception {
        return getAllAction(Constants.Type.CLAIM, Claim.class);        
    }

    @Override
    public Collection<Item> getAllItems() throws Exception {
        return getAllAction(Constants.Type.ITEM, Item.class);        
    }

    @Override
    public Collection<Tag> getAllTags() throws Exception {
        return getAllAction(Constants.Type.TAG, Tag.class);        
    }

    @Override
    public Collection<User> getAllUsers() throws Exception {
        return getAllAction(Constants.Type.USER, User.class);
    }
    
    private <T> Collection<T> getAllAction(Constants.Type type, Class<T> t) throws Exception{
        String query = getAllQueryString();
        Search search = getSearch(query, type);
        return runSearch(search, t);
    }

    @Override
    public Collection<Claim> getClaims(UUID user) throws Exception {
        String query = getQueryString("user", user.toString());
        Search search = getSearch(query, Constants.Type.CLAIM);
        return runSearch(search, Claim.class);
    }

    @Override
    public Collection<Item> getExpenses(UUID claim) throws Exception {
        String query = getQueryString("claim", claim.toString());
        Search search = getSearch(query, Constants.Type.ITEM);
        return runSearch(search, Item.class);
    }

    @Override
    public Collection<Tag> getTags(UUID user) throws Exception {
        String query = getQueryString("user", user.toString());
        Search search = getSearch(query, Constants.Type.TAG);
        return runSearch(search, Tag.class);
    }

    @Override
    public User getUser(String name) throws Exception {
        String query = getQueryString("userName", name);
        Search search = getSearch(query, Constants.Type.USER);
        User [] out = new User[1];
        runSearch(search, User.class).toArray(out);
        return out[0];
    }
    
    @Override
    public User getUser(UUID user) throws Exception {
        String query = getQueryString("docID", user.toString());
        Search search = getSearch(query, Constants.Type.USER);
        User [] out = new User[1];
        runSearch(search, User.class).toArray(out);
        return out[0];
    }

    @Override
    public <T extends Document> void saveDocuments(Collection<T> documents) throws Exception {
        final Builder bulkBuilder = new Bulk.Builder();
        for (Document d : documents){
            bulkBuilder.addAction(new Index.Builder(d)
            .index(Constants.INDEX)
            .type(d.getType().toString())
            .id(d.getUUID().toString()).build());
        }
        
        runESOperation(new ESOperation<Void>() {

            @Override
            public Void run() throws Exception {
                conn.execute(bulkBuilder.build());
                return null;
            }
        });
        
    }
    
    public void closeConnection(){
        conn.shutdownClient();
    }
    
    private Search getSearch(String query, Type type){
        return new Search.Builder(query)
        .addIndex(Constants.INDEX)
        .addType(type.toString()).build();
    }
    
    private String getQueryString(String field, String value){
        String query = "{\n" +
                "    \"from\": 0," +
                "    \"size\": 100," +
                "    \"query\" : {\n" +
                "        \"match\" : {\n" +
                "            \""+ field + "\" : \"" + value + "\" \n" +
                "        }\n" +
                "    }\n" +
                "}";
        return query;
    }
    
    private String getAllQueryString(){
        //NOTE arbitrary size limit of 100
        return "{\n" +
                "    \"from\": 0," +
                "    \"size\": 100," +
                "    \"query\" : {\n" +
                "        \"match_all\" : {}\n" +
                "    }\n" +
                "}";
    }
    
    @SuppressWarnings("deprecation")
    private <T> Collection<T> runSearch(Search search, Class<T> t) throws Exception{
        Collection<T> out = null;
        final Search methodSearch = search;
        final Class<T> classT = t;
        
        out = runESOperation(new ESOperation<Collection<T>>() {

            @Override
            public Collection<T> run() throws Exception {
                SearchResult result = conn.execute(methodSearch);
                return result.getSourceAsObjectList(classT);
            }
        });
        
        return out;
        
    }
    
    /**
     * This method performs runs an ESOperation with a single retry for timeouts
     * @param opp the operation that should be run
     * @return T the return type of the passed ESOperation
     * @throws Exception
     */
    private <T> T runESOperation(ESOperation<T> opp) throws Exception{
        T out = null;
        boolean retry = true;
        while (true){
            try{
                out = opp.run();                
                break;
            } catch (ConnectTimeoutException e){
                if (retry){
                    retry = false;
                    continue;
                } else {
                    throw e;
                }
            }
        }
        return out;
    }

    private interface ESOperation <T> {
        public T run() throws Exception;
    }
    
}
