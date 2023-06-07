package adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;

public class CollectionAdapter implements JsonSerializer<List<?>> {
    @Override
    public JsonElement serialize(List<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty())
            return new JsonArray();
        JsonArray array = new JsonArray();
        for (Object child : src) {
            JsonElement element = context.serialize(child);
            array.add(element);
        }
        return array;
    }
}