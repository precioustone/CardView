package info.androidhive.cardview;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by EBU on 10/18/2017.
 */
@SuppressWarnings("serial")
public class order implements Serializable {

    private String size;
    private String time;
    private String laterdate;
    private String qty;
    private String express;
    private String swap;
    private String brand;
    private String payM;
    private double price = 0;
    private String address;
    private int disId = 0;
    private  user User;
    private LatLng latLng;
    private String status;
    //private String fKey;

    public order(){

    }

    public order( user User,String size, String qty, String time, String laterdate,
                 String express, String swap, String brand, String payM,
                  double price, String address,int disId,LatLng latLng){

        this.User = User;
        this.laterdate = laterdate;
        this.size = size;
        this.time = time;
        this.qty = qty;
        this.brand = brand;
        this.express = express;
        this.payM = payM;
        this.swap = swap;
        this.address = address;
        this.price = price;
        this.disId = disId;
        this.latLng = latLng;
        this.status = "pending";
        //this.fKey = fKey;
    }

    public String getSize(){
        return this.size;
    }
    public String getTime(){
        return this.time;
    }
    public String getLaterdate(){
        return this.laterdate;
    }
    public String getQty(){
        return this.qty;
    }
    public String getPayM(){
        return this.payM;
    }
    public String getExpress(){
        return this.express;
    }
    public String getBrand(){
        return this.brand;
    }
    public String getSwap(){
        return this.swap;
    }
    public String getAddress(){
        return this.address;
    }
    public String getStatus(){
        return this.status;
    }
    /*public String getfKey(){
        return this.fKey;
    }*/
    public double getPrice(){
        return this.price;
    }
    public user getUser(){
        return this.User;
    }
    public double getDisId(){
        return this.disId;
    }
    public LatLng getLatLng(){
        return this.latLng;
    }


    public void setTime(String time){
        this.time = time;
    }
    public void setLaterdate(String laterdate){
        this.laterdate = laterdate;
    }
    public void setSize(String size){
        this.size = size;
    }
    public void setQty(String qty){
        this.qty = qty;
    }
    public void setBrand(String brand){
        this.brand = brand;
    }
    public void setSwap(String swap){
        this.swap = swap;
    }
    public void setExpress(String express){
        this.express = express;
    }
    public void setPrice(double price){
        this.price = price;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public void setPayM(String paym){
        this.payM = paym;
    }
    /*public void setfKey(String fKey){
        this.fKey = fKey;
    }*/
    public void setDisId(int disId){
        this.disId = disId;
    }
    public void setUser(user User){
        this.User = User;
    }
    public void setLatLng(LatLng latLng){
        this.latLng = latLng;
    }


}
