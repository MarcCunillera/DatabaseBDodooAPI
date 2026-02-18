package com.mycompany.databaseodoo.api;

import com.mycompany.databaseodoo.model.Dashboard;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class MetabaseDashboard {

    private final String baseUrl;
    private final String token;
    private Integer collectionId = null;

    public MetabaseDashboard(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    // Configurar en que coleccion se crean los dashboards
    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
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

    // Llistar tots els dashboards
    public List<Dashboard> listDashboards() throws Exception {
        HttpRequest request = baseRequest("/api/dashboard").GET().build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        JSONArray arr = new JSONArray(response.body());
        List<Dashboard> result = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject d = arr.getJSONObject(i);
            Dashboard dash = new Dashboard(
                    d.getInt("id"),
                    d.getString("name"),
                    d.optString("description", ""),
                    d.optString("created_at", ""),
                    d.optBoolean("archived", false),
                    d.isNull("public_uuid") ? null : d.getString("public_uuid")
            );
            result.add(dash);
        }
        return result;
    }

    // Buscar un dashboard por nombre
    public Dashboard findDashboardByName(String name) throws Exception {
        List<Dashboard> dashboards = listDashboards();
        for (Dashboard d : dashboards) {
            if (name.equals(d.getName()) && !d.isArchived()) {
                System.out.println("Dashboard ya existe: " + d.getName());
                return d;
            }
        }
        return null;
    }

    // Crear un dashboard
    public Dashboard createDashboard(String name, String description) throws Exception {
        Dashboard existing = findDashboardByName(name);
        if (existing != null) return existing;

        JSONObject body = new JSONObject()
                .put("name", name)
                .put("description", description);
        
        if (collectionId != null) {
            body.put("collection_id", collectionId);
        }

        HttpRequest request = baseRequest("/api/dashboard")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        JSONObject d = new JSONObject(response.body());
        Dashboard dash = new Dashboard(
                d.getInt("id"),
                d.getString("name"),
                d.optString("description", ""),
                d.optString("created_at", ""),
                false, null
        );
        System.out.println("Dashboard creado: " + dash.getName());
        return dash;
    }

    // Añadir una card al dashboard
    public void addCardToDashboard(int dashboardId, int cardId, int row, int col, int width, int height) throws Exception {
        // Comprobar si la card ya esta en el dashboard
        HttpRequest getReq = baseRequest("/api/dashboard/" + dashboardId).GET().build();
        HttpResponse<String> getResp = client().send(getReq, HttpResponse.BodyHandlers.ofString());
        JSONObject dashDetail = new JSONObject(getResp.body());

        if (dashDetail.has("dashcards")) {
            JSONArray dashcards = dashDetail.getJSONArray("dashcards");
            for (int i = 0; i < dashcards.length(); i++) {
                JSONObject dc = dashcards.getJSONObject(i);
                
                // Verificar si tiene el campo "card" con un objeto dentro
                if (dc.has("card") && !dc.isNull("card")) {
                    JSONObject card = dc.getJSONObject("card");
                    int existingCardId = card.optInt("id", -1);
                    if (existingCardId == cardId) {
                        System.out.println("Card ya esta en el dashboard, se omite");
                        return;
                    }
                }
            }
        }

        // Añadir la card
        JSONObject body = new JSONObject()
                .put("cardId", cardId)
                .put("row", row)
                .put("col", col)
                .put("size_x", width)
                .put("size_y", height);

        HttpRequest request = baseRequest("/api/dashboard/" + dashboardId + "/cards")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client().send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Card añadida al dashboard");
    }

    // Publicar el dashboard (hacerlo publico)
    public String publishDashboard(int dashboardId) throws Exception {
        HttpRequest request = baseRequest("/api/dashboard/" + dashboardId + "/public_link")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        String uuid = new JSONObject(response.body()).getString("uuid");
        return baseUrl + "/public/dashboard/" + uuid;
    }
}