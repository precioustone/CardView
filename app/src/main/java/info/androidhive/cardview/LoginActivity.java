package info.androidhive.cardview;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.cardview.helper.SQLiteHandler;
import info.androidhive.cardview.helper.SessionManager;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static String URL_WALLET = "https://gasnownow.ng/rest_api/userDetails.php";
    private ProgressDialog pDialog;
    RequestQueue rq;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");


        mFirebaseAuth = FirebaseAuth.getInstance();

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        rq = Volley.newRequestQueue(this);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                } else {
                    // not signed in

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.new_logo)
                                    .setTheme(R.style.AppTheme_SecondaryColor2)
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setTosUrl("http://gasnownow.com/privacy-policy.html")
                                    .setPrivacyPolicyUrl("http://gasnownow.com/privacy-policy.html")
                                    .build(),
                                    //.setTheme(R.style.background)

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
                //userDetails();
                session.setLogin(true);
                checkNewUser();
                //startActivity(new Intent(LoginActivity.this,MainActivity.class));

                //Toast.makeText(this,"Sign In",Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(this, "Sign in is cancelled", Toast.LENGTH_SHORT).show();
                this.finishAffinity();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        checkConnection();
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
            Snackbar.make(findViewById(R.id.login),message,Snackbar.LENGTH_LONG)
                    .setAction("Action",null).setActionTextColor(color).show();
            if(mFirebaseAuth != null){
                mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            }
        } else {

            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Snackbar.make(findViewById(R.id.login),message,Snackbar.LENGTH_LONG)
                    .setAction("Action",null).setActionTextColor(color).show();
            RelativeLayout rvi = findViewById(R.id.rVi);
            rvi.setVisibility(View.VISIBLE);

        }


    }


    public void userDetails() {
        DialogFragment newFragment = new extra();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(),"USER DETAILS");
    }

    public void checkNewUser(){
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(LoginActivity.this,response,Toast.LENGTH_LONG).show();
                hideDialog();
                try {
                   // Toast.makeText(LoginActivity.this,"check 0",Toast.LENGTH_LONG).show();
                    JSONObject jObj = new JSONObject(response);


                    // Check for error node in json

                    // Now store the user in SQLite
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");
                    //Toast.makeText(LoginActivity.this,status+" "+msg,Toast.LENGTH_LONG).show();

                    if(status.equals("success") && msg.equals("exists")){
                        //Toast.makeText(LoginActivity.this,"check 1",Toast.LENGTH_LONG).show();

                        String uid = jObj.getString("uid");
                        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String add = jObj.getString("adsress").equals("null")?"":jObj.getString("adsress");
                        String phone = jObj.getString("phone");
                        String birth = jObj.getString("birthday").equals("null")?"":jObj.getString("birthday");
                        String created_at = jObj.getString("join_date");

                        // Inserting row in users table
                        db.addUser(name, email, uid,add,phone,birth,created_at);
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    // JSON error
                    Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Snackbar.make(findViewById(R.id.login),"Bad network",Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry",new action()).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();

                Snackbar.make(findViewById(R.id.login),"Bad network",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry",new action()).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());


                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rq.add(strReq );
    }


    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setMessage("Please wait..");
        pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public class action implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            checkNewUser();
        }
    }

}
