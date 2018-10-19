package info.androidhive.cardview;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by EBU on 12/4/2017.
 */

public class schedule extends DialogFragment implements DialogInterface.OnShowListener {


    EditText kg3,kg5,kg7,kg10,kg12,kg25,kg50;
    private RadioGroup rg;
    private RadioButton no1,no2;
    private String freq = "Weekly";
    pl.droidsonroids.gif.GifImageView gl;
    accountActivity ac;

    String sizes = "";
    String qty = "";

    private static String URL_WALLET = "https://gasnownow.ng/rest_api/schedule.php";
    RequestQueue rr;
    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Schedule Delivery")
                .setPositiveButton("Schedule",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        LayoutInflater i = getActivity().getLayoutInflater();
        View view = i.inflate(R.layout.schedule, null);
        ac = (accountActivity)getActivity();
        kg3 = view.findViewById(R.id.kg3);
        kg5 = view.findViewById(R.id.kg5);
        kg7 = view.findViewById(R.id.kg7);
        kg10 = view.findViewById(R.id.kg10);
        kg12 = view.findViewById(R.id.kg12);
        kg25 = view.findViewById(R.id.kg25);
        kg50 = view.findViewById(R.id.kg50);
        rg = (RadioGroup) view.findViewById(R.id.rg);
        no1 = (RadioButton) view.findViewById(R.id.no1);
        no2 = (RadioButton) view.findViewById(R.id.no2);
        gl = view.findViewById(R.id.gl);


        rr = Volley.newRequestQueue(getActivity().getApplicationContext());
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton r = rg.findViewById(checkedId);
                freq = r.getText().toString();
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {

            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TextView date = (TextView) getActivity().findViewById(R.id.ad);
                    //date.setText(cA.getText().toString());
                    gl.setVisibility(View.VISIBLE);
                    check();
                    validate();

                    getSchedule(email);

                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

    }

    private void check(){
        if(kg3.getText().toString().equals("") == false){
            sizes += "3KG";
            qty += kg3.getText().toString();
        }
        if(kg5.getText().toString().equals("") == false){
            sizes += "5KG,";
            qty += kg5.getText().toString();
        }
        if(kg7.getText().toString().equals("") == false){
            sizes += "7KG,";
            qty += kg7.getText().toString();

        if(kg10.getText().toString().equals("") == false){
            sizes += "10KG,";
            qty += kg10.getText().toString();
        }
        if(kg12.getText().toString().equals("") == false){
            sizes += "12.5KG,";
            qty += kg12.getText().toString();
        }
        if(kg25.getText().toString().equals("") == false){
            sizes += "25KG,";
            qty += kg25.getText().toString();
        }
        if(kg50.getText().toString().equals("") == false){
            sizes += "50KG";
            qty += kg50.getText().toString();
        }

        if(no1.isChecked()){
            freq = no1.getText().toString();
        }
        if(no2.isChecked()){
            freq = no2.getText().toString();
        }
    }}
    private boolean  validate(){
        if(sizes.equals("") && qty.equals("")){
            gl.setVisibility(View.INVISIBLE);
            return false;
        }

        return true;
    }

    private void getSchedule(final String email){
        gl.setVisibility(View.VISIBLE);
        StringRequest strReqs = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                gl.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    //Toast.makeText(getActivity().getApplicationContext(), "Json error: middle", Toast.LENGTH_LONG).show();
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    if(status.equals("success")){
                        dismiss();
                        ac.restart(msg);
                    }else if(status.equals("failed")){
                        dismiss();
                        ac.restart("1"+msg);
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    dismiss();
                    ac.restart("2failed to add schedule, Please try again");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(getActivity().getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                gl.setVisibility(View.GONE);
                dismiss();
                ac.restart("3failed to add schedule, Please try again");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("size",sizes);
                params.put("qty", qty);
                params.put("freq", freq);

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rr.add(strReqs );
    }

}
