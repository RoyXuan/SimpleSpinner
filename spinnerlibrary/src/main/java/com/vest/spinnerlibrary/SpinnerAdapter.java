package com.vest.spinnerlibrary;

import java.util.List;

/**
 * Created by Administrator on 2017/3/30.
 */
public class SpinnerAdapter<T extends List> extends SimpleSpinnerBaseAdapter {

    public SpinnerAdapter(Channel channel, OnItemClickerListener listener) {
        super(channel, listener);
    }

    @Override
    public String getItemInDataSet(int position) {
        return channel.onShow(position);
    }
}