package info.androidhive.cardview;

/**
 * Created by EBU on 12/3/2017.
 */

public class mainOrder {
    private String amt;
    private String date;
    private String oid;
    private String status;
    private String sizes;
    private String qty;
    private String pay;
    private String brand;
    private String add;
    private double lat;
    private double lng;
    private int disId;
    //private String disName

    public mainOrder() {
    }

    public mainOrder(String amt, String date, String sizes,String qty,String pay,
                     String brand,String oid,String status,String add,double lat,double lng,int disId) {
        this.oid = oid;

        this.date = date;

        this.sizes = sizes;

        this.amt = amt;

        this.pay = pay;

        this.qty = qty;

        this.brand = brand;

        this.status = status;

        this.add = add;

        this.lat = lat;

        this.lng = lng;

        this.disId = disId;

        //this.disName = disName;
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

    public void setAdd(String add) {
        this.add = add;
    }

    public void setLat(double o) {
        this.lat = o;
    }

    public void setLng(double add) {
        this.lng = add;
    }

    public void setDisId(int disId) {
        this.disId = disId;
    }

   /* public void setDisName(String disName) {
        this.disName = disName;
    }*/

    public String getAmt() {
        return amt;
    }

    public String getOid() {
        return oid;
    }

    public String getStatus() {
        return status;
    }

    public String getSizes() {
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

    public String getAdd() {
        return add;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public int getDisId() {
        return disId;
    }

   /* public String getDisName() {
        return disName;
    }*/
}

