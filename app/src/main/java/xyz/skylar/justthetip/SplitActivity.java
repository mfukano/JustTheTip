package xyz.skylar.justthetip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.List;


public class SplitActivity extends ActionBarActivity {
    private MyAdapter aa;
    private ArrayList<ListElement> aList;

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
        addSomeone(name,profilePic);
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

    private class ListElement {
        ListElement() {};
        public String textLabel;
        public Bitmap profilePic;
        public float amount;
        public int slider = 0;
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
            LinearLayout newView;
            final ListElement le = getItem(position);
            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(resource,  newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }
            // Fills in the message.
            TextView mtv = (TextView) newView.findViewById(R.id.testText);
            mtv.setText(le.textLabel);
            ImageView iv = (ImageView) newView.findViewById(R.id.profilePic);

            if(le.profilePic == null){
                Log.i("","~~~~~before set profile pic is null");
            }else{
                Log.i("","~~~~~before set Pic is not null");
            }
            iv.setImageBitmap(le.profilePic);
            final SeekBar msb = (SeekBar) newView.findViewById(R.id.splitSeekBar);
            msb.setProgress(le.slider);
            msb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int prog = 0;
                boolean status;
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    prog = progress;
                    msb.setProgress(prog);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Log.d("onStopTrackingTouch", "prog: " + prog);
                    le.slider = prog;
                    msb.setProgress(prog);
                }
            });
            return newView;
        }
    }
    public void clickButton(View v){
        IntentIntegrator integrator = new IntentIntegrator(SplitActivity.this);
        integrator.initiateScan();
    }

    private void addSomeone(String text, Bitmap profilePic) {
        if(profilePic == null){
            Log.i("","~~~~~profile pic is null");
        }else{
            Log.i("","~~~~~Pic is not null");
        }
        ListElement ael = new ListElement();
        ael.textLabel = text;
        ael.amount = (float) 20.0;
        ael.profilePic = profilePic;
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
                String recipient = null;
                String url = null;
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
            URL PhotoURL = null;
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
            if(profilePic == null){
                Log.i("","~~~~~profile pic is null");
            }else{
                Log.i("","~~~~~Pic is not null");
            }
            return profilePic;
        }
        public void onPostExecute (Bitmap profilePic) {
            if(profilePic == null){
                Log.i("","~~~~~profile pic is null");
            }else{
                Log.i("","~~~~~Pic is not null");
            }
            addSomeone(name,profilePic);
        }
    }
}
