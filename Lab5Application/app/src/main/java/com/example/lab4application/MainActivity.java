package com.example.lab4application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<TodoItem> itemList;
    private TodoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemList = new ArrayList<>();
        adapter = new TodoListAdapter();

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        EditText editText = findViewById(R.id.editText);
        Switch urgentSwitch = findViewById(R.id.urgentSwitch);
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                boolean isUrgent = urgentSwitch.isChecked();

                TodoItem item = new TodoItem(text, isUrgent);
                itemList.add(item);

                editText.getText().clear();
                adapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Do you want to delete this?")
                        .setMessage("The selected row is: " + position)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing or add any other desired action
                            }
                        })
                        .show();
            }
        });
    }

    private class TodoItem {
        private String text;
        private boolean isUrgent;

        public TodoItem(String text, boolean isUrgent) {
            this.text = text;
            this.isUrgent = isUrgent;
        }

        public String getText() {
            return text;
        }

        public boolean isUrgent() {
            return isUrgent;
        }
    }

    private class TodoListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.todo_item, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.textViewItem);
            TodoItem item = itemList.get(position);
            textView.setText(item.getText());
            textView.setTextSize(20);

            if (item.isUrgent()) {
                convertView.setBackgroundColor(Color.RED);
                textView.setTextColor(Color.WHITE);
            } else {
                convertView.setBackgroundColor(Color.WHITE);
                textView.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }
}
