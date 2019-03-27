package com.example.exovolley;

import java.util.Date;

public class Post {
    private int id;
    private String name;
    private String description;
    private String picture;
    private Date date;
    private int likes;
    private int idCategory;

    public Post() {
    }

    public Post(int id) {
        this.id = id;
    }

    public Post(int id, String name, String description, String picture, Date date, int likes, int idCategory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.date = date;
        this.likes = likes;
        this.idCategory = idCategory;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", date=" + date +
                ", likes=" + likes +
                ", idCategory=" + idCategory +
                '}';
    }
}
