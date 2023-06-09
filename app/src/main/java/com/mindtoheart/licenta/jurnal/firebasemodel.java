package com.mindtoheart.licenta.jurnal;

public class firebasemodel {
    private  String title;
    private  String content;
    private String dateAdded;


    public firebasemodel() {

    }

    public firebasemodel(String title, String content, String currentDate) {
        this.title = title;
        this.content = content;
        this.dateAdded=currentDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}


