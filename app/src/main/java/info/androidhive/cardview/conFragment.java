package info.androidhive.cardview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 *factory method to
 * create an instance of this fragment.
 */
public class conFragment extends Fragment {
    private TextView cyli,price,quan,date,time,address;
    private Button button;
    private orderActivity o;

    public conFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.confirmation_frag, container, false);

        cyli = view.findViewById(R.id.cyliSize);
        price = view.findViewById(R.id.p);
        quan = view.findViewById(R.id.qtyVal);
        date = view.findViewById(R.id.datVal);
        time = view.findViewById(R.id.timVal);
        address = view.findViewById(R.id.resVal);
        button = (Button) view.findViewById(R.id.conbtn);
        o = (orderActivity)getActivity();

        setVals();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PaymentActivity.class);
                Cart.setOrder(o.Order);
                intent.putExtra("Address",o.getAdr());
                intent.putExtra("price",o.price);
                startActivity(intent);
            }
        });

        return view;
    }

    public void setVals(){
        o.Order.setPrice(o.price);
        cyli.setText(o.Order.getSize());
        price.setText("NGN "+o.price);
        quan.setText(o.Order.getQty());
        address.setText(o.getAdr());

        String calender = DateFormat.getDateTimeInstance().format(new Date());
        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        int year = dt.getYear();
        int day = dt.getDay();
        int month = dt.getMonth();
        String curTime = hours + ":" + minutes + ":" + seconds;
        String[] c = calender.split(" ");

        if(o.Order.getTime().isEmpty() && o.Order.getLaterdate().isEmpty()) {

            date.setText(c[0]+" "+c[1]+" "+c[2]);
            time.setText(curTime);
            o.Order.setTime(time.getText().toString());
            o.Order.setLaterdate(date.getText().toString());
        }
        else{
            date.setText(c[0]+" "+c[1]+" "+c[2]);
            time.setText(curTime);
        }



    }

}
