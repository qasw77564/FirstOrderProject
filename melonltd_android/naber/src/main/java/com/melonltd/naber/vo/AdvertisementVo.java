package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;
import com.melonltd.naber.model.type.LinkType;

public class AdvertisementVo {
    public String title;
    public String content_text;
    public String photo;
    public LinkType link_type;
    public String link_to;

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LinkType getLink_type() {
        return link_type;
    }

    public void setLink_type(LinkType link_type) {
        this.link_type = link_type;
    }

    public String getLink_to() {
        return link_to;
    }

    public void setLink_to(String link_to) {
        this.link_to = link_to;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("title", title)
                .add("content_text", content_text)
                .add("photo", photo)
                .add("link_type", link_type)
                .add("link_to", link_to)
                .toString();
    }
}
