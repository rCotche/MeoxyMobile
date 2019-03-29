package com.example.exovolley;

public class Post {
    private int id;
    private String name;
    private String description;
    private String picture;


    Post() {
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
        return "Post{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}
