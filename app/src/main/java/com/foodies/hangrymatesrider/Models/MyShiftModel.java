package com.foodies.hangrymatesrider.Models;

/**
 * Created by Dinosoftlabs on 1/25/2018.
 */

public class MyShiftModel {
    String id,date,starting_time,created,ending_time,confirm,admin_confirm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStarting_time() {
        return starting_time;
    }

    public void setStarting_time(String starting_time) {
        this.starting_time = starting_time;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getEnding_time() {
        return ending_time;
    }

    public void setEnding_time(String ending_time) {
        this.ending_time = ending_time;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getAdmin_confirm() {
        return admin_confirm;
    }

    public void setAdmin_confirm(String admin_confirm) {
        this.admin_confirm = admin_confirm;
    }
}
