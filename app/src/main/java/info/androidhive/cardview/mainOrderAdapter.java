package info.androidhive.cardview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by EBU on 12/3/2017.
 */

public class mainOrderAdapter extends RecyclerView.Adapter<mainOrderAdapter.MyViewHolder> {

    private Context mContext;
    private List<mainOrder> orderList;
    protected GoogleMap mGoogleMap;
    callDis cD;


    public class MyViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback   {
        public TextView amt,pay,date,oid,brand,status,qty,sizes,add;
        public CardView cardView;
        MapView mapView;
        Button call;

        LatLng center;

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
            add = (TextView) view.findViewById(R.id.addre);
            mapView = (MapView)view.findViewById(R.id.orderMap);
            call = (Button) view.findViewById(R.id.call);

            cardView = (CardView) view.findViewById(R.id.card_view1);

            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

        public void setMapLocation(LatLng latLng) {
            this.center = latLng;

            // If the map is ready, update its content.
            if (mGoogleMap != null) {
                updateMapContents();
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            MapsInitializer.initialize(mContext);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            // If we have map data, update the map content.
            if (center != null) {
                updateMapContents();
            }
        }

        protected void updateMapContents() {
            // Since the mapView is re-used, need to remove pre-existing mapView features.
            mGoogleMap.clear();

            // Update the mapView feature data and camera position.
            mGoogleMap.addMarker(new MarkerOptions().position(center));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(center, 10f);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }


    public mainOrderAdapter(Context mContext,orderListFragment oL, List<mainOrder> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
        cD = (callDis)oL;
    }

    @Override
    public mainOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_order, parent, false);

        return new mainOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final mainOrderAdapter.MyViewHolder holder, int position) {
        final mainOrder his = orderList.get(position);
        holder.amt.setText(his.getAmt());
        holder.sizes.setText(his.getSizes());
        holder.date.setText(his.getDate());
        holder.status.setText(his.getStatus());
        holder.qty.setText(his.getQty());
        holder.brand.setText(his.getBrand());
        holder.oid.setText(his.getOid());
        holder.pay.setText(his.getPay());
        holder.add.setText(his.getAdd());

        LatLng latLng = new LatLng(his.getLat(),his.getLng());

        holder.setMapLocation(latLng);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (his.getDisId() == 0){
                    Toast.makeText(mContext,"No Dispatcher Assigned yet",Toast.LENGTH_LONG).show();
                }
                else {
                    cD.call(his.getDisId());
                }
            }
        });

        //holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
    }


    interface callDis{
        void call(int id);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

}
