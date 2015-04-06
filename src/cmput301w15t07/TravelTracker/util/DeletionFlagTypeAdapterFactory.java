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
        if (!DeletionFlag.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        
        Log.d("TypeAdapter", "Good type: " + type.getRawType().getCanonicalName());
        for (Type param : ((ParameterizedType) type.getType()).getActualTypeArguments()) {
            Log.d("TypeAdapter", "Parameter type: " + param.toString());
        }
        
        Type elementType = ((ParameterizedType) type.getType()).getActualTypeArguments()[0];
        TypeAdapter<?> documentAdapter = gson.getAdapter(TypeToken.get(elementType));
        TypeAdapter<Type> typeAdapter = gson.getAdapter(Type.class);
        return (TypeAdapter<T>) new DeletionFlagAdapter<Document>((TypeAdapter<Document>) documentAdapter, typeAdapter);
    }
    
    private class DeletionFlagAdapter<E extends Document> extends
    TypeAdapter<DeletionFlag<E>> {
        private TypeAdapter<E> elementAdapter;
        private TypeAdapter<Type> typeAdapter;

        @SuppressWarnings("unused")
        public DeletionFlagAdapter(TypeAdapter<E> elementAdapter, TypeAdapter<Type> typeAdapter) {
            this.elementAdapter = elementAdapter;
            this.typeAdapter = typeAdapter;
        }

        @SuppressWarnings("unchecked")
        @Override
        public DeletionFlag<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            
            in.beginObject();
            long time = in.nextLong();
            Type t = (Type) typeAdapter.read(in);
            E d = (E) elementAdapter.read(in);
            in.endObject();
            
            DeletionFlag<E> flag = new DeletionFlag<E>(new Date(time), d, t);
            return flag;
        }

        @Override
        public void write(JsonWriter out, DeletionFlag<E> flag)  throws IOException {
            if (flag == null) {
              out.nullValue();
              return;
            }
            
            out.beginObject();
            out.name("date").value(flag.getDate().getTime());
            out.name("type");
            typeAdapter.write(out, flag.getWrappedClass());
            out.name("toDelete");
            elementAdapter.write(out, flag.getToDelete());
            out.endObject();
        }
    }
}
