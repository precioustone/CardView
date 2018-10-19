package info.androidhive.cardview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.cardview.helper.SQLiteHandler;
import info.androidhive.cardview.helper.SessionManager;

public class SplashScreen extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 5000;
	private static String URL_WALLET = "https://gasnownow.ng/rest_api/userDetails.php";
	RequestQueue rq;
	private SessionManager session;
	private SQLiteHandler db;
	private HashMap<String, String> user;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_splash);
		FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
		rq = Volley.newRequestQueue(this);

		session = new SessionManager(getApplicationContext());
		db = new SQLiteHandler(getApplicationContext());
		user = db.getUserDetails();

		if(session.isLoggedIn()){
			db.deleteUsers();
			checkNewUser();

		}else{
			startActivity(new Intent(this,WelcomeActivity.class));
		}

		/*new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			/*@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreen.this, WelcomeActivity.class);
				startActivity(i);
				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);*/

	}

	public void checkNewUser(){
		StringRequest strReq = new StringRequest(Request.Method.POST,
				URL_WALLET, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				//Toast.makeText(LoginActivity.this,response,Toast.LENGTH_LONG).show();
				try {
					// Toast.makeText(LoginActivity.this,"check 0",Toast.LENGTH_LONG).show();
					JSONObject jObj = new JSONObject(response);


					// Check for error node in json

					// Now store the user in SQLite
					String status = jObj.getString("status");
					String msg = jObj.getString("msg");
					//Toast.makeText(LoginActivity.this,status+" "+msg,Toast.LENGTH_LONG).show();

					if(status.equals("success")){
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
						Intent intent = new Intent(SplashScreen.this,WelcomeActivity.class);
						startActivity(intent);
					}
				} catch (JSONException e) {
					// JSON error
					Toast.makeText(SplashScreen.this,e.toString(),Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {


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

}
