package com.melonltd.naber.vo;

import java.io.Serializable;

public class DateRangeVo implements Serializable {
    private static final long serialVersionUID = 1410610929668714215L;

    public String status;
    public String date;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
