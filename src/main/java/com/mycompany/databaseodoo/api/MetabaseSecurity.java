package com.mycompany.databaseodoo.api;

import com.mycompany.databaseodoo.model.MetabaseUser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class MetabaseSecurity {

    private final String baseUrl;
    private final String token;

    public MetabaseSecurity(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    // Helpers
    private HttpClient client() {
        return HttpClient.newHttpClient();
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("X-Metabase-Session", token)
                .header("Content-Type", "application/json");
    }

    // Crear Usuaris
    public MetabaseUser createUser(String firstName, String lastName,
            String email, String password) throws Exception {
        JSONObject body = new JSONObject()
                .put("first_name", firstName)
                .put("last_name", lastName)
                .put("email", email)
                .put("password", password);

        HttpRequest request = baseRequest("/api/user")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body().trim();
        System.out.println("createUser resposta: " + responseBody);

        JSONObject u = new JSONObject(responseBody);

        if (!u.has("id")) {
            System.out.println("L'usuari ja existeix o no s'ha pogut crear. Buscant usuari existent amb email: " + email);
            List<MetabaseUser> users = listUsers();
            for (MetabaseUser existing : users) {
                if (email.equalsIgnoreCase(existing.getEmail())) {
                    System.out.println("Usuari existent trobat: " + existing);
                    return existing;
                }
            }
            throw new Exception("No s'ha pogut crear ni trobar l'usuari amb email: " + email);
        }

        MetabaseUser user = parseUser(u);
        System.out.println("Usuari creat: " + user);
        return user;
    }
    
    // Mostrar Users
    public List<MetabaseUser> listUsers() throws Exception {
        HttpRequest request = baseRequest("/api/user").GET().build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        JSONObject root = new JSONObject(response.body());
        JSONArray arr = root.has("data")
                ? root.getJSONArray("data")
                : new JSONArray(response.body());

        List<MetabaseUser> result = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            MetabaseUser u = parseUser(arr.getJSONObject(i));
            result.add(u);
            System.out.printf("Usuari → ID: %d | Nom: %s | Email: %s%n",
                    u.getId(), u.getFullName(), u.getEmail());
        }
        return result;
    }

    // Desactivar Users
    public void deactivateUser(int userId) throws Exception {
        HttpRequest request = baseRequest("/api/user/" + userId)
                .DELETE()
                .build();
        client().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Usuari " + userId + " desactivat.");
    }

    // Crear Grup
    public JSONObject createGroup(String groupName) throws Exception {
        JSONObject body = new JSONObject().put("name", groupName);

        HttpRequest request = baseRequest("/api/permissions/group")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body().trim();
        System.out.println("createGroup resposta: " + responseBody);

        JSONObject group = null;

        if (responseBody.startsWith("{")) {
            JSONObject parsed = new JSONObject(responseBody);
            if (parsed.has("id")) {
                group = parsed;
            }
        } else if (responseBody.startsWith("[")) {
            JSONArray arr = new JSONArray(responseBody);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject g = arr.getJSONObject(i);
                if (groupName.equals(g.optString("name"))) {
                    group = g;
                    break;
                }
            }
            if (group == null && arr.length() > 0) {
                group = arr.getJSONObject(arr.length() - 1);
            }
        }

        if (group == null) {
            System.out.println("El grup ja existeix o la resposta no és JSON. Buscant grup existent amb nom: " + groupName);
            JSONArray groups = listGroups();
            for (int i = 0; i < groups.length(); i++) {
                JSONObject g = groups.getJSONObject(i);
                if (groupName.equals(g.optString("name"))) {
                    group = g;
                    break;
                }
            }
        }

        if (group == null) {
            throw new Exception("No s'ha pogut crear ni trobar el grup amb nom: " + groupName);
        }

        System.out.printf("Grup → ID: %d | Nom: %s%n",
                group.getInt("id"), group.getString("name"));
        return group;
    }

    // Mostra Grups
    public JSONArray listGroups() throws Exception {
        HttpRequest request = baseRequest("/api/permissions/group").GET().build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body().trim();
        JSONArray groups;

        if (responseBody.startsWith("{")) {
            JSONObject root = new JSONObject(responseBody);
            groups = root.has("data")
                    ? root.getJSONArray("data")
                    : new JSONArray();
        } else {
            groups = new JSONArray(responseBody);
        }

        for (int i = 0; i < groups.length(); i++) {
            JSONObject g = groups.getJSONObject(i);
            System.out.printf("Grup → ID: %d | Nom: %s%n",
                    g.getInt("id"), g.getString("name"));
        }
        return groups;
    }
    
    // Affegir Usuari al Grup
    public void addUserToGroup(int groupId, int userId) throws Exception {
        JSONObject body = new JSONObject().put("user_id", userId);

        HttpRequest request = baseRequest(
                "/api/permissions/group/" + groupId + "/membership")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        System.out.printf("Usuari %d afegit al grup %d%n", userId, groupId);
    }

    // Permisos de col·leccions
    public void setCollectionPermissions(int groupId, int collectionId,
            String access) throws Exception {

        HttpRequest getReq = baseRequest("/api/collection/graph").GET().build();
        HttpResponse<String> getResp = client().send(
                getReq, HttpResponse.BodyHandlers.ofString());

        String graphBody = getResp.body().trim();
        System.out.println("collection/graph resposta: " + graphBody);

        JSONObject graph = new JSONObject(graphBody);
        JSONObject groups = graph.getJSONObject("groups");
        String gKey = String.valueOf(groupId);
        String cKey = String.valueOf(collectionId);

        if (!groups.has(gKey)) {
            groups.put(gKey, new JSONObject());
        }
        if (access != null) {
            groups.getJSONObject(gKey).put(cKey, access);
        } else {
            groups.getJSONObject(gKey).remove(cKey);
        }

        HttpRequest putReq = baseRequest("/api/collection/graph")
                .PUT(HttpRequest.BodyPublishers.ofString(graph.toString()))
                .build();
        HttpResponse<String> putResp = client().send(
                putReq, HttpResponse.BodyHandlers.ofString());

        System.out.printf("Permís '%s' → grup %d sobre col·lecció %d%n",
                access, groupId, collectionId);
    }

    // Helper privat
    private MetabaseUser parseUser(JSONObject u) {
        MetabaseUser user = new MetabaseUser(
                u.getInt("id"),
                u.optString("first_name", ""),
                u.optString("last_name", ""),
                u.optString("email", ""),
                u.optBoolean("is_active", true),
                u.optBoolean("is_superuser", false)
        );
        user.setDateJoined(u.optString("date_joined", ""));
        user.setLastLogin(u.optString("last_login", ""));
        return user;
    }
}
