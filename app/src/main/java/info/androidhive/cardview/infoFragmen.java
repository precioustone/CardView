package info.androidhive.cardview;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 *factory method to
 * create an instance of this fragment.
 */
public class infoFragmen extends Fragment {

    private Button next,chg;
    private EditText name,num,add;
    private RadioGroup rg;
    private PaymentActivity l;
    private payOnNextbuttonClickListener mCallback;
    private String payMethod = "On Delivery";
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/order.php";
    private static String URL_DETAILS = "https://gasnownow.ng/rest_api/userDetails.php";
    RequestQueue rr;
    private ProgressDialog pDialog;
    private String key;



    public infoFragmen(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.info_frag, container, false);


        name = (EditText) view.findViewById(R.id.fn);
        num = (EditText) view.findViewById(R.id.pn);
        add = (EditText) view.findViewById(R.id.ad);
        rg = (RadioGroup) view.findViewById(R.id.rg);
        l = (PaymentActivity)getActivity();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        setVal();
        chg = view.findViewById(R.id.btnChange);
        chg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.getOrder().setAddress(add.getText().toString());
                Snackbar.make(getActivity().findViewById(R.id.infoCont),"Details changed",Snackbar.LENGTH_SHORT)
                        .setAction("Retry",null).show();
            }
        });
        next = (Button) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.User = new user(name.getText().toString(),num.getText().toString(),add.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail());
                Cart.getOrder().setUser(l.User);
                Cart.getOrder().setLatLng(myLocation.getLatLng());
                if(l.newuser == 1){
                    if(payMethod.equals("On Delivery")) {
                        Cart.getOrder().setPayM(payMethod);
                        l.adduser();
                        key = l.addOrder();
                        addOrder("succ");

                    }else if(payMethod.equals("G wallet")){
                        Cart.getOrder().setPayM(payMethod);
                        l.adduser();
                        key = l.addOrder();
                        addOrder("succ");
                    }
                    else if(payMethod.equals("Card")){
                        Cart.getOrder().setPayM(payMethod);
                        l.adduser();
                        addOrder("pay");
                        //l.addOrder();
                    }
                }
                else {
                    if(payMethod.equals("On Delivery")) {
                        Cart.getOrder().setPayM(payMethod);
                        key = l.addOrder();
                        addOrder("succ");
                    }else if(payMethod.equals("G wallet")){
                        Cart.getOrder().setPayM(payMethod);
                        DialogFragment newFragment = new notice();
                        Bundle args = new Bundle();
                        args.putString("msg", "Charges will be made from your G-wallet");
                        args.putString("btn", "Continue");
                        newFragment.setArguments(args);
                        newFragment.show(getActivity().getFragmentManager(),"Notice");
                    }
                    else if(payMethod.equals("Card")){
                        Cart.getOrder().setPayM(payMethod);
                        key = l.addOrder();
                        addOrder("pay");
                        //l.addOrder();
                    }
                }

            }
        });


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton r = rg.findViewById(checkedId);
                payMethod = r.getText().toString();
            }
        });

        rr = Volley.newRequestQueue(getActivity().getApplicationContext());
        checkNewUser();
        return view;
    }
    public void useG(){
        key = l.addOrder();
        addOrder("succ");
    }

    public void setVal(){
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        add.setText(l.add);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (payOnNextbuttonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
        private void addOrder(final String des){
            showDialog();
            StringRequest strReqs = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");
                    if(status.equals("success"))
                        mCallback.nextButtonClicked(des);

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Sorry an error occured", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                hideDialog();
                DialogFragment newFragment = new notice();
                Bundle args = new Bundle();
                args.putString("msg", "Could Not process your request at the moment");
                args.putString("btn", "Ok");
                newFragment.setArguments(args);
                newFragment.show(getActivity().getFragmentManager(),"Notice");
                Snackbar.make(getActivity().findViewById(R.id.infoCont),"Bad network",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry",new action(des)).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                params.put("size",Cart.getOrder().getSize());
                params.put("qty", Cart.getOrder().getQty());
                params.put("date", Cart.getOrder().getLaterdate());
                params.put("time",Cart.getOrder().getTime());
                params.put("price", ""+Cart.getOrder().getPrice());
                params.put("express", Cart.getOrder().getExpress());
                params.put("address",Cart.getOrder().getAddress());
                params.put("swap", Cart.getOrder().getSwap());
                params.put("payM", Cart.getOrder().getPayM());
                params.put("prefB", Cart.getOrder().getBrand());
                params.put("lat", ""+myLocation.getLatLng().latitude);
                params.put("lng", ""+myLocation.getLatLng().longitude);
                params.put("fKey",key);

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            rr.add(strReqs );
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setMessage("processing");
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void checkNewUser(){
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_DETAILS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);


                    // Check for error node in json

                    // Now store the user in SQLite
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");
                    if(status.equals("success") && msg.equals("exists")) {
                        String ad = jObj.getString("adsress").equals("null")?"":jObj.getString("adsress");
                        String phone = jObj.getString("phone");
                        num.setText(phone);
                        add.setText(ad);

                    }
                    else {

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Snackbar.make(rView,"Bad network",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity().getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                Snackbar.make(getActivity().findViewById(R.id.infoCont),"Bad network",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry",null).show();
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
        rr.add(strReq );
    }

    public class action implements View.OnClickListener{
        String des;

        public action(String des){
            this.des = des;
        }
        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            addOrder(des);
        }
    }
}
