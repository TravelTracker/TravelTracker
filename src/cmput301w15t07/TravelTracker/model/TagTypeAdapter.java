package cmput301w15t07.TravelTracker.model;

import java.lang.reflect.Type;
import java.util.UUID;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TagTypeAdapter implements JsonDeserializer<Tag>, JsonSerializer<Tag> {
    private static String separator = String.valueOf((char) 0xe2ac9b); // Theoretically a lack box

    @Override
    public JsonElement serialize(Tag tag, Type type,
            JsonSerializationContext context) {
        
        String serialized = "";
        
        serialized += tag.getUUID().toString() + separator;
        serialized += tag.getUser().toString() + separator;
        serialized += tag.getTitle();
        
        return new JsonPrimitive(serialized);
    }

    @Override
    public Tag deserialize(JsonElement json, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        String serialized = json.getAsString();
        Log.d("GsonIOManager", serialized);
        
        String[] strings = serialized.split(separator);
        for (int i = 0; i < strings.length; i++)
            Log.d("GsonIOManager", strings[i]);
        
        if (strings.length != 3) {
            throw new RuntimeException("Can't deserialize Tag with fields != 3.");
        }
        
        UUID docID = UUID.fromString(strings[0]);
        UUID userID = UUID.fromString(strings[1]);
        String title = strings[2];
        
        Tag tag = new Tag(docID, userID);
        tag.setTitle(title);
        
        return tag;
    }

}