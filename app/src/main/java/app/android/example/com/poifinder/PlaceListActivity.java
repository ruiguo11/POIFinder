package app.android.example.com.poifinder;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import app.android.example.com.poifinder.Widget.POIWidgetProvider;
import app.android.example.com.poifinder.data.PlaceContract;
import app.android.example.com.poifinder.utilities.Place;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class PlaceListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, LoaderManager.LoaderCallbacks {

    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;


    protected LocationRequest mLocationRequest;


    private boolean mTwoPane;
    public PlaceListAdapter placeListAdapter;
    public ArrayList<Place> placelist = new ArrayList<>();
    private RecyclerView recyclerView;
    private boolean mIsLoading;
    private boolean isConnected;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE=1;

    private String type;
    private int LOADER_ID=0;
    private Cursor mCursor;

    String TYPE_KEY="type";

    String FAVOURITE="favourites";


    private ImageButton button_hotel;
    private ImageButton button_rest;
    private ImageButton button_shopping;
    private ImageButton button_attr;

    private TextView textView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        type=getString(R.string.restaurant);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        textView = (TextView)findViewById(R.id.list_emptyView);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected==true) {

            buildGoogleClient();
            placelist = new ArrayList<>();
            placelist.clear();

            recyclerView = (RecyclerView) findViewById(R.id.place_list);
            assert recyclerView != null;
            recyclerView.setHasFixedSize(true);

            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            });

            setupButtons();


            getLoaderManager().initLoader(LOADER_ID, null, this);
            placeListAdapter = new PlaceListAdapter(getApplicationContext(), placelist);
            if (savedInstanceState != null) {
                type = savedInstanceState.getString(TYPE_KEY);
                placelist = savedInstanceState.getParcelableArrayList(getString(R.string.PLACE_KEY));
                //placeListAdapter = new PlaceListAdapter(getApplicationContext(), placelist);
                Log.d(LOG_TAG, "there is data in InstanceState");

                PlaceDetailsActivityFragment fragment = (PlaceDetailsActivityFragment) getSupportFragmentManager().findFragmentById(R.id.place_detail_container);

                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            } else {
            /*
            if (mLastLocation != null) {
                String url = BASE_URL + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + ADD_URL + "restaurant" + API_URL + Key;
                Log.d(LOG_TAG, url);

                new AsyncHttpTask().execute(url);
                placeListAdapter = new PlaceListAdapter(getApplicationContext(), placelist);
                if(placelist!=null) {
                    Log.d(LOG_TAG, "onCreate"+placelist.size());
                    //placeListAdapter = new PlaceListAdapter(getApplicationContext(), placelist);
                    placeListAdapter.setGridData(placelist);
                    recyclerView.setAdapter(placeListAdapter);
                    //setupRecyclerView(recyclerView);
                }
                else{

                }
            }
            */
            }
        }
        else{
            textView.setText("No internet connection");
        }

        setupWindowAnimations();
        if (findViewById(R.id.place_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }
    private void setupButtons() {

        button_hotel = (ImageButton) findViewById(R.id.button_hotel);

        button_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = getString(R.string.hotel);
                String url = getString(R.string.List_BASE_URL) + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"+getString(R.string.ADD_URL)+"&type=" + type + "&"+getString(R.string.API_URL) + getString(R.string.Key);
                Log.d(LOG_TAG, url);
                new AsyncHttpTask().execute(url);
                placelist.clear();

            }
        });
        button_rest = (ImageButton) findViewById(R.id.button_rest);
        button_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = getString(R.string.restaurant);
                String url = getString(R.string.List_BASE_URL)+ mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"+getString(R.string.ADD_URL) +"&type="+ type +"&"+getString(R.string.API_URL) + getString(R.string.Key);
                Log.d(LOG_TAG, url);
                new AsyncHttpTask().execute(url);
                placelist.clear();
            }
        });
        button_shopping = (ImageButton) findViewById(R.id.button_shopping);
        button_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = getString(R.string.shopping_mall);
                String url = getString(R.string.List_BASE_URL) + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() +"&"+ getString(R.string.ADD_URL) +"&type=" + type + "&"+getString(R.string.API_URL)+ getString(R.string.Key);
                Log.d(LOG_TAG, url);
                new AsyncHttpTask().execute(url);
                placelist.clear();
            }
        });
        button_attr = (ImageButton) findViewById(R.id.button_attr);
        button_attr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = getString(R.string.park);
                String url = getString(R.string.List_BASE_URL)+ mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"+getString(R.string.ADD_URL)  +"&type="+ type +"&"+getString(R.string.API_URL) + getString(R.string.Key);
                Log.d(LOG_TAG, url);
                new AsyncHttpTask().execute(url);
                placelist.clear();
            }
        });

        ImageButton button_favourite = (ImageButton) findViewById(R.id.list_button_favourite);
        button_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placelist.clear();
                type = "favourites";

                Toast.makeText(getBaseContext(), "Favourites", Toast.LENGTH_LONG).show();
                Cursor cursor = getContentResolver().query(
                        PlaceContract.PlaceEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
                Log.d(LOG_TAG, "no of favouritess" + Integer.toString(cursor.getCount()));

                cursor.moveToFirst();

                while (cursor.isAfterLast() == false) {
                    Place aplace = new Place();

                    aplace.setPlace_id(cursor.getString(1));
                    aplace.setPlace_name(cursor.getString(2));

                    aplace.setPhone(cursor.getString(3));
                    aplace.setPlace_address(cursor.getString(4));
                    aplace.setPlace_rating(cursor.getDouble(5));
                    aplace.setPhoto(cursor.getString(6));
                    aplace.setPlace_type(cursor.getString(7));
                    aplace.setPlace_reviews(cursor.getString(8));


                    Log.d(LOG_TAG, "favourts" + aplace.getPlace_name());
                    cursor.moveToNext();
                    placelist.add(aplace);
                }
                placeListAdapter.setGridData(placelist);
                recyclerView.setAdapter(placeListAdapter);
                PlaceDetailsActivityFragment fragment = (PlaceDetailsActivityFragment) getSupportFragmentManager().findFragmentById(R.id.place_detail_container);

                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                com.google.android.gms.location.places.Place aplace=PlaceAutocomplete.getPlace(this, data);
                Log.i(LOG_TAG, "Place: " + aplace.getName());

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+aplace.getName());

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(getString(R.string.googlemap_package));
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(LOG_TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new PlaceListAdapter(getApplicationContext(), placelist));

    }

    protected synchronized void buildGoogleClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);

    }

    private void updateWidget(){
        Log.d(LOG_TAG, "updateWidget()");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        int [] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, POIWidgetProvider.class));
        if(ids.length>=0){
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widget_poi_collection);
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //setEmptyView();
        // This narrows the return to only the stocks that are most current.
        Log.d(LOG_TAG, "onCreateLoader");

        return new CursorLoader(this, PlaceContract.PlaceEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        Log.d(LOG_TAG, "onLoadFinished");
        updateWidget();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(LOG_TAG, "onLoaderReset");


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(PlaceListActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            type = savedInstanceState.getString(TYPE_KEY);
            placelist = savedInstanceState.getParcelableArrayList(getString(R.string.PLACE_KEY));
            placeListAdapter.setGridData(placelist);
            recyclerView.setAdapter(placeListAdapter);

            Log.d(LOG_TAG, "on RestoredInstanceState " + placelist.size());
            PlaceDetailsActivityFragment fragment =(PlaceDetailsActivityFragment)getSupportFragmentManager().findFragmentById(R.id.place_detail_container);

            if (fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();


        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");

        outState.putParcelableArrayList(getString(R.string.PLACE_KEY), placelist);
        outState.putString(TYPE_KEY, type);
        //outState.putInt("Navgition select", select_nav_index);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED | ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(LOG_TAG, "permission check");


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE);


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            if(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)!=null){
                mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            else{
                startLocationUpdates();


            }

            return;
        }
        else {
            Log.d(LOG_TAG,"on Connection, already has permission");

            if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) != null) {

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                Log.d(LOG_TAG, "last known location is not empty");


                if(type!=FAVOURITE) {
                    String url = getString(R.string.List_BASE_URL) + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"+getString(R.string.ADD_URL) +"&type=" + getString(R.string.API_URL) +"&"+getString(R.string.API_URL)+ getString(R.string.Key);
                    Log.d("onConnected", url);
                    new AsyncHttpTask().execute(url);
                    placelist.clear();
                }


                Log.d("create, on Connected", Integer.toString(placelist.size()));
                /*
                if(placelist.size()>0) {
                    placeListAdapter.setGridData(placelist);
                    recyclerView.setAdapter(placeListAdapter);
                    //setupRecyclerView(recyclerView);
                }

*/
            } else {
                Log.d(LOG_TAG, "last known location is empty");
                startLocationUpdates();

            }
            return;
        }


    }

    protected void startLocationUpdates() {
        Log.d(LOG_TAG, "startLocationUpdates");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        mLastLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null){
            Log.d(LOG_TAG, "updated location but still null");
        }
        else{
            Log.d(LOG_TAG, "Last location is not empty");
        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "request granded");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(60000);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


                    if (mLastLocation != null) {
                        if(type!=FAVOURITE) {
                            String url = getString(R.string.List_BASE_URL) + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"+getString(R.string.ADD_URL) +"&type="+ type +"&"+getString(R.string.API_URL) + getString(R.string.Key);
                            new AsyncHttpTask().execute(url);
                            placeListAdapter.setGridData(placelist);
                            recyclerView.setAdapter(placeListAdapter);
                        }


                        //setupRecyclerView(recyclerView);

                    }
                    else{
                        Log.d(LOG_TAG, "Last location is empty");
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                //
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed");
        recyclerView.setVisibility(View.GONE);

        textView.setText("Google Api connection failed");

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "LocationChanged"+location.toString());
            mLastLocation = location;
        if(type!=FAVOURITE ) {
            String url = getString(R.string.List_BASE_URL)+ mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "&"+getString(R.string.ADD_URL) +"&type=" + type +"&"+getString(R.string.API_URL) + getString(R.string.Key);
            Log.d(LOG_TAG, url);
        new AsyncHttpTask().execute(url);
            placelist.clear();}

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
               placeListAdapter = new PlaceListAdapter(getApplicationContext(), placelist);

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
            Log.d(LOG_TAG, "onPostExecute"+placelist.size());
            PlaceDetailsActivityFragment fragment = (PlaceDetailsActivityFragment)getSupportFragmentManager().findFragmentById(R.id.place_detail_container);

            if(fragment!=null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            if(result==1){
                Log.d(LOG_TAG, "onPostExecute result"+Integer.toString(result)+","+placelist.size());
                //placeListAdapter.notifyDataSetChanged();
                if(placelist.size()>0) {

                    //placeListAdapter = new PlaceListAdapter(getApplicationContext(), placelist);
                    placeListAdapter.setGridData(placelist);

                    recyclerView.setAdapter(placeListAdapter);
                    //setupRecyclerView(recyclerView);
                    Log.d(LOG_TAG, recyclerView.getAdapter().getItemCount()+"update recyclerView");
                }else{
                    Log.d(LOG_TAG+"onPostExecute", Integer.toString(placelist.size()));
                }
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
            placelist.clear();

            try {
                JSONObject json_places = new JSONObject(inputString);
                JSONArray jsonArray = json_places.optJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(LOG_TAG, Integer.toString(i));
                    Place place = new Place();

                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    place.setPlace_name(jsonObject.optString("name"));
                    place.setPlace_id(jsonObject.optString("place_id"));
                    Log.d(LOG_TAG, place.getPlace_name());

                    place.setPlace_lat(jsonObject.optJSONObject("geometry").optJSONObject("location").optDouble("lat"));
                    place.setPlace_lng(jsonObject.optJSONObject("geometry").optJSONObject("location").optDouble("lng"));
                    Log.d(LOG_TAG, Double.toString(place.getPlace_lat()));

                    if(jsonObject.optJSONArray("photos")!=null) {
                        String photoRefereence = jsonObject.optJSONArray("photos").optJSONObject(0).optString("photo_reference");
                        photoRefereence = getString(R.string.PHOTO_BASE_URL) +"&photoreference=" +photoRefereence + "&key=" + getString(R.string.Key);
                        place.setPhoto(photoRefereence);
                        Log.d(LOG_TAG, photoRefereence);
                    }
                    else{
                        place.setPhoto("No preview photos");
                        Log.d(LOG_TAG, place.getPhoto());
                    }



                    place.setPlace_rating(jsonObject.optDouble("rating"));
                    place.setPlace_address(jsonObject.optString("vicinity"));
                    Log.d(LOG_TAG, Double.toString(place.getPlace_rating()));

                    placelist.add(place);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Fade fade = new Fade();

        fade.setDuration(1000);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }
    public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder> {

        private static final String LOG_TAG = "PlaceListAdapter";
        private ArrayList<Place> gridData;
        private Context mContext;
        private int itemLayoutId;


        public PlaceListAdapter (Context context, ArrayList<Place> places){

            this.mContext = context;
            this.gridData= places;
        }
        public void setGridData(ArrayList <Place> placeList){
            this.gridData = placeList;
            notifyDataSetChanged();
            Log.d(LOG_TAG, "setData"+this.gridData.size());
        }

        public void swap (ArrayList list){
            if(gridData!=null){
                gridData.clear();
                gridData.addAll(list);
            }
            else{
                gridData= list;
            }
            notifyDataSetChanged();
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_list_content,parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Place place = gridData.get(position);
            if(place.getPhoto().equals("No preview photos")==false) {
                Picasso.with(mContext).load(place.getPhoto()).into(holder.thumbnail);
            }
            else {
                holder.thumbnail.setImageResource(R.mipmap.ic_launcher);
            }

            holder.nameView.setText(place.getPlace_name());
            holder.ratingBar.setRating((float) place.getPlace_rating());
        }

        @Override
        public int getItemCount() {
            return gridData.size();
        }
        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public ImageView thumbnail;
            public TextView nameView;
            public RatingBar ratingBar;
            Place selectPlace;

            public MyViewHolder(View itemView) {
                super(itemView);
                thumbnail = (ImageView) itemView.findViewById(R.id.grid_item_image);
                nameView = (TextView)itemView.findViewById(R.id.listView_placeName);
                ratingBar= (RatingBar)itemView.findViewById(R.id.listView_placeRating);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        if (mTwoPane) {
                            selectPlace = (Place) placelist.get(getPosition());
                            Bundle arguments = new Bundle();
                            if(selectPlace!=null){
                                arguments.putString(getString(R.string.COLUMN_PLACE_NAME), selectPlace.getPlace_name());
                                arguments.putString(getString(R.string.COLUMN_PLACE_ID), selectPlace.getPlace_id());
                                arguments.putDouble(getString(R.string.COLUMN_PLACE_RATING), selectPlace.getPlace_rating());
                                arguments.putString(getString(R.string.COLUMN_PLACE_ADDRESS), selectPlace.getPlace_address());
                                arguments.putString(getString(R.string.COLUMN_PLACE_PHOTO), selectPlace.getPhoto());
                                arguments.putDouble(getString(R.string.lat), selectPlace.getPlace_lat());
                                arguments.putDouble(getString(R.string.lng), selectPlace.getPlace_lng());
                                Log.d("selectPlace", selectPlace.getPlace_id());

                                PlaceDetailsActivityFragment fragment = new PlaceDetailsActivityFragment();
                                fragment.setArguments(arguments);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.place_detail_container, fragment)
                                        .commit();

                            }
                            else{
                                Log.d(LOG_TAG, "passing object is null");
                                Toast.makeText(getApplicationContext(), "passing object is null", Toast.LENGTH_LONG).show();


                            }

                        } else {
                            Context context = view.getContext();
                            selectPlace= (Place) placelist.get(getPosition());
                            Intent intent = new Intent(context, PlaceDetailsActivity.class);
                            intent.putExtra(getString(R.string.ARG_ITEM_ID), selectPlace.getPlace_id());
                            intent.putExtra(getString(R.string.COLUMN_PLACE_NAME), selectPlace.getPlace_name());
                            intent.putExtra(getString(R.string.COLUMN_PLACE_ID), selectPlace.getPlace_id());
                            intent.putExtra(getString(R.string.COLUMN_PLACE_RATING), selectPlace.getPlace_rating());
                            intent.putExtra(getString(R.string.COLUMN_PLACE_ADDRESS), selectPlace.getPlace_address());
                            intent.putExtra(getString(R.string.COLUMN_PLACE_PHOTO), selectPlace.getPhoto());
                            Log.d("selectPlace single panel",selectPlace.getPhoto());
                            Log.d("selectPlace","place_id"+selectPlace.getPlace_id());

                            //setupWindowAnimations();


                            context.startActivity(intent);
                        }

                    }
                });
            }



            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "item CLicked"+getItemId());
            }
        }
    }

}
