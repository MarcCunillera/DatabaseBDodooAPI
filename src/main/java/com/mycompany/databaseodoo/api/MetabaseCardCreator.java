package com.mycompany.databaseodoo.api;

import com.mycompany.databaseodoo.model.Card;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class MetabaseCardCreator {

    private final String baseUrl;
    private final String token;
    private final int databaseId;
    private Integer collectionId = null;

    public MetabaseCardCreator(String baseUrl, String token, int databaseId) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.databaseId = databaseId;
    }

    // Configurar en que coleccion se crean las cards
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

    // Llistar totes les cards
    public List<Card> listCards() throws Exception {
        HttpRequest request = baseRequest("/api/card").GET().build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        JSONArray arr = new JSONArray(response.body());
        List<Card> result = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject c = arr.getJSONObject(i);
            Card card = new Card(
                    c.getInt("id"),
                    c.getString("name"),
                    c.optString("display", "table"),
                    c.optString("description", ""),
                    c.optString("created_at", ""),
                    databaseId
            );
            result.add(card);
        }
        return result;
    }

    // Buscar una card por nom
    public Card findCardByName(String name) throws Exception {
        List<Card> cards = listCards();
        for (Card c : cards) {
            if (name.equals(c.getName())) {
                System.out.println("Card ya existe: " + c.getName());
                return c;
            }
        }
        return null;
    }

    // CARD 1: Facturado Hoy
    public Card createInvoicedTodayCard() throws Exception {
        Card existing = findCardByName("Facturado Hoy");
        if (existing != null) {
            return existing;
        }

        String sql = "SELECT SUM(amount_total) AS facturado_hoy "
                + "FROM account_move "
                + "WHERE move_type = 'out_invoice' AND state = 'posted' "
                + "AND DATE(invoice_date) = CURRENT_DATE";

        return createCard("Facturado Hoy", "scalar", sql);
    }

    // CARD 2: Facturacion Total
    public Card createTotalBilling() throws Exception {
        Card existing = findCardByName("Facturación Total");
        if (existing != null) {
            return existing;
        }

        String sql = "SELECT SUM(amount_total) AS facturacion_total "
                + "FROM account_move "
                + "WHERE move_type = 'out_invoice' AND state = 'posted'";

        return createCard("Facturación Total", "number", sql);
    }

    // CARD 3: Media por Factura
    public Card createAverageInvoiceCard() throws Exception {
        Card existing = findCardByName("Media por Factura");
        if (existing != null) {
            return existing;
        }

        String sql = "SELECT AVG(amount_total) AS media_facturacion "
                + "FROM account_move "
                + "WHERE move_type = 'out_invoice' AND state = 'posted'";

        return createCard("Media por Factura", "number", sql);
    }

    // Metodo interno para crear una card
    private Card createCard(String name, String display, String sql) throws Exception {
        JSONObject query = new JSONObject()
                .put("type", "native")
                .put("database", databaseId)
                .put("native", new JSONObject()
                        .put("query", sql)
                        .put("template-tags", new JSONObject()));

        JSONObject body = new JSONObject()
                .put("name", name)
                .put("display", display)
                .put("dataset_query", query)
                .put("visualization_settings", new JSONObject());

        if (collectionId != null) {
            body.put("collection_id", collectionId);
        }

        HttpRequest request = baseRequest("/api/card")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client().send(
                request, HttpResponse.BodyHandlers.ofString());

        JSONObject c = new JSONObject(response.body());
        Card card = new Card(
                c.getInt("id"),
                c.getString("name"),
                c.optString("display", display),
                c.optString("description", ""),
                c.optString("created_at", ""),
                databaseId
        );
        System.out.println("Card creada: " + card.getName());
        return card;
    }
}
