package info.androidhive.cardview;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.GraphRequest.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class pstackFragment extends Fragment {

    String backend_url = "https://gasnownow.com/backUrl/index.php";
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/fundWallet.php";
    RequestQueue rr;
    EditText mEditCardNum;
    EditText mEditCVC;
    EditText mEditExpiryMonth;
    EditText mEditExpiryYear;
    EditText amt;
    KeyListener listener;
    pl.droidsonroids.gif.GifImageView gif;

    TextView mTextError;
    private Charge charge;
    static Transaction transaction;
    Button mButtonPerformLocalTransaction;

    String price;
    static String msg;
    accountActivity ac;
    ImageButton im;

    public pstackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            PaystackSdk.setPublicKey(bundle.getString("co.paystack.android.PublicKey"));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        PaystackSdk.initialize(getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pstack, container, false);
        ac = (accountActivity)getActivity();
        mEditCardNum = view.findViewById(R.id.edit_card_number);
        mEditCVC = view.findViewById(R.id.edit_cvc);
        mEditExpiryMonth = view.findViewById(R.id.edit_expiry_month);
        mEditExpiryYear = view.findViewById(R.id.edit_expiry_year);
        gif = view.findViewById(R.id.gif);

        mButtonPerformLocalTransaction = view.findViewById(R.id.button_perform_local_transaction);

        mTextError = view.findViewById(R.id.textview_error);

        amt = view.findViewById(R.id.amt);

        //set click listener
        mButtonPerformLocalTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    startAFreshCharge(true);
                } catch (Exception e) {
                    mTextError.setText(String.format("An error occurred while charging card: %s %s", e.getClass().getSimpleName(), e.getMessage()));

                }
            }
        });
        rr = Volley.newRequestQueue(getActivity().getApplicationContext());
        im = view.findViewById(R.id.close);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac.rfra();
                ac.restart("");
            }
        });

        return view;
    }

    private void startAFreshCharge(boolean local) {
        // initialize the charge
        price = amt.getText().toString();
        charge = new Charge();
        charge.setCard(loadCardFromForm());


        if (local) {
            // Set transaction params directly in app (note that these params
            // are only used if an access_code is not set. In debug mode,
            // setting them after setting an access code would throw an exception

            charge.setAmount(Integer.parseInt(price));
            charge.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
            try {
                charge.putCustomField("Charged From", "Android SDK");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard();
        } else {
            // Perform transaction/initialize on our server to get an access code
            // documentation: https://developers.paystack.co/reference#initialize-a-transaction
            new pstackFragment.fetchAccessCodeFromServer().execute(backend_url + "/new-access-code");
        }
    }

    /**
     * Method to validate the form, and set errors on the edittexts.
     */
    private Card loadCardFromForm() {
        //validate fields
        gif.setVisibility(View.VISIBLE);
        disableView();
        Card card;

        String cardNum = mEditCardNum.getText().toString().trim();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        String cvc = mEditCVC.getText().toString().trim();
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        String sMonth = mEditExpiryMonth.getText().toString().trim();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = mEditExpiryYear.getText().toString().trim();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        return card;
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    private void chargeCard() {
        gif.setVisibility(View.VISIBLE);
        disableView();
        pstackFragment.transaction = null;
        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {

                gif.setVisibility(View.INVISIBLE);
                enableView();
                pstackFragment.transaction = transaction;
                mTextError.setText(" ");

                Toast.makeText(getActivity(), transaction.getReference(), Toast.LENGTH_LONG).show();
                fundWallet();
                updateTextViews();
                new pstackFragment.verifyOnServer().execute(transaction.getReference());

            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {
                pstackFragment.transaction = transaction;
                Toast.makeText(getActivity(), transaction.getReference(), Toast.LENGTH_LONG).show();
                updateTextViews();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                gif.setVisibility(View.INVISIBLE);
                enableView();
                pstackFragment.transaction = transaction;
                if (error instanceof ExpiredAccessCodeException) {
                    startAFreshCharge(false);
                    chargeCard();
                    return;
                }



                if (transaction.getReference() != null) {
                    Toast.makeText(getActivity(), transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()));
                    new pstackFragment.verifyOnServer().execute(transaction.getReference());
                } else {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }
                updateTextViews();
            }

        });
    }


    private void updateTextViews() {
        if (transaction.getReference() != null) {
            mTextError.setText(String.format("Reference: %s", "Processing..."));
        } else {
            mTextError.setText("Please enter card details");
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() < 1;
    }



    private class fetchAccessCodeFromServer extends AsyncTask<String, Void, String> {
        private String error;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                charge.setAccessCode(result);
                chargeCard();
            } else {

            }
        }

        @Override
        protected String doInBackground(String... ac_url) {
            try {
                URL url = new URL(ac_url[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                String inputLine;
                inputLine = in.readLine();
                in.close();
                return inputLine;
            } catch (Exception e) {
                error = e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            return null;
        }
    }

    private class verifyOnServer extends AsyncTask<String, Void, String> {
        private String reference;
        private String error;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {

            } else {

            }
        }

        @Override
        protected String doInBackground(String... reference) {
            try {
                this.reference = reference[0];
                URL url = new URL(backend_url + "/verify/" + this.reference);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                String inputLine;
                inputLine = in.readLine();
                in.close();
                return inputLine;
            } catch (Exception e) {
                error = e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            return null;
        }
    }

    private void fundWallet(){
        gif.setVisibility(View.VISIBLE);
        disableView();
        StringRequest strReqs = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                gif.setVisibility(View.GONE);
                Toast.makeText(getActivity().getApplicationContext(),response, Toast.LENGTH_LONG).show();
                enableView();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    if(status.equals("success")){
                        mTextError.setText(msg);
                        //ac.restart(msg);
                    }
                    else if(status.equals("failed")){
                        mTextError.setText(msg);
                        //ac.restart("Couldnt fund your wallet, Please try again later");
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    //ac.restart("failed to add schedule, Please try again");

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity().getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                gif.setVisibility(View.GONE);
                enableView();
                mTextError.setText("Bad network, Please try again later");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                params.put("amount", price);


                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rr.add(strReqs );
    }

    public void disableView(){

        listener = mEditCardNum.getKeyListener();

        mEditCardNum.setFocusable(false);
        mEditCardNum.setClickable(false);
        mEditCardNum.setKeyListener(null);
        mEditCardNum.setCursorVisible(false);
        mEditCardNum.setFocusableInTouchMode(false);

        mEditCVC.setFocusable(false);
        mEditCVC.setClickable(false);
        mEditCVC.setKeyListener(null);
        mEditCVC.setCursorVisible(false);
        mEditCVC.setFocusableInTouchMode(false);

        mEditExpiryMonth.setFocusable(false);
        mEditExpiryMonth.setClickable(false);
        mEditExpiryMonth.setKeyListener(null);
        mEditExpiryMonth.setCursorVisible(false);
        mEditExpiryMonth.setFocusableInTouchMode(false);

        mEditExpiryYear.setFocusable(false);
        mEditExpiryYear.setClickable(false);
        mEditExpiryYear.setKeyListener(null);
        mEditExpiryYear.setCursorVisible(false);
        mEditExpiryYear.setFocusableInTouchMode(false);

        amt.setFocusable(false);
        amt.setClickable(false);
        amt.setKeyListener(null);
        amt.setCursorVisible(false);
        amt.setFocusableInTouchMode(false);

        mButtonPerformLocalTransaction.setClickable(false);
        im.setClickable(false);

    }

    public void enableView(){

        mEditCardNum.setFocusable(true);
        mEditCardNum.setClickable(true);
        mEditCardNum.setKeyListener(listener);
        mEditCardNum.setCursorVisible(true);
        mEditCardNum.setFocusableInTouchMode(true);

        mEditCVC.setFocusable(true);
        mEditCVC.setClickable(true);
        mEditCVC.setKeyListener(listener);
        mEditCVC.setCursorVisible(true);
        mEditCVC.setFocusableInTouchMode(true);

        mEditExpiryMonth.setFocusable(true);
        mEditExpiryMonth.setClickable(true);
        mEditExpiryMonth.setKeyListener(listener);
        mEditExpiryMonth.setCursorVisible(true);
        mEditExpiryMonth.setFocusableInTouchMode(true);

        mEditExpiryYear.setFocusable(true);
        mEditExpiryYear.setClickable(true);
        mEditExpiryYear.setKeyListener(listener);
        mEditExpiryYear.setCursorVisible(true);
        mEditExpiryYear.setFocusableInTouchMode(true);

        amt.setFocusable(true);
        amt.setClickable(true);
        amt.setKeyListener(listener);
        amt.setCursorVisible(true);
        amt.setFocusableInTouchMode(true);

        mButtonPerformLocalTransaction.setClickable(true);
        im.setClickable(false);
    }

}
