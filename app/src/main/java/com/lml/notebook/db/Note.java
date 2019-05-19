package com.lml.notebook.db;


public class Note {

    private int id;


    private String name;
    //The note title
    private String title;

    //The note description
    private String description;

    //The priority of the note.
    private  int priority;



    private String path;
    public Note() {
    }

    //Constructor for the class


    public Note(String name, String title, String description, int priority, String path) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
