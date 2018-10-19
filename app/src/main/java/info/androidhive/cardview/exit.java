package info.androidhive.cardview;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by EBU on 1/5/2018.
 */

public class exit extends DialogFragment implements DialogInterface.OnShowListener {


    TextView cA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Exit")
                .setPositiveButton("OK",
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
        View view = i.inflate(R.layout.notice, null);

        cA = view.findViewById(R.id.msgN);
        cA.setText("Do you want to logout?");
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
                    if(logout.getLogoutCode() == 1){
                        MainActivity ma = (MainActivity)getActivity();
                        dismiss();
                        ma.logout();
                    }
                    else if(logout.getLogoutCode() == 2){
                        accountActivity ma = (accountActivity)getActivity();
                        dismiss();
                        ma.logout();
                    }
                    else if(logout.getLogoutCode() == 3){
                        TransActivity ma = (TransActivity)getActivity();
                        dismiss();
                        ma.logout();
                    }
                    else if(logout.getLogoutCode() == 4){
                        ProfileActivity ma = (ProfileActivity)getActivity();
                        dismiss();
                        ma.logout();
                    }

                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();}
            });
        }

    }

}

