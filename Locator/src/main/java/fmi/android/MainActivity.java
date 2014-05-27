package fmi.android;



import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import fmi.android.data.SQLHelper;

/**
 * Created by dkpavlov on 1/26/14.
 */
public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView locationListView;

    private EditText editText;

    private static final int URL_LOADER = 0;

    private String searchPhrase = "";

    String[] columns = { SQLHelper.COLUMN_NAME };
    int[] fields = { android.R.id.text1 };

    private SimpleCursorAdapter mAdapter;

    private final MainActivity ma = this;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("");

        Intent intent = getIntent();
        boolean gps = intent.getBooleanExtra("gps_enabled", true);
        boolean network = intent.getBooleanExtra("network_enabled", true);
        String toastMessage = "";
        toastMessage += !gps ? "need GPS " : "";
        toastMessage += !network ? "need Network" : "";
        if(!gps || !network){
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        }

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_1, null, columns, fields, 0);
        getLoaderManager().initLoader(URL_LOADER, null, this);
        editText = (EditText) this.findViewById(R.id.search_locations);

        locationListView = (ListView) this.findViewById(android.R.id.list);

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MainActivity.this, ViewLocationActivity.class);
                intent.putExtra("id", l);
                startActivity(intent);
                finish();

            }
        });

        setListAdapter(mAdapter);

        TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            public void afterTextChanged(Editable s) {
                searchPhrase = s.toString();
                getLoaderManager().restartLoader(URL_LOADER, null, ma);
            }

        };
        editText.addTextChangedListener(watcher);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_add:
                Intent intent = new Intent(MainActivity.this, NewLocationActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case URL_LOADER:

                String[] projections = { SQLHelper.COLUMN_ID, SQLHelper.COLUMN_NAME };
                return new CursorLoader(this, Uri.parse("content://fmi.android.locator.cursorloader.data"),
                                            projections, SQLHelper.COLUMN_NAME + " LIKE ?",new String[] {"%" + searchPhrase + "%" }, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((SimpleCursorAdapter)this.getListAdapter()).swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }
}
