package com.intimealarm.carerhelp;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 27/11/2016.
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Variables
    final int CALL_CODE = 1;
    final int LOGIN_CODE = 2;
    final int LOGOUT_CODE = 3;
    final int EDIT_CLIENT = 4;
    Client client;
    String company_no;
    String login_temp;
    String loginMessage;
    String logout_temp;
    String logoutMessage;
    SupportMapFragment mapFragment;

    // View Bindings
    @BindView(R.id.tv_details_name)
    TextView nameTv;

    @BindView(R.id.tv_details_phone)
    TextView phoneTv;

    @BindView(R.id.tv_details_address)
    TextView addressTv;

    @BindView(R.id.tv_details_id)
    TextView idTv;


    // On Click Listeners
    @OnClick(R.id.btn_logout)
    public void logout(View v) {
        if (company_no.equalsIgnoreCase(getString(R.string.placeholder_phone))) {
            showDialog();
        } else {
            sendText(logoutMessage, LOGOUT_CODE);
        }
    }

    @OnClick(R.id.btn_login)
    public void login(View v) {
        if (company_no.equalsIgnoreCase(getString(R.string.placeholder_phone))) {
            showDialog();
        } else {
            sendText(loginMessage, LOGIN_CODE);
        }
    }

    // On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle bundle = getIntent().getParcelableExtra("ARGS");
        client = new Client(
                bundle.getString("ID"),
                bundle.getString("NAME"),
                bundle.getString("ADDRESS"),
                bundle.getString("PHONE"),
                bundle.getString("TIME"),
                (LatLng) bundle.getParcelable("GEO")
        );

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        setViews(client);

    }

    // Option Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.call_client) {
            makeCall();
        } else if (id == R.id.edit_client) {
            Intent intent = new Intent(this, AddClientActivity.class);
            Bundle args = new Bundle();
            args.putString("NAME", client.getName());
            args.putString("ADDRESS", client.getAddress());
            args.putString("PHONE", client.getPhone());
            args.putString("ID", client.getClient_id());
            args.putString("TIME", client.getTime());
            args.putParcelable("GEO", client.getGeo());
            intent.putExtra("ARGS", args);
            startActivityForResult(intent, EDIT_CLIENT);
        }
        return super.onOptionsItemSelected(item);
    }

    // Permission Requests Method
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALL_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            }
        } else if (requestCode == LOGIN_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendText(loginMessage, requestCode);
            }
        } else if (requestCode == LOGOUT_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendText(logoutMessage, requestCode);
            }
        }

    }

    // Make Phone Call Method
    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + client.getPhone()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_CODE);
        } else {
            startActivity(intent);
        }
    }

    // Send SMS Method
    private void sendText(String message, int code) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, code);
        } else {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(company_no, null, message, null, null);
            showSnack(code);
        }
    }

    private void showDialog() {
        DialogFragment newFragment = new Dialog();
        newFragment.show(getFragmentManager(), "Dialog");
    }

    // Map Controller
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = client.getGeo();
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(client.getName())
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Uri gmmIntentUri = Uri.parse("geo:" + latLng.latitude + "," + latLng.longitude + "?q=" + client.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }

    // On Start Method
    @Override
    protected void onStart() {
        super.onStart();
        getPrefs();
    }

    // Get Prefrences
    private void getPrefs() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        company_no = sharedPref.getString(SettingsActivity.COMPANY_NUMBER, "");
        login_temp = sharedPref.getString(SettingsActivity.LOGIN_TEMPLATE, "");
        logout_temp = sharedPref.getString(SettingsActivity.LOGOUT_TEMPLATE, "");
        loginMessage = String.format(login_temp, client.getClient_id());
        logoutMessage = String.format(logout_temp, client.getClient_id());
    }

    // Show Snackbar after SMS
    private void showSnack(int code) {
        String message = new String();
        if (code == LOGIN_CODE) {
            message = getString(R.string.successLogIn);
        } else if (code == LOGOUT_CODE) {
            message = getString(R.string.successLogOut);
        } else {
            message = getString(R.string.genericError);
        }
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snack.show();
    }

    // Update Client when Result is Retrieved from AddClientActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_CLIENT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getParcelableExtra("ARGS");
                Client c = new Client(
                        bundle.getString("ID"),
                        bundle.getString("NAME"),
                        bundle.getString("ADDRESS"),
                        bundle.getString("PHONE"),
                        bundle.getString("TIME"),
                        (LatLng) bundle.getParcelable("GEO")
                );

                DbHelper db = new DbHelper(this);
                setViews(db.update(c, client.getClient_id()));
            }
        }
    }

    // Set Views
    public void setViews(Client c) {
        client = c;
        nameTv.setText(c.getName());
        phoneTv.setText(c.getPhone());
        addressTv.setText(c.getAddress());
        idTv.setText(c.getClient_id());
        mapFragment.getMapAsync(this);

    }
}
