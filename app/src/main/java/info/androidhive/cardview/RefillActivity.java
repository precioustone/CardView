package info.androidhive.cardview;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import info.androidhive.cardview.CCFragment.CCNameFragment;
import info.androidhive.cardview.CCFragment.CCNumberFragment;
import info.androidhive.cardview.CCFragment.CCSecureCodeFragment;
import info.androidhive.cardview.CCFragment.CCValidityFragment;
import info.androidhive.cardview.Utils.CreditCardUtils;
import info.androidhive.cardview.Utils.ViewPagerAdapter;
import info.androidhive.cardview.helper.SQLiteHandler;
import info.androidhive.cardview.helper.SessionManager;

import static com.facebook.GraphRequest.TAG;
import static java.lang.Integer.parseInt;

public class RefillActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,FragmentManager.OnBackStackChangedListener {

    ViewFlipper viewFlipper;
    @BindView(R.id.btnNext)
    Button btnNext;

    private SessionManager session;
    private SQLiteHandler db;

    public CardFrontFragment cardFrontFragment;
    public CardBackFragment cardBackFragment;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOrder;

    //This is our viewPager
    private ViewPager viewPager;
    private ProgressDialog dialog;

    String backend_url = "https://gasnownow.com/backUrl/index.php";

    private TextView mTextReference;
    private Charge charge;
    static Transaction transaction;

    CCNumberFragment numberFragment;
    CCNameFragment nameFragment;
    CCValidityFragment validityFragment;
    CCSecureCodeFragment secureCodeFragment;

    int total_item;
    boolean backTrack = false;

    private boolean mShowingBack = false;

    String cardNumber, cardCVV, cardValidity, cardName;


    protected order Order;
    protected user User;
    protected static String adr;
    public static int[] v = new int[7];
    public static double priceTotal = 0;
    int extra = 0;
    int percent;
    static int k = 0;
    int qty = 0;
    String sizes,brandVal,swapRefill,orderDate,delDate,key,expressdel = "yes",payM = "On Delivery",emailRef = "";
    private static String URL_WALLET = "https://gasnownow.ng/rest_api/order.php";
    private static String URL_DETAILS = "https://gasnownow.ng/rest_api/userDetails.php";

    String[] cylinders = {"3KG","5KG","7KG","10KG","12.5KG","25KG","50KG"};

    private static final int logoutCode = 2;

    public int[] price = new int[7];

    private static String URL_PRICE = "https://gasnownow.ng/rest_api/prices.php";
    RequestQueue rs;
    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    private static final String ORDER_VALUES = "order values";

    //purchase form

    private RecyclerView rView;
    private ProgressBar pg;
    private RelativeLayout relHide,express,notExpress;
    private FloatingActionButton add;
    private Spinner brand;
    private Button next,checkout,done,proceed;
    private SeekBar sb;
    private java.util.List<List> PlaceList;
    private OrderReAdapter adapter;

    //end purchase form

    //user form

    private EditText firstN,lastN,street,state,phone,refEmail;
    private TextView tName,Atext,pText,qText,sText;
    HashMap<String, String> user;
    // end userf form


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            PaystackSdk.setPublicKey(bundle.getString("co.paystack.android.PublicKey"));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        PaystackSdk.initialize(getApplicationContext());
        Intent intent = getIntent();
        adr = intent.getStringExtra("Address");

        rs = Volley.newRequestQueue(getApplicationContext());
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOrder = mFirebaseDatabase.getReference().child("Orders");




        cardFrontFragment = new CardFrontFragment();
        cardBackFragment = new CardBackFragment();


        viewFlipper = findViewById(R.id.viewflip);

        //purchase form
        rView = findViewById(R.id.rview5);
        brand = (Spinner)findViewById(R.id.spin);
        add = findViewById(R.id.fab);
        next = findViewById(R.id.next);
        proceed = findViewById(R.id.proceed);
        checkout = findViewById(R.id.checkout);
        done = findViewById(R.id.done);
        sb = findViewById(R.id.sb);
        pg = findViewById(R.id.pg);
        relHide = findViewById(R.id.relHide);
        express = findViewById(R.id.express);
        notExpress = findViewById(R.id.not_express);

        Atext = findViewById(R.id.addLVal);
        tName = findViewById(R.id.nameLVal);
        sText =  findViewById(R.id.sizeLVal);
        qText = findViewById(R.id.noVal);
        pText = findViewById(R.id.phoneLVal);

        rView = (RecyclerView) findViewById(R.id.rview5);


// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(this,
                R.array.brands, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        brand.setAdapter(mAdapter);
        brand.setOnItemSelectedListener(this);

        refEmail = findViewById(R.id.refEmail);


        PlaceList = new ArrayList<>();
        adapter = new OrderReAdapter(this, PlaceList);
        sb.setOnSeekBarChangeListener(seekBarListener);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCylinderPickerDialog(v);
            }
        });


        next.setOnClickListener(pro);
        proceed.setOnClickListener(pro1);
        checkout.setOnClickListener(pro2);
        done.setOnClickListener(pro3);

        Button track = (Button)findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RefillActivity.this,TransActivity.class));
            }
        });
        //end purchase form

        //user form

        firstN = findViewById(R.id.firstN);
        lastN = findViewById(R.id.lastN);
        street = findViewById(R.id.street);
        state = findViewById(R.id.state);
        phone = findViewById(R.id.phone);

        String name[] = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ");

        firstN.setText(name[0]);
        lastN.setText(name[1]);
        street.setText(adr);
        state.setText("Lagos");

        //end user form

        if (savedInstanceState == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, cardFrontFragment).commit();

        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        getFragmentManager().addOnBackStackChangedListener(this);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == total_item)
                    btnNext.setText("SUBMIT");
                else
                    btnNext.setText("NEXT");

                Log.d("track", "onPageSelected: " + position);

                if (position == total_item) {
                    flipCard();
                    backTrack = true;
                } else if (position == total_item - 1 && backTrack) {
                    flipCard();
                    backTrack = false;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager.getCurrentItem();
                if (pos < total_item) {
                    viewPager.setCurrentItem(pos + 1);
                } else {
                    checkEntries();
                }

            }
        });

        price(email);
        user = db.getUserDetails();
    }



    public void slideRightToLeft(View v) {
        viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
        viewFlipper.showNext();

    }

    View.OnClickListener pro = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            k = 1;
            addDetails(k,v);
            validate();
        }
    };

    View.OnClickListener pro1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            k = 2;
            addDetails(k,v);
            validate();
        }
    };

    View.OnClickListener pro2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            k = 3;
            addDetails(k,v);
            validate();
        }
    };

    View.OnClickListener pro3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(RefillActivity.this,MainActivity.class));
        }
    };

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

    private void addDetails( int k, View v){
        showDialog();

        if(k == 1) {
            Order = new order();
            sizes = "";
            qty = 0;
            priceTotal = 0;
            String c[] = new String[2];
            showDialog();
            if(delDate != null)
                c = delDate.split(" ");
            for (int i = 0; i < this.v.length; i++) {
                qty += this.v[i];
                priceTotal += price[i] * this.v[i];
                for(int j = 0; j < this.v[i]; j++){
                   sizes += cylinders[i]+"| ";
                }
            }
            priceTotal += extra;
            Order.setSize(sizes);
            Order.setBrand(brandVal);
            Order.setQty(String.valueOf(qty));
            Order.setLaterdate(c[0]);
            Order.setTime(c[1]);
            Order.setSwap(swapRefill);
            Order.setPrice(priceTotal);
            Order.setLatLng(myLocation.getLatLng());
            Order.setExpress(expressdel);
            phone.setText(user.get("phone"));
            street.setText(user.get("address"));

            if(Order.getSize() == null || Order.getExpress()== null || Order.getTime()== null || Order.getBrand()== null||
                    Order.getSwap()== null || Order.getPrice() == 0||Order.getQty()== null || Order.getLaterdate()== null||
                    Order.getLatLng() == null){
                hideDialog();
                Toast.makeText(RefillActivity.this,"All details must be entered",Toast.LENGTH_LONG).show();
            }
            else {
                hideDialog();
                slideRightToLeft(v);
            }
        }
        if(k == 2) {
            showDialog();
            User = new user(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),phone.getText().toString(),
            street.getText().toString()+","+state.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail());
            Order.setUser(User);
            Order.setAddress(street.getText().toString()+","+state.getText().toString());
            Cart.setOrder(this.Order);

            if(Order.getUser().getAddress().equals("You are at this location") || Order.getUser().getAddress().equals("")||
                    Order.getUser().getPhone() == null){
                hideDialog();
                Toast.makeText(RefillActivity.this,"All details must be entered",Toast.LENGTH_LONG).show();
            }
            else {
                hideDialog();
                tName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                Atext.setText(Order.getAddress());
                pText.setText(phone.getText().toString());
                sText.setText(Order.getSize());
                qText.setText(Order.getQty());
                emailRef = refEmail.getText().toString();
                TextView prText = findViewById(R.id.priceLVal);
                prText.setText("\u20A6"+Order.getPrice());
                slideRightToLeft(v);
            }
        }
        if(k == 3){
            showDialog();
            if(payM.equals("On Delivery")){
                Order.setPayM(payM);
                key = addFOrder();
                addOrder("lastView");
            }
            else if(payM.equals("G-Wallet")){
                Order.setPayM(payM);
                key = addFOrder();
                DialogFragment newFragment = new notice();
                Bundle args = new Bundle();
                args.putString("msg", "Charges will be made from your G-wallet");
                args.putString("btn", "Continue");
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(),"Notice");
            }
            else{
                Order.setPayM(payM);
                key = addFOrder();
               addOrder("nextView");
            }

        }
    }

    private void validate(){

    }

    public  void fillRecycler(){
        //Toast.makeText(getActivity(),"Got here as well",Toast.LENGTH_LONG).show();
        if(PlaceList.size() > 0 ) {
            rView.setVisibility(View.VISIBLE);
            //adapter.notifyDataSetChanged();
            adapter.notifyItemInserted(PlaceList.size() - 1);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        brandVal = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showCylinderPickerDialog(View v) {
        DialogFragment newFragment = new PickCylinderSize();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(),"PICK CYLINDER");
    }

    @Override
    protected void onStop() {
        super.onStop();
        for(int i = 0; i < v.length; i++){
            v[i] = 0;
        }
    }


    // listener object for the SeekBar's progress changed events
    private final SeekBar.OnSeekBarChangeListener seekBarListener =
            new SeekBar.OnSeekBarChangeListener() {
                // update percent, then call calculate
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    TextView textView = findViewById(R.id.sbPro);
                    textView.setText(progress+" mins");
                    //Toast.makeText(RefillActivity.this," "+progress,Toast.LENGTH_LONG).show();
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
        else if(cal < 20 && cal >= 10)
            extra += 200;
        else if(cal < 10 && cal >= 0)
            extra += 300;

        priceTotal += extra;
    }

    private void price(final String email){
        //String tag_string_req = "req_login";
        showDialog();
        StringRequest strReqs = new StringRequest(Request.Method.POST,
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
                        Snackbar.make(findViewById(R.id.infoCont),"Bad network",Snackbar.LENGTH_LONG)
                                .setAction("Retry",new action()).show();
                    }else {
                        //calcPrice()
                        viewFlipper.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(RefillActivity.this, "Sorry an error occured", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Snackbar.make(viewFlipper,"Bad network",Snackbar.LENGTH_LONG)
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
        //ScrollView scrollView = findViewById(R.id.sumScr);
        pg.setVisibility(View.VISIBLE);
        /*if(pg.getVisibility() == View.VISIBLE){
            if(viewFlipper.getCurrentView() == findViewById(R.id.summary)){
                final boolean isBlockedScrollView = false;
                scrollView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        return isBlockedScrollView;
                    }
                });
            }
        }*/
    }

    private void hideDialog() {
        //ScrollView scrollView = findViewById(R.id.sumScr);
        pg.setVisibility(View.GONE);
        /*if(pg.getVisibility() == View.VISIBLE){
            if(viewFlipper.getCurrentView() == findViewById(R.id.summary)){
                final boolean isBlockedScrollView = true;
                scrollView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        return isBlockedScrollView;
                    }
                });
            }
        }*/
    }

    public class action implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            price(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.kg3:
                if (checked){
                    v[0] += 1;
                }
                break;
            case R.id.kg5:
                if (checked)
                {
                    v[1] += 1;
                }
                break;
            case R.id.kg7:
                if (checked)
                {
                    v[2] += 1;
                }
                break;
            case R.id.kg10:
                if (checked)
                {
                   v[3] += 1;
                }
                break;
            case R.id.kg12:
                if (checked){
                    v[4] += 1;
                }

                break;
            case R.id.kg25:
                if (checked){
                    v[5] += 1;
                }

                break;
            case R.id.kg50:
                if (checked)
                {
                    v[6] += 1;
                }
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_yes:
                if (checked) {
                    expressdel = "yes";
                    if (express.getVisibility() == View.GONE) {
                        express.setVisibility(View.VISIBLE);
                        notExpress.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.radio_no:
                if (checked) {
                    expressdel = "no";
                    if (notExpress.getVisibility() == View.GONE) {
                        notExpress.setVisibility(View.VISIBLE);
                        express.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.swap:
                if (checked)
                    swapRefill = "Swap";
                break;
            case R.id.refill:
                if (checked)
                    swapRefill = "Refill";
                break;
            case R.id.hour:
                if (checked) {
                    if (relHide.getVisibility() == View.GONE && express.getVisibility() == View.VISIBLE) {
                        relHide.setVisibility(View.VISIBLE);
                    }
                    getDate(view.getId());
                }
                    break;
            case R.id.hour2:
                if (checked)
                    if (relHide.getVisibility() == View.VISIBLE) {
                        relHide.setVisibility(View.GONE);
                    }
                    getDate(view.getId());
                    break;
            case R.id.twth:
                if (checked)
                    if (relHide.getVisibility() == View.VISIBLE) {
                        relHide.setVisibility(View.GONE);
                    }
                    getDate(view.getId());
                    break;
            case R.id.twth2:
                if (checked)
                    if (relHide.getVisibility() == View.VISIBLE) {
                        relHide.setVisibility(View.GONE);
                    }
                    getDate(view.getId());
                    break;
            case R.id.frfi:
                if (checked)
                    if (relHide.getVisibility() == View.VISIBLE) {
                        relHide.setVisibility(View.GONE);
                    }
                    getDate(view.getId());
                    break;
            case R.id.frfi2:
                if (checked)
                    if (relHide.getVisibility() == View.VISIBLE) {
                        relHide.setVisibility(View.GONE);
                    }
                    getDate(view.getId());
                    break;
            case R.id.pod:
                if (checked) {
                    payM = "On Delivery";
                    Order.setPayM("On Delivery");
                }
                break;
            case R.id.card:
                if (checked)
                {
                    payM = "Card";
                    Order.setPayM("Card");
                }
                break;
            case R.id.gwallet:
                if (checked){
                    payM = "G-Wallet";
                    Order.setPayM("G-Wallet");
                }
                break;
        }
    }

    public void useG(){
        addOrder("lastView");
    }

    private String addFOrder(){
        String key;
        key = mOrder.push().getKey();
        mOrder.child(key).setValue(Cart.getOrder());
        return key;
    }

    private void getDate(int id){

        Calendar c = Calendar.getInstance();
        String calender = DateFormat.getDateTimeInstance().format(new Date());
        //int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String[] co = calender.split(" ");

        String dat = (co[0]+" "+co[1]+" "+co[2]);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderDate = df.format(c.getTime());


        switch(id) {

            case R.id.hour:
                c.add(Calendar.HOUR_OF_DAY,(percent/60));
                delDate = df.format(c.getTime());
                break;
            case R.id.hour2:
                c.add(Calendar.DATE,1);
                delDate = df.format(c.getTime());
                break;
            case R.id.twth:
                c.add(Calendar.HOUR_OF_DAY,3);
                delDate = df.format(c.getTime());
                break;
            case R.id.twth2:
                c.add(Calendar.DATE,3);
                delDate = df.format(c.getTime());
                break;
            case R.id.frfi:
                c.add(Calendar.HOUR_OF_DAY,5);
                delDate = df.format(c.getTime());
                break;
            case R.id.frfi2:
                c.add(Calendar.HOUR_OF_DAY,5);
                delDate = df.format(c.getTime());
                break;
        }
    }

    private void addOrder(final String des){
        showDialog();
        final String TAG = "ORDER_TAG";
        StringRequest strReqst = new StringRequest(Request.Method.POST,
                URL_WALLET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");
                    if(status.equals("success")){
                        if(des.equals("lastView")){
                            viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.info)));
                        }
                        else{
                            viewFlipper.setOutAnimation(RefillActivity.this, R.anim.slide_out_left);
                            viewFlipper.showNext();
                        }

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Sorry an error occured", Toast.LENGTH_LONG).show();
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
                newFragment.show(getFragmentManager(),"Notice");
                Snackbar.make(viewFlipper,"Bad network",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry",new action1(des)).show();
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
                params.put("ref",emailRef);

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        rs.add(strReqst);
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
                        RefillActivity.this.phone.setText(phone);
                    }
                    else {

                    }
                    if(viewFlipper.getVisibility() == View.GONE)
                        viewFlipper.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Snackbar.make(rView,"Bad network",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                Snackbar.make(viewFlipper,"Bad network",Snackbar.LENGTH_INDEFINITE)
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
        rs.add(strReq );
    }
    public class action1 implements View.OnClickListener{
        String des;

        public action1(String des){
            this.des = des;
        }
        @Override
        public void onClick(View v) {

            // Code to undo the user's last action
            addOrder(des);
        }
    }

    public void checkEntries() {
        cardName = nameFragment.getName();
        cardNumber = numberFragment.getCardNumber();
        cardValidity = validityFragment.getValidity();
        cardCVV = secureCodeFragment.getValue();

        if (TextUtils.isEmpty(cardName)) {
            Toast.makeText(RefillActivity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardNumber) || !CreditCardUtils.isValid(cardNumber.replace(" ",""))) {
            Toast.makeText(RefillActivity.this, "Enter Valid card number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardValidity)||!CreditCardUtils.isValidDate(cardValidity)) {
            Toast.makeText(RefillActivity.this, "Enter correct validity", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cardCVV)||cardCVV.length()<3) {
            Toast.makeText(RefillActivity.this, "Enter valid security number", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(RefillActivity.this, "Your card is added", Toast.LENGTH_SHORT).show();
            try {
                startAFreshCharge(true);
            } catch (Exception e) {
                Toast.makeText(RefillActivity.this,String.format("An error occurred while charging card: %s %s", e.getClass().getSimpleName(), e.getMessage()),Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        numberFragment = new CCNumberFragment();
        nameFragment = new CCNameFragment();
        validityFragment = new CCValidityFragment();
        secureCodeFragment = new CCSecureCodeFragment();
        adapter.addFragment(numberFragment);
        adapter.addFragment(nameFragment);
        adapter.addFragment(validityFragment);
        adapter.addFragment(secureCodeFragment);

        total_item = adapter.getCount() - 1;
        viewPager.setAdapter(adapter);

    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }
        // Flip to the back.
        //setCustomAnimations(int enter, int exit, int popEnter, int popExit)

        mShowingBack = true;

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.fragment_container, cardBackFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(viewFlipper.getCurrentView() == findViewById(R.id.check)) {
            int pos = viewPager.getCurrentItem();
            if (pos > 0) {
                viewPager.setCurrentItem(pos - 1);
            } else
                viewFlipper.showPrevious();
        }
        else if(viewFlipper.getCurrentView() == findViewById(R.id.details)) {
            viewFlipper.showPrevious();
        }
        else if(viewFlipper.getCurrentView() == findViewById(R.id.summary ) && pg.getVisibility() == View.GONE) {
            rs.stop();
            viewFlipper.showPrevious();
        }
        else {
            super.onBackPressed();
            this.finish();
        }
    }

    public void nextClick() {
        btnNext.performClick();
    }

    private void startAFreshCharge(boolean local) {
        // initialize the charge
        charge = new Charge();
        charge.setCard(loadCardFromForm());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Performing transaction... please wait");
        dialog.show();

        if (local) {
            // Set transaction params directly in app (note that these params
            // are only used if an access_code is not set. In debug mode,
            // setting them after setting an access code would throw an exception

            charge.setAmount((int)priceTotal *100);
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
            new RefillActivity.fetchAccessCodeFromServer().execute(backend_url + "/new-access-code");
        }
    }

    /**
     * Method to validate the form, and set errors on the edittexts.
     */
    private Card loadCardFromForm() {
        //validate fields
        Card card;

        String cardNum = cardNumber;

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        String cvc = cardCVV;
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        String sMonth = cardValidity.substring(0,2);
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = cardValidity.substring(3,5);
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

        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    private void chargeCard() {
        RefillActivity.transaction = null;
        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {
                dismissDialog();

                RefillActivity.transaction = transaction;
                //mTextError.setText(" ");
                Toast.makeText(RefillActivity.this, transaction.getReference(), Toast.LENGTH_LONG).show();
                updateTextViews();
                //mCallback.nextButtonClicked("succ");
                new RefillActivity.verifyOnServer().execute(transaction.getReference());
                //startActivity(new Intent(RefillActivity.this,SuccessActivity.class));
                viewFlipper.showNext();
            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {
                RefillActivity.transaction = transaction;
                Toast.makeText(RefillActivity.this, transaction.getReference(), Toast.LENGTH_LONG).show();
                updateTextViews();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                RefillActivity.transaction = transaction;
                if (error instanceof ExpiredAccessCodeException) {
                    startAFreshCharge(false);
                    chargeCard();
                    return;
                }

                dismissDialog();

                if (transaction.getReference() != null) {
                    Toast.makeText(RefillActivity.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(RefillActivity.this,String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()),Toast.LENGTH_LONG).show();
                    new RefillActivity.verifyOnServer().execute(transaction.getReference());
                } else {
                    Toast.makeText(RefillActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(RefillActivity.this,String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()), Toast.LENGTH_LONG).show();
                }
                updateTextViews();
            }

        });
    }

    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void updateTextViews() {
        if (transaction.getReference() != null) {
            Toast.makeText(RefillActivity.this,String.format("Reference: %s", transaction.getReference()),Toast.LENGTH_LONG).show();
        }/* else {
            mTextReference.setText("No transaction");
        }*/
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
                dismissDialog();
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
                dismissDialog();
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


}
