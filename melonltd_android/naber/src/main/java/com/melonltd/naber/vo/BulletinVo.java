package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

public class BulletinVo {
    public String title;
    public String content_text;
    public String bulletin_category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent_text() {
        return content_text;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public String getBulletin_category() {
        return bulletin_category;
    }

    public void setBulletin_category(String bulletin_category) {
        this.bulletin_category = bulletin_category;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("title",title)
                .add("content_text",content_text)
                .add("bulletin_category",bulletin_category)
                .toString();
    }
}
