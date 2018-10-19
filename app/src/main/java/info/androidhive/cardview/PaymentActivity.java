package info.androidhive.cardview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

public class PaymentActivity extends AppCompatActivity implements payOnNextbuttonClickListener {

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference,mOrder;
    user User;
    String add;
    int newuser;
    public static double price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setupActionBar();
        //FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Users");
        mOrder = mFirebaseDatabase.getReference().child("Orders");


        Intent intent = getIntent();
        add = intent.getStringExtra("Address");
        price = intent.getDoubleExtra("price",0.0);
        initportraitScreen();

        if (findViewById(R.id.frag_cont) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            Fragment firstFragment;
            // Create a new Fragment to be placed in the activity layout

            firstFragment = new infoFragmen();


            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_cont, firstFragment,"infoFragment").commit();
        }


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {

                } else {
                    // not signed in
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build()))
                                    .setTosUrl("https://swiftgas-1507735180355.firebaseapp.com")
                                    .setPrivacyPolicyUrl("https://swiftgas-1507735180355.firebaseapp.com")
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"Sign In",Toast.LENGTH_SHORT).show();
                newuser = 1;

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in is cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            //mMessagesDatabaseReference = null;
            //mMessagesDatabaseReferenceOrder = null;
        }
    }




    public void onSignedOutCleanup() {
        //detachDatabaseReadListener();
    }

    public void adduser() {
        this.User.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
        mMessagesDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(this.User);
    }
    public String addOrder(){
        String key;
        key = mOrder.push().getKey();
                mOrder.child(key).setValue(Cart.getOrder());
        return key;
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.gas);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }


    public void nextButtonClicked(String name) {
            if(name.equals("pay")) {
                /*paymentFragment newFragment = new paymentFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.frag_cont, newFragment,"payfragment");
                //transaction.addToBackStack(null);

                transaction.commit();*/
                Intent intent = new Intent(PaymentActivity.this,CheckOutActivity.class);
                intent.putExtra("price",price);
                startActivity(intent);
            }
            else if(name.equals("succ")){
                FinalFragment newFragment = new FinalFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.frag_cont, newFragment,"finalfragment");
                //transaction.addToBackStack(null);

                transaction.commit();
            }

    }

    @Override
    public void onBack() {
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void restart(){

        infoFragmen w = (infoFragmen)getSupportFragmentManager().findFragmentByTag("infoFragment");
        w.useG();
    }

    private void initportraitScreen(){
        PaymentActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics metrics = new DisplayMetrics();
        PaymentActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        boolean isportrait = (metrics.heightPixels >= metrics.widthPixels);

        if(!isportrait){
            if(PaymentActivity.this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                PaymentActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else {
                PaymentActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else {
            PaymentActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }

}
