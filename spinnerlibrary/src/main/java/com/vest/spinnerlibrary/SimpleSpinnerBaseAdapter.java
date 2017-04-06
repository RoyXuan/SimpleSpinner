package com.vest.spinnerlibrary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class SimpleSpinnerBaseAdapter<T> extends RecyclerView.Adapter {
    protected final Channel channel;
    protected OnItemClickerListener listener;

    public SimpleSpinnerBaseAdapter(Channel channel, OnItemClickerListener listener) {
        this.channel = channel;
        this.listener = listener;
    }

    public void setListener(OnItemClickerListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return channel.onCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextViewHolder viewHolder = (TextViewHolder) holder;
        viewHolder.textView.setText(getItemInDataSet(position));
    }

    public abstract String getItemInDataSet(int position);

    class TextViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public TextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_tinted_spinner);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    channel.onResultPosition(position);
                    if (view instanceof TextView) {
                        listener.onResult(channel.onSelected(position), position);
                    } else {
                        throw new ClassCastException("this view must be TextView or extends TextView");
                    }
                }
            });
        }
    }

}
