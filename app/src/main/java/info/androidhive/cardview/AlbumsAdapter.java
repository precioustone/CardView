package info.androidhive.cardview;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;
    private pickCylinder m;
    private OnnextButtonClickedListener n;
    String type;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, tic;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            tic = (ImageView)view.findViewById(R.id.tic);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }


    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        n = (OnnextButtonClickedListener)mContext;
        m = (pickCylinder)mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position) {
        Album album = albumList.get(position);
        final orderActivity or = (orderActivity)mContext;
        holder.title.setText(album.getName());
        holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        if(or.v[holder.getAdapterPosition()] != 0){
            holder.tic.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.tw__solid_white));
        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.tic.getVisibility() == View.GONE){
                    holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.tw__solid_white));
                    holder.tic.setVisibility(View.VISIBLE);
                    m.changeFrag(holder.getAdapterPosition());

                }
                else {
                    holder.tic.setVisibility(View.GONE );
                    holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    m.changeFrag(holder.getAdapterPosition());
                }

            }
        });
    }





    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
