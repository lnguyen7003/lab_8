package com.example.lab4application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private TodoDatabaseHelper databaseHelper;

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
                adapter.notifyDataSetChanged();

                insertTodoItemToDatabase(text, isUrgent);

                editText.getText().clear();
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
                                deleteTodoItemFromDatabase(position);
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

        databaseHelper = new TodoDatabaseHelper(this);
        loadTodoItemsFromDatabase();
        printCursor(loadCursorFromDatabase());
    }

    private void loadTodoItemsFromDatabase() {
        Cursor cursor = loadCursorFromDatabase();

        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_TEXT));
                int isUrgent = cursor.getInt(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_IS_URGENT));
                TodoItem item = new TodoItem(text, isUrgent == 1);
                itemList.add(item);
            } while (cursor.moveToNext());

            adapter.notifyDataSetChanged();
        }

        cursor.close();
    }

    private Cursor loadCursorFromDatabase() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TodoDatabaseHelper.TABLE_NAME;
        return db.rawQuery(query, null);
    }

    private void insertTodoItemToDatabase(String text, boolean isUrgent) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoDatabaseHelper.COLUMN_TEXT, text);
        values.put(TodoDatabaseHelper.COLUMN_IS_URGENT, isUrgent ? 1 : 0);
        db.insert(TodoDatabaseHelper.TABLE_NAME, null, values);
    }

    private void deleteTodoItemFromDatabase(int position) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String text = itemList.get(position).getText();
        String selection = TodoDatabaseHelper.COLUMN_TEXT + " = ?";
        String[] selectionArgs = {text};
        db.delete(TodoDatabaseHelper.TABLE_NAME, selection, selectionArgs);
    }

    private void printCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_TEXT));
                int isUrgent = cursor.getInt(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_IS_URGENT));
                Log.d("Database", "Text: " + text + ", IsUrgent: " + isUrgent);
            } while (cursor.moveToNext());
        }

        cursor.close();
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
                convertView = getLayoutInflater().inflate(R.layout.list_item_todo, parent, false);
            }

            TodoItem item = itemList.get(position);

            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(item.getText());

            TextView urgencyTextView = convertView.findViewById(R.id.urgencyTextView);
            if (item.isUrgent()) {
                urgencyTextView.setVisibility(View.VISIBLE);
            } else {
                urgencyTextView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    private static class TodoItem {
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
}
