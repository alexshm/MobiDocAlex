package example.com.mobidoc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by alex.shmaltsuev on 15/03/2015.
 */
public class MessurePop  extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messurepopscreen);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView t = (TextView) findViewById(R.id.message);
        Bundle extras = getIntent().getExtras();
        String newString = extras.getString("msg");
        t.setText(newString);

    }

    public void done(View view) {
        finish();
    }
}
