package info.androidhive.cardview;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.androidhive.cardview.R;
import info.androidhive.cardview.AppController;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link walletFragment} factory method to
 * create an instance of this fragment.
 */
public class walletFragment extends Fragment {

    private static String URL_WALLET = "https://gasnownow.ng/rest_api/wallet.php";


    private ProgressBar pg;
    RequestQueue rq;
    private TextView credit,debit,ref,total;

    private static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private RecyclerView rView;
    String sid;
    FloatingActionButton fb;
    LinearLayout mailL;


    public walletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);



        credit = view.findViewById(R.id.credit_val);
        debit = view.findViewById(R.id.debit_val);
        ref = view.findViewById(R.id.ref_val);
        total = view.findViewById(R.id.total_val);
        mailL = view.findViewById(R.id.mainL);

        // Progress dialog
        final accountActivity o = (accountActivity)getActivity();

        fb = view.findViewById(R.id.fab);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.setVisibility(View.GONE);
                fb.setClickable(false);
                o.fund();
            }
        });

        pg = view.findViewById(R.id.pro2);
        rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        getWallet(email);
         return view;
    }

    private void updateFields(String c, String d, String r, String t){
        mailL.setVisibility(View.VISIBLE);
        credit.setText("\u20A6"+c);
        debit.setText("\u20A6"+d);
        ref.setText("\u20A6"+r);
        total.setText("\u20A6"+t);
    }

    public void getWallet(final String email){
        fb.setVisibility(View.VISIBLE);
        fb.setClickable(true);
        String tag_string_req = "req_login";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);


                    // Check for error node in json

                        // Now store the user in SQLite
                    String status = jObj.getString("status");
                    if(status.equals("success")) {
                        String crd = jObj.getString("credit");
                        String deb = jObj.getString("debit");
                        String r = jObj.getString("referal");
                        String tot = jObj.getString("total");

                        updateFields(crd, deb, r, tot);
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                Snackbar.make(rView,"Bad network",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
                new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

                    @Override
                    public void run() {
                        getWallet(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                }, 10000);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);


                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rq.add(strReq );
    }

    private void showDialog() {
        if (pg.getVisibility() == View.GONE)
            pg.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        if (pg.getVisibility() == View.VISIBLE)
            pg.setVisibility(View.GONE);
    }




}
