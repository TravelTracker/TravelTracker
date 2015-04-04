package cmput301w15t07.TravelTracker.util;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Facade to wrap streams and GSON ugliness.
 * 
 * @author kdbanman
 *
 */
public class GsonIOManager {
	
	private Context ctx;
	
	public GsonIOManager(Context ctx) {
		this.ctx = ctx;
	}
	
	public <T> T load(String filename) throws FileNotFoundException {
		Gson gson = new Gson();
		T ret;
		try {
			FileInputStream fis = ctx.openFileInput(filename);
			InputStreamReader reader = new InputStreamReader(fis);
			
			ret = gson.fromJson(reader, (new TypeToken<T>() {}).getType());
			
			fis.close();
		} catch (IOException e) {
			Log.e("GSONIOManager", "file read failed");
			ret = null;
		}
		return ret;
	}
	
	public <T> void save(T toSave, String filename) {
		Gson gson = new Gson();
		try {
			FileOutputStream fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
			OutputStreamWriter writer = new OutputStreamWriter(fos);
			gson.toJson(toSave, (new TypeToken<T>() {}).getType(), writer);
			writer.flush();
			fos.close();
		} catch (IOException e) {
			Log.e("GSONIOManager", "file read failed");
		}
	}
}
