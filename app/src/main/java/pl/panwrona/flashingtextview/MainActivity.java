package pl.panwrona.flashingtextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import pl.panwrona.flashingtextview.widget.FlashingTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FlashingTextView flashingTextView = (FlashingTextView)findViewById(R.id.flashingTextView);
        Button startButton = (Button)findViewById(R.id.buttonStart);
        Button stopButton = (Button)findViewById(R.id.buttonStop);
        Button changeButton = (Button)findViewById(R.id.buttonChange);

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashingTextView.setText("Changed Flashing Text View");
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashingTextView.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashingTextView.end();
            }
        });
    }
}
