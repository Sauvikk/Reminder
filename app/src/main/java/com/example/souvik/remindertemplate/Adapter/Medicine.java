package com.example.souvik.remindertemplate.Adapter;

public class Medicine {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(int headerColor) {
        this.headerColor = headerColor;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public int getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(int statusIcon) {
        this.statusIcon = statusIcon;
    }

    int photoId;
    int headerColor;

    String schedule;
    String instruction;
    String statusString;
    int statusIcon;

    public long getItem_id() {
        return item_id;
    }

    public void setItem_id(long item_id) {
        this.item_id = item_id;
    }

    long item_id;

    public Medicine(String name,int photoId, int headerColor,
             String schedule,String instruction, String statusString, int statusIcon ) {
        this.name = name;
        this.photoId = photoId;
        this.headerColor = headerColor;
        this.schedule=schedule;
        this.instruction =instruction;
        this.statusString=statusString;
        this.statusIcon=statusIcon;
    }

    public Medicine(){

    }
}