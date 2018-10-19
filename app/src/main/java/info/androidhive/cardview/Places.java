package info.androidhive.cardview;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by EBU on 3/22/2018.
 */

public class Places {
    private String Location,Dist;
    Double Lat;
    Double Lng;

    public Places(){

    }

    public Places(String Location, String Dist, Double lat, Double lng){
        this.Location = Location;
        this.Dist = Dist;
        this.Lat = lat;
        this.Lng = lng;
    }

    public void setLng(Double lng) {
        this.Lng = lng;
    }

    public void setLat(Double lat) {
        this.Lat = lat;
    }

    public void setDist(String dist) {
        Dist = dist;
    }

    public void setLocation(String name) {
        Location = name;
    }

    public Double getLat() {
        return Lat;
    }

    public Double getLng() {
        return Lng;
    }

    public String getDist() {
        return Dist;
    }

    public String getLocation() {
        return Location;
    }
}
