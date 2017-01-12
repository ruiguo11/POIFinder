package app.android.example.com.poifinder;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import app.android.example.com.poifinder.data.PlaceContract;
import app.android.example.com.poifinder.utilities.Place;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceDetailsActivityFragment extends Fragment {
    //public static final String ARG_ITEM_ID = "item_id";
    public static final String LOG_TAG = "PlaceDetailFragment";


    private Place place;
    private ImageView imageView;
    private RatingBar ratingBar;
    private TextView addressView;
    private TextView nameView;
    private TextView phoneView;
    private ImageButton favouritetButton;
    private TextView openhourView;
    private TextView reviewView;
    private boolean isFavourite;
    private String id;
    private String address;
    private String name;
    private double rating;
    private String photo_url;
    private String phone;
    private String opening_hours;
    private String review;

    //private String BASE_URL="https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    //private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";


    //private static final String Key = "AIzaSyAY1pzlS7HRxxZbELz2y8hPByMcsqUN0mo";
    //private static final String Key = "AIzaSyAE9cMWoCIhXhz9rUF51ed54mAQ_cttNzM";
    private CollapsingToolbarLayout appBarLayout;

    public PlaceDetailsActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            place = new Place();
            id= getArguments().getString(getString(R.string.COLUMN_PLACE_ID));
            place.setPlace_id(id);

            name = getArguments().getString(getString(R.string.COLUMN_PLACE_NAME));
            place.setPlace_name(name);

            address = getArguments().getString(getString(R.string.COLUMN_PLACE_ADDRESS));
            place.setPlace_address(address);

            rating = getArguments().getDouble(getString(R.string.COLUMN_PLACE_RATING));
            place.setPlace_rating(rating);

            photo_url = getArguments().getString(getString(R.string.COLUMN_PLACE_PHOTO));
            place.setPhoto(photo_url);

            //Log.d("onCreate", place.getPlace_name());
        }

        final Activity activity = this.getActivity();
        appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if(activity.findViewById(R.id.place_details_photo)!=null){
            imageView= (ImageView)activity.findViewById(R.id.place_details_photo);
        }


        if (appBarLayout != null) {
            appBarLayout.setTitle(place.getPlace_name());
            appBarLayout.setExpandedTitleColor(getResources().getColor(R.color.pink_500));
        }
    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.place_details, container, false);

            ratingBar=(RatingBar)rootView.findViewById(R.id.place_details_rating);

            addressView=(TextView)rootView.findViewById(R.id.place_details_address);
            favouritetButton=(ImageButton)rootView.findViewById(R.id.button_favourite);



            phoneView=(TextView)rootView.findViewById(R.id.place_details_phone);
            openhourView=(TextView)rootView.findViewById(R.id.place_details_openinghours);
            reviewView =(TextView)rootView.findViewById(R.id.acomment);

            if(imageView==null){
                imageView=(ImageView)rootView.findViewById(R.id.place_details_photo);
                imageView.setVisibility(View.VISIBLE);
            }


            setUpUI();

            return rootView;
    }

    private void setUpUI() {
        if(place.getPhoto()!=null) {

            if (place.getPhoto().equals("No preview photos") == false) {
                Picasso.with(getContext()).load(place.getPhoto()).into(imageView);
            } else {

                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }
        //Log.d("photo url", place.getPhoto());
        ratingBar.setRating((float) place.getPlace_rating());
        if(address!=null) {
            addressView.setText(place.getPlace_address().replaceAll(",", ",\n"));
        }
        String url = getString(R.string.DETAIL_BASE_URL)+place.getPlace_id()+"&key="+getString(R.string.Key);
        Log.d("update ui", url);
        new AsyncHttpTask().execute(url);


        if(isFavourts()==true) {
            favouritetButton.setImageResource(R.mipmap.ic_favourite);
        }
        else
            favouritetButton.setImageResource(R.mipmap.ic_unfavourite);
            favouritetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFavourts()==false) {
                    favouritetButton.setImageResource(R.mipmap.ic_favourite);
                    ContentValues values = new ContentValues();
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, id);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ADDRESS, address);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_NAME, name);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_PHONE, phone);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_PHOTO, photo_url);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_RATING, rating);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_HOURS, opening_hours);
                    values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_REVIEW, review);

                    Log.d(LOG_TAG, PlaceContract.PlaceEntry.CONTENT_URI.toString());
                    getContext().getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, values);


                    Log.d(LOG_TAG+ "insert values", values.toString());
                    Log.d(LOG_TAG, " ADD_to_favourt");
                }
                else{
                    favouritetButton.setImageResource(R.mipmap.ic_unfavourite);
                    Log.d(LOG_TAG, "already in favourts will be removed from favourts");
                    getContext().getContentResolver().delete(PlaceContract.PlaceEntry.CONTENT_URI,
                            PlaceContract.PlaceEntry.COLUMN_PLACE_ID+" = ?",
                            new String[]{id});

                }

            }
        });

    }

    public boolean isFavourts(){

        Cursor cursor = getContext().getContentResolver().query(
                PlaceContract.PlaceEntry.CONTENT_URI,
                null,
                PlaceContract.PlaceEntry.COLUMN_PLACE_ID + " = ?",
                new String[] { id },
                null
        );
        Log.d(LOG_TAG,"favourt id="+id);




        Log.d(LOG_TAG, PlaceContract.PlaceEntry.CONTENT_URI.toString());

        int numRows = cursor.getCount();
        Log.d(LOG_TAG, Integer.toString(numRows));

        cursor.close();
        if (numRows==1)
            return true;
        else
            return false;

    }


    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            int result = 0;
            try {
                // Create Apache HttpClient
                Log.d(LOG_TAG, "create Apache HttpClient");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                Log.d(LOG_TAG+"AsyncHttpTask", Integer.toString(statusCode));


                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = parseToString(httpResponse.getEntity().getContent());
                    parseJSON(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getLocalizedMessage());
            }
            Log.d(LOG_TAG+"AsyncHttpTask", Integer.toString(result));
            return result;
        }

        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI

            if(result==1){
               Log.d(LOG_TAG, "onPostExecute got data");
            }
            else{
                Log.d(LOG_TAG, "OnPostExecute Failed to load data");

            }


        }
        private String parseToString(InputStream inputStream) throws IOException {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            // Close stream
            if (null != inputStream) {
                inputStream.close();
            }
            return result;
        }

        private void parseJSON(String inputString) {
            Log.d(LOG_TAG, "parseJSON()");
            Log.d(LOG_TAG, inputString);


            try {
                JSONObject json_place = new JSONObject(inputString);
                JSONObject jsonObject = json_place.optJSONObject("result");
                Log.d(LOG_TAG, jsonObject.toString());


                    name = jsonObject.optString("name");
                    place.setPlace_name(name);


                rating = jsonObject.optDouble("rating");
                place.setPlace_rating(rating);

                photo_url = jsonObject.optJSONArray("photos").getJSONObject(0).optString("photo_reference");
                photo_url= getString(R.string.PHOTO_BASE_URL)+"&photoreference="+photo_url+"&key="+getString(R.string.Key);
                place.setPhoto(photo_url);
                Log.d("photo url", photo_url);
                if(jsonObject.optString("formatted_phone_number")!=null){
                    phone = jsonObject.optString("formatted_phone_number");
                    place.setPhone(phone);
                }
                address = jsonObject.optString("formatted_address");
                address= address.replaceAll(",", ",\n");

                if(jsonObject.optJSONObject("opening_hours").optJSONArray("weekday_text")!=null){
                    //Log.d("opening hours", jsonObject.optJSONObject("opening_hours").optJSONArray("weekday_text").toString());

                    opening_hours= jsonObject.optJSONObject("opening_hours").optJSONArray("weekday_text").toString();
                    opening_hours=opening_hours.substring(1, opening_hours.length()-1);
                    opening_hours= opening_hours.replaceAll(",", System.getProperty ("line.separator"));


                    place.setOpening_text(opening_hours);

                }
                review="";
                for(int i=0;i<jsonObject.optJSONArray("reviews").length();i++){
                    JSONObject temp = (JSONObject)jsonObject.optJSONArray("reviews").get(i);
                    review = review+"Reviewed by "+temp.optString("author_name")+", Rating: "+temp.optString("rating")+", Comment: "+temp.optString("text")+";";
                    review = review.replaceAll(";", "\n\n\n\n");
                    review= review.replaceAll(", ", "\n");


                    place.setPlace_reviews(review);
                }
                Log.d("review", review);

                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        //stuff that updates ui
                        if(appBarLayout!=null) {
                            appBarLayout.setTitle(place.getPlace_name());
                        }
                        addressView.setText(address);
                        ratingBar.setRating((float) place.getPlace_rating());
                        phoneView.setText(place.getPhone());
                        openhourView.setText(place.getOpening_text());
                        reviewView.setText(place.getPlace_reviews());
                        Picasso.with(getContext()).load(place.getPhoto()).into(imageView);


                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
