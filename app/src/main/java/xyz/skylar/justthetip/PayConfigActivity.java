package xyz.skylar.justthetip;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by matfukano on 5/31/15.
 */
public class PayConfigActivity extends ActivityBase {
    TextView tipStr;
    EditText total, tipCalc;
    SeekBar seek;
    Button tenPerc, fifPerc, eigPerc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        seek = (SeekBar) findViewById(R.id.seekBar);
        tipStr = (TextView) findViewById(R.id.tipTracker);
        total = (EditText) findViewById(R.id.totalInput);
        tipCalc = (EditText) findViewById(R.id.tipOut);
        tenPerc = (Button) findViewById(R.id.ten);
        tenPerc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                seek.setProgress(seek.getMax() - 20);
            }
        });

        fifPerc = (Button) findViewById(R.id.fifteen);
        fifPerc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                seek.setProgress(seek.getMax() - 15);
            }
        });

        eigPerc = (Button) findViewById(R.id.eighteen);
        eigPerc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                seek.setProgress(seek.getMax() + 12);
            }
        });

        seek.setOnSeekBarChangeListener(customSeekBarListener);
    }

 /*   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.activity_payment, container, false);
        return fragmentView;
    }
*/
    private SeekBar.OnSeekBarChangeListener customSeekBarListener =
        new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek, int progress, boolean fromUsr){
                setTipTotal(progress);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        };

    public void setTipTotal(int progress){
        progress = progress + 10;
        float prc = progress / 100;
        float ttl = Float.parseFloat(total.getText().toString());
        ttl = ttl * prc;
        tipCalc.setText(Float.toString(ttl));
    }
}
