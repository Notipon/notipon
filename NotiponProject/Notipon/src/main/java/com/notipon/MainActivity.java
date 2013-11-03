package com.notipon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private TextView descriptionView;
    private TextView locationView;

    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "Hello world!");
        setContentView(R.layout.activity_main);

        String item = "Space needle, Seattle";

        descriptionView = (TextView)findViewById(R.id.description_text);
        locationView    = (TextView)findViewById(R.id.location_text);
        imgView         = (ImageView)findViewById(R.id.myDeal);

        // get defaults if available
        SharedPreferences settings = getSharedPreferences(MainService.PACKAGE_NAME, MODE_PRIVATE);
        Filter currentFilter = Filter.getActiveFilter(settings);
        if (currentFilter != null) {
            if (currentFilter.name != null) {
                descriptionView.setText(currentFilter.name);
            }

            if (currentFilter.location != null) {
                locationView.setText(currentFilter.location);
            }
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        startService(new Intent(this, MainService.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View view) {
        //setTestFilter();
        SharedPreferences settings = getSharedPreferences(MainService.PACKAGE_NAME, MODE_PRIVATE);
        Filter exampleFilter = new Filter(descriptionView.getText().toString(), locationView.getText().toString());
        exampleFilter.setActiveFilter(settings);

        Toast toast = Toast.makeText(this, "Set alert for " + exampleFilter.name, Toast.LENGTH_SHORT);
        toast.show();

        DealTask task = new DealTask();
        task.execute();
    }

    private void setTestFilter() {
        SharedPreferences settings = getSharedPreferences(MainService.PACKAGE_NAME, MODE_PRIVATE);

        Filter exampleFilter = new Filter("Clearly Comfortable Smiles", "Seattle");
        exampleFilter.setActiveFilter(settings);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    protected class DealTask extends AsyncTask<String, Void, Deal> {

        @Override
        protected Deal doInBackground(String... params) {
            Deal d = new Deal();
            d.imgData = new DealHttpClient().downloadImage("ecwqQzKizoBR5zWMTK6r/Dv-440x267/v1/t460x279.jpg");
            return d;
        }

        @Override
        protected void onPostExecute(Deal mydeal) {
            super.onPostExecute(mydeal);

            if (mydeal != null && mydeal.imgData != null ) {
                imgView.setImageBitmap(mydeal.imgData);
            }
        }
    }

}
