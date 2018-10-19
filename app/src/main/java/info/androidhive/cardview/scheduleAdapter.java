package info.androidhive.cardview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by EBU on 12/4/2017.
 */

public class scheduleAdapter extends RecyclerView.Adapter<scheduleAdapter.MyViewHolder> {

    public interface   remove{
        void remove(final String v,final String sid);
    }
    private Context mContext;
    private List<schd> albumList;

    public remove rem;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView freq, ldate,ndate,size,qty,hide;
        public Button cancel;
        public CardView vi;

        public MyViewHolder(View view) {
            super(view);
            vi = (CardView) view.findViewById(R.id.sCard);
            freq = (TextView) view.findViewById(R.id.freq_val);
            size = (TextView) view.findViewById(R.id.siz_val);
            hide = (TextView) view.findViewById(R.id.hide);
            qty = (TextView) view.findViewById(R.id.qt_val);
            ndate = (TextView) view.findViewById(R.id.next_del_val);
            ldate = (TextView) view.findViewById(R.id.last_del_val);
            cancel = (Button) view.findViewById(R.id.schd_btn);

        }
    }


    public scheduleAdapter(Context mContext, List<schd> albumList, remove rem) {
        this.mContext = mContext;
        this.rem = rem;
        this.albumList = albumList;

    }

    @Override
    public scheduleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schd, parent, false);

        return new scheduleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final scheduleAdapter.MyViewHolder holder, int position) {
        schd album = albumList.get(position);
        holder.freq.setText(album.getFreq());
        holder.hide.setText(album.getId());
        holder.size.setText(album.getSize());
        holder.qty.setText(album.getQty());
        holder.ndate.setText(album.getNdate());
        holder.ldate.setText(album.getLdate());
        holder.vi.setCardBackgroundColor(mContext.getResources().getColor(R.color.tw__solid_white));
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sid = holder.hide.getText().toString();
                rem.remove("preciousidam@yahoo.com",sid);
            }
        });

    }





    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
