package xyz.skylar.justthetip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(SendListener);
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
        double p = Double.parseDouble(str);
        double prc = p / 100;
        double ttl = Double.parseDouble(total.getText().toString());
        ttl = ttl * prc;

        String amt = String.format("%.2f", ttl);
        tipCalc.setText(amt);
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

    // confirmation of payment dialog after QR scan
    public AlertDialog createDialog (String recipient, String amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Send " + amount + " to " + recipient + "?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // send money w/ api call
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // return to pay config
                    }
                });
        return builder.create();
    }

    // send button -- initiates QR code scan to send money
    View.OnClickListener SendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // make sure tip total is acceptable
            if (!tipCalc.getText().toString().equals(null)
                    && !tipCalc.getText().toString().equals("0")
                    && !tipCalc.getText().toString().equals("tip comes out")) {
                IntentIntegrator integrator = new IntentIntegrator(PayConfigActivity.this);
                integrator.initiateScan();
            } else {
                Toast sendToast = Toast.makeText(context, "please enter a valid tip amount", Toast.LENGTH_SHORT);
                sendToast.show();
            }
        }
    };

    // result from the QR code scan above
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                Log.i("Success!", contents.toString());
                // set recipient
                String recipient = null;
                String amount = tipCalc.getText().toString();
                try {
                    JSONObject userInfoJSON = new JSONObject(contents);
                    recipient = userInfoJSON.getString("display_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (recipient!=null) {
                    AlertDialog alertDialog = createDialog(recipient, amount);
                    alertDialog.show();
                }
            } else {
                Log.i("Failed.", "no result to show");
            }
        }
    }
    public void splitActivity(View v){
        Intent intent = new Intent(PayConfigActivity.this, SplitActivity.class);
        startActivity(intent);
    }
}
