package info.androidhive.cardview;

/**
 * Created by EBU on 4/11/2018.
 */

public class Address {
    private static String address = "";

    public static void setAddress(String address) {
        Address.address = address;
    }

    public static String getAddress() {
        return address;
    }
}
