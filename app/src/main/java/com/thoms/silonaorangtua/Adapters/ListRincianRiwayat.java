package com.thoms.silonaorangtua.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thoms.silonaorangtua.LiveMapsActivity;
import com.thoms.silonaorangtua.Model.AnakDaftar;
import com.thoms.silonaorangtua.Model.Rincian;
import com.thoms.silonaorangtua.R;

import java.util.ArrayList;

public class ListRincianRiwayat extends RecyclerView.Adapter<ListRincianRiwayat.ConnectedAdapterViewHolder> {

    ArrayList<Rincian> listRincian;
    Context c;

    public ListRincianRiwayat(ArrayList<Rincian> listRincian,Context c)
    {
        this.listRincian = listRincian;
        this.c=c;

    }

    @Override
    public int getItemCount()
    {
        return listRincian.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListRincianRiwayat.ConnectedAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rincian_riwayat,parent,false);
        ListRincianRiwayat.ConnectedAdapterViewHolder viewHolder1 = new ListRincianRiwayat.ConnectedAdapterViewHolder(view,c,listRincian);
        return viewHolder1;
    }



    @Override
    public void onBindViewHolder(ListRincianRiwayat.ConnectedAdapterViewHolder holder, int position) {

        Rincian rincian = listRincian.get(position);
        //   String name = nameList.get(position);
        holder.waktu_txt.setText(rincian.Waktu);

    }

    public static class ConnectedAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener
    {
        TextView waktu_txt;
        View v;
        Context ctx;
        DatabaseReference reference,currentReference;
        FirebaseAuth auth;
        FirebaseUser user;
        ArrayList<Rincian> waktuArrayList;
        //  CircleImageView i1;
        public ConnectedAdapterViewHolder(View itemView,Context ctx,ArrayList<Rincian> waktuArrayList) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            this.v = itemView;
            this.ctx=ctx;
            this.waktuArrayList = waktuArrayList;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua").child(user.getUid()).child("DaftarAnggota");
            currentReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child("Jumat").child("lasttime");

            waktu_txt = (TextView)itemView.findViewById(R.id.locJumatTxt);


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }








}
