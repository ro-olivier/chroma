package fr.zigomar.chroma.chroma.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.Adapters.DrinkAdapter;
import fr.zigomar.chroma.chroma.Model.Drink;
import fr.zigomar.chroma.chroma.R;

public class AlcoholActivity extends InputActivity {

    private TextView descField;
    private TextView volumeField;
    private TextView degreeField;

    private ArrayList<Drink> drinks;
    private DrinkAdapter drinkAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting the views from their id
        this.descField = findViewById(R.id.TextDescription);
        this.volumeField = findViewById(R.id.DrinkVolume);
        this.degreeField = findViewById(R.id.DrinkDegree);
        Button addButton = findViewById(R.id.AddButton);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String d = descField.getText().toString();
                String vs = volumeField.getText().toString();
                String ds = degreeField.getText().toString();
                if (d.length() > 0 && vs.length() > 0 && ds.length() > 0) {
                    try {
                        double vol = Double.parseDouble(vs);
                        double deg = Double.parseDouble(ds);
                        drinkAdapter.add(new Drink(d, vol, deg));
                        updateSummary();
                        resetViews();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataThreeValuesRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // init of the data : fetch drinks data in the currentDate file if it exist
        this.drinks = getDrinks();
        updateSummary();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) drinks
        ListView drinksListView = findViewById(R.id.ListViewAlcohol);

        this.drinkAdapter = new DrinkAdapter(AlcoholActivity.this, this.drinks);
        drinksListView.setAdapter(this.drinkAdapter);

        drinksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(R.string.DeleteTitle);
                builder.setMessage(R.string.DeleteItemQuestion);

                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drinkAdapter.remove(drinks.get(pos));
                        drinkAdapter.notifyDataSetChanged();
                        updateSummary();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
    }

    private void resetViews() {
        degreeField.setText("");
        descField.setText("");
        volumeField.setText("");
    }

    private void updateSummary() {
        LinearLayout data_summary = findViewById(R.id.data_summary);
        data_summary.setVisibility(View.VISIBLE);

        TextView field0_data = findViewById(R.id.data_summary_field0);
        TextView field1_data = findViewById(R.id.data_summary_field1);
        TextView field2_data = findViewById(R.id.data_summary_field2);
        TextView field1_text = findViewById(R.id.data_summary_text1);
        TextView field2_text = findViewById(R.id.data_summary_text2);

        if (this.drinks.size() > 0) {

            field0_data.setText(R.string.Summary);

            field1_data.setVisibility(View.INVISIBLE);
            field1_text.setVisibility(View.INVISIBLE);

            double ua_total = 0;
            for (Drink d : this.drinks) {
                ua_total += d.getUA();
            }
            DecimalFormat df = new DecimalFormat("0.00");
            field2_data.setText(df.format(ua_total));

            field2_text.setText(R.string.ua);

        } else {
            data_summary.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<Drink> getDrinks(){
        // getting the data is handled by the DataHandler
        return this.dh.getDrinksList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current drinks");
        this.dh.saveAlcoholData(this.drinks);
    }
}