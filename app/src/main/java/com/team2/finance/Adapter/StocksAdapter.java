package com.team2.finance.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.team2.finance.Model.Stock;
import com.team2.finance.R;
import java.util.ArrayList;

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.MyViewHolder> {

    private ArrayList<Stock> stocks;

    public StocksAdapter(ArrayList<Stock> stocks){
        this.stocks = stocks;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView symbol;
        private TextView name;
        private TextView closeRate;

        public MyViewHolder(final View view)
        {
            super(view);
            symbol = view.findViewById(R.id.symbol);
            name = view.findViewById(R.id.name);

            closeRate = view.findViewById(R.id.closerate);
        }
    }
    @NonNull
    @Override
    public StocksAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_card_view,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StocksAdapter.MyViewHolder holder, int position) {
        String symbol = stocks.get(position).getSymbol();
        String name = stocks.get(position).getName();

        String closeRate = stocks.get(position).getCloseRate();

        holder.symbol.setText(symbol);
        holder.name.setText(name);
        holder.closeRate.setText(closeRate);

    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }
}
