package info.androidhive.cardview;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class orderListFragment extends Fragment implements mainOrderAdapter.callDis {

    private mainOrderAdapter adapter;
    private List<mainOrder> orderList;
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/retrieve_order.php";
    private static String URL_DISPATCHER = "https://gasnownow.ng/rest_api/dispatcher.php";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private ProgressBar pg;
    RequestQueue rq;
    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private RecyclerView rView;
    private TextView display;
    //CoordinatorLayout oC;
    View view;
    private int id = 0;



    public orderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_list, container, false);
        rView = (RecyclerView) view.findViewById(R.id.rview3);

        orderList = new ArrayList<>();
        adapter = new mainOrderAdapter(getActivity(),this, orderList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);
        display = view.findViewById(R.id.display);
        pg = view.findViewById(R.id.progress);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(getActivity(), RefillActivity.class);
                intent.putExtra("Address",Address.getAddress().equals("")?"": Address.getAddress());
                startActivity(intent);
            }
        });
        rq = Volley.newRequestQueue(getActivity().getApplicationContext());

        getWalletHis(email);

        return view;
    }

    public  void fillRecycler(){
        if (orderList.size() > 0){
            display.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
        else{
            display.setVisibility(View.VISIBLE);
            rView.setVisibility(View.GONE);
        }
    }



    public void makePhoneCall(View view) {

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            getPhone(id);
        }

    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhone(id);
                }
            }
        }
    }

    @Override
    public void call(int id){
        this.id = id;
        makePhoneCall(view);
    }



    private void getPhone(final int id){
        //String tag_string_req = "req_login";
        showDialog();
        StringRequest strReqqq = new StringRequest(Request.Method.POST,
                URL_DISPATCHER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();
                //Toast.makeText(getActivity().getApplicationContext(),response,Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);

                    String number = jObj.getString("phone");
                    if(number != null){
                        dialPhoneNumber(number);
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Sorry an error occured", Toast.LENGTH_LONG).show();
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
                params.put("uid", ""+id);

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rq.add(strReqqq );
    }




    private void getWalletHis(final String email){
        //String tag_string_req = "req_login";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity().getApplicationContext(),response,Toast.LENGTH_LONG).show();
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);

                    // Check for error node in json
                    mainOrder a;

                    for (int i = 0; i < jObj.length();i++) {
                        JSONObject o = jObj.getJSONObject(i);
                        String amt = o.getString("price");
                        String date = o.getString("order_time");
                        String pay = o.getString("paymethod");
                        String brand = o.getString("prefBrand");
                        String oid = o.getString("order_id");
                        String qty = o.getString("qty");
                        String size = o.getString("sizes");
                        String status = o.getString("status");
                        String add = o.getString("address");
                        double lat = o.getDouble("lat");
                        double lng = o.getDouble("lng");
                        int disId = o.getInt("d_uid");

                        a = new mainOrder(amt,date,size,qty,pay,brand,oid,status,add,lat,lng,disId);
                        orderList.add(a);
                    }
                        fillRecycler();




                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Sorry an error occured", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
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
                        getWalletHis(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                }, 2000);
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
