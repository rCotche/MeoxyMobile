package com.example.exovolley;

public class Category {
    private int id;
    private String name;
    private String description;
    private String picture;

    Category() {}

    Category(int id, String name, String description, String picture) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String getPicture() {
        return picture;
    }

    void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}
