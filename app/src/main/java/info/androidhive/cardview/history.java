package info.androidhive.cardview;

/**
 * Created by EBU on 12/2/2017.
 */

public class history {
    private String amt;
    private String date;
    private String decs;

    public history() {
    }

    public history(String amt, String date, String decs) {
        this.amt = amt;

        this.date = date;

        this.decs = decs;
    }


    public void setAmt(String amt) {
        this.amt = amt;
    }

    public void setDesc(String decs) {
        this.decs = decs;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getAmt() {
        return amt;
    }

    public String getDate() {
        return date;
    }

    public String getDecs() {
       return decs;
    }


}
