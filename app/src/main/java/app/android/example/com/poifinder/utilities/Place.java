package app.android.example.com.poifinder.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiguo on 31/10/16.
 */

public class Place implements Parcelable {
    private String place_id;
    private String place_name;
    private String place_address;
    private double place_lat;
    private double place_lng;
    private boolean place_open_now;
    private String place_type;
    private String opening_text;
    private String place_reviews;
    private double place_rating;
    private String photo;
    private String phone;

    public Place(){
        place_id="";
        place_name="";
        place_address="";
        place_lat=0.0;
        place_lng=0.0;
        place_open_now=false;
        place_type="";
        opening_text="";
        place_reviews= "";
        place_rating=0.0;
        photo="";
        phone="";

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(place_id);
        parcel.writeString(place_name);
        parcel.writeString(place_address);
        parcel.writeDouble(place_lat);
        parcel.writeDouble(place_lng);
        parcel.writeString(Boolean.toString(place_open_now));
        parcel.writeString(place_type);
        parcel.writeString(opening_text);
        parcel.writeString(place_reviews);
        parcel.writeDouble(place_rating);
        parcel.writeString(photo);
        parcel.writeString(phone);

    }

    public static final Parcelable.Creator<Place> CREATOR
            = new Parcelable.Creator<Place>() {
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };


    private Place (Parcel in) {
        place_id=in.readString();
        place_name = in.readString();
        place_address= in.readString();
        place_lat = in.readDouble();
        place_lng=in.readDouble();
        if(in.readString().equals("true"))
            place_open_now=true;
        else
            place_open_now=false;
        place_type = in.readString();
        opening_text=in.readString();
        place_reviews=in.readString();
        place_rating=in.readDouble();
        photo=in.readString();
        phone=in.readString();
    }




    public String getPlace_id(){return place_id;}
    public void setPlace_id(String id){place_id=id;}

    public String getPlace_name(){return place_name;}
    public void setPlace_name(String name){place_name=name;}

    public String getPlace_address(){return place_address;}
    public void setPlace_address(String address){place_address=address;}

    public double getPlace_lat(){return place_lat;}
    public void setPlace_lat(double lat){place_lat=lat;}

    public double getPlace_lng() {return place_lng;}
    public void setPlace_lng(double lng){place_lng = lng;}

    public boolean getplace_open_now(){return place_open_now;}
    public void setPlace_open_now(boolean isopen){place_open_now=isopen;}

    public String getPlace_type(){return place_type;}
    public void setPlace_type(String type){place_type=type;}

    public String getOpening_text(){return opening_text;}
    public void setOpening_text(String text){opening_text=text;}

    public double getPlace_rating(){return place_rating;}
    public void setPlace_rating(double rating){place_rating=rating;}

    public String getPlace_reviews(){return place_reviews;}
    public void setPlace_reviews(String reviews){place_reviews=reviews;}

    public String getPhoto(){return photo;}
    public void setPhoto(String s){photo=s;}
    public String getPhone(){
        return phone;
    }
    public void setPhone(String s){phone=s;}

}
