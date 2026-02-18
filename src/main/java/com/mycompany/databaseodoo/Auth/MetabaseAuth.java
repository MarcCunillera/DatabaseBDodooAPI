package com.mycompany.databaseodoo.Auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 * @author marccunillera
 */

public class MetabaseAuth {

    private final String baseUrl;
    private String sessionToken;

    public MetabaseAuth() {
        this.baseUrl = com.mycompany.databaseodoo.config.MetabaseConfig.getBaseUrl();
    }

    public MetabaseAuth(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Autentica a Metabase i desa el token de sessió.
    public String authenticate(String username, String password) throws Exception {
        String body = new JSONObject()
                .put("username", username)
                .put("password", password)
                .toString();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Error d'autenticació [" + response.statusCode() + "]: " + response.body());
        }

        JSONObject json = new JSONObject(response.body());
        this.sessionToken = json.getString("id");
        System.out.println("Autenticat correctament. Token: " + sessionToken);
        return this.sessionToken;
    }

    // Invalida el token al servidor.
    public void logout() throws Exception {
        if (sessionToken == null) {
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/session"))
                .header("X-Metabase-Session", sessionToken)
                .DELETE()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
        this.sessionToken = null;
        System.out.println("Sessió tancada.");
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
