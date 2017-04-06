package com.vest.spinnerlibrary;

/**
 * Created by Administrator on 2017/3/30.
 */
public interface Channel {

    int onCount();

    String onShow(int position);

    String onSelected(int position);

    void onResultPosition(int position);
}
