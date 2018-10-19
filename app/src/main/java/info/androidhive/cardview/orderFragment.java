package info.androidhive.cardview;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link orderFragment} factory method to
 * create an instance of this fragment.
 */
public class orderFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button next,change;
    private RadioGroup rg,rg1;
    private SeekBar sb;
    private EditText date,time;
    //EditText kg3,kg5,kg7,kg10,kg12,kg25,kg50;
    private TextView pr,sbt,ad;
    orderActivity o = (orderActivity)getActivity();
    RelativeLayout rl;
    private int[] price = new int[7];
    double total = 0;
    int extra = 0;

    String cylSize = "",brand = "",express = "Yes",swap = "Yes",payM="";
    int gasQty = 0;

    int percent;

    private static String URL_WALLET = "https://gasnownow.ng/rest_api/prices.php";
    private ProgressDialog pDialog;
    RequestQueue rs;
    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    View view;

    private OnnextButtonClickedListener mCallback;
    public static final orderFragment newInstance(){
        orderFragment newFragment = new orderFragment();
        return newFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.order_frag, container, false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        rs = Volley.newRequestQueue(getActivity().getApplicationContext());

        rl = (RelativeLayout) view.findViewById(R.id.imView);


        o = (orderActivity)getActivity();
        sb = (SeekBar) view.findViewById(R.id.sb);
        rg = (RadioGroup) view.findViewById(R.id.rg);
        rg1 = (RadioGroup) view.findViewById(R.id.rg1);
        date = (EditText) view.findViewById(R.id.date);
        time = (EditText) view.findViewById(R.id.time);
        pr = (TextView) view.findViewById(R.id.pr);
        sbt = (TextView) view.findViewById(R.id.sbt);
        ad = (TextView) view.findViewById(R.id.ad);
        change = (Button)view.findViewById(R.id.change);

        Button pick = (Button)view.findViewById(R.id.cylsize);
        pick.setOnClickListener(callFrag);

        /*kg3 = view.findViewById(R.id.kg3);
        kg5 = view.findViewById(R.id.kg5);
        kg7 = view.findViewById(R.id.kg7);
        kg10 = view.findViewById(R.id.kg10);
        kg12 = view.findViewById(R.id.kg12);
        kg25 = view.findViewById(R.id.kg25);
        kg50 = view.findViewById(R.id.kg50);



        kg3.setOnClickListener(callFrag);
        kg5.setOnClickListener(callFrag);
        kg7.setOnClickListener(callFrag);
        kg10.setOnClickListener(callFrag);
        kg12.setOnClickListener(callFrag);
        kg25.setOnClickListener(callFrag);
        kg50.setOnClickListener(callFrag);



        kg3.setOnFocusChangeListener(gasFrag);
        kg5.setOnFocusChangeListener(gasFrag);
        kg7.setOnFocusChangeListener(gasFrag);
        kg10.setOnFocusChangeListener(gasFrag);
        kg12.setOnFocusChangeListener(gasFrag);
        kg25.setOnFocusChangeListener(gasFrag);
        kg50.setOnFocusChangeListener(gasFrag);*/

        Spinner spinner = (Spinner) view.findViewById(R.id.spin);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.brands, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

       setvalues();

        date.setInputType(InputType.TYPE_NULL);
        time.setInputType(InputType.TYPE_NULL);

        date.setOnClickListener(pickeDialog);
        time.setOnClickListener(pickeDialog);

        date.setOnFocusChangeListener(pickerFocus);
        time.setOnFocusChangeListener(pickerFocus);

        sb.setOnSeekBarChangeListener(seekBarListener);
        ad.setText(o.getAdr());





        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton r = rg.findViewById(checkedId);
                if(r.getText().equals("No")){
                    sb.setVisibility(View.GONE);
                    date.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    sbt.setVisibility(View.GONE);
                    pr.setVisibility(View.VISIBLE);
                    express = r.getText().toString();

                }else if(r.getText().equals("Yes")){
                    sb.setVisibility(View.VISIBLE);
                    date.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    sbt.setVisibility(View.VISIBLE);
                    pr.setVisibility(View.GONE);
                    express = r.getText().toString();

                }
            }
        });


        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton s = rg1.findViewById(checkedId);

                swap = s.getText().toString();
            }
        });


        // Inflate the layout for this fragment
        next = (Button) view.findViewById(R.id.Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user User = new user();
                o.Order = new order(User,cylSize,""+gasQty,time.getText().toString(),
                        date.getText().toString(),express,swap,brand,payM,total,ad.getText().toString(),0,new LatLng(0,0));
                o.adr = ad.getText().toString();

                if(validate() == true){
                    //o.addOrder();
                    getWalletHis(email);
                }
                else {
                    Toast.makeText(getActivity(),"All feed must be filled",Toast.LENGTH_SHORT).show();
                }

            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o.changeAdr(v);

            }
        });

        /*np.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.nextButtonClicked("gass");
            }
        });*/


        return view;
    }

    public View.OnFocusChangeListener pickerFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if(hasFocus){
                switch (v.getId()){
                    case R.id.date:
                        o.showDatePickerDialog(v);
                        break;
                    case R.id.time:
                        o.showTimePickerDialog(v);
                        break;
                }
            }

        }
    };

    public void setvalues(){

        if(cylSize.equals("") == false){
            cylSize = "";
            gasQty = 0;
        }

        for(int i = 0; i < o.v.length; i++){
            if(o.v[i] != 0 && i == 0){
                //kg3.setText(""+o.v[i]);
                cylSize += "3Kg ";
                gasQty += o.v[i];
                ImageView im = view.findViewById(R.id.three);
                im.setVisibility(View.VISIBLE);
            }
            if(o.v[i] != 0 && i == 1){
                //kg5.setText(""+o.v[i]);
                cylSize += "5Kg ";
                gasQty += o.v[i];
                ImageView im = view.findViewById(R.id.five);
                im.setVisibility(View.VISIBLE);
            }
            if(o.v[i] != 0 && i == 2){
                //kg7.setText(""+o.v[i]);
                cylSize += "7Kg ";
                gasQty += o.v[i];
                ImageView im = view.findViewById(R.id.seven);
                im.setVisibility(View.VISIBLE);
            }
            if(o.v[i] != 0 && i == 3){
                //kg10.setText(""+o.v[i]);
                cylSize += "10Kg ";
                gasQty += o.v[i];
                ImageView im = view.findViewById(R.id.ten);
                im.setVisibility(View.VISIBLE);
            }
            if(o.v[i] != 0 && i == 4){
                //kg12.setText(""+o.v[i]);
                cylSize += "12.5Kg ";
                gasQty += o.v[i];
                ImageView im = view.findViewById(R.id.twel);
                im.setVisibility(View.VISIBLE);
            }
            if(o.v[i] != 0 & i == 5){
                //kg25.setText(""+o.v[i]);
                cylSize += "25Kg ";
                gasQty += o.v[i];
                ImageView im = view.findViewById(R.id.twen);
                im.setVisibility(View.VISIBLE);
            }
            if(o.v[i] != 0 && i == 6){
                //kg50.setText(""+o.v[i]);
                cylSize += "50Kg ";
                gasQty += o.v[i];
                ImageView im = view.findViewById(R.id.fif);
                im.setVisibility(View.VISIBLE);
            }
        }
    }

    public View.OnClickListener pickeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.date:
                    o.showDatePickerDialog(v);
                    break;
                case R.id.time:
                    o.showTimePickerDialog(v);
                    break;
            }
        }
    };


    public boolean validate(){
        if(o.Order.getSize().isEmpty() || o.Order.getQty().isEmpty()||
                (o.Order.getAddress().isEmpty()|| o.Order.getAddress().equals("You are at this location"))){
            return false;
        }

        return true;
    }


    // listener object for the SeekBar's progress changed events
    private final SeekBar.OnSeekBarChangeListener seekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
                // update percent, then call calculate
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    percent = progress; // set percent based on progress
                    calculate(percent); // calculate and display tip and total
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            };

    // calculate and display tip and total amounts
    private void calculate(int cal) {
        if(cal < 30 && cal >= 20)
            extra += 100;
        if(cal < 20 && cal >= 10)
            extra += 200;
        if(cal < 10 && cal >= 0)
            extra += 300;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnnextButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private void calcPrice(){

        if(o.price != 0 || total != 0){
            o.price = 0;
            total = 0;
        }

        for(int i = 0; i < o.v.length; i++){
            o.price += price[i] * o.v[i];
        }
        o.price += extra;
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

                    for (int i = 0; i < jObj.length();i++) {
                        JSONObject o = jObj.getJSONObject(i);
                        price[i] = parseInt(o.getString("prices"));

                    }

                    if(price[0] == 0){
                        Snackbar.make(getActivity().findViewById(R.id.infoCont),"Bad network",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry",new action()).show();
                    }else {
                        calcPrice();
                        mCallback.nextButtonClicked("con");
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
                Snackbar.make(getActivity().findViewById(R.id.scroll),"Bad network",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry",new action()).show();
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

        rs.add(strReqs );
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setMessage("Please wait...");
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        brand = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public View.OnClickListener callFrag = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallback.nextButtonClicked("gass");
        }
    };
    public View.OnFocusChangeListener gasFrag = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            mCallback.nextButtonClicked("gass");
        }
    };
    public class action implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            getWalletHis(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

}
