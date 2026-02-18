/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.databaseodoo.Database;

import com.mycompany.databaseodoo.Auth.MetabaseAuth;
import com.mycompany.databaseodoo.api.MetabaseCardCreator;
import com.mycompany.databaseodoo.api.MetabaseCollection;
import com.mycompany.databaseodoo.api.MetabaseDashboard;
import com.mycompany.databaseodoo.api.MetabaseSecurity;
import com.mycompany.databaseodoo.config.MetabaseConfig;

/**
 *
 * @author marccunillera
 */
public class MetabaseClient {

    private final String baseUrl;
    private final MetabaseAuth auth;
    private MetabaseDashboard dashboardApi;
    private MetabaseCardCreator cardApi;
    private MetabaseSecurity securityApi;
    private MetabaseCollection collectionApi;

    // Constructor per defecte (llegeix de metabase.properties)
    public MetabaseClient() {
        this.baseUrl = MetabaseConfig.getBaseUrl();
        this.auth = new MetabaseAuth(this.baseUrl);
    }

    // Constructor manual
    public MetabaseClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.auth = new MetabaseAuth(baseUrl);
    }

    // Autentica i inicialitza totes les APIs
    public MetabaseClient connect() throws Exception {
        return connect(
                MetabaseConfig.getUsername(),
                MetabaseConfig.getPassword(),
                MetabaseConfig.getDatabaseId()
        );
    }

    // Autentica i inicialitza totes les APIs amb credencials manuals.
    public MetabaseClient connect(String username, String password,
            int databaseId) throws Exception {
        String token = auth.authenticate(username, password);

        this.dashboardApi = new MetabaseDashboard(baseUrl, token);
        this.cardApi = new MetabaseCardCreator(baseUrl, token, databaseId);
        this.securityApi = new MetabaseSecurity(baseUrl, token);
        this.collectionApi = new MetabaseCollection(baseUrl, token);

        System.out.println("MetabaseClient connectat a: " + baseUrl);
        return this;
    }

    // Accessors de les APIs 
    public MetabaseDashboard dashboards() {
        checkConnected();
        return dashboardApi;
    }

    public MetabaseCardCreator cards() {
        checkConnected();
        return cardApi;
    }

    public MetabaseSecurity security() {
        checkConnected();
        return securityApi;
    }

    public MetabaseCollection collections() {
        checkConnected();
        return collectionApi;
    }

    // Desconnexio
    public void disconnect() throws Exception {
        auth.logout();
        this.dashboardApi = null;
        this.cardApi = null;
        this.securityApi = null;
        this.collectionApi = null;
        System.out.println("MetabaseClient desconnectat.");
    }

    // Utilitat
    private void checkConnected() {
        if (auth.getSessionToken() == null) {
            throw new IllegalStateException(
                    "No connectat. Executa connect() primer.");
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isConnected() {
        return auth.getSessionToken() != null;
    }
}
