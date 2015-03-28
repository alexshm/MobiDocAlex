package example.com.mobidoc.Screens.popUpScreens;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import example.com.mobidoc.R;

/**
 * Created by alex.shmaltsuev on 12/03/2015.
 */
public class QuestionPopScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionpopscreen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView questionMessage = (TextView) findViewById(R.id.questionMessage);
        TextView answer1 = (TextView) findViewById(R.id.answer1);
        TextView answer2 = (TextView) findViewById(R.id.answer2);
        Bundle extras = getIntent().getExtras();
        String question = extras.getString("question");
        String yesAns = extras.getString("yesAns");
        String noAns = extras.getString("noAns");
        questionMessage.setText(question);
        answer1.setText(yesAns);
        answer2.setText(noAns);

    }

    public void close(View view) {
        finish();
    }


    public void answer1Clicked(View view) {
        Button done = (Button) findViewById(R.id.answer1);
        done.setVisibility(View.VISIBLE);
    }

    public void answer2Clicked(View view) {
        Button done = (Button) findViewById(R.id.answer2);
        done.setVisibility(View.VISIBLE);
    }
}
