package com.example.ramitix.locations;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, LocationListener {

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 3;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 4;
    private static final int MY_PERMISSIONS_INTERNET = 5;
    private GoogleMap mMap;
    //    Tracker gpsTracker;
    private List<Marker> originMarker = new ArrayList<>();
    private List<Marker> destinationMarker = new ArrayList<>();
    private List<Polyline> polylinePath = new ArrayList<>();
    private ProgressDialog progressDialog;
    private int NOTIFICATION_ID = 1001;
    private Notification.Builder mBuilder;
    private double lat = 0;
    private double lon = 0;
    private String distance;
    private int timeValue = 0;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );


        ((NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE )).cancelAll();

        int time = getIntent().getIntExtra( "time", 0 );
        if (time == 0) {
            timeValue = 30;
        } else if (time == 1) {
            timeValue = 60;
        } else if (time == 2) {
            timeValue = 300;
        } else if (time == 3) {
            timeValue = 600;
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        MapActivity.this.finish();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            return;
        }

        locationManager.removeUpdates( MapActivity.this );
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.getUiSettings().setZoomControlsEnabled(true);
        myLocation();
        findRoute();

    }

    public void notifyMe() {


        // 2. Create a notification
        mBuilder = new Notification.Builder(this);

        // 3. Set the text (first row) of the notification
        mBuilder.setContentTitle("WhereAreYou");


        // 4. Set icon for notification.
        mBuilder.setSmallIcon(R.drawable.pin);

        //5. We also want our application to open when the notification is clicked. For that we create a PendingIntent ( like we did for Alarm example)
        Intent resultIntent = new Intent(this, MapActivity.class);

        PendingIntent pendingIntentToOpenApp = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Supply the PendingIntent to send when the notification is clicked.
        mBuilder.setContentIntent(pendingIntentToOpenApp);

        // 6. Set the vibration pattern to use.
        //mBuilder.setVibrate(new long[]{0, 500, 500, 500, 500, 500, 500});

        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setColor(Color.parseColor("#04a0d9"));

        //7. Set a notification sound for the notification
        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Set the text (second row) of the notification
        mBuilder.setContentText("Your friend is " + distance + " from your location ");

        //9. Trigger the notification
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


    }


    private void locationCoords(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
    }

    private void findRoute() {
        String origin = lat + "," + lon;//"40.782,-73.9854";    //gpsTracker.getLatitude()+","+gpsTracker.getLongitude() ;
        String destination = "40.742,-73.9834";
        if (origin.equals(null)) {
            Toast.makeText(this, "No origin", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding Location", true);

        if (originMarker != null) {
            for (Marker marker : originMarker) {
                marker.remove();
            }
        }

        if (destinationMarker != null) {
            for (Marker marker : destinationMarker) {
                marker.remove();
            }
        }

        if (polylinePath != null) {
            for (Polyline polyline : polylinePath) {
                polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePath = new ArrayList<>();
        originMarker = new ArrayList<>();
        destinationMarker = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.distance)).setText(route.distance.text);
            distance = route.distance.text;
            originMarker.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    .title("Your location: " + route.startAddress)
                    .position(route.startLocation)));
            destinationMarker.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2))
                    .title("Your friend: " + route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePath.add(mMap.addPolyline(polylineOptions));

            Toast.makeText(this, "Your friend is " + route.distance.text + " from your location", Toast.LENGTH_LONG).show();
            notifyMe();
        }


    }


    @Override
    public void onLocationChanged(Location location) {
        locationCoords(location);
        findRoute();
        Toast.makeText(getApplicationContext(), "---GPS---\n"+"lat: "+lat+"\nlon: "+lon, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu for enable GPS?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                alertDialog.getContext().startActivity(intent);

            }


        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    private void myLocation() {


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.INTERNET},
                        MY_PERMISSIONS_INTERNET);


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_ACCESS_FINE_LOCATION);

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,timeValue*1000,0,this);
        if (locationManager != null) {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationCoords(location);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,timeValue*1000,0,this);
        if (locationManager != null) {

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationCoords(location);


        }




    }


    }

