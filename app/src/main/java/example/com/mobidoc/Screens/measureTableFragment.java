package example.com.mobidoc.Screens;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.os.Build;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import example.com.mobidoc.R;
import projections.Utils;


public class measureTableFragment extends Fragment {

    private List<String> measuresList;
    TableLayout MeasureTable;
    View android;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        measuresList=  new ArrayList<String>();

        //measuresList=getArguments().getStringArrayList("measures");
        Log.i("taconstructor","measures  sizze is : "+measuresList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        android = inflater.inflate(R.layout.fragment_tab, container, false);
       // ((TextView) android.findViewById(R.id.textView)).setText("Android");
        MeasureTable = (TableLayout) android.findViewById(R.id.measureTable);
        createTable();
        ScrollView sv = new ScrollView(android.getContext());

        HorizontalScrollView hsv = new HorizontalScrollView(android.getContext());
        hsv.addView(MeasureTable);
        sv.addView(hsv);


        Log.i("tablefregment","measures list array sizze is : "+measuresList.size());
        return android;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    //private TableLayout createTable(String[] obs, String[] cv, int rowCount, int columnCount) {
    private void createTable()
    {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        //  TableLayout tableLayout = new TableLayout(android.getContext());
        //  Drawable res = getResources().getDrawable(R.drawable.health_background);
        // tableLayout.setBackground(res);
        // TextView messuresText = new TextView(android.getContext());
        // messuresText.setText("measures");
        // messuresText.setTextSize(23);
        // messuresText.setTextColor(Color.parseColor("#ff9cffd4"));

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        //tableRowParams.setMargins(1, 1,1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < 2; i++) {
            // 3) create tableRow
            TableRow newRow = createNewRow("", "c1", 1);
            MeasureTable.addView(newRow);
            MeasureTable.setColumnStretchable(i, true);
            //
        }

    }

    private enum TableTypes {
        Content, Title
    }

    private TableRow createNewRow(String value, String Date, int type) {
        TableRow tableRow = new TableRow(android.getContext());
        for (int i = 0; i < 4; i++) {
            TextView textView = createNewCell(TableTypes.Content);
            int newID = Utils.generateViewId();
            textView.setId(newID);
            textView.setText("rrrr" + i + " " + value);
            tableRow.addView(textView);

        }

        return tableRow;
    }

    @TargetApi(21)
    private TextView createNewCell(TableTypes type) {
        TextView textView = new TextView(android.getContext());
        Drawable drawable = drawable= getResources().getDrawable(R.drawable.title_shape);


        if (type.equals(TableTypes.Content)) {
            drawable= getResources().getDrawable(R.drawable.cell_shape);


        }
        textView.setPadding(5,0,0,0);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setBackground(drawable);


        return textView;
    }





}
