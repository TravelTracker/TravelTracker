package cmput301w15t07.TravelTracker.model;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class TagTypeAdapter implements JsonDeserializer<Tag>, JsonSerializer<Tag> {
    private static String separator = String.valueOf((char) 0xe2ac9b); // Theoretically a black box

    @Override
    public JsonElement serialize(Tag tag, Type type,
            JsonSerializationContext context) {
        
        JsonObject object = new JsonObject();
        
        object.addProperty("uuid", tag.getUUID().toString());
        object.addProperty("useruuid", tag.getUser().toString());
        object.addProperty("title", tag.getTitle());
        
        Log.d("TypeAdapter", "Serialize: " + object.entrySet().toString());
        
        return object;
    }

    @Override
    public Tag deserialize(JsonElement json, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) throw new RuntimeException("Not a json object.");
        JsonObject object = json.getAsJsonObject();
        
        Log.d("TypeAdapter", "Deserialize: " + object.entrySet().toString());
        
        String uuidStr = object.get("uuid").getAsString();
        String userUUIDStr = object.get("useruuid").getAsString();
        String title = object.get("title").getAsString();
        
        UUID docID = UUID.fromString(uuidStr);
        UUID userID = UUID.fromString(userUUIDStr);
        
        Tag tag = new Tag(docID, userID);
        tag.setTitle(title);
        
        return tag;
    }

//    @Override
//    public Tag read(JsonReader reader) throws IOException {
//        if (reader.peek() == JsonToken.NULL) {
//            reader.nextNull();
//            return null;
//        }
//        
//        reader.beginObject();
//        UUID docID = UUID.fromString(reader.nextString());
//        UUID userID = UUID.fromString(reader.nextString());
//        String title = reader.nextString();
//        reader.endObject();
//        
//        Tag tag = new Tag(docID, userID);
//        tag.setTitle(title);
//        
//        return tag;
//    }
//
//    @Override
//    public void write(JsonWriter writer, Tag tag) throws IOException {
//        if (tag == null) {
//            writer.nullValue();
//            return;
//        }
//        writer.beginObject();
//        writer.value(tag.getUUID().toString());
//        writer.value(tag.getUser().toString());
//        writer.value(tag.getTitle());
//        writer.endObject();
//    }

}