package com.ghp55.eli.ghp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;


import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class ControlActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    Context ctx;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;

    public PiInterface piInterface;

    public void stop(View v){
        Button but = ((Button)v);
        if(but.getText().equals("Stop")){
        but.setText("Start");
        piInterface.stop();
        }else{
            but.setText("Stop");
            piInterface.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ctx = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        PowerManager.WakeLock wl = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK, "GHDrive");
        wl.acquire();

        piInterface = new PiInterface();

    }

    public void setVisible(boolean visible){
        AppBarLayout appBar = (AppBarLayout)findViewById(R.id.appbar);
        if(visible){
            appBar.setVisibility(View.GONE);
        }else{
            appBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_connect:
                if (!piInterface.isConnected()) {
                    piInterface.openConnection();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (piInterface.isConnected()) {
                                item.setTitle("Disconnect");
                                accellerometerControl = new AccellerometerControl(ctx, piInterface);
                                final VideoView vv = (VideoView)findViewById(R.id.videoFeed);
                                vv.setVisibility(View.VISIBLE);
                                vv.setVideoURI(Uri.parse("http://"+piInterface.piIp+":8090"));
                                Toast.makeText(ctx, "http://"+piInterface.piIp+":8090", Toast.LENGTH_SHORT).show();
                                vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        vv.start();
                                    }
                                });
                            } else {
                                Toast.makeText(ctx, "Failed to Connect after 2 seconds.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 2000);
                } else {
                    //disconnect
                    piInterface.closeConnection();
                    item.setTitle("Connect");
                }
        }
        return super.onOptionsItemSelected(item);
    }

    AccellerometerControl accellerometerControl;


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        final double[] coords = {34.287051, -85.190125};
        Vibrator vibrator;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
        private Bundle savedInstanceState;
        private View root;
        private Context ctx;
        ControlActivity activity;
        MapView mapView;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ctx = this.getContext();
            vibrator = (Vibrator)ctx.getSystemService(Context.VIBRATOR_SERVICE);
            activity = (ControlActivity)getActivity();
            this.savedInstanceState = savedInstanceState;
            int page = getArguments().getInt(ARG_SECTION_NUMBER);
            int layout = R.layout.manual_control;
            if(page == 2){
                layout = R.layout.autonomous_control;
                root = inflater.inflate(layout, container, false);
                handleAutonomousLayout(root);
            }else {
                root = inflater.inflate(layout, container, false);
                handleManualLayout(root);
            }
            handleBothLayouts(root);
            return root;
        }
        boolean fullscreen = false;
        boolean videoViewIsTouched = false;

        public void handleBothLayouts(View _root){
            VideoView v = (VideoView)_root.findViewById(R.id.videoFeed);
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(!videoViewIsTouched){
                        videoViewIsTouched = true;
                        fullscreen = !fullscreen;
                        // Toast.makeText(ctx, "Click", Toast.LENGTH_SHORT).show();
                        setFullscreen(fullscreen);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                videoViewIsTouched = false;
                            }
                        }, 100);
                    }
                    return false;
                }
            });

            //Camera Controll
            OnScreenJoystick joystick = (OnScreenJoystick)_root.findViewById(R.id.cameraControl);
            joystick.setOnSlideListener(new OnScreenJoystick.OnSlideListener() {
                @Override
                public void onSlide(int x, int y) {
                    //System.out.println(x/12+","+y/12);
                    //Log.v("GHP", x/12+","+y/12);
                    try{
                        activity.piInterface.lookLeftRight(x/12);
                        activity.piInterface.lookUpDown(y/12);
                    }catch (Exception e){
                        Toast.makeText(ctx, "There was an error talking with the PI", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

        }

        public void handleManualLayout(View root){

        }

        public void handleAutonomousLayout(final View root){

            final Button startButt = (Button)root.findViewById(R.id.startRouteButton);
            startButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final VideoView vv = (VideoView)root.findViewById(R.id.videoFeed);
                    final OnScreenJoystick camControl = (OnScreenJoystick)root.findViewById(R.id.cameraControl);
                    if(startButt.getText().equals("Start Route")) {
                        Toast.makeText(ctx, "Set Destination", Toast.LENGTH_SHORT).show();
                        activity.piInterface.startNavigationToCoordinates(new LatLng(coords[0], coords[1]));
                        startButt.setText("Change Route");
                        //show video view and controller
                        mapView.setVisibility(View.GONE);
                        camControl.setVisibility(View.VISIBLE);
                        streamVideo(vv);
                    }else{
                        startButt.setText("Start Route");
                        vv.setVisibility(View.GONE);
                        camControl.setVisibility(View.GONE);
                        mapView.setVisibility(View.VISIBLE);
                    }

                }
            });
            //location permissions

            //final double[] coords = {0.0, 0.0};
            mapView = (MapView)root.findViewById(R.id.mapView);
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(activity, permissions, 0);
            }
            else{
                //location stuff
                GPSTracker gps = new GPSTracker(ctx);
                coords[0] = gps.getLatitude();
                coords[1] = gps.getLongitude();
                Toast.makeText(ctx, coords[0]+", "+coords[1], Toast.LENGTH_SHORT).show();
            }

            //map stuff

            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            }

            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gmap) {
                    Toast.makeText(ctx, "Map Ready", Toast.LENGTH_SHORT).show();
                    gmap.setMinZoomPreference(15);
                    LatLng ny = new LatLng(coords[0], coords[1]);
                    gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
                    //make marker
                    MarkerOptions opts = new MarkerOptions();
                    opts.position(ny);
                    opts.title("Deliver Here");
                    opts.draggable(true);
                    gmap.addMarker(opts);
                    gmap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            vibrator.vibrate(100);
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            coords[0] = marker.getPosition().latitude;
                            coords[1] = marker.getPosition().longitude;
                            vibrator.vibrate(50);
                        }
                    });

                    //ui stuffs
                    gmap.setIndoorEnabled(true);
                    UiSettings uiSettings = gmap.getUiSettings();
                    uiSettings.setIndoorLevelPickerEnabled(true);
                    uiSettings.setMyLocationButtonEnabled(true);
                    uiSettings.setMapToolbarEnabled(true);
                    uiSettings.setCompassEnabled(true);
                    uiSettings.setZoomControlsEnabled(true);
                }
            });

        }

        void streamVideo(final VideoView vv){

            final MediaPlayer mp = new MediaPlayer();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(1.2f));
            }
            vv.setVisibility(View.VISIBLE);
            vv.setVideoURI(Uri.parse("http://"+PiInterface.piIp+":8090"));
            vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    vv.start();
                }
            });
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
            if (mapViewBundle == null) {
                mapViewBundle = new Bundle();
                outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
            }
            if(mapView != null)
                mapView.onSaveInstanceState(mapViewBundle);
        }
        @Override
        public void onResume() {
            super.onResume();
            if(mapView!=null)
                mapView.onResume();
        }

        @Override
        public void onStart() {
            super.onStart();
           // mapView.onStart();
        }

        @Override
        public void onStop() {
            super.onStop();
            //mapView.onStop();
        }
        @Override
        public void onPause() {
            if(mapView != null)
                mapView.onPause();
            super.onPause();
        }
        @Override
        public void onDestroy() {
            if(mapView != null)
                mapView.onDestroy();
            super.onDestroy();
        }
        @Override
        public void onLowMemory() {
            super.onLowMemory();
            if(mapView != null)
                mapView.onLowMemory();
        }

        @Override
        @SuppressWarnings({"MissingPermission"})
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleAutonomousLayout(root);
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        public void setFullscreen(boolean fullscreen)
        {
            //get rid of appbar
            //((ControlActivity)getActivity()).setVisible(fullscreen);
            RelativeLayout.LayoutParams params;
            if(fullscreen){
                root.findViewById(R.id.cameraControl).setVisibility(View.INVISIBLE);
                root.findViewById(R.id.stopButton).setVisibility(View.INVISIBLE);
                View startRouteButton = root.findViewById(R.id.startRouteButton);//only in autonomous root view
                if(startRouteButton!=null){
                    startRouteButton.setVisibility(View.INVISIBLE);
                }
                //adjust player size
               /* params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ((RelativeLayout)root).setLayoutParams(params);*/
            }
            else{
                root.findViewById(R.id.cameraControl).setVisibility(View.VISIBLE);
                root.findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
                View startRouteButton = root.findViewById(R.id.startRouteButton);//only in autonomous root view
                if(startRouteButton!=null){
                    startRouteButton.setVisibility(View.VISIBLE);
                }
                //root.getLayoutParams().height = 400;
            }
        }

    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Manual";
                case 1:
                    return "Autonomous";
            }
            return null;
        }
    }
}
