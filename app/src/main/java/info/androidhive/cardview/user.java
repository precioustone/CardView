package info.androidhive.cardview;

/**
 * Created by EBU on 10/18/2017.
 */

public class user {

    private String fullname;
    private String address;
    private String phone;
    private String Email;

    public user(){

    }

    public user( String fullname,String phone,String address,String email){
        this.fullname = fullname;
        this.address = address;
        this.phone = phone;
        this.Email = email;
    }


    public String getFullname(){
        return this.fullname;
    }
    public String getAddress(){
        return this.address;
    }
    public String getPhone(){
        return this.phone;
    }
    public String getEmail(){
        return this.Email;
    }


    public void setFullname(String fullname){
        this.fullname = fullname;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setEmail(String Email){
        this.Email = Email;
    }


}
