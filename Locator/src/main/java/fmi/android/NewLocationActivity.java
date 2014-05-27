package fmi.android;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fmi.android.data.SQLHelper;


public class NewLocationActivity extends Activity implements LocationListener {

    private static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private ImageView spinner;
    private Address address;

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;

    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinner = (ImageView) findViewById(R.id.spinner_image_view);
        spinner.setVisibility(View.VISIBLE);
        spinner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);



        if(!gps_enabled || !network_enabled){
            Intent intent = new Intent(NewLocationActivity.this, MainActivity.class);
            intent.putExtra("gps_enabled", gps_enabled);
            intent.putExtra("network_enabled", network_enabled);
            startActivity(intent);
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else {
            stopLocationUpdates();
            startActivity(new Intent(NewLocationActivity.this, MainActivity.class));
            return true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        stopLocationUpdates();
        spinner.setVisibility(View.INVISIBLE);
        String addressAsString = "";
        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        try {
            if(myLocation.isPresent()){
                List<Address> myList = myLocation.getFromLocation(latitude, longitude, 1);
                if(!myList.isEmpty()){
                    addressAsString = myList.get(0).getAddressLine(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Date date = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.COLUMN_DATE, format.format(date));
        contentValues.put(SQLHelper.COLUMN_ADDRESS, addressAsString);
        contentValues.put(SQLHelper.COLUMN_LAT, latitude);
        contentValues.put(SQLHelper.COLUMN_LNG, longitude);
        contentValues.put(SQLHelper.COLUMN_NAME, "");
        contentValues.put(SQLHelper.COLUMN_PICTURE_PATH, "");

        Uri uri = getContentResolver().insert(Uri.parse("content://fmi.android.locator.cursorloader.data"), contentValues);


        Intent intent = new Intent(NewLocationActivity.this, ViewLocationActivity.class);
        intent.putExtra("id", Long.parseLong(uri.toString()));
        startActivity(intent);



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NewLocationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void stopLocationUpdates(){
        this.locationManager.removeUpdates(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }


}
