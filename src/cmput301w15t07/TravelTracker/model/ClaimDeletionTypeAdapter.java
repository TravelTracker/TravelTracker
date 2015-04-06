package cmput301w15t07.TravelTracker.model;

import java.lang.reflect.Type;

import cmput301w15t07.TravelTracker.util.DeletionFlag;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ClaimDeletionTypeAdapter implements JsonDeserializer<DeletionFlag<Claim>>, JsonSerializer<DeletionFlag<Claim>> {

	@Override
	public JsonElement serialize(DeletionFlag<Claim> claimDeletion, Type type,
			JsonSerializationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeletionFlag<Claim> deserialize(JsonElement element, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

}
