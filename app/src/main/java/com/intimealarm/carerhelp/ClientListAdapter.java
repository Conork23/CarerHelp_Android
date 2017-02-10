package com.intimealarm.carerhelp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 27/11/2016.
 */

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ViewHolder> {
    // Variables
    private ArrayList<Client> dataset;
    DbHelper db;

    // Constructor
    public ClientListAdapter(Context context) {
        db = new DbHelper(context);
        dataset = db.allClients();
    }

    // Get Client at Position X
    public Client getItemAtPosition(int position) {
        return dataset.get(position);
    }

    // Add Client to End of List
    public void add(Client client) {
        dataset.add(db.addClient(client));
        notifyItemInserted(getItemCount());
    }

    // Add Client at Position X
    public void add(int position, Client client) {
        dataset.add(position, client);
        notifyItemInserted(position);
    }

    // Delete Client at Position X
    public void remove(Client client) {
        db.deleteClient(client);
    }

    // Temp Delete Client at Position X
    public void tempRemove(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    // Create Instance of View Holder
    @Override
    public ClientListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_client, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    // Bind Client to View
    @Override
    public void onBindViewHolder(final ClientListAdapter.ViewHolder holder, int position) {
        final Client c = dataset.get(position);
        holder.name.setText(c.getName());
        holder.address.setText(c.getAddress());
        holder.time.setText(c.getTime());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.context, DetailsActivity.class);
                Bundle args = new Bundle();
                args.putString("NAME", c.getName());
                args.putString("ADDRESS", c.getAddress());
                args.putString("PHONE", c.getPhone());
                args.putString("ID", c.getClient_id());
                args.putString("TIME", c.getTime());
                args.putParcelable("GEO", c.getGeo());
                intent.putExtra("ARGS", args);

                holder.context.startActivity(intent);
            }
        });
    }

    // Get Total Number of Clients
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // update dataset
    public void update() {
        dataset = db.allClients();
        notifyDataSetChanged();
    }

    // View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        protected TextView name;
        @BindView(R.id.tv_address)
        protected TextView address;
        @BindView(R.id.tv_time)
        protected TextView time;
        View rootView;
        Context context;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            rootView = v;
            context = itemView.getContext();

        }

    }
}
