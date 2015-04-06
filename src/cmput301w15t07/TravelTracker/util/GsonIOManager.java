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

package cmput301w15t07.TravelTracker.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Facade to wrap streams and GSON ugliness.
 * 
 * @author kdbanman
 *
 */
public class GsonIOManager {
    
    private Context ctx;
    private Gson gson;
    
    public GsonIOManager(Context ctx) {
        this.ctx = ctx;
        gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new DateAdapter()).serializeNulls().create();;
    }
    
    public <T> T load(String filename, Type type) throws FileNotFoundException {
        T ret;
        try {
            FileInputStream fis = ctx.openFileInput(filename);
            InputStreamReader reader = new InputStreamReader(fis);
            
            ret = gson.fromJson(reader, type);
            
            fis.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            Log.e("GSONIOManager", "file read failed");
            Log.e("GSONIOManager", e.getMessage());
            ret = null;
        }
        return ret;
    }
    
    public void save(Object toSave, String filename, Type type) {
        try {
            FileOutputStream fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            gson.toJson(toSave, type, writer);
            writer.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("GSONIOManager", "file read failed");
            Log.e("GSONIOManager", e.getMessage());
        }
    }
    
    /*
     * Custom date gson adapter mix and matched from
     *     http://stackoverflow.com/questions/6873020/gson-date-format
     * and from 
     *  http://stackoverflow.com/questions/5671373/unparseable-date-1302828677828-trying-to-deserialize-with-gson-a-millisecond
     * on 4 April 2015
     */
    private class DateAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
              return json == null ? null : new Date(json.getAsJsonPrimitive().getAsLong()); 
        }

        @Override
        public JsonElement serialize(Date date, Type typeOfT, JsonSerializationContext context) {
            return date == null ? null : new JsonPrimitive(date.getTime());
        }
    }
            
}
