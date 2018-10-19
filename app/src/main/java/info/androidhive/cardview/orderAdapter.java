package info.androidhive.cardview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by EBU on 12/2/2017.
 */

public class orderAdapter extends RecyclerView.Adapter<orderAdapter.MyViewHolder> {

    private Context mContext;
    private List<OrderList> orderList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amt,pay,date,oid,brand,status,qty,sizes;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            amt = (TextView) view.findViewById(R.id.amt);
            pay = (TextView) view.findViewById(R.id.pay);
            date = (TextView) view.findViewById(R.id.date);
            qty = (TextView) view.findViewById(R.id.qtys);
            sizes = (TextView) view.findViewById(R.id.sizes);
            status = (TextView) view.findViewById(R.id.status);
            brand = (TextView) view.findViewById(R.id.brand);
            oid = (TextView) view.findViewById(R.id.oid);

            cardView = (CardView) view.findViewById(R.id.card_view1);
        }
    }


    public orderAdapter(Context mContext, List<OrderList> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    @Override
    public orderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order, parent, false);

        return new orderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final orderAdapter.MyViewHolder holder, int position) {
        OrderList his = orderList.get(position);
        holder.amt.setText(his.getAmt());
        holder.sizes.setText(his.getSize());
        holder.date.setText(his.getDate());
        holder.status.setText(his.getStatus());
        holder.qty.setText(his.getQty());
        holder.brand.setText(his.getBrand());
        holder.oid.setText(his.getOid());
        holder.pay.setText(his.getPay());
        //holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
    }





    @Override
    public int getItemCount() {
        return orderList.size();
    }

}

