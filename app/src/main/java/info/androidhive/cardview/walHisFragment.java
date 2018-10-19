package info.androidhive.cardview;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link walHisFragment} factory method to
 * create an instance of this fragment.
 */
public class walHisFragment extends Fragment {

    private RecyclerView rView;

    private historyAdapter adapter;
    private List<history> historyList;
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/wallet_his.php";
    private ProgressBar pg;
    RequestQueue rs;
    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    public walHisFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wal_his, container, false);
        rView = (RecyclerView) view.findViewById(R.id.rview1);

        historyList = new ArrayList<>();
        adapter = new historyAdapter(getActivity(), historyList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);
        pg = view.findViewById(R.id.pro1);
        rs = Volley.newRequestQueue(getActivity().getApplicationContext());

        getWalletHis(email);
        return view;
    }

    public  void fillRecycler(){
        adapter.notifyDataSetChanged();
    }

    public void getWalletHis(final String email){
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
                    history a;
                    for (int i = 0; i < jObj.length();i++) {
                        JSONObject o = jObj.getJSONObject(i);
                        String amt = o.getString("amt");
                        String date = o.getString("t_time");
                        String decs = o.getString("descp");

                        a = new history(amt,date,decs);
                        historyList.add(a);
                    }

                    fillRecycler();




                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Nothing to see here", Toast.LENGTH_LONG).show();
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
