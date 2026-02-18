/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.databaseodoo.model;

/**
 *
 * @author marccunillera
 */
public class Card {

    private int id;
    private String name;
    private String display;      // bar, line, table, area...
    private String description;
    private String createdAt;
    private int databaseId;

    public Card() {
    }

    public Card(int id, String name, String display,
            String description, String createdAt, int databaseId) {
        this.id = id;
        this.name = name;
        this.display = display;
        this.description = description;
        this.createdAt = createdAt;
        this.databaseId = databaseId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    @Override
    public String toString() {
        return String.format("Card{id=%d, name='%s', display='%s', databaseId=%d}",
                id, name, display, databaseId);
    }
}
