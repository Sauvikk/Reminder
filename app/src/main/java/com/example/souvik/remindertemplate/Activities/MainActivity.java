package com.example.souvik.remindertemplate.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.souvik.remindertemplate.Adapter.ContextMenuRecyclerView;
import com.example.souvik.remindertemplate.Adapter.Medicine;
import com.example.souvik.remindertemplate.Adapter.RVAdapter;
import com.example.souvik.remindertemplate.Alarm.AlarmService;
import com.example.souvik.remindertemplate.Alarm.GetAlarm;
import com.example.souvik.remindertemplate.R;
import com.example.souvik.remindertemplate.RemindMe;
import com.example.souvik.remindertemplate.Util;
import com.example.souvik.remindertemplate.model.Alarm;
import com.example.souvik.remindertemplate.model.AlarmMsg;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    public static RecyclerView rv;
    private List<Medicine> medicines;

    private TextView rangeText;
    public  Calendar cal = Calendar.getInstance();
    public final Date dt = new Date();
    private String[] monthArr;
    GestureDetector gd;
    private SQLiteDatabase db;

    LinearLayout rv_linearLayout;
    TextView emptyView;

    private AlarmMsg alarmMsg = new AlarmMsg();
    private Alarm alarm = new Alarm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        rv=(RecyclerView)findViewById(R.id.rv);
        registerForContextMenu(rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reminders");
        initialize();
        listeners();



        int r = RemindMe.getDateRange();
        switch(r) {
            case 3: // Yearly
                cal.set(Calendar.MONTH, 0);

            case 2: // Monthly
                cal.set(Calendar.DATE, 1);

            case 1: // Weekly
                if (r==1) cal.set(Calendar.DATE, cal.getFirstDayOfWeek());

            case 0: // Daily
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
        }

        initializeData();
        initializeAdapter();

    }

    private void listeners() {


       /* findViewById(android.R.id.content).getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gd.onTouchEvent(motionEvent);
                return false;
            }
        });

        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gd.onTouchEvent(motionEvent);
                return false;
            }
        });*/

        rangeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gd.onTouchEvent(motionEvent);
                return false;
            }
        });

        rangeText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rangeText.setText(getRangeResetStr());
                initializeData();
                initializeAdapter();
//                setList();
                return true;
            }
        });



        /*rangeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rangeText.setText(getRangeResetStr());
                initializeData();
                initializeAdapter();
//                setList();
                return false;
            }
        });*/
    }

    private void initialize() {
        monthArr = getResources().getStringArray(R.array.spinner3_arr);
        rangeText = (TextView) findViewById(R.id.range);
        rangeText.setText(getRangeStr());
        emptyView = (TextView)findViewById(R.id.empty_view);

        rv_linearLayout = (LinearLayout)findViewById(R.id.rv_linearLayout);

        GestureDetector.SimpleOnGestureListener gestureListener = new MyGestureListener();
        gd = new GestureDetector(this, gestureListener);


        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
//        rv.getRootView().setClickable(true);
//        rv.getRootView().setFocusable(true);


        db = RemindMe.db;
    }


   private void initializeData(){
        medicines = new ArrayList<>();
        Cursor cursor = createCursor();

       if (cursor != null && cursor.moveToFirst()) {
           do {
               Log.d("Name", cursor.getString(cursor.getColumnIndex(Alarm.COL_NAME)));
               Log.d("Quantity", cursor.getInt(cursor.getColumnIndex(Alarm.COL_QUANTITY))+"");
               Log.d("Date", cursor.getLong(cursor.getColumnIndex(AlarmMsg.COL_DATETIME)) + "");
               Log.d("Alarm id", cursor.getLong(cursor.getColumnIndex(AlarmMsg.COL_ID))+"");

               String status = cursor.getString(cursor.getColumnIndex(AlarmMsg.COL_STATUS));

               long time = cursor.getLong(cursor.getColumnIndex(AlarmMsg.COL_DATETIME));
               dt.setTime(time);
               String txt =  Util.getActualTime(dt.getHours(), dt.getMinutes());
               Medicine medicine = new Medicine();
               medicine.setName(cursor.getString(cursor.getColumnIndex(Alarm.COL_NAME)));
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

               medicine.setSchedule("Take " + cursor.getInt(cursor.getColumnIndex(Alarm.COL_QUANTITY)) + " after food");
               medicine.setItem_id(cursor.getLong(cursor.getColumnIndex(AlarmMsg.COL_ID)));
               medicines.add(medicine);

           } while (cursor.moveToNext());
       }

       if (medicines.isEmpty()) {
           rv.setVisibility(View.GONE);
           emptyView.setVisibility(View.VISIBLE);
       }
       else {
           rv.setVisibility(View.VISIBLE);
           emptyView.setVisibility(View.GONE);
       }
      /*  medicines.add(new Medicine("Crocin",R.drawable.capsule, Color.parseColor("#F7002D"),
                "Was scheduled for 10:00 AM", "Take 2 after food", "Taken at 10:00 AM", R.drawable.ic_check_green));

        medicines.add(new Medicine("Paracetamol", R.drawable.tablet, Color.parseColor("#F7002D"),
                "Was scheduled for 1:00 PM", "Take 1 before food", "Taken at 1:00 PM", R.drawable.ic_check_green));


        medicines.add(new Medicine("Voveran", R.drawable.capsule_one, Color.parseColor("#F7002D"),
                "Was scheduled for 6:00 PM", "Take 2 after food", "Skipped", R.drawable.ic_skip_red));

        medicines.add(new Medicine("Plavix", R.drawable.injection, Color.parseColor("#F7002D"),
                "Scheduled at 9:00 PM", "Take 1 after food", "Yet to take", R.drawable.ic_blue_check ));*/
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(medicines, this, "not notification");
        rv.setAdapter(adapter);
        rv.setItemAnimator(new DefaultItemAnimator());
    }

    private Cursor createCursor() {
        Cursor cursor = RemindMe.dbHelper.listNotifications(db, cal.getTimeInMillis() + move(+1), cal.getTimeInMillis() + move(-1));
        Log.d("Cursor : ", DatabaseUtils.dumpCursorToString(cursor));
        return cursor;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo;
        long itemIndex = (( ContextMenuRecyclerView.RecyclerContextMenuInfo) menuInfo).id;
        Log.d("info id " , ""+itemIndex);
        alarmMsg.setId(info.id);
        Log.d("on create context menu", info+"111");
        getMenuInflater().inflate(R.menu.context_menu, menu);
        menu.setHeaderTitle("Choose an Option");
        menu.setHeaderIcon(R.drawable.ic_setting);

        alarmMsg.load(db);
        alarm.reset();
        alarm.setId(alarmMsg.getAlarmId());
        alarm.load(db);

        if(alarm.getSound()==true){
            menu.findItem(R.id.ringtone).setTitle("Disable Ringtone");
        }else{
            menu.findItem(R.id.ringtone).setTitle("Enable Ringtone");
        }

        Log.d("alarmMsg.getDateTime()" , alarmMsg.getDateTime()+"");
        Log.d("currentTimeMillis()" , System.currentTimeMillis()+"");

        if (alarmMsg.getDateTime() < System.currentTimeMillis()){
            menu.removeItem(R.id.menu_edit);
            menu.removeItem(R.id.ringtone);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();
         if (id == R.id.action_settings) {
             startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_add){
            new GetAlarm(this);
            return true;
        }

        if(id == R.id.action_calendar){
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setAccentColor(Color.parseColor("#c0392b"));
            dpd.show(getFragmentManager(), "Calendar");
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("cal", cal.getTimeInMillis());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        cal.setTimeInMillis(state.getLong("cal"));
    }

    private String getRangeStr() {
        int date = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        dt.setTime(System.currentTimeMillis());

        switch(RemindMe.getDateRange()) {
            case 0: // Daily
                if (date==dt.getDate() && month==dt.getMonth() && year==dt.getYear()+1900) return "Today";
                else return date+" "+monthArr[month+1];

            case 1: // Weekly
                return date+" "+monthArr[month+1] + move(+1) + " - " + cal.get(Calendar.DATE)+" "+monthArr[cal.get(Calendar.MONTH)+1] + move(-1);

            case 2: // Monthly
                return monthArr[month+1]+" "+year;

        }
        return null;
    }

    private String getRangeResetStr() {
        cal = Calendar.getInstance();
        int date = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        dt.setTime(System.currentTimeMillis());
        switch(RemindMe.getDateRange()) {
            case 0: // Daily
                if (date==dt.getDate() && month==dt.getMonth() && year==dt.getYear()+1900) return "Today";
                else return date+" "+monthArr[month+1];

            case 1: // Weekly
                return date+" "+monthArr[month+1] + move(+1) + " - " + cal.get(Calendar.DATE)+" "+monthArr[cal.get(Calendar.MONTH)+1] + move(-1);

            case 2: // Monthly
                return monthArr[month+1]+" "+year;

        }
        return null;
    }

    private String move(int step){
        switch(RemindMe.getDateRange()) {
            case 0:
                cal.add(Calendar.DATE, 1*step);
                break;
            case 1:
                cal.add(Calendar.DATE, 7*step);
                break;
            case 2:
                cal.add(Calendar.MONTH, 1 * step);
                break;
            case 3:
                cal.add(Calendar.YEAR, 1 * step);
                break;
        }
        return "";
    }

    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ringtone:
                if(item.getTitle().equals("Enable Ringtone")){
                    alarm.setSound(true);
                }else{
                    alarm.setSound(false);
                }
                alarm.persist(db);
                break;
            case R.id.menu_edit:
                Calendar now = Calendar.getInstance();
                TimePickerDialog timePickerDialog=null;
                if(RemindMe.is24Hours()) {
                    timePickerDialog =TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                }else{
                    timePickerDialog =TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                }
                timePickerDialog.setAccentColor(Color.parseColor("#c0392b"));
                timePickerDialog.show(getFragmentManager(), "timePicker");
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DATE, dayOfMonth);
        rangeText.setText(getRangeStr());
        initializeData();
        initializeAdapter();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = hourOfDay+":"+minute;
        Log.d("time ", time);
        RemindMe.dbHelper.cancelNotification(db, alarmMsg.getAlarmId(), true);
        Intent cancelRepeating = new Intent(this, AlarmService.class);
        cancelRepeating.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmMsg.getAlarmId()));
        cancelRepeating.setAction(AlarmService.CANCEL);
        this.startService(cancelRepeating);
        new GetAlarm(this,alarm.getName(),alarm.getFromDate(),alarm.getToDate(),time);
        initializeData();
        initializeAdapter();
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            boolean result = false;
            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > 120 && Math.abs(velocityX) > 200) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
                result = true;
            }


            return result;
        }

        protected void onSwipeLeft() {
            Log.d("on swipe left", "yes");
            move(+1);
            rangeText.setText(getRangeStr());
            initializeData();
            initializeAdapter();
//            setList();
        }

        protected void onSwipeRight() {
            Log.d("on swipe right", "yes");
            move(-1);
            rangeText.setText(getRangeStr());
            initializeData();
            initializeAdapter();
//            setList();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            return true;
        }


    }

}
