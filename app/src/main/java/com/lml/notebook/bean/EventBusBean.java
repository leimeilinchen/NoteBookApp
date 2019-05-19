package com.lml.notebook.bean;

public class EventBusBean {
    private String tag;

    public EventBusBean() {
    }

    public EventBusBean(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
