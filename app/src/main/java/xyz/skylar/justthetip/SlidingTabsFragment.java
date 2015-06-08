package xyz.skylar.justthetip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by Skylar on 5/28/2015.
 */
public class SlidingTabsFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsFragment";

    static final int NO_SPLIT_REQUEST = 2;
    static final int SPLIT_REQUEST = 3;

    Context context;

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabsLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabsLayout} above.
     */
    private ViewPager mViewPager;

    // this makes it so we can display the return result of the QR scan in the fragment
    public final class FragmentIntegrator extends IntentIntegrator {
        private final Fragment fragment;

        public FragmentIntegrator(Fragment fragment) {
            super(fragment.getActivity());
            this.fragment = fragment;
            context = fragment.getActivity().getApplicationContext();
        }

        @Override
        protected void startActivityForResult(Intent intent, int code) {
            fragment.startActivityForResult(intent, code);
        }
    }

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabsLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(1);
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabsLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabsLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabsLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position + 1){
                case 1:
                    return "tag";
                case 2:
                    return "tip";
                case 3:
                    return "you";
            }
            return "err";
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // if on the 'tag' tab (QR code stuff)
            View view;
            String currPage = getPageTitle(position).toString();
            switch (currPage) {
                case "tag":
                    view = getActivity().getLayoutInflater().inflate(R.layout.qr_tag,
                            container, false);
                    container.addView(view);

                    Button btn = (Button) view.findViewById(R.id.login);
                    ImageView qrImage = (ImageView) view.findViewById(R.id.qrCode);
                    qrImage.setVisibility(View.INVISIBLE);

                    if (MainActivity.authCode != null) {
                        String qr_data = GetMyInfo.userInfo;
                        int qr_dimension = 500;
                        //Log.i ("QR DATA", qr_data);

                        QRCodeGen qrCodeGen = new QRCodeGen(qr_data, null, Contents.Type.TEXT,
                                BarcodeFormat.QR_CODE.toString(), qr_dimension);

                        // create and display the QR bitmap
                        try {
                            Bitmap qr_bitmap = qrCodeGen.encodeAsBitmap();
                            qrImage.setImageBitmap(qr_bitmap);
                            btn.setVisibility(View.GONE);
                            qrImage.setVisibility(View.VISIBLE);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    }/* else {
                        tv.setText("Please log in to Venmo to get your QR code.");
                    }*/

                    return view;

                case "tip":
                    view = getActivity().getLayoutInflater().inflate(R.layout.tip,
                            container, false);
                    container.addView(view);

                    Button newTip = (Button) view.findViewById(R.id.newTip);
                    newTip.setOnClickListener(NewTipListener);

                    return view;

                case "you":
                    view = getActivity().getLayoutInflater().inflate(R.layout.activity_you,
                            container, false);
                    container.addView(view);
                    TextView tv = (TextView) view.findViewById(R.id.textView);
                    ImageView profilePic = (ImageView) view.findViewById(R.id.profilePic);

                    if (MainActivity.authCode != null && GetMyInfo.userInfo != null) {
                        String usr_data = GetMyInfo.userInfo;

                        profilePic.setImageBitmap(GetMyInfo.PICTURE);
                        tv.setText(GetMyInfo.DISPLAY_NAME);
                    }
                    return view;
                default:
                    // Inflate a new layout from our resources
                    view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                            container, false);
                    // Add the newly created View to the ViewPager
                    container.addView(view);

                    // Retrieve a TextView from the inflated View, and update it's text
                    TextView title = (TextView) view.findViewById(R.id.item_title);
                    title.setText(String.valueOf(position + 1));

                    // Return the View
                    return view;
            }
        }

        View.OnClickListener NewTipListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.authCode != null) {
                    Intent intent = new Intent(getActivity(), PayConfigActivity.class);
                    startActivity(intent);
                } else {
                    mViewPager.setCurrentItem(0);
                }
            }
        };

         View.OnClickListener tipForResult = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, PayConfigActivity.class);
                startActivityForResult(intent, NO_SPLIT_REQUEST);
            }
        };

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
