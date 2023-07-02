package com.example.lab7application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CharacterAdapter extends ArrayAdapter<Character> {

    private Context context;
    private List<Character> characters;

    public CharacterAdapter(Context context, List<Character> characters) {
        super(context, 0, characters);
        this.context = context;
        this.characters = characters;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Character currentCharacter = characters.get(position);

        TextView nameTextView = listItemView.findViewById(R.id.nameTextView);
        nameTextView.setText(currentCharacter.getName());

        return listItemView;
    }
}
