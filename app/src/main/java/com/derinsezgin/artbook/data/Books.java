package com.derinsezgin.artbook.data;

public class Books {

    private String title;
    private String writerName;
    private String year;
    private String image;
    private String id;

    public Books(){

    }

    public Books(String title, String writerName, String year, String image) {
        this.title = title;
        this.writerName = writerName;
        this.year = year;
        this.image = image;
    }

    public Books(String title, String writerName, String year, String image, String id) {
        this.title = title;
        this.writerName = writerName;
        this.year = year;
        this.image = image;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
