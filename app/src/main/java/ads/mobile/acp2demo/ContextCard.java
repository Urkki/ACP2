package ads.mobile.acp2demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.aware.utils.IContextCard;

import ads.mobile.acp2demo.R;

public class ContextCard implements IContextCard {

    //Constructor used to instantiate this card
    public ContextCard() {}

    @Override
    public View getContextCard(Context context) {
        //Load card layout
        View card = LayoutInflater.from(context).inflate(R.layout.card, null);

        //Initialize UI elements from the card

        //Set data on the UI

        //Return the card to AWARE/apps
        return card;
    }
}