package info.androidhive.cardview.CCFragment;

/**
 * Created by EBU on 2/6/2018.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.cardview.CardFrontFragment;
import info.androidhive.cardview.CheckOutActivity;
import info.androidhive.cardview.R;
import info.androidhive.cardview.RefillActivity;
import info.androidhive.cardview.Utils.CreditCardEditText;

/**
        * A simple {@link Fragment} subclass.
        */

public class CCAmount extends Fragment {

    @BindView(R.id.et_name)
    CreditCardEditText et_name;
    TextView tv_Name;

    RefillActivity activity;
    CardFrontFragment cardFrontFragment;

    public CCAmount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ccname, container, false);
        ButterKnife.bind(this, view);

        activity = (RefillActivity) getActivity();
        cardFrontFragment = activity.cardFrontFragment;

        tv_Name = cardFrontFragment.getName();

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(tv_Name!=null)
                {
                    if (TextUtils.isEmpty(editable.toString().trim()))
                        tv_Name.setText("CARD HOLDER");
                    else
                        tv_Name.setText(editable.toString());

                }

            }
        });

        et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if(activity!=null)
                    {
                        activity.nextClick();
                        return true;
                    }

                }
                return false;
            }
        });


        et_name.setOnBackButtonListener(new CreditCardEditText.BackButtonListener() {
            @Override
            public void onBackClick() {
                if(activity!=null)
                    hideSoftKeyboard(activity);
            }
        });

        return view;
    }

    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
        else
            activity.onBackPressed();
    }

    public String getName()
    {
        if(et_name!=null)
            return et_name.getText().toString().trim();

        return null;
    }

}
