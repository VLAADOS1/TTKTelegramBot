package com.vlaados.api;

import com.vlaados.config.BotConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ApiClient {
    static BotConfig botConfig = new BotConfig();
    private static final String BASE_URL = botConfig.url();
    private final HttpClient httpClient;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> putTelegramLocal(String telegramId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram/" + telegramId + "/local"))
                .PUT(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public String extractLocal(String telegramId) throws Exception {
        HttpResponse<String> response = getTelegramById(telegramId);

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            String searchKey = "\"local\":";
            int startIndex = responseBody.indexOf(searchKey) + searchKey.length();

            if (responseBody.startsWith("null", startIndex)) {
                return null;
            }

            int valueStartIndex = responseBody.indexOf("\"", startIndex) + 1;
            int valueEndIndex = responseBody.indexOf("\"", valueStartIndex);

            if (valueStartIndex != 0 && valueEndIndex != -1) {
                return responseBody.substring(valueStartIndex, valueEndIndex);
            } else {
                throw new RuntimeException("Failed to parse 'local' from response.");
            }
        } else {
            throw new RuntimeException("Failed to fetch local: " + response.statusCode());
        }
    }

    public HttpResponse<String> updatePhoneNumber(String telegramId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram/" + telegramId + "/phone"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }



    public HttpResponse<String> createRequest(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/request"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getTelegram() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram"))
                .GET()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> getContractInfo(String telegramId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram/" + telegramId + "/contract"))
                .GET()
                .header("Accept", "*/*")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }


    public HttpResponse<String> updateAddress(String telegramId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram/" + telegramId + "/address"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }


    public HttpResponse<String> postTelegram(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram"))
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> registerTelegram(String telegramId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram/" + telegramId + "/register"))
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> loginTelegram(String telegramId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram/" + telegramId + "/login"))
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> getTelegramById(String telegramId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/telegram/" + telegramId))
                .GET()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> getUser() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user"))
                .GET()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> postUser(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user"))
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> loginUser(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user/login"))
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> getUserRole(String username) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user/" + username + "/role"))
                .GET()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> deleteUserRole(String username) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user/" + username + "/role"))
                .DELETE()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> patchUserRole(String username, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/user/" + username + "/role"))
                .method("PATCH", BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> getRequest() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/request"))
                .GET()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> postRequest(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/request"))
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> patchRequestStatus(String requestId, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/request/" + requestId + "/status"))
                .method("PATCH", BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> getEditor() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/editor"))
                .GET()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> getAdmin() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/admin"))
                .GET()
                .build();

        return httpClient.send(request, BodyHandlers.ofString());
    }
}