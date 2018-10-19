package info.androidhive.cardview;

/**
 * Created by EBU on 12/2/2017.
 */

public class OrderList {
    private String amt;
    private String date;
    private String oid;
    private String status;
    private String sizes;
    private String qty;
    private String pay;
    private String brand;

    public OrderList() {
    }

    public OrderList(String amt, String date, String sizes,String qty,String pay,String brand,String oid,String status) {
        this.oid = oid;

        this.date = date;

        this.sizes = sizes;

        this.amt = amt;

        this.pay = pay;

        this.qty = qty;

        this.brand = brand;

        this.status = status;
    }


    public void setAmt(String amt) {
        this.amt = amt;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQty(String decs) {
        this.qty = decs;
    }

    public void setBrand(String b) {
        this.brand = b;
    }

    public void setPay(String decs) {
        this.pay = decs;
    }

    public void setOid(String o) {
        this.oid = o;
    }


    public String getAmt() {
        return amt;
    }

    public String getOid() {
        return oid;
    }

    public String getStatus() {
        return status;
    }

    public String getSize() {
        return sizes;
    }

    public String getQty() {
        return qty;
    }

    public String getPay() {
        return pay;
    }

    public String getBrand() {
        return brand;
    }

    public String getDate() {
        return date;
    }



}
