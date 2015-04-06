package cmput301w15t07.TravelTracker.util;

import java.io.IOException;
import java.lang.reflect.TypeVariable;

import android.util.Log;
import cmput301w15t07.TravelTracker.model.Document;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DeletionFlagTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!DeletionFlag.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        Log.d("TypeAdapter", "Good type: " + type.getRawType().getCanonicalName());
        for (TypeVariable<?> param : type.getRawType().getTypeParameters()) {
            Log.d("TypeAdapter", "Parameter type: " + param.getBounds()[0] + ", " + param.getBounds().length);
        }
        
        return (TypeAdapter<T>) new DeletionFlagAdapter<Document>();
    }

    
    /**
     * @author Braedy
     *
     * @param <E> A document type.
     */
    public class DeletionFlagAdapter<E extends Document> extends
            TypeAdapter<DeletionFlag<E>> {
        @Override
        public DeletionFlag<E> read(JsonReader reader) throws IOException {
            DeletionFlag<E> flag = new DeletionFlag<E>(null, new TypeToken<E>(){}.getType());
            return null;
        }

        @Override
        public void write(JsonWriter in, DeletionFlag<E> arg1)  throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            
            return null;
        }
    }
}
