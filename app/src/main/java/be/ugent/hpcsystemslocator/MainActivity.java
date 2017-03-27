/*
 * Copyright 2016-2016 Ghent University
 *
 * This file is part of vsc-quattor,
 * originally created by the HPC team of Ghent University (http://ugent.be/hpc/en),
 * with support of Ghent University (http://ugent.be/hpc),
 * the Flemish Supercomputer Centre (VSC) (https://www.vscentrum.be),
 * the Flemish Research Foundation (FWO) (http://www.fwo.be/en)
 * and the Department of Economy, Science and Innovation (EWI) (http://www.ewi-vlaanderen.be/en).
 *
 * https://github.ugent.be/hpcugent/vsc-quattor
 *
 * All rights reserved.
 *
 */
package be.ugent.hpcsystemslocator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private final static String FILE_LOCATION = "overview.csv";

    private String actualHostname;
    private String vendor;
    private String serialNumber;
    private String type;
    private String model;
    private String room;
    private String rack;
    private String location;
    private String support;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init end point variables
        actualHostname = "";
        vendor = "";
        serialNumber = "";
        type = "";
        model = "";
        room = "";
        rack = "";
        location = "";
        support = "";

        //init spinners
        Spinner osSpinner = (Spinner) findViewById(R.id.spinnerNetwork);
        ArrayAdapter<CharSequence> osAdapter = ArrayAdapter.createFromResource(this,
                R.array.nw_array, android.R.layout.simple_spinner_item);
        osAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        osSpinner.setAdapter(osAdapter);


        Spinner whichSpinner = (Spinner) findViewById(R.id.spinnerwhichLookup);
        ArrayAdapter<CharSequence> whichAdapter = ArrayAdapter.createFromResource(this,
                R.array.lookup_array, android.R.layout.simple_spinner_item);
        whichAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whichSpinner.setAdapter(whichAdapter);


        // intercept "enter"
        final EditText editText = (EditText) findViewById(R.id.edit_message);
        TextView.OnEditorActionListener keyListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                // User pressed soft keyboard search
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doLookup(editText);
                }
                // Hardware keyboard enter
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    doLookup(editText);
                }
                return true;
            }
        };

        editText.setOnEditorActionListener(keyListener);

    }

    private void setValues(String line){
        String[] separated = line.split(",");
        actualHostname = separated[0];
        vendor = separated[1];
        serialNumber = separated[2];
        type = separated[3];
        model = separated[4];
        room = separated[5];
        rack = separated[6];
        location = separated[7];
        support = separated[8];
    }

    private boolean lookUpByHost(String hostname, String network){
        BufferedReader reader;
        boolean found = false;

        try {
            InputStream inputstream = getResources().getAssets().open(FILE_LOCATION);
            reader = new BufferedReader(new InputStreamReader(inputstream));
            for (String line = reader.readLine(); line != null && ! found ; line = reader.readLine()) {
                if (line.startsWith(hostname)){
                    setValues(line);
                    if(actualHostname.endsWith(network)) {
                        found = true;
                    }
                }
            }
            reader.close();

        } catch (Exception e) {
            found = false;
        }

        return found;
    }

    private boolean lookUpBySerial(String serial, String network){
        BufferedReader reader;
        boolean found = false;

        try {
            InputStream inputstream = getResources().getAssets().open(FILE_LOCATION);
            reader = new BufferedReader(new InputStreamReader(inputstream));
            for (String line = reader.readLine(); line != null && ! found ; line = reader.readLine()) {
                setValues(line);
                if(serialNumber.equals(serial) && actualHostname.endsWith(network)) {
                    found = true;
                }
            }
            reader.close();

        } catch (Exception e) {
            found = false;
        }

        return found;

    }

    /** Called when the user clicks the Search button **/
    public void doLookup(View view) {
        // Close keyboard so it does not obstruct data
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Get basic data and identifiers
        TextView hostDataField = (TextView) findViewById(R.id.hostData);
        EditText searchField = (EditText) findViewById(R.id.edit_message);
        Spinner osSpinner=(Spinner) findViewById(R.id.spinnerNetwork);
        Spinner whichSpinner = (Spinner) findViewById(R.id.spinnerwhichLookup);

        String network = osSpinner.getSelectedItem().toString();
        String lookup = whichSpinner.getSelectedItem().toString();
        String searchData = searchField.getText().toString();

        boolean found;
        String endData = "Not found!";

        // Choose lookup
        if ( lookup.equals("hostname")){
            found = lookUpByHost(searchData, network);
        }else {
            found = lookUpBySerial(searchData,network);
        }

        // Show data to screen
        if(found) {
            endData = "Hostname: " + actualHostname + "\n";
            endData += "Vendor: " + vendor + "\n";
            endData += "Serial Number: " + serialNumber + "\n";
            endData += "Type: " + type + "\n";
            endData += "Room: " + room + "\n";
            endData += "Model: " + model + "\n";
            endData += "Rack: " + rack + "\n";
            endData += "Location: " + location + "\n";
            endData += "Support Date: " + support + "\n";
        }

        hostDataField.setTextSize(20);
        hostDataField.setText(endData);
    }
}
