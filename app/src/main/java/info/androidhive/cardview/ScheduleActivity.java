package info.androidhive.cardview;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import co.paystack.android.Transaction;
import co.paystack.android.model.Charge;
import info.androidhive.cardview.CCFragment.CCNameFragment;
import info.androidhive.cardview.CCFragment.CCNumberFragment;
import info.androidhive.cardview.CCFragment.CCSecureCodeFragment;
import info.androidhive.cardview.CCFragment.CCValidityFragment;
import info.androidhive.cardview.helper.SQLiteHandler;
import info.androidhive.cardview.helper.SessionManager;

public class ScheduleActivity extends AppCompatActivity {

    ScheduleFragment scheduleFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setActionBar(toolbar);
        ImageView imageView = findViewById(R.id.toolbarIcon);
        TextView textView = findViewById(R.id.toolbarText);
        textView.setText("Schedule");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleActivity.this.onBackPressed();
            }
        });
        scheduleFragment = (ScheduleFragment)getFragmentManager().findFragmentByTag("SCHEDULE");

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        ScheduleFragment scheduleFragment = (ScheduleFragment)getFragmentManager().findFragmentByTag("SCHEDULE");
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.kg3:
                if (checked){
                    scheduleFragment.v[0] += 1;
                }
                break;
            case R.id.kg5:
                if (checked)
                {
                    scheduleFragment.v[1] += 1;
                }
                break;
            case R.id.kg7:
                if (checked)
                {
                    scheduleFragment.v[2] += 1;
                }
                break;
            case R.id.kg10:
                if (checked)
                {
                    scheduleFragment.v[3] += 1;
                }
                break;
            case R.id.kg12:
                if (checked){
                    scheduleFragment.v[4] += 1;
                }

                break;
            case R.id.kg25:
                if (checked){
                    scheduleFragment.v[5] += 1;
                }

                break;
            case R.id.kg50:
                if (checked)
                {
                    scheduleFragment.v[6] += 1;
                }
                break;
            case R.id.weekly:
                if (checked){
                    scheduleFragment.freq = "Weekly";
                }

                break;
            case R.id.monthly:
                if (checked){
                    scheduleFragment.freq = "Monthly";
                }

                break;
            case R.id.twomonths:
                if (checked)
                {
                    scheduleFragment.freq = "Two months";
                }
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.swap:
                if (checked)
                    scheduleFragment.swapRefill = "Swap";
                break;
            case R.id.refill:
                if (checked)
                    scheduleFragment.swapRefill = "Refill";
                break;
        }
    }

    public void swapFrag(String toWhich){
        //Toast.makeText(this,"i was called",Toast.LENGTH_SHORT).show();
        if(toWhich.equals("success")){
            SuccessFragment successFragment = new SuccessFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.schCont,successFragment,"SUCCESS").commit();
        }else {
            PaymentFlowFragment paymentFlowFragment = new PaymentFlowFragment();
            Bundle args = new Bundle();
            args.putDouble("price",scheduleFragment.priceTotal);
            Intent intent = new Intent(this,PaymentFlowActivity.class);
            intent.putExtra("price",scheduleFragment.priceTotal);
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    protected void populate(){
        scheduleFragment.populate();
    }

}
