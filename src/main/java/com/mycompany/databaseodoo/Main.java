package com.mycompany.databaseodoo;

import com.mycompany.databaseodoo.Database.MetabaseClient;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) throws Exception {

        // 1. Conectar a Metabase
        MetabaseClient mb = new MetabaseClient().connect();

        // 2. Crear la coleccion API JAVA o creala
        JSONObject coleccion = mb.collections().createCollection("API JAVA");
        int idColeccion = coleccion.getInt("id");

        // 3. Configurar per que tot es crei dintre de "API JAVA"
        mb.cards().setCollectionId(idColeccion);
        mb.dashboards().setCollectionId(idColeccion);

        // 4. Crear les 3 cards
        var cardHoy = mb.cards().createInvoicedTodayCard();
        var cardTotal = mb.cards().createTotalBilling();
        var cardMedia = mb.cards().createAverageInvoiceCard();

        // 5. Crear el dashboard
        var dashboard = mb.dashboards().createDashboard("Resum de Vendes", "Dashboard principal");

        // 6. Afegir les cards al dashboard
        mb.dashboards().addCardToDashboard(dashboard.getId(), cardHoy.getId(), 0, 0, 6, 4);
        mb.dashboards().addCardToDashboard(dashboard.getId(), cardTotal.getId(), 0, 6, 6, 4);
        mb.dashboards().addCardToDashboard(dashboard.getId(), cardMedia.getId(), 4, 0, 6, 4);

        // 7. Publicar el dashboard
        String url = mb.dashboards().publishDashboard(dashboard.getId());
        System.out.println("\n=== DASHBOARD PUBLICADO ===");
        System.out.println("URL: " + url);
        System.out.println("===========================\n");

        // 8. Desconectar
        mb.disconnect();
    }
}
