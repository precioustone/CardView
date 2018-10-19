package info.androidhive.cardview;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
public class order_hisFragment extends Fragment {

    private orderAdapter adapter;
    private List<OrderList> orderList;
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/order_his.php";
    RequestQueue rs;
    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private RecyclerView rView;
    private TextView display;
    private ProgressBar pg;


    public order_hisFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_his, container, false);

        rView = (RecyclerView) view.findViewById(R.id.rview2);

        orderList = new ArrayList<>();
        adapter = new orderAdapter(getActivity(), orderList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);
        display = view.findViewById(R.id.display);
       pg = view.findViewById(R.id.progress);
        rs = Volley.newRequestQueue(getActivity().getApplicationContext());

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

    private void getWalletHis(final String email){
        //String tag_string_req = "req_login";
        showDialog();
        StringRequest strReqs = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);

                    // Check for error node in json
                    OrderList a;

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

                        a = new OrderList(amt,date,size,qty,pay,brand,oid,status);
                        orderList.add(a);
                    }

                        fillRecycler();




                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "sorry an error occured", Toast.LENGTH_LONG).show();
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
        rs.add(strReqs );
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
