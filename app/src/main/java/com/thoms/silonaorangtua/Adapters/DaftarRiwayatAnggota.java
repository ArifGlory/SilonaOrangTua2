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
import com.thoms.silonaorangtua.IsiRiwayat;
import com.thoms.silonaorangtua.LiveMapsActivity;
import com.thoms.silonaorangtua.Model.AnakDaftar;
import com.thoms.silonaorangtua.R;

import java.util.ArrayList;

public class  DaftarRiwayatAnggota extends RecyclerView.Adapter<DaftarRiwayatAnggota.ConnectedAdapterViewHolder> {
    ArrayList<AnakDaftar> nameList;
    Context c;

    public DaftarRiwayatAnggota(ArrayList<AnakDaftar> nameList,Context c)
    {
        this.nameList = nameList;
        this.c=c;



    }


    @Override
    public int getItemCount()
    {
        return nameList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public DaftarRiwayatAnggota.ConnectedAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daftar_riwayat_anggota,parent,false);
        DaftarRiwayatAnggota.ConnectedAdapterViewHolder viewHolder1 = new DaftarRiwayatAnggota.ConnectedAdapterViewHolder(view,c,nameList);
        return viewHolder1;
    }



    @Override
    public void onBindViewHolder(DaftarRiwayatAnggota.ConnectedAdapterViewHolder holder, int position) {

        AnakDaftar userRegister = nameList.get(position);
        //   String name = nameList.get(position);
        holder.name_txt.setText(userRegister.nama);

    }

    public static class ConnectedAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener,View.OnClickListener
    {
        TextView name_txt;
        View v;
        Context ctx;
        DatabaseReference reference,currentReference;
        FirebaseAuth auth;
        FirebaseUser user;
        ArrayList<AnakDaftar> nameArrayList;
        //  CircleImageView i1;
        public ConnectedAdapterViewHolder(View itemView,Context ctx,ArrayList<AnakDaftar> nameArrayList) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
            this.v = itemView;
            this.ctx=ctx;
            this.nameArrayList = nameArrayList;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua").child(user.getUid()).child("DaftarAnggota");
            currentReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");

            name_txt = (TextView)itemView.findViewById(R.id.item_title);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            AnakDaftar anakDaftar = this.nameArrayList.get(position);

            String latitude_user = anakDaftar.latitude;
            String longitude_user = anakDaftar.longitude;
            String lasttime_user = anakDaftar.lasttime;

            if(latitude_user.equals("na") && longitude_user.equals("na") && lasttime_user.equals("na"))
            {

                Toast.makeText(ctx,"Anggota Tidak Sedang Berbagi Lokasi.",Toast.LENGTH_SHORT).show();


            }
            else
            {
                Intent mYIntent = new Intent(ctx,IsiRiwayat.class);
                // mYIntent.putExtra("createuserobject",addCircle);
                mYIntent.putExtra("latitude",latitude_user);
                mYIntent.putExtra("longitude",longitude_user);
                mYIntent.putExtra("name",anakDaftar.nama);
                mYIntent.putExtra("userid",anakDaftar.userid);
                mYIntent.putExtra("date",anakDaftar.lasttime);
                mYIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(mYIntent);
            }



        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int position = getAdapterPosition();
            final AnakDaftar addCircle = this.nameArrayList.get(position);

            reference.child(addCircle.userid).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                currentReference.child(addCircle.userid).child("DaftarAnggota").child(user.getUid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(ctx,"Removed successfully",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });



                            }
                        }
                    });

            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add("REMOVE");
            myActionItem.setOnMenuItemClickListener(this);
        }


    }








}
