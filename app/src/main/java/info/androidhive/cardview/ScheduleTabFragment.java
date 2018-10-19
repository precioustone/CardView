package info.androidhive.cardview;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleTabFragment extends Fragment implements scheduleAdapter.remove {

    private static String URL_SCH = "https://gasnownow.ng/rest_api/schdel.php";
    private static String URL_rSCH = "https://gasnownow.ng/rest_api/schRem.php";

    private ProgressBar pg;
    RequestQueue rq;

    private static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private RecyclerView rView;
    private scheduleAdapter adapter;
    private java.util.List<schd> historyList;
    String sid;
    FloatingActionButton sch;


    public ScheduleTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_schedule, container, false);

        sch = view.findViewById(R.id.schd_btn);
        final TransActivity o = (TransActivity)getActivity();
        sch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ScheduleActivity.class));
            }
        });

        rView = (RecyclerView) view.findViewById(R.id.rview5);

        historyList = new ArrayList<>();
        adapter = new scheduleAdapter(getActivity(), historyList,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);
        pg = view.findViewById(R.id.pro2);
        rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        getSch(email);

        return view;
    }

    public  void fillRecycler(){
        adapter.notifyDataSetChanged();
    }

    public void getSch(final String email){
        //String tag_string_req = "req_login";
        showDialog();
        StringRequest strReqs = new StringRequest(Request.Method.POST,
                URL_SCH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();
                try {
                    JSONArray jObj = new JSONArray(response);

                    if(!historyList.isEmpty()){
                        historyList.clear();
                    }

                    // Check for error node in json
                    schd a;

                    for (int i = 0; i < jObj.length();i++) {
                        JSONObject o = jObj.getJSONObject(i);
                        String freq = o.getString("freq");
                        String size = o.getString("size");
                        String qty = o.getString("qty");
                        String ndate = o.getString("ndate");
                        String ldate = o.getString("ldate");
                        String sid = o.getString("sch_id");

                        a = new schd(sid,size,ndate,qty,ldate,freq);
                        historyList.add(a);
                    }
                    fillRecycler();


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
                        getSch(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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
        rq.add(strReqs );
    }
    @Override
    public void remove(final String email,final String sid){

        //String tag_string_req = "req_login";
        showDialog();
        StringRequest strReqt = new StringRequest(Request.Method.POST,
                URL_rSCH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                //Toast.makeText(getActivity().getApplicationContext(),response, Toast.LENGTH_LONG).show();

                try {

                    JSONObject jObj = new JSONObject(response);

                    String res = jObj.getString("msg");

                    if(res.equals("success")){

                        historyList.clear();

                        getSch(email);
                    }
                    else {
                        Snackbar.make(rView,"Bad network",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "sorry an error occur", Toast.LENGTH_LONG).show();
                    Snackbar.make(rView,"Bad network",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Snackbar.make(rView,"Bad network",Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("sid", sid);


                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rq.add(strReqt );

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
