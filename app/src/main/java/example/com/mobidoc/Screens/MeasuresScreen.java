package example.com.mobidoc.Screens;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


import android.support.v4.app.FragmentPagerAdapter;

import android.util.Log;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.CommunicationLayer.ServicesToBeDSS.PicardCommunicationLayer;
import example.com.mobidoc.ConfigReader;
import example.com.mobidoc.R;


/**
 * Created by Alex on 22/04/2015.
 */
public class MeasuresScreen extends FragmentActivity {
    private OpenMrsApi openMrsApi;
    ViewPager viewpager;
    ActionBar tabs;
    FragmentPagerAdapter fragmentPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] obs = openMrsApi.getObs();

        setContentView(R.layout.measuresscreen);
        viewpager = (ViewPager) findViewById(R.id.view_pager);

        fragmentPager = new measureAdapter(getSupportFragmentManager());


        Bundle args = new Bundle();
        args.putStringArrayList("measures", null);

        viewpager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {

                        tabs = getActionBar();
                        tabs.setSelectedNavigationItem(position);
                    }
                });
        viewpager.setAdapter(fragmentPager);

        tabs = getActionBar();
        tabs.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {


                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };
        /////////////////////////////
        tabs.addTab(tabs.newTab().setText("Blood Glucose").setTabListener(tabListener));
        tabs.addTab(tabs.newTab().setText("Ketanuria").setTabListener(tabListener));
        tabs.addTab(tabs.newTab().setText("Blood Pressure").setTabListener(tabListener));

    }





    /* make original table
    ===============================
    private void ttmep()
    {
        String[] column = { "#Row","Observation"};
        int rl=obs.length; int cl=column.length;

//        Log.d("--", "R-Lenght--"+rl+"   "+"C-Lenght--"+cl);

        ScrollView sv = new ScrollView(this);
        TableLayout tableLayout = createTableLayout(obs, column,rl, cl);
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.addView(tableLayout);
        sv.addView(hsv);
        setContentView(sv);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private TableLayout createTableLayout(String [] obs, String [] cv,int rowCount, int columnCount) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(this);
        Drawable res = getResources().getDrawable(R.drawable.health_background);
        tableLayout.setBackground(res);
        TextView messuresText = new TextView(this);
        messuresText.setText("measures");
        messuresText.setTextSize(23);
        messuresText.setTextColor(Color.parseColor("#ff9cffd4"));
        tableLayout.addView(messuresText);
        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(1, 1,1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < rowCount; i++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.BLACK);
            for (int j= 0; j < columnCount; j++) {
                // 4) create textView
                TextView textView = new TextView(this);
                //  textView.setText(String.valueOf(j));
                textView.setBackgroundColor(Color.WHITE);

                if (i ==0){
                    textView.setText(cv[j]);
                } else if(i>0 && j==0){
                    textView.setText("Row "+i);
                }else {
                    textView.setText(obs[i]);

                }
                textView.setGravity(Gravity.LEFT);
                // 5) add textView to tableRow
                tableRow.addView(textView, tableRowParams);
            }
            tableLayout.setColumnStretchable(i, false);
            // 6) add tableRow to tableLayout
            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }

    */

}


