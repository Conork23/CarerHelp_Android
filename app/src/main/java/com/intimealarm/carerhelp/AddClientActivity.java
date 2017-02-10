package com.intimealarm.carerhelp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 27/11/2016.
 */

public class AddClientActivity extends AppCompatActivity {
    // Variables
    String name, address, phone, client_id, time;
    int hour, min;
    Client client;

    //View Bindings
    @BindView(R.id.et_name)
    EditText nameTf;

    @BindView(R.id.et_address)
    EditText addressTf;

    @BindView(R.id.et_phone)
    EditText phoneTf;

    @BindView(R.id.et_client_id)
    EditText client_idTF;

    @BindView(R.id.timePicker)
    TimePicker timePicker;

    // On Click Listeners
    @OnClick(R.id.btn_save)
    public void saveClient(View view) {
        name = nameTf.getText().toString();
        address = addressTf.getText().toString();
        phone = phoneTf.getText().toString();
        client_id = client_idTF.getText().toString();
        hour = timePicker.getCurrentHour();
        min = timePicker.getCurrentMinute();
        time = formatTime(hour, min);

        if (checkFields(name, address, phone, client_id, time)) {
            new AddClientActivity.Operation().execute(address);
        } else {
            Toast.makeText(this, "Please Fill In All Fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check if fields are empty
    private boolean checkFields(String... fields) {
        boolean isValid = true;

        for (String x : fields) {
            if (x.trim().length() < 1) {
                isValid = false;
            }
        }

        return isValid;
    }

    // Method to format time string
    private String formatTime(int hour, int min) {
        String hourS, minS;

        if (hour < 10) {
            hourS = "0" + hour;
        } else {
            hourS = "" + hour;
        }

        if (min < 10) {
            minS = "0" + min;
        } else {
            minS = "" + min;
        }

        return hourS + ":" + minS;


    }

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        ButterKnife.bind(this);

        // This happens if Activity is being opened to edit an existing client
        Intent intent = getIntent();
        Bundle bundle = getIntent().getParcelableExtra("ARGS");
        if (bundle != null) {
            client = new Client(
                    bundle.getString("ID"),
                    bundle.getString("NAME"),
                    bundle.getString("ADDRESS"),
                    bundle.getString("PHONE"),
                    bundle.getString("TIME"),
                    (LatLng) bundle.getParcelable("GEO")
            );
            nameTf.setText(client.getName());
            client_idTF.setText(client.getClient_id());
            phoneTf.setText(client.getPhone());
            addressTf.setText(client.getAddress());
            int hour = Integer.parseInt(client.getTime().substring(0, 2));
            int min = Integer.parseInt(client.getTime().substring(3, 5));
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(min);
        }

    }

    // cancle activity on back button click
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    // Return Client
    private void returnClient(Client client) {
        Intent intent = new Intent();
        Bundle args = new Bundle();
        args.putString("NAME", client.getName());
        args.putString("ADDRESS", client.getAddress());
        args.putString("PHONE", client.getPhone());
        args.putString("ID", client.getClient_id());
        args.putString("TIME", client.getTime());
        args.putParcelable("GEO", client.getGeo());
        intent.putExtra("ARGS", args);

        // This is sending the data back to the main activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // Return Network Error
    private void returnError() {
        Toast.makeText(this, "A Network Error Has Occured", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    // Async Network Call
    private class Operation extends AsyncTask<String, Void, LatLng> {

        // Network Call
        @Override
        protected LatLng doInBackground(String... strings) {


            OkHttpClient client = new OkHttpClient();

            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("maps.google.com")
                    .addPathSegment("maps")
                    .addPathSegment("api")
                    .addPathSegment("geocode")
                    .addPathSegment("json")
                    .addQueryParameter("address", strings[0] + ", Ireland")
                    .addQueryParameter("sensor", "false")
                    .build();


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());

                Double longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                Double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");


                return new LatLng(latitude, longitute);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        // Post Network Call Request
        @Override
        protected void onPostExecute(LatLng latLng) {
            if (latLng != null) {
                Client client = new Client(
                        client_id,
                        name,
                        address,
                        phone,
                        time,
                        latLng
                );

                returnClient(client);
            } else {
                returnError();
            }

        }
    }


}
