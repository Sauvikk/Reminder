package com.example.souvik.remindertemplate.Activities;

import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.souvik.remindertemplate.Adapter.Medicine;
import com.example.souvik.remindertemplate.Adapter.RVAdapter;
import com.example.souvik.remindertemplate.R;
import com.example.souvik.remindertemplate.RemindMe;
import com.example.souvik.remindertemplate.Util;
import com.example.souvik.remindertemplate.model.Alarm;
import com.example.souvik.remindertemplate.model.AlarmMsg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Souvik on 03-Apr-16.
 */
public class NotificationOnClickActivity extends AppCompatActivity {

    private List<Medicine> medicines;
    public static RecyclerView rv;

    Alarm alarm;
    AlarmMsg alarmMsg;
    public final Date dt = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_on_click_activity_layout);

        long alarmMsgId = getIntent().getLongExtra(AlarmMsg.COL_ID, -1);
        long alarmId = getIntent().getLongExtra(AlarmMsg.COL_ALARMID, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Today");
        rv=(RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setVisibility(View.VISIBLE);

        /*String  s = Context.NOTIFICATION_SERVICE;
        NotificationManager mNM = (NotificationManager)getSystemService(s);
        mNM.cancel((int) alarmMsgId);*/

        alarmMsg = new AlarmMsg(alarmMsgId);
        alarmMsg.load(RemindMe.db);
        alarm = new Alarm(alarmId);
        alarm.load(RemindMe.db);

        initializeData();
        initializeAdapter();

    }


    private void initializeData(){
        medicines = new ArrayList<>();

        Log.d("Name", alarm.getName());
        Log.d("Quantity", alarm.getQuantity()+"");
        Log.d("Date", alarmMsg.getDateTime()+"");
        Log.d("Alarm Msg id", alarmMsg.getId()+"");

        String status = alarmMsg.getStatus();

        long time = alarmMsg.getDateTime();
        dt.setTime(time);
        String txt =  Util.getActualTime(dt.getHours(), dt.getMinutes());
        Medicine medicine = new Medicine();
        medicine.setName(alarm.getName());
        medicine.setPhotoId(R.drawable.tablet);
        medicine.setHeaderColor(Color.parseColor("#F7002D"));

        if(status.equals(AlarmMsg.SKIPPED) || status.equals(AlarmMsg.EXPIRED) ){
            medicine.setInstruction("Was Scheduled for " + txt);
            medicine.setStatusIcon(R.drawable.ic_skip_red);
            medicine.setStatusString("Skipped at 10:00 AM");
        }else if(status.equals(AlarmMsg.TAKEN)){
            medicine.setInstruction("Was Scheduled for " + txt);
            medicine.setStatusIcon(R.drawable.ic_done_all);
            medicine.setStatusString("Taken at 10:00 AM");
        }else{
            medicine.setInstruction("Scheduled for " + txt);
            medicine.setStatusIcon(R.drawable.ic_check_green);
            medicine.setStatusString("Yet to take");
        }

        medicine.setSchedule("Take " + alarm.getQuantity() + " after food");
        medicine.setItem_id(alarmMsg.getId());
        medicines.add(medicine);





    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(medicines, this, "notification");
        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());
    }


}
