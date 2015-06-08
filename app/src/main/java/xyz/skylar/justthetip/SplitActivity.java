package xyz.skylar.justthetip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class SplitActivity extends ActivityBase {
    private MyAdapter aa;
    private ArrayList<ListElement> aList;
    private Double total;
    private Double remainingAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        aList = new ArrayList<ListElement>();
        aa = new MyAdapter(this, R.layout.split_element, aList);
        ListView myListView = (ListView) findViewById(R.id.listView);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        Bitmap profilePic = intent.getParcelableExtra("profilePic");
        total = intent.getExtras().getDouble("total");
        remainingAmount = total;
        TextView tipTotal = (TextView) findViewById(R.id.tipTotal);
        tipTotal.setText("tip total: " + String.format("$%.2f",total));
        TextView remaining = (TextView) findViewById(R.id.remaining);
        DecimalFormat df = new DecimalFormat("0.00");
        remaining.setText("remaining: "+String.format("$%.2f",total));
        Button evenSplit = (Button) findViewById(R.id.evenSplit);
        evenSplit.setOnClickListener(EvenSplitListener);
        addSomeone(name, profilePic, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_split, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // split the total evenly among the people
    View.OnClickListener EvenSplitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // iterate aList
            int size = aList.size();
            int percentage = 100 / size;
            for (ListElement listElement : aList) {
                listElement.msb.setProgress(percentage);
            }
        }
    };

    private class ListElement {
        ListElement() {}
        public String textLabel;
        public Bitmap profilePic;
        public double amount;
        public int percent = 0;
        public SeekBar msb;
        public boolean isPrimary;
        public int lastPositive;
    }


    private class MyAdapter extends ArrayAdapter<ListElement> {

        int resource;
        Context context;

        public MyAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
            this.context = _context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LinearLayout newView;
            final ListElement le = getItem(position);
            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(resource, newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }
            // Fills in the message.
            TextView mtv = (TextView) newView.findViewById(R.id.testText);
            mtv.setText(le.textLabel);
            ImageView iv = (ImageView) newView.findViewById(R.id.profilePic);
            iv.setImageBitmap(le.profilePic);
            Button mb = (Button) newView.findViewById(R.id.removeSplitter);
            mb.setVisibility(newView.INVISIBLE);
            if (!le.isPrimary){
                mb.setVisibility(newView.VISIBLE);
                mb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeSplitter(le.textLabel);
                    }
                });
            }
            le.msb = (SeekBar) newView.findViewById(R.id.splitSeekBar);
            le.msb.setProgress(le.percent);
            le.msb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int prog = 0;
                boolean status;
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //Log.d("onProgressChanged", "prog: " + prog);
                    //updateTotal();
                    prog = progress;
                    le.amount = 0.0;
                    if(progress > 0){
                        double result = roundUp((total * (progress / 100.0)));
                        le.amount = result;
                        TextView amt = (TextView) newView.findViewById(R.id.amount);
                        amt.setText(String.format("$%.2f", result));
                        Log.d("onProgressChanged", "set amount to: " + result);
                    }
                    le.percent = prog;
                    le.msb.setProgress(prog);
                    updateTotal();
                    if(remainingAmount >= 0.0){
                        le.lastPositive=progress;
                    }
                    TextView amount = (TextView) newView.findViewById(R.id.amount);
                    amount.setText(String.format("$%.2f", le.amount));

                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    updateTotal();
                    Log.d("onStopTrackingTouch", "prog: " + prog);
                    le.amount = 0.0;
                    if(true) {
                        double result = roundUp((total * (prog / 100.0)));
                        le.amount = result;
                        Log.d("onStopTrackingTouch", "set amount to: " + result);
                    }
                    le.percent = prog;
                    updateTotal();
                    if(remainingAmount >= 0.0){
                        le.lastPositive=prog;
                    }
                    le.msb.setProgress(le.lastPositive);
                    TextView amount = (TextView) newView.findViewById(R.id.amount);
                    amount.setText(String.format("$%.2f", le.amount));
                }
            });
            TextView amount = (TextView) newView.findViewById(R.id.amount);
            amount.setText(String.format("$%.2f", le.amount));
            return newView;
        }
    }

    private double roundUp(double d) {
        double result = (double)Math.round(d * 100) / 100;
        //Log.d("roundUp","rounded " + d +" to " + result);
        return result;

    }

    private void removeSplitter(String name) {
        int count = aa.getCount();
        for (int j = 0; j < count; j++) {
            String label;
            try{
                label = aa.getItem(j).textLabel;
                if(label.equals(name)) {
                    aa.remove(aa.getItem(j));
                    aa.notifyDataSetChanged();
                    ListView lv = (ListView) findViewById(R.id.listView);
                    lv.setAdapter(aa);
                    lv.invalidateViews();
                }
            }catch(IndexOutOfBoundsException e) {
                Log.i("","Out of bounds: " + j);
            }
        }
        updateTotal();
    }

    public void clickButton(View v){
        IntentIntegrator integrator = new IntentIntegrator(SplitActivity.this);
        integrator.initiateScan();
    }

    private void addSomeone(String text, Bitmap profilePic, boolean isPrimary) {
        ListElement ael = new ListElement();
        ael.textLabel = text;
        ael.amount = (float) 0.0;
        ael.profilePic = profilePic;
        ael.isPrimary = isPrimary;
        aList.add(ael);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(aa);
        aa.notifyDataSetChanged();
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                Log.i("Success!", contents.toString());
                // set recipient
                String recipient;
                String url;
                try {
                    JSONObject userInfoJSON = new JSONObject(contents);
                    recipient = userInfoJSON.getString("display_name");
                    url = userInfoJSON.getString("pic_url");
                    new GetProfilePic(recipient).execute(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Failed.", "no result to show");
            }
        }
    }
    private class GetProfilePic extends AsyncTask<String, Void, Bitmap> {
        public Bitmap profilePic = null;
        public String name;
        public GetProfilePic(String recipient){
            name = recipient;
        }
        protected Bitmap doInBackground (String... url) {
            URL PhotoURL;
            try {
                PhotoURL = new URL(url[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) PhotoURL.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                profilePic = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return profilePic;
        }
        public void onPostExecute (Bitmap profilePic) {
            addSomeone(name,profilePic,false);
        }
    }
    private void updateTotal(){
        int count = aa.getCount();
        double split = 0.0;
        for (int j = 0; j < count; j++) {
            double percent;
            double amount;
            try{
                DecimalFormat df = new DecimalFormat("0.00");
                //percent = aa.getItem(j).percent;
                //aa.getItem(j).amount = (total*(percent/100));
                amount = aa.getItem(j).amount;
                split += amount;
                //Log.d("updateTotal", "amount: " + amount);
                split = roundUp(split);
                remainingAmount = roundUp(total - split);
                /*if(remainingAmount < 0.0) {
                    aa.getItem(j).amount += remainingAmount;
                    remainingAmount = 0.0;
                    updateTotal();
                }*/
                TextView rm = (TextView) findViewById(R.id.remaining);
                rm.setText("remaining: $" + df.format(remainingAmount));
                //aa.notifyDataSetChanged();
                //ListView lv = (ListView) findViewById(R.id.listView);
                //lv.invalidateViews();

            }catch(IndexOutOfBoundsException e) {
                Log.i("","Out of bounds: " + j);
            }
        }
    }
}
