package info.androidhive.cardview;


import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import de.hdodenhof.circleimageview.CircleImageView;
import info.androidhive.cardview.PlaceAdapter.Swipe;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Dashboard extends Fragment implements Swipe, MainActivity.viewFinder{

    ImageView order,gas,loc,wallet,profile,history;
    LinearLayout row00,row01,row10,row11,row20,row21;
    ViewFlipper viewFlipper;



    private List<info.androidhive.cardview.Places> PlaceList;
    private PlaceAdapter adapter;
    //private static String URL_WALLET = "https://gasnownow.ng/rest_api/retrieve_order.php";
    //private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private RecyclerView rView;
    public static final String ANONYMOUS = "anonymous";
    private ProgressBar pg;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;


    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;


    private LocationManager locationManager;
    private Criteria criteria;
    private Location location;
    private String myLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    TextView greet;

    private View view;

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            location = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        //mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //initCollapsingToolbar();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //time = (TextView) view.findViewById(R.id.time);
        greet = (TextView) view.findViewById(R.id.greet);
        //date = (TextView) view.findViewById(R.id.date);

        MainActivity main = (MainActivity)getActivity();


        order = (ImageView) view.findViewById(R.id.purchase);
        gas = (ImageView) view.findViewById(R.id.refill);
        loc = (ImageView) view.findViewById(R.id.location);
        wallet = (ImageView) view.findViewById(R.id.wallet);
        profile = (ImageView) view.findViewById(R.id.profile);
        history = (ImageView) view.findViewById(R.id.history);
        row00 = (LinearLayout) view.findViewById(R.id.row00);
        row01 = (LinearLayout) view.findViewById(R.id.row01);
        row10 = (LinearLayout) view.findViewById(R.id.row10);
        row11 = (LinearLayout) view.findViewById(R.id.row11);
        row20 = (LinearLayout) view.findViewById(R.id.row20);
        row21 = (LinearLayout) view.findViewById(R.id.row21);
        viewFlipper = (ViewFlipper)view.findViewById(R.id.viewflip);

        CircleImageView profile = view.findViewById(R.id.backdrop);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
            try {
                Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(grid.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(topCard);
                    grid.setVisibility(View.VISIBLE);
                }else {
                    TransitionManager.beginDelayedTransition(topCard);
                    grid.setVisibility(View.GONE);
                }
            }
        });*/

        loc.setOnClickListener(locc);
        row20.setOnClickListener(locc);

        gas.setOnClickListener(gass);
        row21.setOnClickListener(gass);

        order.setOnClickListener(ord);
        row00.setOnClickListener(ord);

        wallet.setOnClickListener(wal);
        row01.setOnClickListener(wal);

        history.setOnClickListener(pur);
        row10.setOnClickListener(pur);

        profile.setOnClickListener(pro);
        row11.setOnClickListener(pro);

        greetings();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Places");

        rView = (RecyclerView) view.findViewById(R.id.rview5);

        pg = (ProgressBar) view.findViewById(R.id.progress);

        PlaceList = new ArrayList<>();
        adapter = new PlaceAdapter(getActivity(),this, PlaceList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);

        return view;
    }

    View.OnClickListener pur = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity main = (MainActivity)getActivity();
            startActivity(new Intent(getActivity(),TransActivity.class));
        }
    };

    View.OnClickListener pro = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity main = (MainActivity)getActivity();
            startActivity(new Intent(getActivity(),ProfileActivity.class));
        }
    };

    View.OnClickListener wal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity main = (MainActivity)getActivity();
            startActivity(new Intent(getActivity(),accountActivity.class));
        }
    };

    View.OnClickListener ord = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity main = (MainActivity)getActivity();
            Intent intent = new Intent(getActivity(), RefillActivity.class);
            intent.putExtra("Address",main.addr.equals("")?"":main.addr);
            startActivity(intent);
        }
    };

    View.OnClickListener gass = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*if(grid.getVisibility() == View.GONE){
                TransitionManager.beginDelayedTransition(topCard);
                grid.setVisibility(View.VISIBLE);
            }else {
                TransitionManager.beginDelayedTransition(topCard);
                grid.setVisibility(View.GONE);
            }*/
            final MainActivity main = (MainActivity) getActivity();
            main.toolbarImageView.setVisibility(View.VISIBLE);
            main.textView.setText("REFILL STATIONS");
            main.toolbarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.textView.setText("DASHBOARD");
                    main.toolbarImageView.setVisibility(View.GONE);
                    viewFlipper.showPrevious();
                }
            });
            slideRightToLeft(v);
            attachDatabaseReadListener();
        }
    };



    View.OnClickListener locc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity main = (MainActivity)getActivity();
            viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
            main.showMap(0);
        }
    };

    public void slideRightToLeft(View v) {
        viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
        viewFlipper.showNext();
    }


    private void setHeaderTxt(String msg, String date, String time){
        greet.setText(msg);
        //this.date.setText(date);
        //this.time.setText(time);
    }
    private void greetings(){
        Calendar c = Calendar.getInstance();
        String calender = DateFormat.getDateTimeInstance().format(new Date());
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting = null;
        if (timeOfDay >= 0 && timeOfDay < 12){
            greeting = "Good Morning, "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting = "Good Afternoon, "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting = "Good Evening, "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting = "Good Night, "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }

        String[] co = calender.split(" ");

        String dat = (co[0]+" "+co[1]+" "+co[2]);

        String time = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);

        setHeaderTxt(greeting,dat,time);
    }

    @Override
    public void swipe(){
        MainActivity main = (MainActivity)getActivity();
        main.showMap(1);
    }

    public  void fillRecycler(){
        //Toast.makeText(getActivity(),"Got here as well",Toast.LENGTH_LONG).show();
        if(PlaceList.size() > 0 ) {
            adapter.notifyDataSetChanged();
        }
        pg.setVisibility(View.GONE);
    }

    private void attachDatabaseReadListener(){
        pg.setVisibility(View.VISIBLE);
        if(mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    info.androidhive.cardview.Places place = dataSnapshot.getValue(info.androidhive.cardview.Places.class);

                    PlaceList.add(place);

                    fillRecycler();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }


        mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public void view(){
        if(viewFlipper.getCurrentView() != view.findViewById(R.id.dash)){
            viewFlipper.showPrevious();
        }
    }

}
