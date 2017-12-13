package json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import data.Sentences;

public class SentenceDeserializer implements JsonDeserializer<Sentences> {
	@Override
	public Sentences deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}
}
