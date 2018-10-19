package info.androidhive.cardview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by EBU on 12/2/2017.
 */

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.MyViewHolder> {

private Context mContext;
private List<history> historyList;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView amt,decs,date;
    public CardView cardView;

    public MyViewHolder(View view) {
        super(view);
        amt = (TextView) view.findViewById(R.id.amt);
        decs = (TextView) view.findViewById(R.id.decs);
        date = (TextView) view.findViewById(R.id.date);
        cardView = (CardView) view.findViewById(R.id.card_view1);
    }
}


    public historyAdapter(Context mContext, List<history> historyList) {
        this.mContext = mContext;
        this.historyList = historyList;
    }

    @Override
    public historyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history, parent, false);

        return new historyAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final historyAdapter.MyViewHolder holder, int position) {
        history his = historyList.get(position);
        holder.amt.setText(his.getAmt());
        holder.decs.setText(his.getDecs());
        holder.date.setText(his.getDate());
        //holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
    }





    @Override
    public int getItemCount() {
        return historyList.size();
    }

}

