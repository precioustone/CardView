package info.androidhive.cardview;

/**
 * Created by EBU on 3/31/2018.
 */

public class List {

    int price;
    String size;

    public List(){

    }

    public List(String size,int price){
       this.price = price;
       this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
