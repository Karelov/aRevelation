/*
 * Copyright 2013 Aleksey Kislin
 * Copyright 2013 Michal Švirec
 *
 * This file is part of aRevelation.
 *
 * aRevelation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aRevelation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aRevelation.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.marmalade.aRevelation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: <a href="mailto:alexey.kislin@gmail.com">Alexey Kislin</a>
 * Date: 9/4/13
 * Time: 2:00 AM
 */
public class EntryFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String PASSWORD = "password";

    private ListView lv;
    private Activity activity;
    private FileEntriesFragment.Entry entry;
    private String password;
    private boolean isBlocked;
    HashMap<String, String> values;


    EntryFragment(FileEntriesFragment.Entry entry, String password) {
        this.entry = entry;
        Bundle bundle = new Bundle();
        bundle.putString(PASSWORD, password);
        setArguments(bundle);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.entry_layout, container, false);
    }


    @Override
    public void onResume() {
        Bundle savesState = this.getArguments();
        if(savesState != null)
            password = savesState.getString(PASSWORD);
        super.onResume();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
            password = savedInstanceState.getString(PASSWORD);
        activity = getActivity();
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        lv = (ListView)activity.findViewById(R.id.entry_list);
        if(isBlocked)
            restoreAccess();
        else
            showRevelationEntry(entry, activity);
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PASSWORD, password);
    }


    void blockAccess() {
        isBlocked = true;
        this.values = new HashMap<String, String>();
        ((SimpleAdapter)lv.getAdapter()).notifyDataSetChanged();
    }


    /**
     * Restore access on application open
     */
    private void restoreAccess() {
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if(password.equals(input.getText().toString())) {
                            showRevelationEntry(entry, activity);
                            isBlocked = false;
                        } else {
                            restoreAccess();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        ((MainActivity)getActivity()).reload(); // Go to file menu
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Input password")
                .setView(input)
                .setNegativeButton("Cancel", dialogClickListener)
                .setPositiveButton("Submit", dialogClickListener)
                .show();
    }


    private void showRevelationEntry(FileEntriesFragment.Entry entry, Activity activity) {
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        values = new HashMap<String, String>();
        values.put("First Line", activity.getString(R.string.name));
        values.put("Second Line", entry.name);
        data.add(values);
        values = new HashMap<String, String>();
        values.put("First Line", activity.getString(R.string.description));
        values.put("Second Line", entry.description);
        data.add(values);
        for(String key : entry.fields.keySet()) {
            values = new HashMap<String, String>();
            values.put("First Line", FileEntriesFragment.Entry.getFieldName(key, activity));
            values.put("Second Line", entry.fields.get(key));
            data.add(values);
        }
        values = new HashMap<String, String>();
        values.put("First Line", activity.getString(R.string.notes));
        values.put("Second Line", entry.notes);
        data.add(values);
        values = new HashMap<String, String>();
        values.put("First Line", "Updated");
        values.put("Second Line", entry.updated);
        data.add(values);

        SimpleAdapter adapter = new SimpleAdapter(activity, data,
                android.R.layout.simple_list_item_2,
                new String[] {"First Line", "Second Line" },
                new int[] {android.R.id.text1, android.R.id.text2 });
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO Implement behaviour
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO Implement behaviour
        return false;
    }

}
