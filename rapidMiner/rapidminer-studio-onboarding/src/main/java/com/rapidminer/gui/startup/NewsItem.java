package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
class NewsItem {
    private String topic;
    private String content;
    private String link;
    private String linkText;

    NewsItem() {
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkText() {
        return this.linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }
}
