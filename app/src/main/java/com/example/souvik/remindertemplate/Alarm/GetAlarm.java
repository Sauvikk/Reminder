package com.example.souvik.remindertemplate.Alarm;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;


import com.example.souvik.remindertemplate.RemindMe;
import com.example.souvik.remindertemplate.Util;
import com.example.souvik.remindertemplate.model.Alarm;
import com.example.souvik.remindertemplate.model.AlarmMsg;
import com.example.souvik.remindertemplate.model.AlarmTime;
import com.example.souvik.remindertemplate.model.DbHelper;
import com.example.souvik.remindertemplate.model.HttpGet;
import com.example.souvik.remindertemplate.model.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GetAlarm{

    private SQLiteDatabase db;
    private DbHelper dbHelper;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Context context;

    String today = sdf.format(new Date());


//    sdf.applyPattern(RemindMe.getDateFormat());

    public GetAlarm(Context context){
        db = RemindMe.db;
        dbHelper=RemindMe.dbHelper;
        dbHelper.onUpgrade(db,0,0);
        this.context = context;
        createAlarm();

//        new getAlarmFromServer().execute();

    }


    public GetAlarm(Context context, String name, String fromDate, String toDate, String time){
        db = RemindMe.db;
        Alarm alarm ;
        AlarmTime alarmTime;
        Intent service;


            alarm = new Alarm();
            alarmTime = new AlarmTime();

            alarm.setName(name);
            alarm.setSound(true);

            long alarmId = 0;

            alarm.setFromDate(Util.toPersistentDate(fromDate, sdf));
            alarm.setToDate(Util.toPersistentDate(toDate, sdf));
            alarm.setRule(Util.concat(0, " ", 0, " ", 0));

            alarmId = alarm.persist(db);

//            int j=20+i*5;

//            String time = "0:"+j+" AM";

            alarmTime.setAt(Util.toPersistentTime(time));
            Log.d("Time",time);
            alarmTime.setAlarmId(alarmId);
            alarmTime.persist(db);

            service = new Intent(context, AlarmService.class);
            service.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
            service.setAction(AlarmService.POPULATE);
            context.startService(service);


    }

    public void createAlarm() {
        Alarm alarm ;
        AlarmTime alarmTime;
        Intent service;
// create alarms for testing
        for (int i=0; i<15; i++) {
            alarm = new Alarm();
            alarmTime = new AlarmTime();

            alarm.setName("Sample Medicine" + " " + i);
            alarm.setSound(true);

            long alarmId = 0;

            alarm.setFromDate(Util.toPersistentDate(today, sdf));
            alarm.setToDate(Util.toPersistentDate("2017-04-21", sdf));
            alarm.setRule(Util.concat(0, " ", 0, " ", 0));

            alarmId = alarm.persist(db);

            int j=15+i*5;

            String time = "0:"+j+" AM";

            alarmTime.setAt(Util.toPersistentTime(time));
            Log.d("Time",time);
            alarmTime.setAlarmId(alarmId);
            alarmTime.persist(db);

            service = new Intent(context, AlarmService.class);
            service.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
            service.setAction(AlarmService.POPULATE);
            context.startService(service);
        }

    }


    private class getAlarmFromServer extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        JSONObject jsonObjRecv;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Getting Alarms...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            jsonObjRecv = HttpGet.ReceiveHttpGet("http://104.131.46.2:5000/Prescription/api/v1.0/prescription/all/1");


            if (jsonObjRecv != null) {
                Log.d("Received Json : ", jsonObjRecv.toString());
            } else {
                Log.d("Received Json : ", "failed");
            }
            try {
//                hardcoding data for testing
                jsonObjRecv = new JSONObject("{\n" +
                        "  \"Prescription\": [\n" +
                        "    {\n" +
                        "      \"id\": 28, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \"{\\\"Medicines\\\": {\\\"Lol\\\": {\\\"Night\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"morning\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"morning\\\"}, \\\"Total_Quantity\\\": 10, \\\"Afternoon\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"Afternoon\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"morning\\\"}}, \\\"Bem\\\": {\\\"Night\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"morning\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Total_Quantity\\\": 10, \\\"Afternoon\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"Afternoon\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}}}, \\\"Signature\\\": \\\"Default\\\", \\\"Diagnosis\\\": \\\"Cancer \\\", \\\"Doctor_Name\\\": \\\"Random Doctor\\\", \\\"Patient_Name\\\": \\\"Vivek\\\"}\", \n" +
                        "      \"timestamp\": \"2016-02-23T00:38:23\"\n" +
                        "    }, \n" +
                        "    {\n" +
                        "      \"id\": 27, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \"{\\\"Medicines\\\": {\\\"Med two \\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Total_Quantity\\\": 10, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"Afternoon\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}}, \\\"Med three \\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Total_Quantity\\\": 10, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"Afternoon\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}}, \\\"Med one \\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Total_Quantity\\\": 10, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"Afternoon\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}}, \\\"Med four \\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}, \\\"Total_Quantity\\\": 10, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"Afternoon\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": false, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"morning\\\"}}}, \\\"Signature\\\": \\\"Default\\\", \\\"Diagnosis\\\": \\\"Sample diagnosis \\\", \\\"Doctor_Name\\\": \\\"Random Doctor\\\", \\\"Patient_Name\\\": \\\"Sample patient \\\"}\", \n" +
                        "      \"timestamp\": \"2016-02-16T12:39:55\"\n" +
                        "    }, \n" +
                        "    {\n" +
                        "      \"id\": 3, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \" {    \\\"prescription_id\\\" : 0,   \\\"Date\\\" : \\\"30/12/2016\\\",   \\\"Patient_Name\\\": \\\"Random Patient\\\",   \\\"Doctor_Name\\\": \\\"Dr Who\\\",    \\\"Diagnosis\\\" : \\\"\\\",    \\\"Medicines\\\" : {     \\\"Name1\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\",\\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name2\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name3\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     }   },    \\\"Signature\\\" : \\\"\\\" }\", \n" +
                        "      \"timestamp\": \"2016-09-01T15:28:20\"\n" +
                        "    }, \n" +
                        "    {\n" +
                        "      \"id\": 24, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \"{\\\"Medicines\\\": {\\\"M Stat (250mg)\\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Total_Quantity\\\": 12, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}}, \\\"Cetrazin\\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Total_Quantity\\\": 30, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}}, \\\"T Mycin Forte (250 mg)\\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Total_Quantity\\\": 3, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}}, \\\"Jexin DT (125mg)\\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Total_Quantity\\\": 6, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}}, \\\"Tab Serrodase D (50+10)\\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Total_Quantity\\\": 2, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}}, \\\"T Mycin Kid (125 mg)\\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Total_Quantity\\\": 15, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}}, \\\"Rabag (20mg)\\\": {\\\"Night\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 20, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}, \\\"Evening\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Total_Quantity\\\": 1, \\\"Afternoon\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": false, \\\"time\\\": \\\"\\\"}, \\\"Morning\\\": {\\\"With_Food\\\": true, \\\"Quantity\\\": 1, \\\"Take\\\": true, \\\"time\\\": \\\"\\\"}}}, \\\"Doctor_Name\\\": \\\"Dr Reddy\\\", \\\"Patient_Name\\\": \\\"Saurabh\\\", \\\"Diagnosis\\\": \\\"\\\", \\\"Signature\\\": \\\"\\\", \\\"Date\\\": \\\"30/12/2016\\\", \\\"prescription_id\\\": 3}\", \n" +
                        "      \"timestamp\": \"2016-07-04T11:11:55\"\n" +
                        "    }, \n" +
                        "    {\n" +
                        "      \"id\": 6, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \" {    \\\"prescription_id\\\" : 0,   \\\"Date\\\" : \\\"30/12/2016\\\",   \\\"Patient_Name\\\": \\\"Random Patient\\\",   \\\"Doctor_Name\\\": \\\"Dr No\\\",    \\\"Diagnosis\\\" : \\\"\\\",    \\\"Medicines\\\" : {     \\\"Name1\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\",\\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name2\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name3\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     }   },    \\\"Signature\\\" : \\\"\\\" }\", \n" +
                        "      \"timestamp\": \"2016-05-19T09:34:42\"\n" +
                        "    }, \n" +
                        "    {\n" +
                        "      \"id\": 5, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \" {    \\\"prescription_id\\\" : 0,   \\\"Date\\\" : \\\"30/12/2016\\\",   \\\"Patient_Name\\\": \\\"Random Patient\\\",   \\\"Doctor_Name\\\": \\\"Dr Jaykal\\\",    \\\"Diagnosis\\\" : \\\"\\\",    \\\"Medicines\\\" : {     \\\"Name1\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\",\\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name2\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name3\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     }   },    \\\"Signature\\\" : \\\"\\\" }\", \n" +
                        "      \"timestamp\": \"2016-07-21T15:37:20\"\n" +
                        "    }, \n" +
                        "    {\n" +
                        "      \"id\": 2, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \" {    \\\"prescription_id\\\" : 0,   \\\"Date\\\" : \\\"30/12/2016\\\",   \\\"Patient_Name\\\": \\\"Random Patient\\\",   \\\"Doctor_Name\\\": \\\"Dr Khan\\\",    \\\"Diagnosis\\\" : \\\"\\\",    \\\"Medicines\\\" : {     \\\"Name1\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\",\\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name2\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name3\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     }   },    \\\"Signature\\\" : \\\"\\\" }\", \n" +
                        "      \"timestamp\": \"2016-07-15T11:46:02\"\n" +
                        "    }, \n" +
                        "    {\n" +
                        "      \"id\": 7, \n" +
                        "      \"patient_id\": {\n" +
                        "        \"address\": \"lolol\", \n" +
                        "        \"blood_group\": \"O+\", \n" +
                        "        \"email\": \"ratuljain1991@gmail.com\", \n" +
                        "        \"first_name\": \"Ratul\", \n" +
                        "        \"google_id\": \"101472697035423123233\", \n" +
                        "        \"last_name\": \"Jain\", \n" +
                        "        \"patient_id\": 1, \n" +
                        "        \"phone_number\": \"9703331743\", \n" +
                        "        \"timestamp\": \"2016-03-03T13:03:35\"\n" +
                        "      }, \n" +
                        "      \"prescription\": \" {    \\\"prescription_id\\\" : 0,   \\\"Date\\\" : \\\"30/12/2016\\\",   \\\"Patient_Name\\\": \\\"Random Patient\\\",   \\\"Doctor_Name\\\": \\\"Dr Mantri\\\",    \\\"Diagnosis\\\" : \\\"\\\",    \\\"Medicines\\\" : {     \\\"Name1\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\",\\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name2\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     },      \\\"Name3\\\" : {     \\\"Morning\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Afternoon\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Evening\\\" : {\\\"Take\\\" : false ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Night\\\" : {\\\"Take\\\" : true ,\\\"With_Food\\\" : true, \\\"time\\\" : \\\"\\\", \\\"Quantity\\\" : 1},     \\\"Total_Quantity\\\" : 10     }   },    \\\"Signature\\\" : \\\"\\\" }\", \n" +
                        "      \"timestamp\": \"2016-10-28T09:28:16\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");
            }catch (Exception e){

            }
           return null;

        }

        @Override
        protected void onPostExecute(String str) {

            pDialog.dismiss();
            Alarm alarm ;
            AlarmTime alarmTime;
            Intent service;
            boolean setAlarm;
            try {
                JSONArray presList = JSON.getListOfAllPrescriptions(jsonObjRecv.toString());

                for(int i = 0; i<presList.length();i++){

                    JSONObject prescriptionFromArr = presList.getJSONObject(i);
//                    Log.d("medicines ", medicine.get(i).toString());
                    Date presDate = JSON.getDateofPrescription(prescriptionFromArr.toString());

                    List<String> medicineNameList = JSON.returnListofMedicineNames(prescriptionFromArr.toString());
                    List<JSONObject> medicineInfoList = JSON.returnListofIndividualMedicineinfo(prescriptionFromArr.toString());
                    Log.d("mNameList size : ", medicineInfoList.size()+"");
                    Log.d("mInfoList size : ", medicineInfoList.size()+"");
                    for(int j=0;j<medicineInfoList.size();j++){
//                        Log.d("medicineInfo  : " +j, medicineInfoList.get(j).toString());
                        JSONObject medicinObject = medicineInfoList.get(j);
                        int totalQuantity = medicinObject.getInt("Total_Quantity");
                        String medicineName = medicineNameList.get(j);
                        JSONObject medicineObjectMorning = medicinObject.getJSONObject("Morning");
                        JSONObject medicineObjectAfternoon = medicinObject.getJSONObject("Afternoon");
                        JSONObject medicineObjectEvening = medicinObject.getJSONObject("Evening");
                        JSONObject medicineObjectNight = medicinObject.getJSONObject("Night");
                        int mQuantity = medicineObjectMorning.getInt("Quantity");
                        int aQuantity = medicineObjectAfternoon.getInt("Quantity");
                        int eQuantity = medicineObjectEvening.getInt("Quantity");
                        int nQuantity = medicineObjectNight.getInt("Quantity");
                        int noOfDays = totalQuantity/(mQuantity+aQuantity+eQuantity+nQuantity);
                        Log.d("noOfDays ",noOfDays+"");

                        List<JSONObject> medicineBasedOnTime = JSON.returnMedicineInfoBasedOnTime(medicinObject);

                        for(int k= 0; k<medicineBasedOnTime.size();k++){
                            JSONObject object = medicineBasedOnTime.get(k);
                            Log.d("mediOBJ",medicineBasedOnTime.get(k).toString());
                            if(object.getBoolean("Take")){
                                alarm = new Alarm();
                                alarmTime = new AlarmTime();
                                alarm.setName(medicineName);
                                alarm.setSound(true);
                                alarm.setQuantity(object.getInt("Quantity"));
                                long alarmId = 0;
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(presDate);
                                calendar.add(Calendar.DATE, noOfDays);
                                Date toDate = calendar.getTime();
                                Log.d("presDate", sdf.format(presDate));
                                Log.d("toDate", sdf.format(toDate));
                                alarm.setFromDate(Util.toPersistentDate(sdf.format(presDate), sdf));
                                alarm.setToDate(Util.toPersistentDate(sdf.format(toDate), sdf));
                                alarm.setRule(Util.concat(0, " ", 0, " ", 0));

                                alarmId = alarm.persist(db);

                                if(object.getString("time").toLowerCase().equals("morning")){
                                    Log.d("setting time","1");
                                    alarmTime.setAt(Util.toPersistentTime("9:00 AM"));
                                }else if(object.getString("time").toLowerCase().equals("afternoon")){
                                    Log.d("setting time","2");
                                    alarmTime.setAt(Util.toPersistentTime("1:00 PM"));
                                }else if(object.getString("time").toLowerCase().equals("evening")){
                                    Log.d("setting time","3");
                                    alarmTime.setAt(Util.toPersistentTime("6:00 PM"));
                                }else if(object.getString("time").toLowerCase().equals("night")){
                                    Log.d("setting time","4");
                                    alarmTime.setAt(Util.toPersistentTime("9:00 PM"));
                                }else{
                                    Log.d("setting time","5");
                                    Log.d("In else",object.getString("time")+"hihi");
                                    alarmTime.setAt(Util.toPersistentTime("9:37 PM"));
                                }

                                alarmTime.setAlarmId(alarmId);
                                alarmTime.persist(db);

                                service = new Intent(context, AlarmService.class);
                                service.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
                                service.setAction(AlarmService.POPULATE);
                                context.startService(service);
                            }
                        }

                        /*JSONObject medicineObjectMorning = medicinObject.getJSONObject("Morning");
                        JSONObject medicineObjectAfternoon = medicinObject.getJSONObject("Afternoon");
                        JSONObject medicineObjectEvening = medicinObject.getJSONObject("Evening");
                        JSONObject medicineObjectNight = medicinObject.getJSONObject("Night");
                        int mQuantity = medicineObjectMorning.getInt("Quantity");
                        int aQuantity = medicineObjectAfternoon.getInt("Quantity");
                        int eQuantity = medicineObjectEvening.getInt("Quantity");
                        int nQuantity = medicineObjectNight.getInt("Quantity");
                        int noOfDays = totalQuantity/(mQuantity+aQuantity+eQuantity+nQuantity);

                        alarm = new Alarm();
                        alarmTime = new AlarmTime();
                        alarm.setName(medicineNameList.get(j));
                        alarm.setSound(true);
                        long alarmId = 0;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(presDate.getYear(),presDate.getMonth(),presDate.getDay());
                        calendar.add(Calendar.DATE, noOfDays);
                        Date toDate = calendar.getTime();
                        alarm.setFromDate(Util.toPersistentDate(sdf.format(presDate), sdf));
                        alarm.setToDate(Util.toPersistentDate(sdf.format(toDate), sdf));
                        alarm.setRule(Util.concat(0, " ", 0, " ", 0));

                        alarmId = alarm.persist(db);

                        alarmTime.setAt(Util.toPersistentTime("2:00 AM"));
                        alarmTime.setAlarmId(alarmId);
                        alarmTime.persist(db);

                        service = new Intent(context, AlarmService.class);
                        service.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
                        service.setAction(AlarmService.POPULATE);
                        context.startService(service);

                        Log.d("presDate", presDate.toString());
                        Log.d("toDate", toDate.toString());
                        Log.d("totalQuantity", totalQuantity+"");
                        Log.d("morning quantity", mQuantity+"");
                        Log.d("after quantity",aQuantity+"");
                        Log.d("eve quantity",eQuantity+"");
                        Log.d("nigh quantity",nQuantity+"");
                        Log.d("noOfDays ",noOfDays+"");*/
                    }
                }

            }catch (Exception e){
                Log.d("Exception ", e.toString());
            }
        }

    }

}
