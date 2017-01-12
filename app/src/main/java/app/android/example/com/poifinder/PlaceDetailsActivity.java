package app.android.example.com.poifinder;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;


import app.android.example.com.poifinder.utilities.Place;

public class PlaceDetailsActivity extends AppCompatActivity {
    private Place place;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            place = new Place();
            arguments.putString(getString(R.string.COLUMN_PLACE_ID), getIntent().getStringExtra(getString(R.string.COLUMN_PLACE_ID)));
            arguments.putString(getString(R.string.COLUMN_PLACE_NAME), getIntent().getStringExtra(getString(R.string.COLUMN_PLACE_NAME)));
            arguments.putDouble(getString(R.string.COLUMN_PLACE_RATING), getIntent().getDoubleExtra(getString(R.string.COLUMN_PLACE_RATING), 0.0));
            arguments.putString(getString(R.string.COLUMN_PLACE_ADDRESS), getIntent().getStringExtra(getString(R.string.COLUMN_PLACE_ADDRESS)));
            arguments.putString(getString(R.string.COLUMN_PLACE_PHOTO), getIntent().getStringExtra(getString(R.string.COLUMN_PLACE_PHOTO)));
            arguments.putDouble("lat", getIntent().getDoubleExtra("lat", 0.0));
            arguments.putDouble("lng", getIntent().getDoubleExtra("lng", 0.0));


            PlaceDetailsActivityFragment fragment = new PlaceDetailsActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.place_detail_container, fragment)
                    .commit();


        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+getIntent().getStringExtra("address"));

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }



            }
        });

        setupWindowAnimations();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Fade fade = new Fade();

        fade.setDuration(1000);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }
}
