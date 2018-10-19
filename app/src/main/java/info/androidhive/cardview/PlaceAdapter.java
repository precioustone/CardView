package info.androidhive.cardview;

import android.content.Context;
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

import java.util.List;

/**
 * Created by EBU on 3/22/2018.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {

    private Dashboard dashboard;
    private Context mContext;
    private List<Places> PlaceList;
    private MainActivity main;
    private Swipe n;
    String type;
    static int i = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,dist,iconTxt;
        public ImageView thumbnail;
        public RelativeLayout rView;
        public ImageButton dot;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.location);
            dist = (TextView) view.findViewById(R.id.dist);
            thumbnail = (ImageView) view.findViewById(R.id.iconImg);
            dot = (ImageButton)view.findViewById(R.id.showMap);
            rView = (RelativeLayout) view.findViewById(R.id.top);
            iconTxt = (TextView) view.findViewById(R.id.iconTxt);
        }
    }


    public PlaceAdapter(Context context,Dashboard dashboard, List<Places> PlaceList) {
        this.dashboard = dashboard;
        mContext = context;
        this.PlaceList = PlaceList;
        n = (Swipe)dashboard;
        main = (MainActivity) mContext;
    }

    @Override
    public PlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stations, parent, false);

        return new PlaceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaceAdapter.MyViewHolder holder, final int position) {

        int[] drawable = {R.drawable.dblue,R.drawable.dred,R.drawable.dgreen,R.drawable.dyellow, R.drawable.dgray};


        final Places places = PlaceList.get(position);
        holder.name.setText(places.getLocation());
        holder.dist.setText(places.getDist());

        String text = holder.name.getText().toString();
        if(text != null){
            String[] c = text.split(" ");
            if(c[1] != null)
                holder.iconTxt.setText(""+c[1].charAt(0));
            else
                holder.iconTxt.setText("T");
        }
        else
            holder.iconTxt.setText("F");

        // loading album cover using Glide library
        Glide.with(mContext).load(drawable[i%5]).into(holder.thumbnail);
        i++;

        holder.dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.places = new LatLng(places.getLat(),places.getLng());
                showPopupMenu(holder.dot);

            }
        });
    }


    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    n.swipe();
                    return true;
                default:
            }
            return false;
        }
    }


    @Override
    public int getItemCount() {
        return PlaceList.size();
    }

    public interface Swipe{
         void swipe();
    }

}

