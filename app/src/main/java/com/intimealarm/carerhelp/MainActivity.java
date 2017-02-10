package com.intimealarm.carerhelp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 27/11/2016.
 */

public class MainActivity extends AppCompatActivity {
    private static final int ADD_CLIENT = 1;
    // Variables
    ArrayList<Client> arr = new ArrayList<>();
    ClientListAdapter adapter;

    // View Bindings
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.list_client)
    RecyclerView clientList;

    // On Click Listeners
    @OnClick(R.id.fab)
    public void addClient(View view) {

        Intent intent = new Intent(MainActivity.this, AddClientActivity.class);
        startActivityForResult(intent, ADD_CLIENT);

    }

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // Preference Manager
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Initilizing Recycler View
        clientList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientListAdapter(this);
        clientList.setAdapter(adapter);

        // Assigning Touch Helper to Recyler View
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback());
        touchHelper.attachToRecyclerView(clientList);

        // Check if a Company number has been added
        String company_no = sharedPref.getString(SettingsActivity.COMPANY_NUMBER, "");
        if (company_no.equalsIgnoreCase(getString(R.string.placeholder_phone))) {
            DialogFragment newFragment = new Dialog();
            newFragment.show(getFragmentManager(), "Dialog");
        }


    }

    // Recycler View touch handler
    private ItemTouchHelper.Callback touchHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT) {

                    // [UNUSED] On Move Method
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    // On Swipe Method
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        deleteItem(viewHolder.getAdapterPosition());
                    }

                    // Drawing Under View Method
                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                            View itemView = viewHolder.itemView;

                            Paint paint = new Paint();
                            Bitmap deleteIcon;

                            paint.setColor(getResources().getColor(R.color.colorAccent));
                            deleteIcon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_delete);
                            float height = (itemView.getHeight() / 2) - (deleteIcon.getHeight() / 2);
                            float bitmapWidth = deleteIcon.getWidth() + 50f;
                            float iconRight = (float) itemView.getRight() - bitmapWidth;

                            c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                            if (dX < -bitmapWidth) {
                                c.drawBitmap(deleteIcon, iconRight, (float) itemView.getTop() + height, null);
                            }


                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                        }
                    }

                };
        return simpleCallback;
    }

    // Method to delete client
    private void deleteItem(int adapterPosition) {
        Client c = adapter.getItemAtPosition(adapterPosition);
        adapter.tempRemove(adapterPosition);
        offerUndo(adapterPosition, c);
    }

    // Method to Display Undo SnackBar
    private void offerUndo(final int position, final Client c) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Undo", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.add(position, c);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent));

        snack.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    adapter.remove(c);
                }
                super.onDismissed(snackbar, event);

            }
        });

        snack.show();
    }

    // Option Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    // Getting the Result from the AddClientActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CLIENT) {
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

                adapter.add(c);
            }
        }
    }

    // On Restart
    @Override
    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

}
