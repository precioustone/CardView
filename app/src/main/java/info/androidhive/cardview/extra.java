package info.androidhive.cardview;

/**
 * Created by EBU on 1/2/2018.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import info.androidhive.cardview.helper.SQLiteHandler;
import info.androidhive.cardview.helper.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class extra extends DialogFragment implements DialogInterface.OnShowListener {

    EditText password,phone;
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/newuser.php";
    RequestQueue rr;
    LoginActivity ma;
    private ProgressBar pg;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("USER DETAILS")
                .setPositiveButton("CONTINUE",
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
        View view = i.inflate(R.layout.extra, null);
        // SQLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // Session manager
        session = new SessionManager(getActivity().getApplicationContext());
        ma = (LoginActivity)getActivity();

        password = view.findViewById(R.id.password);

        phone = view.findViewById(R.id.phone);

        pg = view.findViewById(R.id.progress);

        rr = Volley.newRequestQueue(getActivity().getApplicationContext());
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
                    getSchedule(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

    private void getSchedule(final String email, final String username){
        pg.setVisibility(View.VISIBLE);
        StringRequest strReqs = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pg.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);

                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");
                    if(status.equals("success")) {

                        String uid = jObj.getString("uid");
                        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        String add = jObj.getString("adsress").equals("null")?"":jObj.getString("adsress");
                        String phone = jObj.getString("phone");
                        String birth = jObj.getString("birthday").equals("null")?"":jObj.getString("birthday");
                        String created_at = jObj.getString("join_date");

                        // Inserting row in users table
                        db.addUser(name, email, uid,add,phone,birth,created_at);

                        Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                        getActivity().startActivity(intent);
                        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                    else if(status.equals("failed")){
                        Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                        getActivity().startActivity(intent);
                        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        dismiss();
                    }

                } catch (JSONException e) {
                    Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                    getActivity().startActivity(intent);
                    e.printStackTrace();
                    dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pg.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                getActivity().startActivity(intent);
                dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("username", username);
                params.put("photo", ""+FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rr.add(strReqs );
    }
}
