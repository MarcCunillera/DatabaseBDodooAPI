/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.databaseodoo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author marccunillera
 */
public class MetabaseConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = MetabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("metabase.properties")) {
            if (in == null) {
                throw new RuntimeException("No es troba metabase.properties a resources");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error carregant metabase.properties", e);
        }
    }

    public static String getBaseUrl() {
        return props.getProperty("metabase.base-url");
    }

    public static String getUsername() {
        return props.getProperty("metabase.username");
    }

    public static String getPassword() {
        return props.getProperty("metabase.password");
    }

    public static int getDatabaseId() {
        return Integer.parseInt(props.getProperty("metabase.database-id"));
    }
}
