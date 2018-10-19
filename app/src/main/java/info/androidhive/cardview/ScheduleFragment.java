package info.androidhive.cardview;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.util.Map;

import co.paystack.android.Transaction;
import co.paystack.android.model.Charge;

import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {




    public static int[] v = new int[7];
    public static double priceTotal = 0;
    static int k = 0;
    private ProgressBar pg;
    String date = "", address = "", swapRefill = "",brandVal = "";
    String[] cylinders = {"3KG","5KG","7KG","10KG","12.5KG","25KG","50KG"};

    public int[] price = new int[7];

    private static String URL_PRICE = "https://gasnownow.ng/rest_api/prices.php";
    private static String URL_SCHD = "https://gasnownow.ng/rest_api/schedule.php";
    RequestQueue rs;
    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


    private RecyclerView rView;
    private LinearLayout mainL;
    public String freq = "Monthly",size = "";
    int qty = 0;

    private java.util.List<List> PlaceList;
    private SchSizeAdapter adapter;
    private FloatingActionButton add;
    private ScheduleActivity scheduleActivity;


    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        scheduleActivity = (ScheduleActivity)getActivity();

        rs = Volley.newRequestQueue(getActivity().getApplicationContext());
        rView = view.findViewById(R.id.rview5);

        PlaceList = new ArrayList<>();
        adapter = new SchSizeAdapter(getActivity(),this, PlaceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);
        pg = view.findViewById(R.id.pro2);
        mainL = view.findViewById(R.id.mainL);
        add = view.findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCylinderPickerDialog(v);
            }
        });

        Spinner brand = (Spinner)view.findViewById(R.id.spin);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.brands, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        brand.setAdapter(mAdapter);
        brand.setOnItemSelectedListener(this);

        CalendarView calendarView = view.findViewById(R.id.calV);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = month+" "+dayOfMonth+" ,"+year;
            }
        });
        final EditText address = view.findViewById(R.id.addEdit);
        this.address = address.getText().toString();
        Button sch = view.findViewById(R.id.schd_btn);

        sch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(address.equals("") || date.equals("") ||
                        ScheduleFragment.this.v == null || brandVal.equals("") || swapRefill.equals("")){
                    Toast.makeText(getActivity(),"Enter all details",Toast.LENGTH_SHORT).show();
                }
                else {

                    for (int i = 0; i < ScheduleFragment.this.v.length; i++) {
                        priceTotal += price[i] * ScheduleFragment.this.v[i];
                        for (int j = 0; j < ScheduleFragment.this.v[i]; j++){
                            size += cylinders[i]+"|";
                            qty += ScheduleFragment.this.v[i];
                        }
                    }
                    Schedule(email);
                    Toast.makeText(getActivity(),"success",Toast.LENGTH_LONG).show();
                }
            }
        });

        price(email);
        return view;
    }

    public void showCylinderPickerDialog(View v) {
        pickCylinderSizeSch newFragment = new pickCylinderSizeSch();
        newFragment.show(getChildFragmentManager(),"PICK CYLINDER");
    }

    @Override
    public void onStop() {
        super.onStop();
        for(int i = 0; i < v.length; i++){
            v[i] = 0;
        }
    }

    public void populate(){
        PlaceList.clear();
        List list;
        for (int i = 0; i < v.length; i++){
            if(v[i] > 0){
                for(int j = 0; j < v[i]; j++){
                    list = new List();
                    list.setSize(cylinders[i]);
                    list.setPrice(price[i]);
                    PlaceList.add(list);
                }
            }
        }
        fillRecycler();
        //Toast.makeText(RefillActivity.this,""+priceTotal,Toast.LENGTH_LONG).show();
    }

    public  void fillRecycler(){
        //Toast.makeText(getActivity(),"Got here as well",Toast.LENGTH_LONG).show();
        if(PlaceList.size() > 0 ) {
            rView.setVisibility(View.VISIBLE);
            //adapter.notifyDataSetChanged();
            adapter.notifyItemInserted(PlaceList.size() - 1);
        }
    }

    private void price(final String email){
        //String tag_string_req = "req_login";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_PRICE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONArray jObj = new JSONArray(response);

                    // Check for error node in json

                    for (int i = 0; i < jObj.length();i++) {
                        JSONObject o = jObj.getJSONObject(i);
                        price[i] = parseInt(o.getString("prices"));

                    }

                    if(price[0] == 0){
                        Toast.makeText(getActivity(), "bad network", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Sorry an error occured", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
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

        rs.add(strReq );
    }

    private void showDialog() {
        pg.setVisibility(View.VISIBLE);
        mainL.setVisibility(View.GONE);

    }

    private void hideDialog() {
        pg.setVisibility(View.GONE);
        mainL.setVisibility(View.VISIBLE);

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.swap:
                if (checked)
                    swapRefill = "Swap";
                break;
            case R.id.refill:
                if (checked)
                    swapRefill = "Refill";
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        brandVal = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void Schedule(final String email){
        showDialog();
        StringRequest strReqs = new StringRequest(Request.Method.POST,
                URL_SCHD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    //Toast.makeText(getActivity().getApplicationContext(), "Json error: middle", Toast.LENGTH_LONG).show();
                    String status = jObj.getString("error");
                    //String msg = jObj.getString("msg");

                    if(status.equals("no")){
                        scheduleActivity.swapFrag("");
                    }else if(status.equals("yes")){
                        //Toast.makeText(getActivity(),"Failed",Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("size",size);
                params.put("qty", String.valueOf(qty));
                params.put("freq", freq);
                params.put("ldate", date);

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rs.add(strReqs );
    }

}
