package xyz.skylar.justthetip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
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

/**
 * Created by matfukano on 5/31/15.
 */
public class PayConfigActivity extends Activity {
    TextView tipStr;
    EditText total, tipCalc;
    SeekBar seek;
    Button tenPerc, fifPerc, twnPerc, twfPerc, full;
    LinearLayout ll;
    StringBuilder sb;
    Context context;
    Toast toast;
    Intent resultIntent;
    private String note = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        context = this;
        resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);

        if (savedInstanceState != null){
            String totIn = savedInstanceState.getString("tot_IN");
            int seekProg = savedInstanceState.getInt("seek_prog");
            int tipPerc = savedInstanceState.getInt("tip_PRC");
            String tipOut = savedInstanceState.getString("tip_OUT");
            total.setText(totIn);
            seek.setProgress(seekProg);
            tipStr.setText(tipPerc);
            tipCalc.setText(tipOut);
        }
        //FF01579B

        tenPerc = (Button) findViewById(R.id.ten);
        fifPerc = (Button) findViewById(R.id.fifteen);
        twnPerc = (Button) findViewById(R.id.twenty);
        twfPerc = (Button) findViewById(R.id.twentyfive);
        full = (Button) findViewById(R.id.custom);

        seek = (SeekBar) findViewById(R.id.seekBar);
        tipStr = (TextView) findViewById(R.id.tipTracker);
        total = (EditText) findViewById(R.id.totalInput);
        tipCalc = (EditText) findViewById(R.id.tipOut);

        // LoadPrefs();


        Button send = (Button) findViewById(R.id.sendButton);
        send.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
        Button split = (Button) findViewById(R.id.splitButton);
        Button save = (Button) findViewById(R.id.saveButton);
        split.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
        save.setBackgroundColor(getResources().getColor(R.color.apptheme_color));

        // Allow user to tap off EditText
        total.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                ll.setVisibility(View.VISIBLE);
                total.requestFocus();
                return false;
            }
        });
        tipCalc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                ll.setVisibility(View.VISIBLE);
                tipCalc.requestFocus();
                return false;
            }
        });

        /* button onClick logic */
            // ten percent
            tenPerc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearFocus();
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax() - 20);
                    }
                }
            });
            // fifteen percent
            fifPerc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearFocus();
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax() - 15);
                    }
                }
            });
            // twenty percent
            twnPerc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearFocus();
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax() - 10);
                    }
                }
            });
            // twenty five percent
            twfPerc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearFocus();
                    if (total.getText().toString().matches("")) {
                        displayToast("I need a total!");
                    } else {
                        seek.setProgress(seek.getMax() - 5);
                    }
                }
            });
            full.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearFocus();
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
                clearFocus();
            }
        });

        // Default tip % to 18% when the user presses enter on the soft keyboard
        total.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.FLAG_EDITOR_ACTION) {
                    seek.setProgress(seek.getMax() - 12);
                    setTipTotal("18");
                    return true;
                }
                return false;
            }
        });

        // Default tip % to 18% when the user taps off the total editText
        total.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!total.getText().toString().matches("")) {
                        seek.setProgress(seek.getMax() - 12);
                        setTipTotal("18");
                    }
                }
            }
        });

        seek.setOnSeekBarChangeListener(customSeekBarListener);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(SendListener);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(SaveListener);
    }

    // Tip % slider logic
    private SeekBar.OnSeekBarChangeListener customSeekBarListener =
        new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seek, int progress, boolean fromUsr) {
                if (!total.getText().toString().matches("")) {
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
                clearFocus();
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (total.getText().toString().matches("")) {
                    displayToast("Can't move the slider if you don't have a total");
                }
            }

        };

    // Removes focus from a selected element
    public void clearFocus(){
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(total.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tipCalc.getWindowToken(), 0);
        ll.setVisibility(View.GONE);
        total.clearFocus();
        tipCalc.clearFocus();
    }

    /*
        Method to construct the value placed in the second edit text below "tip total"
     */
    public void setTipTotal(String str){
        if(total.getText().toString().matches(""))
            displayToast("Can't calculate a tip without a total");
        else {
            double p = Double.parseDouble(str);
            double prc = p / 100;
            double ttl = Double.parseDouble(total.getText().toString());
            ttl = ttl * prc;

            String amt = String.format("%.2f", ttl);
            tipCalc.setText(amt);
       }
    }

    // Toast helper method
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    // confirmation of payment dialog after QR scan
    public AlertDialog createDialog (final String recipient, final String email, final String amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setMessage("Send " + amount + " to " + recipient + "?\nEnter note:")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // send money w/ api call
                        note = input.getText().toString();
                        makeAPIcall(email, amount, note);

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
                    && !tipCalc.getText().toString().equals("")
                    && !tipCalc.getText().toString().equals("0")) {
                IntentIntegrator integrator = new IntentIntegrator(PayConfigActivity.this);
                integrator.initiateScan();
            } else {
                Toast sendToast = Toast.makeText(context, "Please enter a valid tip amount", Toast.LENGTH_SHORT);
                sendToast.show();
            }
        }
    };

    // save button -- sends result to slidingTabsFragment for restore intent
    View.OnClickListener SaveListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            setResult(Activity.RESULT_OK, resultIntent);
            // TODO: FIGURE OUT LOGIC
            /*
            SharedPreferences sharedPreferences = context.getSharedPreferences("pay", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("totIn", total.getText().toString());
            editor.commit();
            finish();
            */
            Toast.makeText(context, "Save coming soon!", Toast.LENGTH_LONG).show();
        }
    };

    private void LoadPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pay", MODE_PRIVATE);
        String TOTAL = "butt";
        TOTAL = sharedPreferences.getString("totIn", "NULL");
        Log.i("~~~~~~~~~~~~~~", TOTAL);
        //if (TOTAL != null) total.setText(TOTAL);
        total.setText(TOTAL);
    }

    /*
        Recovers tip info if tip is destroyed
        Tried to use this to pass data with startActivityForResult but
        we didn't read the documentation; if you use finish() it destroys
        the activity and you can't recover the data.
     */
    protected void onSavedInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String totIn = savedInstanceState.getString("tot_IN");
        int seekProg = savedInstanceState.getInt("seek_prog");
        int tipPerc = savedInstanceState.getInt("tip_PRC");
        String tipOut = savedInstanceState.getString("tip_OUT");


        if(!((seekProg < 10 || seekProg > 30) || totIn == null || tipPerc == 0 || tipOut == null)){
            // restores saved stuff
            total.setText(totIn);
            seek.setProgress(seekProg);
            tipStr.setText(tipPerc);
            tipCalc.setText(tipOut);
            Log.i("GGGGG", "On Restore");
        }
    }

    // Recovers information from destroy (same as above)
    protected void onResumeInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String totIn = total.getText().toString();
        outState.putString("tot_IN", totIn);

        int seekProg = seek.getProgress();
        outState.putInt("seek_prog", seekProg);

        String tipTrk = tipStr.getText().toString();
        tipTrk = tipTrk.substring(0, tipStr.getText().toString().length()-1);
        int tipPerc = Integer.parseInt(tipTrk);
        outState.putInt("tip_PRC", tipPerc);

        String tipOut = tipCalc.getText().toString();
        outState.putString("tip_OUT", tipOut);
        Log.i("GGGGG", "On Save");
    }

    // result from the QR code scan above
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(requestCode == 5 && resultCode == RESULT_OK){
            TextView msg = (TextView) findViewById(R.id.tipHeader);
            msg.setText("amount to send");
            total.setFocusable(false);
            seek.setEnabled(false);
            tipCalc.setFocusable(false);
            Button send = (Button) findViewById(R.id.sendButton);
            send.setBackgroundColor(0xFF009688);
            Button split = (Button) findViewById(R.id.splitButton);
            Button save = (Button) findViewById(R.id.saveButton);
            split.setBackgroundColor(0xFF222222);
            save.setBackgroundColor(0xFF009688);

        }else if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                Log.i("Success!", contents.toString());
                makePayment(contents);
            } else {
                Log.i("Failed.", "no result to show");
            }
        }

    }

    // shows alert dialog and makes api call to send money
    public void makePayment (String contents) {
        String recipient = null;
        String email = null;
        String amount = tipCalc.getText().toString();
        try {
            JSONObject userInfoJSON = new JSONObject(contents);
            recipient = userInfoJSON.getString("display_name");
            email = userInfoJSON.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (recipient!=null && email!=null) {
            // clicking OK on the alert dialog will call makeAPIcall below
            AlertDialog alertDialog = createDialog(recipient, email, amount);
            alertDialog.show();
        }
    }

    // Makes the appropriate API call depending on the action
    public void makeAPIcall (String email, String amount, String note) {
        String params[] = {MainActivity.authCode, email, amount, note};
        new MakePayment(context, PayConfigActivity.this).execute(params);
    }

    // Launches split activity upon pressing the split button
    public void splitActivity(View v){
        EditText et = (EditText) findViewById(R.id.tipOut);
        try{
            double total = Double.parseDouble(et.getText().toString());
            if(total > 0.0) {
                Intent intent = new Intent(PayConfigActivity.this, SplitActivity.class);
                intent.putExtra("name", GetMyInfo.DISPLAY_NAME);
                intent.putExtra("profilePic", GetMyInfo.PICTURE);
                intent.putExtra("total", total);
                startActivityForResult(intent,5);
            }else{
                displayToast("I need a total!");
            }
        }catch(NumberFormatException e){
            displayToast("I need a total!");
        }
    }
}
