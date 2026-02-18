package com.mycompany.databaseodoo.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class MetabaseCollection {

    private final String baseUrl;
    private final String token;

    public MetabaseCollection(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    private HttpClient client() {
        return HttpClient.newHttpClient();
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("X-Metabase-Session", token)
                .header("Content-Type", "application/json");
    }

    // Llistar col·leccions
    public JSONArray listCollections() throws Exception {
        HttpRequest request = baseRequest("/api/collection").GET().build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        JSONArray collections = new JSONArray(response.body());

        for (int i = 0; i < collections.length(); i++) {
            JSONObject c = collections.getJSONObject(i);
            if ("root".equals(c.opt("id"))) {
                continue;
            }
            System.out.printf("Col·leccio -> ID: %d | Nom: %s%n",
                    c.getInt("id"), c.getString("name"));
        }

        return collections;
    }

    // Buscar col·leccio per nom
    public JSONObject findCollectionByName(String name) throws Exception {
        JSONArray collections = listCollections();

        for (int i = 0; i < collections.length(); i++) {
            JSONObject c = collections.getJSONObject(i);
            if ("root".equals(c.opt("id"))) {
                continue;
            }
            if (name.equals(c.getString("name"))) {
                System.out.println("Col·leccio trobada: " + c.getString("name"));
                return c;
            }
        }

        return null;
    }

    // Crear col·leccio
    public JSONObject createCollection(String name) throws Exception {
        // Buscar si ja existeix
        JSONObject existing = findCollectionByName(name);
        if (existing != null) {
            return existing;
        }

        // Crear nova col·leccio
        JSONObject body = new JSONObject()
                .put("name", name)
                .put("color", "#509EE3");

        HttpRequest request = baseRequest("/api/collection")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        JSONObject collection = new JSONObject(response.body());
        System.out.println("Col·leccio creada: " + collection.getString("name"));

        return collection;
    }
}
