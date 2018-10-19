package info.androidhive.cardview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import info.androidhive.cardview.helper.SQLiteHandler;
import info.androidhive.cardview.helper.SessionManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class showProfileFragment extends Fragment {
    private TextView nam,ema,phon,addr,bir;
    RequestQueue rq;

    private SQLiteHandler db;
    private HashMap<String, String> user;
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/userDetails.php";

    ProfileActivity profileActivity;

    public showProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_profile, container, false);
        profileActivity = (ProfileActivity)getActivity();
        db = new SQLiteHandler(getActivity().getApplicationContext());


        rq = Volley.newRequestQueue(getActivity());



        nam = view.findViewById(R.id.nameText);
        phon = view.findViewById(R.id.phoneText);
        ema = view.findViewById(R.id.emailText);
        addr = view.findViewById(R.id.addText);
        bir = view.findViewById(R.id.birthText);
        user = db.getUserDetails();
        userDetails();
        return view;
    }

    private void userDetails(){
        this.nam.setText(user.get("name"));
        this.ema.setText(user.get("email"));
        this.addr.setText(user.get("address"));
        this.phon.setText(user.get("phone"));
        this.bir.setText(user.get("birthday"));
        //profileActivity.hideDialog();
    }

}
