package server;

import custom_exceptions.ManagerSaveException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    HttpClient client;
    private final String apiToken;
    private final String serverURL = "http://localhost:" + KVServer.PORT;;

    public KVTaskClient() {
        this.client = HttpClient.newHttpClient();
        this.apiToken = register();
    }

    private String register() {
        URI url = URI.create(serverURL + "/register/" + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно получить данные", e);
        }
    }

    public void put(String key, String json) {
        URI url = URI.create(serverURL + "/save/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(url).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно загрузить данные", e);
        }
    }

    public String load(String key) {
        URI url = URI.create(serverURL + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно получить данные", e);
        }
    }
}