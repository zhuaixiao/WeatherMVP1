package com.example.test.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.R;

import java.util.List;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private List<WeatherforecastItem> list;


    public WeatherRVAdapter(List<WeatherforecastItem> l) {
        list = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_rv, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.forecast_cond.setText(list.get(i).getCond());
        viewHolder.forecast_tmp.setText(list.get(i).getTmpmin() + "℃～" + list.get(i).getTmpmax() + "℃");
        viewHolder.forecast_date.setText(list.get(i).getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView forecast_cond;
        TextView forecast_tmp;
        TextView forecast_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            forecast_cond = itemView.findViewById(R.id.forecast_cond);
            forecast_tmp = itemView.findViewById(R.id.forecast_tmp);
            forecast_date = itemView.findViewById(R.id.forecast_date);
        }
    }
}
