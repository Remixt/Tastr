package com.tastr.Other;


public class MenuItem {
    String imagePath;
    String ingredients;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getIngredients() {
        return ingredients;
    }

    public MenuItem() {
        // defaults the image to a 404 image for debugging
        imagePath = "https://firebasestorage.googleapis.com/v0/b/unt-team-project.appspot.com/o/download.jpg?alt=media&token=c1a4bae0-6fb1-487a-b6c5-c9293eb311d1";
        ingredients = "Unknown";
        name = "Unknown";
    }
}