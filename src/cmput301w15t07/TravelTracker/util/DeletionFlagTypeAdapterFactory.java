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

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import android.util.Log;
import cmput301w15t07.TravelTracker.model.Document;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author Braedy
 *
 * References https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/TypeAdapterFactory.html
 * heavily.
 *
 */
public class DeletionFlagTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        // TODO technically isAssignableFrom is deprecated and we should change
        // it. Maybe some time in the future.
        if (!DeletionFlag.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        
        Log.d("TypeAdapter", "Good type: " + type.getRawType().getCanonicalName());
        for (Type param : ((ParameterizedType) type.getType()).getActualTypeArguments()) {
            Log.d("TypeAdapter", "Parameter type: " + param.toString());
        }
        
        // Get the parameterizing type
        Type elementType = ((ParameterizedType) type.getType()).getActualTypeArguments()[0];
        
        // Get adapters specific to the object we need so that we can delegate
        TypeAdapter<?> documentAdapter = gson.getAdapter(TypeToken.get(elementType));
        
        return (TypeAdapter<T>) new DeletionFlagAdapter<Document>((TypeAdapter<Document>) documentAdapter);
    }
    
    private class DeletionFlagAdapter<E extends Document> extends
    TypeAdapter<DeletionFlag<E>> {
        private TypeAdapter<E> elementAdapter;

        public DeletionFlagAdapter(TypeAdapter<E> elementAdapter) {
            this.elementAdapter = elementAdapter;
        }

        @Override
        public DeletionFlag<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            // Pre-declare variables
            long time = 0;
            E element = null;
            
            // Read the actual Json
            in.beginObject();
            while (in.hasNext()) {
              String name = in.nextName();
              if (name.equals("date")) {
                  time = in.nextLong();
              } else if (name.equals("toDelete")) {
                  element = (E) elementAdapter.read(in);
              } else {
                in.skipValue();
              }
            }
            in.endObject();
            
            if (time == 0 || element == null) {
                throw new RuntimeException("Malformed DeletionFlag in JSON. Missing a field.");
            }
            
            DeletionFlag<E> flag = new DeletionFlag<E>(new Date(time), element);
            return flag;
        }

        @Override
        public void write(JsonWriter out, DeletionFlag<E> flag)  throws IOException {
            if (flag == null) {
              out.nullValue();
              return;
            }
            
            // Write actual JSON
            out.beginObject();
            out.name("date").value(flag.getDate().getTime());
            out.name("toDelete");
            elementAdapter.write(out, flag.getToDelete());
            out.endObject();
        }
    }
}
