package info.androidhive.cardview;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PickCylinderSize extends DialogFragment implements DialogInterface.OnShowListener {

    String sizes;
    RefillActivity refill;

    public PickCylinderSize() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("PICK CYLINDER SIZES")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {

                                } catch (Exception e) {


                                }
                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.pick_cylinder, null);

        refill = (RefillActivity)getActivity();

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


                    dismiss();
                    refill.populate();
                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.kg3:
                if (checked){
                    sizes += "3KG|";
                    refill.v[0] += 1;
                    refill.cylinders[0] = "3KG";
                }

                break;
            case R.id.kg5:
                if (checked)
                {
                    sizes += "5KG|";
                    refill.v[1] += 1;
                    refill.cylinders[1] = "5KG";
                }

                break;
            case R.id.kg7:
                if (checked)
                {
                    sizes += "7KG|";
                    refill.v[2] += 1;
                    refill.cylinders[2] = "7KG";
                }

                break;
            case R.id.kg10:
                if (checked)
                {
                    sizes += "10KG|";
                    refill.v[3] += 1;
                    refill.cylinders[3] = "10KG";
                }
                break;
            case R.id.kg12:
                if (checked){
                    sizes += "12.5KG|";
                    refill.v[4] += 1;
                    refill.cylinders[4] = "12.5KG";
                }

                break;
            case R.id.kg25:
                if (checked){
                    sizes += "25KG|";
                    refill.v[5] += 1;
                    refill.cylinders[5] = "25KG";
                }

                break;
            case R.id.kg50:
                if (checked)
                {
                    sizes += "50KG|";
                    refill.v[6] += 1;
                    refill.cylinders[6] = "50KG";
                }

                break;
        }
    }

}
