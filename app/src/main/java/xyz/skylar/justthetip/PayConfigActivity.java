package xyz.skylar.justthetip;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

/**
 * Created by matfukano on 5/31/15.
 */
public class PayConfigActivity extends ActivityBase {
    TextView tipStr;
    EditText total, tipCalc;
    SeekBar seek;
    Button tenPerc, fifPerc, eigPerc, full;
    LinearLayout ll;
    StringBuilder sb;
    Context context;
    Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_payment);
        seek = (SeekBar) findViewById(R.id.seekBar);
        tipStr = (TextView) findViewById(R.id.tipTracker);
        total = (EditText) findViewById(R.id.totalInput);
        total.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                ll.setVisibility(View.VISIBLE);
                total.requestFocus();
                return false;
            }
        });

        tipCalc = (EditText) findViewById(R.id.tipOut);
        tipCalc.setFocusable(false);
        tenPerc = (Button) findViewById(R.id.ten);
        fifPerc = (Button) findViewById(R.id.fifteen);
        eigPerc = (Button) findViewById(R.id.eighteen);
        full = (Button) findViewById(R.id.custom);

        /* switch for button pressing with a default to toast if total is null */

            tenPerc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax() - 20);
                    }
                }
            });
            fifPerc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax() - 15);
                    }
                }
            });
            eigPerc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax() - 12);
                    }
                }
            });
            full.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax());
                    }
                }
            });


        ll = (LinearLayout) findViewById(R.id.keyboard_overlay);
        ll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(total.getWindowToken(), 0);
                ll.setVisibility(View.GONE);
                total.clearFocus();
            }
        });

        seek.setOnSeekBarChangeListener(customSeekBarListener);
    }


    private SeekBar.OnSeekBarChangeListener customSeekBarListener =
        new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek, int progress, boolean fromUsr) {
                if (total.getText().toString().matches("")) {
                    displayToast("Buddy, you can't move the slider if you don't have a total.");
                } else {
                    progress = progress + 10;
                    String str = Integer.toString(progress);
                    sb = new StringBuilder();
                    sb.append(str);
                    sb.append("%");
                    tipStr.setText(sb.toString());
                    setTipTotal(str);
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        };
    /*
        Method to construct the value placed in the second edit text below "tip total"
     */
    public void setTipTotal(String str){
        float p = Float.parseFloat(str);
        float prc = p / 100;
        float ttl = Float.parseFloat(total.getText().toString());
        ttl = ttl * prc;

        float total;
        total = round(ttl, 2);
        tipCalc.setText(Float.toString(total));
    }

    /*
        Rounding helper method to clean up the float.
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
