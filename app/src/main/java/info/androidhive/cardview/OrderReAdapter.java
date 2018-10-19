package info.androidhive.cardview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;


/**
 * Created by EBU on 3/31/2018.
 */

public class OrderReAdapter extends RecyclerView.Adapter<OrderReAdapter.MyViewHolder> {

    private Context mContext;
    private List<info.androidhive.cardview.List> list;
    private RefillActivity main;
    static int i = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,dist;
        public ImageView thumbnail;
        public RelativeLayout rView;
        public ImageButton dot;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.size);
            dist = (TextView) view.findViewById(R.id.price);
            thumbnail = (ImageView) view.findViewById(R.id.icon);
            dot = (ImageButton)view.findViewById(R.id.cancel);
            rView = (RelativeLayout) view.findViewById(R.id.top);
        }
    }

    public OrderReAdapter(Context context, List<info.androidhive.cardview.List> list) {
        mContext = context;
        this.list = list;
        main = (RefillActivity) mContext;
    }

    @Override
    public OrderReAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list, parent, false);

        return new OrderReAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrderReAdapter.MyViewHolder holder, final int position) {

        int[] ids = {R.drawable.three,R.drawable.five,R.drawable.seven,R.drawable.ten,
                R.drawable.twel,R.drawable.twen,R.drawable.fif};
        int[] colors = {R.color.red,R.color.blue,R.color.green,R.color.yellow,R.color.grey};

        Drawable color = new ColorDrawable(mContext.getResources().getColor(colors[i%5]));
        Drawable image = mContext.getResources().getDrawable(ids[i%7]);
        image.setTint(ContextCompat.getColor(mContext, R.color.tw__solid_white));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
        holder.thumbnail.setImageDrawable(ld);



        final info.androidhive.cardview.List mlist = list.get(position);
        holder.name.setText(mlist.getSize());
        holder.dist.setText("\u20A6"+String.valueOf(mlist.getPrice()));


        

        // loading album cover using Glide library
        //Glide.with(mContext).load(ld).into(holder.thumbnail);
        i++;

        holder.dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    removeItem(mlist.getSize());
                    list.remove(position);
                    notifyItemRemoved(position);
                    //this line below gives you the animation and also updates the
                    //list items after the deleted item
                    notifyItemRangeChanged(position, getItemCount());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //showPopupMenu(holder.dot);

            }
        });
    }


    public void removeItem(String s){
        if(s.equals("3KG")){
            main.v[0] -= 1;
            if(main.priceTotal >= main.price[0])
                main.priceTotal -= main.price[0];
        }
        else if(s.equals("5KG")){
            main.v[1] -= 1;
            if(main.priceTotal >= main.price[1])
                main.priceTotal -= main.price[1];
        }
        else if(s.equals("7KG")){
            main.v[2] -= 1;
            if(main.priceTotal >= main.price[2])
                main.priceTotal -= main.price[2];
        }
        if(s.equals("10KG")){
            main.v[3] -= 1;
            if(main.priceTotal >= main.price[3])
                main.priceTotal -= main.price[3];
        }
        if(s.equals("12.5KG")){
            main.v[4] -= 1;
            if(main.priceTotal >= main.price[4])
                main.priceTotal -= main.price[4];
        }
        if(s.equals("25KG")){
            main.v[5] -= 1;
            if(main.priceTotal >= main.price[5])
                main.priceTotal -= main.price[5];
        }
        if(s.equals("50KG")){
            main.v[6] -= 1;
            if(main.priceTotal >= main.price[6])
                main.priceTotal -= main.price[6];
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
