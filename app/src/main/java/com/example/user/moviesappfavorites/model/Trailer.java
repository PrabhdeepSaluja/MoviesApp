package com.example.user.moviesappfavorites.model;

public class Trailer {
    private String id;
    private String name;
    private String key;

    // constructor
    public Trailer(){
    }

    public Trailer(String id, String name, String key){
        this.id = id;
        this.name = name;
        this.key = key;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}