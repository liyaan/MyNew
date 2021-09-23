package com.liyaan.mvvm.net.ui.bean;

import java.util.List;

public class ResultBean<T> {

    private String stat;
    private List<T> data;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }


}


