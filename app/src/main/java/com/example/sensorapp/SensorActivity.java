package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorAdapter adapter;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    public static final String KEY_SENSOR_TYPE = "SensorActivity.sensor";
    private boolean subtitleVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        recyclerView=findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList=sensorManager.getSensorList(Sensor.TYPE_ALL);
        if(adapter==null){
            adapter=new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensor_activity_menu,menu);
        MenuItem subtitleItem=menu.findItem(R.id.show_subtitle);
        if(subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        if(itemId==R.id.show_subtitle) {
            subtitleVisible=!subtitleVisible;
            invalidateOptionsMenu();
            updateSubtitle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void updateSubtitle(){
        String subtitle=null;
        if(subtitleVisible) subtitle=getString(R.string.sensor_count,sensorList.size());
        getSupportActionBar().setSubtitle(subtitle);
    }

    private class SensorHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private TextView sensorNameView;
        private ImageView sensorIconView;
        private Sensor sensor;
        public SensorHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.sensor_list_item,parent,false));
            itemView.setOnClickListener(this::onClick);
            itemView.setOnLongClickListener(this::onLongClick);
            sensorNameView=itemView.findViewById(R.id.sensor_name);
            sensorIconView=itemView.findViewById(R.id.sensor_icon);
        }
        public void bind(Sensor sensor){
            this.sensor=sensor;
            sensorNameView.setText(sensor.getName());
            sensorIconView.setImageResource(R.drawable.ic_sensor_icon);
            Log.d("Sensor",sensor.getName()+" "+sensor.getType()+" "+getBindingAdapterPosition());
            if(sensor.getType()==Sensor.TYPE_LIGHT||sensor.getType()==Sensor.TYPE_ACCELEROMETER) sensorNameView.setBackgroundColor(Color.GRAY);else sensorNameView.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onClick(View view) {
            Intent intent;
            if(sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) intent=new Intent(SensorActivity.this, LocationActivity.class);
             else intent=new Intent(SensorActivity.this,SensorDetailsActivity.class);
            intent.putExtra(KEY_SENSOR_TYPE,sensor.getType());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            showMessage();
            return false;
        }
        public void showMessage(){
            Log.d("tag",sensor.getName());
            AlertDialog.Builder builder=new AlertDialog.Builder(SensorActivity.this);
            String details_text=getString(R.string.sensor_details_message,sensor.getVendor(),sensor.getMaximumRange());
            builder.setMessage(details_text).setTitle(getString(R.string.sensor_details_title,sensor.getName()));
            AlertDialog dialog =builder.create();
            dialog.show();
        }
    }
    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder>{
        private List<Sensor> sensorList;
        public SensorAdapter(List<Sensor> sensorList) {this.sensorList=sensorList;}

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(SensorActivity.this);
            return new SensorHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor=sensorList.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensorList.size();
        }
    }
}