package info.androidhive.cardview;

/**
 * Created by EBU on 12/4/2017.
 */

public class schd {

    private String size;
    private String ldate;
    private String ndate;
    private String qty;
    private String freq;
    private String id;

    public schd() {
    }

    public schd(String id,String size, String ldate, String qty,String ndate,String freq) {
        this.id = id;

        this.size = size;

        this.ldate = ldate;

        this.qty = qty;

        this.ndate = ndate;

        this.freq = freq;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setLdate(String ldate) {
        this.ldate = ldate;
    }

    public void setNdate(String ndate) {
        this.ndate = ndate;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }


    public String getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public String getQty() {
        return qty;
    }

    public String getLdate() {
        return ldate;
    }

    public String getNdate() {
        return ndate;
    }

    public String getFreq() {
        return freq;
    }

}
