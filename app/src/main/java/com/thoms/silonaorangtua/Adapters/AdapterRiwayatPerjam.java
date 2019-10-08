package com.thoms.silonaorangtua.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.thoms.silonaorangtua.Model.RiwayatPerJam;
import com.thoms.silonaorangtua.R;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterRiwayatPerjam extends RecyclerView.Adapter<AdapterRiwayatPerjam.MyViewHolder> {

    private Context mContext;
    private List<RiwayatPerJam> riwayatPerJamList;
    private SweetAlertDialog pDialogLoading,pDialodInfo;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvJam,tvAlamat;

        public MyViewHolder(View view) {
            super(view);
            tvJam = (TextView) view.findViewById(R.id.tvjam);
            tvAlamat = (TextView) view.findViewById(R.id.tvAlamat);

        }
    }

    public AdapterRiwayatPerjam(Context mContext, List<RiwayatPerJam> riwayatPerJamList) {
        this.mContext = mContext;
        this.riwayatPerJamList = riwayatPerJamList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat_perjam, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (riwayatPerJamList.isEmpty()){

            Log.d("isiSize: ",""+riwayatPerJamList.size());
        }else {


            final RiwayatPerJam riwayatPerJam = riwayatPerJamList.get(position);

            holder.tvJam.setText("Jam : "+riwayatPerJam.getJam());
            holder.tvAlamat.setText(riwayatPerJam.getAlamat());


        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return riwayatPerJamList.size();
    }
}
