package app.android.example.com.poifinder.utilities;

/**
 * Created by ruiguo on 1/11/16.
 */

public enum Place_Type {
    RESTAURANT, SHOPPING_MALL, AMUSEMENT_PARK, AQUARIUM, ART_GALLERY, MUSEUM, ZOO, POINT_OF_INTEREST;
    public static Place_Type convert(String str){
        for(Place_Type type: Place_Type.values()){
            if(type.toString().equals(str)){
                return type;
            }
        }
        return null;
    }
}
