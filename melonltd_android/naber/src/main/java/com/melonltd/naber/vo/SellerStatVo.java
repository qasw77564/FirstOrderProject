package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

public class SellerStatVo {
    public String year_income;
    public String month_income;
    public String day_income;
    public String finish_count;
    public String[] status_dates;
    public String unfinish_count;
    public String processing_count;
    public String can_fetch_count;
    public String cancel_count;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("year_income",year_income)
                .add("month_income",month_income)
                .add("day_income",day_income)
                .add("finish_count",finish_count)
                .add("status_dates",status_dates)
                .add("unfinish_count",unfinish_count)
                .add("processing_count",processing_count)
                .add("can_fetch_count",can_fetch_count)
                .add("cancel_count",cancel_count)
                .toString();
    }
}
