package info.androidhive.cardview;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

public class PaymentFlowActivity extends AppCompatActivity {
    Double price;
    PaymentFlowFragment paymentFlowFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_flow);
        Toolbar toolbar = findViewById(R.id.customToolbar);
        setActionBar(toolbar);
        ImageView imageView = findViewById(R.id.toolbarIcon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlowActivity.this.onBackPressed();
            }
        });
        price = getIntent().getDoubleExtra("price",0.0);

        paymentFlowFragment = (PaymentFlowFragment)getSupportFragmentManager().findFragmentByTag("PAYMETHOD");
    }

    public void swapFrag(String toWhich){
        if(toWhich.equals("success")){
            SuccessFragment successFragment = new SuccessFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.schCont,successFragment,"SUCCESS").commit();
        }
    }
    public void showNotice(){
        DialogFragment newFragment = new ShowNotice();
        Bundle args = new Bundle();
        args.putString("msg", "Charges will be made from your G-wallet");
        args.putString("btn", "Continue");
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(),"Notice");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,TransActivity.class));
    }

    public void backpress(){
        if(paymentFlowFragment.viewFlipper.getCurrentView() == findViewById(R.id.spm)){
            this.onBackPressed();
        }else {
            paymentFlowFragment.viewFlipper.showPrevious();
        }
    }
}
