package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.HTTP_Manager.HTTPTaskManager;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

abstract public class AbstractHandler implements HttpHandler {

    protected final HTTPTaskManager manager;
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new AbstractHandler.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new AbstractHandler.DurationAdapter())
            .registerTypeAdapter(List.class, new AbstractHandler.CollectionAdapter())
            .create();

    public AbstractHandler(HTTPTaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes());
    }

    protected void sendResponse(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes();
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
    }

    // Адаптер для сериализации и десериализации объектов LocalDateTime
    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime != null) {
                jsonWriter.value(localDateTime.format(formatter));
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            String string = jsonReader.nextString();
            if (!string.isBlank()) {
                return LocalDateTime.parse(string, formatter);
            } else {
                return null;
            }
        }
    }

    // Адаптер для сериализация и десериализации объектов Duration в формате количества минут
    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            if (duration != null) {
                jsonWriter.value(duration.toMinutes());
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            String string = jsonReader.nextString();
            if (!string.isBlank()) {
                long minutes = Long.parseLong(string);
                return Duration.ofMinutes(minutes);
            } else {
                return null;
            }
        }
    }

    // Адаптер для сериализация коллекций (List) в формате JSON
    public static class CollectionAdapter implements JsonSerializer<List<?>> {
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

    public void write(JsonWriter writer, String value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.value(value);
    }
}


