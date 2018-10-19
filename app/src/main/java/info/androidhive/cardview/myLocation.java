package info.androidhive.cardview;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by EBU on 2/8/2018.
 */

public class myLocation {
    private static LatLng latLng = new LatLng(6.34773,62344);

    public static LatLng getLatLng(){
        return myLocation.latLng;
    }

    public static void setLatLng(LatLng latLng){
        myLocation.latLng = latLng;
    }
}
