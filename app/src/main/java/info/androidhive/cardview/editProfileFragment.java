package info.androidhive.cardview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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
 * Activities that contain this fragment must implement the
 * {@link editProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class editProfileFragment extends Fragment {

    private EditText phone,address,birth;
    private TextView name,email;
    View view;
    RequestQueue rq;
    private static String URL_EDIT = "https://gasnownow.ng/rest_api/editDetails.php";
    private SQLiteHandler db;
    HashMap<String, String> user;

    ProfileActivity profileActivity;

    private OnFragmentInteractionListener mListener;

    public editProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileActivity = (ProfileActivity)getActivity();
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // Session manager

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        birth = view.findViewById(R.id.birth);
        user = db.getUserDetails();

        userDetails();
        rq = Volley.newRequestQueue(getActivity());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
   public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }

    private void userDetails(){
        this.name.setText(user.get("name"));
        this.email.setText(user.get("email"));
        this.address.setText(user.get("address"));
        this.phone.setText(user.get("phone"));
        this.birth.setText(user.get("birthday"));
    }

    public void editDetails(){
        profileActivity.showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_EDIT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                profileActivity.hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);


                    // Check for error node in json

                    // Now store the user in SQLite
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");
                    if(status.equals("success")) {


                        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                        db.deleteUsers();
                        db.addUser(name.getText().toString(),email.getText().toString(),
                                user.get("uid"),address.getText().toString(),
                                phone.getText().toString(),birth.getText().toString(),user.get("created_at"));
                        mListener.onFragmentInteraction();

                    }
                    else {

                        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                        /*db.addUser(name.getText().toString(),email.getText().toString(),
                                user.get("uid"),address.getText().toString(),
                                phone.getText().toString(),birth.getText().toString(),user.get("created_at"));*/
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_LONG).show();
                profileActivity.hideDialog();
                //Snackbar.make(view.findViewById(R.id.prof),"Bad network",Snackbar.LENGTH_INDEFINITE)
                        //.setAction("Retry",new action()).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                params.put("address", address.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("birthday",birth.getText().toString());


                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rq.add(strReq );
    }
    public class action implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            editDetails();

        }
    }
}
