package com.example.souvik.remindertemplate.Adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.souvik.remindertemplate.R;
import com.example.souvik.remindertemplate.RemindMe;
import com.example.souvik.remindertemplate.model.AlarmMsg;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MedicineViewHolder> {




    public static class MedicineViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        CardView cv;
        TextView medicineName;
        ImageView medicinePhoto;
        RelativeLayout header;

        TextView item_id;

        TextView schedule;
        TextView instruction;
        TextView statusString;
        ImageView statusIcon;

        View separatorLine;
        LinearLayout cardButtons;

        Button skip;
        Button take;



        private SQLiteDatabase db = RemindMe.db;





      public MedicineViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            medicineName = (TextView)itemView.findViewById(R.id.take_dialog_pillname);
            medicinePhoto = (ImageView)itemView.findViewById(R.id.take_dialog_pill);
            header = (RelativeLayout)itemView.findViewById(R.id.take_dialog_title_wrap);

            item_id = (TextView)itemView.findViewById(R.id.item_id);

            schedule = (TextView)itemView.findViewById(R.id.take_dialog_schedule);
            instruction = (TextView)itemView.findViewById(R.id.take_dialog_instructions);
            statusString = (TextView)itemView.findViewById(R.id.take_dialog_status);
            statusIcon = (ImageView)itemView.findViewById(R.id.take_dialog_status_icon);

            separatorLine = itemView.findViewById(R.id.separator_line);
            cardButtons = (LinearLayout)itemView.findViewById(R.id.card_buttons);

            skip = (Button)itemView.findViewById(R.id.card_skip);
            take = (Button)itemView.findViewById(R.id.card_take);



            itemView.setOnLongClickListener(this);
//            itemView.setOnCreateContextMenuListener(this);



        }

        @Override
        public boolean onLongClick(View v) {
//            MainActivity.rv.showContextMenuForChild(v);
            itemView.showContextMenu();
            return true;
        }



   /*     @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView
                    .RecyclerContextMenuInfo) menuInfo;
//            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            alarmMsg.setId(getPosition());
            Log.d("on create context menu", "here");
            menu.setHeaderTitle("Choose an Option");
            menu.setHeaderIcon(R.drawable.ic_setting);
            menu.add(0, R.id.ringtone, 0, "");
            menu.add(0, R.id.menu_edit, 0, "Edit");

            alarmMsg.load(db);
            alarm.reset();
            alarm.setId(alarmMsg.getAlarmId());
            alarm.load(db);

            if(alarm.getSound()==true){
                menu.findItem(R.id.ringtone).setTitle("Disable Ringtone");
            }else{
                menu.findItem(R.id.ringtone).setTitle("Enable Ringtone");
            }

            if (alarmMsg.getDateTime() < System.currentTimeMillis()){
                menu.removeItem(R.id.menu_edit);
                menu.removeItem(R.id.ringtone);
            }

        }*/

    }

    List<Medicine> medicines;
    static Context context;
    String from;

    public RVAdapter(List<Medicine> medicines, Context context, String from ){
        this.medicines = medicines;
        this.context = context;
        this.from =from;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        MedicineViewHolder pvh = new MedicineViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder medicineViewHolder, int i) {

        final long id = medicines.get(i).item_id;
        if(from.equals("notification")){
               medicineViewHolder.separatorLine.setVisibility(View.VISIBLE);
               medicineViewHolder.cardButtons.setVisibility(View.VISIBLE);

            String  s = Context.NOTIFICATION_SERVICE;
            final NotificationManager mNM = (NotificationManager)context.getSystemService(s);

            final AlarmMsg alarmMsg = new AlarmMsg(id);

            medicineViewHolder.skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarmMsg.setStatus(AlarmMsg.SKIPPED);
                    alarmMsg.persist(RemindMe.db);
                    mNM.cancel((int)id );
                }
            });

            medicineViewHolder.take.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarmMsg.setStatus(AlarmMsg.TAKEN);
                    alarmMsg.persist(RemindMe.db);
                    mNM.cancel((int) id);
                }
            });
        }

        medicineViewHolder.medicineName.setText(medicines.get(i).name);
        medicineViewHolder.medicinePhoto.setImageResource(medicines.get(i).photoId);
        medicineViewHolder.header.setBackgroundColor(medicines.get(i).headerColor);

        medicineViewHolder.schedule.setText(medicines.get(i).schedule);
        medicineViewHolder.instruction.setText(medicines.get(i).instruction);
        medicineViewHolder.statusString.setText(medicines.get(i).statusString);

        medicineViewHolder.statusIcon.setImageResource(medicines.get(i).statusIcon);


        medicineViewHolder.item_id.setText(Long.toString(id));



    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    @Override
    public long getItemId(int position) {
       return medicines.get(position).item_id;
    }
}
