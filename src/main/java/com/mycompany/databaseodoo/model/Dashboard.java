/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.databaseodoo.model;

/**
 *
 * @author marccunillera
 */
public class Dashboard {

    private int id;
    private String name;
    private String description;
    private String createdAt;
    private boolean archived;
    private String publicUuid;   // null si és privat

    public Dashboard() {
    }

    public Dashboard(int id, String name, String description,
            String createdAt, boolean archived, String publicUuid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.archived = archived;
        this.publicUuid = publicUuid;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getPublicUuid() {
        return publicUuid;
    }

    public boolean isPublic() {
        return publicUuid != null && !publicUuid.isEmpty();
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setPublicUuid(String uuid) {
        this.publicUuid = uuid;
    }

    @Override
    public String toString() {
        return String.format(
                "Dashboard{id=%d, name='%s', public=%s, archived=%s}",
                id, name, isPublic(), archived);
    }
}
