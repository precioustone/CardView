package info.androidhive.cardview;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import Adapters.ItemData;
import Adapters.ItemDataClass;
import Adapters.SpinnerClassAdapter;
import Adapters.SpinnerCousineAdapter;
import customfonts.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFlowFragment extends Fragment implements View.OnClickListener{

    private LinearLayout americanExpress, visa, mastercard;
    public ImageView rightmark1,rightmark2,rightmark3;
    private EditText_Roboto_Regular cardNum,holder,cvv;
    private MyTextView_Roboto_Medium nextBtn;
    private MyTextView_Roboto_Regular button;
    public ViewFlipper viewFlipper;
    private ImageView imageView;
    PaymentFlowActivity paymentFlowActivity;
    private Double price;


    public PaymentFlowFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_flow, container, false);
        paymentFlowActivity = (PaymentFlowActivity)getActivity();

        price = paymentFlowActivity.price;

        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewflip);
        imageView = (ImageView) view.findViewById(R.id.cardicon);

        ArrayList<ItemData> list = new ArrayList<>();



        list.add(new ItemData("Jan"));
        list.add(new ItemData("Feb"));
        list.add(new ItemData("Mar"));
        list.add(new ItemData("Apr"));
        list.add(new ItemData("May"));
        list.add(new ItemData("Jun"));
        list.add(new ItemData("Jul"));
        list.add(new ItemData("Aug"));
        list.add(new ItemData("Sep"));
        list.add(new ItemData("Oct"));
        list.add(new ItemData("Nov"));
        list.add(new ItemData("Dec"));
        Spinner sp = (Spinner) view.findViewById(R.id.spinner_month);
        SpinnerCousineAdapter adapter = new SpinnerCousineAdapter(getActivity(), R.layout.spinner_selecting_adults, R.id.data, list);
        sp.setAdapter(adapter);

        ArrayList<ItemDataClass> lists = new ArrayList<>();



        lists.add(new ItemDataClass("2017"));
        lists.add(new ItemDataClass("2018"));
        lists.add(new ItemDataClass("2019"));
        lists.add(new ItemDataClass("2020"));
        lists.add(new ItemDataClass("2021"));
        lists.add(new ItemDataClass("2022"));
        lists.add(new ItemDataClass("2023"));
        lists.add(new ItemDataClass("2024"));
        lists.add(new ItemDataClass("2025"));
        lists.add(new ItemDataClass("2026"));
        lists.add(new ItemDataClass("2027"));
        lists.add(new ItemDataClass("2028"));



        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_year);
        SpinnerClassAdapter adapters = new SpinnerClassAdapter(getActivity(), R.layout.spinner_selecting_adults, R.id.data, lists);
        spinner.setAdapter(adapters);

        cardNum = (EditText_Roboto_Regular)view.findViewById(R.id.cardNum);
        holder = (EditText_Roboto_Regular)view.findViewById(R.id.holdName);
        cvv = (EditText_Roboto_Regular) view.findViewById(R.id.cvv);
        nextBtn = (MyTextView_Roboto_Medium) view.findViewById(R.id.nextBtn);
        button = (MyTextView_Roboto_Regular) view.findViewById(R.id.button);

        button.setOnClickListener(this);

        RelativeLayout card = view.findViewById(R.id.card);
        RelativeLayout wallet = view.findViewById(R.id.wallet);
        RelativeLayout pod = view.findViewById(R.id.pod);

        americanExpress = (LinearLayout) view.findViewById(R.id.americanExpress);
        visa = (LinearLayout) view.findViewById(R.id.visa);
        mastercard = (LinearLayout) view.findViewById(R.id.mastercard);
        rightmark1 = (ImageView) view.findViewById(R.id.rightmark1);
        rightmark2 = (ImageView) view.findViewById(R.id.rightmark2);
        rightmark3 = (ImageView) view.findViewById(R.id.rightmark3);

        americanExpress.setOnClickListener(this);
        visa.setOnClickListener(this);
        mastercard.setOnClickListener(this);

        pod.setOnClickListener(this);
        mastercard.setOnClickListener(this);
        wallet.setOnClickListener(this);
        card.setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.americanExpress:

                rightmark1.setImageResource(R.drawable.ic_right);
                rightmark2.setImageResource(R.drawable.ic_round);
                rightmark3.setImageResource(R.drawable.ic_round);
                slideRightToLeft(view);
                imageView.setImageResource(R.drawable.ic_american_express);
                break;

            case R.id.visa:

                rightmark1.setImageResource(R.drawable.ic_round);
                rightmark2.setImageResource(R.drawable.ic_right);
                rightmark3.setImageResource(R.drawable.ic_round);
                slideRightToLeft(view);
                imageView.setImageResource(R.drawable.ic_visa);
                break;

            case R.id.mastercard:

                rightmark1.setImageResource(R.drawable.ic_round);
                rightmark2.setImageResource(R.drawable.ic_round);
                rightmark3.setImageResource(R.drawable.ic_right);
                slideRightToLeft(view);
                imageView.setImageResource(R.drawable.ic_mastercard);
                break;
            case R.id.pod:

                paymentFlowActivity.swapFrag("success");
                break;

            case R.id.wallet:

                paymentFlowActivity.showNotice();
                break;

            case R.id.card:

                slideRightToLeft(view);
                break;
            case R.id.button:

                viewFlipper.showPrevious();
                break;


        }
    }

    public void slideRightToLeft(View v) {
        viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
        viewFlipper.showNext();

    }

    private void changeBackground(int i, LinearLayout americanExpress) {
    }

}
