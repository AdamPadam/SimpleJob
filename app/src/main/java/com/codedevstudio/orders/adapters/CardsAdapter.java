package com.codedevstudio.orders.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codedevstudio.orders.models.Card;

/**
 * Created by fabius on 21/04/2018.
 */

public class CardsAdapter extends ArrayAdapter<Card> {
    private Context context;
    private Card[] cards;
    public CardsAdapter(@NonNull Context context, int resource, @NonNull Card[] cards) {
        super(context, resource, cards);
        this.context = context;
        this.cards = cards;
    }

    @Override
    public int getCount() {
        return cards.length;
    }

    @Nullable
    @Override
    public Card getItem(int position) {
        return cards[position];
    }

    @Override
    public long getItemId(int position) {
        return cards[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(cards[position].getCardNumber());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(cards[position].getCardNumber());

        return label;
    }
}
