package info.androidhive.cardview;

/**
 * Created by EBU on 2/12/2018.
 */

public class Cart {

    private static order Order;

    public static order getOrder() {
        return Order;
    }

    public static void setOrder(order order) {
        Order = order;
    }
}
