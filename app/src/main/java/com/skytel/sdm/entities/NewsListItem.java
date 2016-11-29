package com.skytel.sdm.entities;

/**
 * Created by Altanchimeg on 7/20/2016.
 */

public class NewsListItem {
    private int id;
    private int newsListItemId;
    private String title;
    private String intro;
    private String image;
    private String createdDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNewsListItemId() {
        return newsListItemId;
    }

    public void setNewsListItemId(int newsListItemId) {
        this.newsListItemId = newsListItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
