package fmi.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Build;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fmi.android.data.Location;
import fmi.android.data.SQLHelper;

public class ViewLocationActivity extends FragmentActivity {

    private ShareActionProvider shareActionProvider;

    private EditText nameEditText;

    private EditText addressEditText;

    private EditText dateEditText;

    private ImageView imageView;

    private Location location;

    private static int RESULT_LOAD_IMAGE = 1;

    private String picturePath;

    private static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);


        long id = getIntent().getLongExtra("id", 1);

        setContentView(R.layout.activity_view_location);

        String[] allColumns = {SQLHelper.COLUMN_ID, SQLHelper.COLUMN_NAME,
                SQLHelper.COLUMN_ADDRESS, SQLHelper.COLUMN_LAT, SQLHelper.COLUMN_LNG,
                SQLHelper.COLUMN_PICTURE_PATH, SQLHelper.COLUMN_DATE};

        Cursor cursor = getContentResolver().query(
                Uri.parse("content://fmi.android.locator.cursorloader.data"), allColumns,
                SQLHelper.COLUMN_ID + " = ?", new String[] {String.valueOf(id)}, null);

        cursor.moveToFirst();

        location = new Location(cursor);

        imageView = (ImageView) findViewById(R.id.imgView);

        if(location.getPicturePath() != null){
            if(!location.getPicturePath().trim().isEmpty()){
                imageView.setImageBitmap(BitmapFactory.decodeFile(location.getPicturePath()));
                picturePath = location.getPicturePath();
            }
        }

        dateEditText = (EditText) findViewById(R.id.dateEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        addressEditText = (EditText) findViewById(R.id.addressEditText);

        dateEditText.setInputType(InputType.TYPE_NULL);
        dateEditText.setText(format.format(location.getDateOfCreation()), TextView.BufferType.EDITABLE);
        nameEditText.setText(location.getName(), TextView.BufferType.EDITABLE);
        addressEditText.setText(location.getAddress(), TextView.BufferType.EDITABLE);


    }

    public void startGallery(View arg0){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_location, menu);
        MenuItem item = menu.findItem(R.id.share);
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        setShareIntent(createShareIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveLocation();
        startActivity(new Intent(ViewLocationActivity.this, MainActivity.class));
        return true;
    }

    @SuppressLint("NewApi")
    private void setShareIntent(Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewLocationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private Intent createShareIntent() {
        String uri = "geo:0,0?q="+ location.getLatitude() + "," + location.getLongitude() + " (" + location.getName()+ ")";
        /*+Uri.parse(uri).toString()*/

        String message = location.getName() + " @("+location.getAddress()
                + ") https://maps.google.com/?q=" + location.getLatitude() + ","
                + location.getLatitude();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.ACTION_VIEW, Uri.parse(uri));
        return shareIntent;
    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(super.getSupportFragmentManager(), "datePicker");
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            Date locationDate;
            try {
                locationDate = format.parse(dateEditText.getText().toString());
            } catch (ParseException e) {
                locationDate = new Date();
                e.printStackTrace();
            }
            final Calendar c = Calendar.getInstance();
            c.setTime(locationDate);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, i);
            c.set(Calendar.MONTH, i2);
            c.set(Calendar.DAY_OF_MONTH, i3);
            Date locationDate = c.getTime();
            dateEditText.setText(format.format(locationDate), TextView.BufferType.EDITABLE);
        }
    }

    public void saveLocation(){

        location.setPicturePath(picturePath);
        location.setName(nameEditText.getText().toString());
        location.setAddress(addressEditText.getText().toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.COLUMN_NAME, nameEditText.getText().toString());
        contentValues.put(SQLHelper.COLUMN_ADDRESS, addressEditText.getText().toString());
        contentValues.put(SQLHelper.COLUMN_PICTURE_PATH, picturePath);
        contentValues.put(SQLHelper.COLUMN_DATE, dateEditText.getText().toString());
        getContentResolver().update(Uri.parse("content://fmi.android.locator.cursorloader.data"),
                contentValues, SQLHelper.COLUMN_ID + " = ?", new String[] {String.valueOf(location.getId())});

    }

}
