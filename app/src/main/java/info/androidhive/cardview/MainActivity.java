package info.androidhive.cardview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.*;

import info.androidhive.cardview.helper.SessionManager;

public class MainActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        ConnectivityReceiver.ConnectivityReceiverListener{

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    int exitCount = 0;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */

    //private RecyclerView recyclerView;
    //private AlbumsAdapter adapter;
    //private List<Album> albumList;
    public LatLng places;
    private boolean mPermissionDenied = false;
    private static final int logoutCode = 1;

    private SessionManager session;

    private GoogleMap mMap;

    private CameraPosition mCameraPosition;
    private int showLocationOnMap = 0;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    //private PlaceDetectionClient mPlaceDetectionClient;
    private static final String TAG = MainActivity.class.getSimpleName();

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;


    private LocationManager locationManager;
    private Criteria criteria;
    private Location location;
    private String myLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    de.hdodenhof.circleimageview.CircleImageView imageView;
    TextView eText;
    TextView dnTxt;
    public static String addr = "";
    Dashboard dashboard;
    ImageView toolbarImageView;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            location = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setActionBar(toolbar);
        toolbarImageView = findViewById(R.id.toolbarIcon);
        toolbarImageView.setVisibility(View.GONE);
        textView = findViewById(R.id.toolbarText);
        textView.setText("DASHBOARD");

        dashboard = (Dashboard)getSupportFragmentManager().findFragmentById(R.id.dasboard);

        session = new SessionManager(getApplicationContext());

        // Construct a GeoDataClient.
        mGeoDataClient = com.google.android.gms.location.places.Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        //mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //initCollapsingToolbar();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //locate = (TextView) findViewById(R.id.locate);
        //locate.setText(addr);
        //mapFragment.getMapAsync(this);


    }



    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, location);
            super.onSaveInstanceState(outState);
        }
    }

    private View.OnClickListener cardOptListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, orderActivity.class);
            intent.putExtra("Address",addr.equals("")?"":addr);
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        /*dashboard = new Dashboard();

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_cont,dashboard,"DASHBOARD").
                addToBackStack("DASHBOARD").commit();*/
        MyApplication.getInstance().setConnectivityListener(this);
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    interface viewFinder{
        void view();
    }

    @Override
    public void onBackPressed() {
        //getSupportFragmentManager().beginTransaction().show(dashboard);
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentByTag("MAPSS");
        if( mapFragment != null && mapFragment.isVisible()){
            super.onBackPressed();
            if(dashboard.viewFlipper.getCurrentView() == findViewById(R.id.dash)) {
                textView.setText("DASHBOARD");
                toolbarImageView.setVisibility(View.GONE);
            }else {
                textView.setText("REFILL STATIONS");
            }
            showLocationOnMap = 0;
        }
        else {
            ++exitCount;
            if(exitCount == 2){
                this.finishAffinity();
            }

            Toast.makeText(MainActivity.this,"Tap back again to exit",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //maps code
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    public void showMap(int on){
        getSupportFragmentManager().beginTransaction().hide(dashboard);
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.frag_cont, supportMapFragment,"MAPSS").addToBackStack("MAPSS").commit();
        supportMapFragment.getMapAsync(this);

        toolbarImageView.setVisibility(View.VISIBLE);
        toolbarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(on == 1){
            if(this.places != null)
                this.showLocationOnMap = on;
        }
    }

  /* public void showLocationOnMap(){
        if(this.places != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    this.places, 15));
            mMap.addMarker(new MarkerOptions()
                    .position(this.places)
                    .title(getAddress(this.places.latitude, this.places.longitude)));
        }
        else
            enableMyLocation();
    }*/


    public void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        session.setLogin(false);
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }


    /** Enables the My Location layer if the fine location permission has been granted.
     */
    public void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        String loc ="";
        if(location != null) {
            loc = getAddress(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(),
                            location.getLongitude()), 15));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(getAddress(location.getLatitude(), location.getLongitude())));
            addr = getAddress(location.getLatitude(), location.getLongitude());
            //locate.setText("Your location: " + getAddress(location.getLatitude(), location.getLongitude()));
            info.androidhive.cardview.myLocation.setLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        this.addr = loc;
        info.androidhive.cardview.Address.setAddress(loc);
        //locate.setText(loc);
        Toast.makeText(this,loc, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        location = task.getResult();
                        if(location != null) {
                            textView.setText("MY LOCATION");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(),
                                            location.getLongitude()), 15));
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .title(getAddress(location.getLatitude(), location.getLongitude())));
                            addr = getAddress(location.getLatitude(), location.getLongitude());
                            //locate.setText("Your location: " + getAddress(location.getLatitude(), location.getLongitude()));
                        }

                        if(showLocationOnMap == 1){
                            textView.setText("REFILL STATION");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    places, 15));
                            mMap.addMarker(new MarkerOptions()
                                    .position(places)
                                    .title(getAddress(places.latitude, places.longitude)));
                        }


                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        //mMap.moveCamera(CameraUpdateFactory
                        //.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        //mMap.setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            java.util.List<android.location.Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses.size() > 0) {
                android.location.Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();

            /*add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();*/

                Log.v("IGA", "Address" + add);
                return add;
            }
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "You are at this location";
    }
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Connected to internet";
            color = Color.WHITE;
            Snackbar.make(findViewById(R.id.main_content),message,Snackbar.LENGTH_LONG)
                    .setAction("Action",null).setActionTextColor(color).show();
            errorFragment myFragment = (errorFragment) getSupportFragmentManager().findFragmentByTag("ERROR FRAGMENT");

            if (myFragment != null && myFragment.isVisible()) {
                // add your code here
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_cont,dashboard,"DASHBOARD").commit();
            }

            /*if(dashboard != null ){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.layout,dashboard,"DASH FRAGMENT").commit();
            }*/

        } else {

            errorFragment fragmentA = new errorFragment();
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Snackbar.make(findViewById(R.id.main_content),message,Snackbar.LENGTH_LONG)
                    .setAction("Action",null).setActionTextColor(color).show();
            Dashboard myFragment = (Dashboard) getSupportFragmentManager().findFragmentByTag("DASHBOARD");

            // add your code here
            if(myFragment != null && myFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_cont,fragmentA , "ERROR FRAGMENT").commit();
            }
            else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_cont,fragmentA, "ERROR FRAGMENT").commit();
            }


        }

        //Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }



    public class action implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            checkConnection();
        }
    }

}
