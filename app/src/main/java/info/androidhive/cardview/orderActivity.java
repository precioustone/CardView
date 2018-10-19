package info.androidhive.cardview;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.Key;
import java.util.Arrays;

public class orderActivity extends AppCompatActivity implements OnnextButtonClickedListener,pickCylinder  {


    Button next;

    protected user User;
    protected order Order;
    protected static String adr;
    public static int[] v = new int[7];
    public static double price = 0;

    private static final int logoutCode = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        //FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        setupActionBar();

        initportraitScreen();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        Intent intent = getIntent();
        adr = intent.getStringExtra("Address");


        if (findViewById(R.id.frag_cont) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            Fragment firstFragment;
            // Create a new Fragment to be placed in the activity layout

            firstFragment = new CylinderFragment();


            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_cont, firstFragment).commit();
        }



    }

    public void onSignedInInitialize(){


    }


    public void onSignedOutCleanup(){
        //detachDatabaseReadListener();
    }

    @Override
    public void nextButtonClicked(String name) {
        // Create fragment and give it an argument specifying the article it should show
        Fragment newFragment;
        if(name.equals("order")){
            newFragment = new orderFragment();
            //Bundle args = new Bundle();
            //args.putInt(orderFragment.ARG_POSITION, position);
            //newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.frag_cont, newFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();
        }else if(name.equals("con")) {
            newFragment = new conFragment();
            //Bundle args = new Bundle();
            //args.putInt(orderFragment.ARG_POSITION, position);
            //newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.frag_cont, newFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();
        }
        else if(name.equals("gass")){
            newFragment = new CylinderFragment();
            //Bundle args = new Bundle();
            //args.putInt(orderFragment.ARG_POSITION, position);
            //newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.frag_cont, newFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();
        }

    }

    public String getAdr(){
        return adr;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(),"Pick Date");
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(),"Time picker");
    }

    public void changeAdr(View v) {
        DialogFragment newFragment = new changeAdr();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(),"Change Address");
    }

    @Override
    public void changeFrag(int pos){
        if(this.v[pos] != 0)
            this.v[pos] = 0;
        else {
            //this.v[pos] = 1;
            DialogFragment newFragment = new pickerDialog();
            Bundle args = new Bundle();
            args.putInt("pos", pos);
            newFragment.setArguments(args);
            newFragment.setCancelable(false);
            newFragment.show(getFragmentManager(), "NO OF CYLINDER");
        }
    }

    private void initportraitScreen(){
        orderActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics metrics = new DisplayMetrics();
        orderActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        boolean isportrait = (metrics.heightPixels >= metrics.widthPixels);

        if(!isportrait){
            if(orderActivity.this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                orderActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else {
                orderActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else {
            orderActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        for(int i = 0; i < v.length; i++){
            v[i] = 0;
        }
    }
}

