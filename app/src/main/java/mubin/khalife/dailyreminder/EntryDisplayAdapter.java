package mubin.khalife.dailyreminder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by developer on 8/28/2015.
 */
public class EntryDisplayAdapter extends ArrayAdapter<Entries> {

    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    //private SparseBooleanArray mSelectedItemsIds;

    public EntryDisplayAdapter(Context context, List<Entries> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        View v = super.getView(position, convertView, parent);//let the adapter handle setting up the row views
        v.setBackgroundColor(Color.parseColor("#FFFFFF")); //default color

        if (mSelectedItemsIds.get(position)) {
            v.setBackgroundColor(Color.parseColor("#FFD700"));// this is a selected position so make it red
        }

        return v;
       */

        try {
            Entries entry = getItem(position);

            final int entryId = entry.entryId;

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_entries_layout, parent, false);
            }

            //Lookup view for data population
            TextView tvTitle = (TextView) convertView.findViewById(R.id.txtEntryTitle);
            TextView tvDescription = (TextView) convertView.findViewById(R.id.txtEntryDesccription);
            TextView tvDate = (TextView) convertView.findViewById(R.id.txtEntryDate);
            ImageView imgPriority = (ImageView) convertView.findViewById(R.id.imgPriority);

            TextView txtEditEntry = (TextView) convertView.findViewById(R.id.txtEditEntry);
            txtEditEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext().getApplicationContext(), "Update " + entry.entryId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), AddReminder.class);
                    intent.putExtra("entry_id", entryId);
                    v.getContext().startActivity(intent);
                }
            });

            TextView txtDelEntry = (TextView) convertView.findViewById(R.id.txtDelEntry);
            txtDelEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext().getApplicationContext(), "Delete " + entryId, Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                    ad.setTitle("Delete Entry?!");
                    ad.setMessage("Are you sure you want to delete this entry?");
                    //ad.setIcon(android.R.drawable.ic_dialog_alert);
                    ad.setIcon(R.mipmap.ic_dialog_alert);
                    ad.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Delete from db
                            DBHelper db = new DBHelper(getContext());
                            String resultDeleteEntry = db.deleteEntry(entryId);
                            //Toast.makeText(getContext().getApplicationContext(), resultDeleteEntry, Toast.LENGTH_SHORT).show();
                            getContext().startActivity(new Intent(getContext(), MainActivity.class));
                        }
                    });

                    ad.setNegativeButton(android.R.string.no, null);
                    ad.show();
                }
            });

            //Populate data into template view using the data object
            tvTitle.setText(entry.entryTitle);
            tvDescription.setText(entry.entryDescription);
            tvDate.setText(entry.entryDate);

            //System.out.println("Setting Reminder Id = " + entry.entryId);

            if(entry.entryPriority.equals("low")) {
                imgPriority.setBackgroundResource(R.drawable.priority_low);
                //convertView.setBackgroundColor(Color.parseColor("#0000FF"));
            }
            else if(entry.entryPriority.equals("normal")) {
                imgPriority.setBackgroundResource(R.drawable.priority_normal);
                //convertView.setBackgroundColor(Color.parseColor("#00FF00"));
            }
            else if (entry.entryPriority.equals("high")) {
                imgPriority.setBackgroundResource(R.drawable.priority_high);
                //convertView.setBackgroundColor(Color.parseColor("#FF0000"));
            }

            convertView.setId(entry.entryId);

            /*convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setBackgroundColor(Color.parseColor("#FF0000"));
                    return true;
                }
            });*/

        }catch (Exception e)
        {
            System.out.println("GEt ViewEXception - "+e.toString());
        }

        return convertView;
    }

    @Override
    public void remove(Entries object) {
        try {
            //mSelection.remove((object.entryId) - 1);
            System.out.println("BEfore delete");

            Iterator it = mSelection.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                //it.remove(); // avoids a ConcurrentModificationException
            }


            int keyToDelete = ((object.entryId) - 1);

            //System.out.println(keyToDelete);

            //mSelection.remove(keyToDelete);

            /*notifyDataSetChanged();*/


        }catch (Exception e){
            System.out.println("remove error occured - " + e.toString());
        }
    }

    /*
    public void toggleSelection(int position) {
        selectView(position, mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        mSelectedItemsIds.put(position, value);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
       // return mSelectedItemsIds.size();
        return mSelection.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }*/

    public HashMap<Integer, Boolean> getSelectedIds() {
        return mSelection;
    }

    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);

        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection()
    {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }
}
