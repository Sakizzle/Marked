package com.example.saketh.marked;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    DBHandler handler = new DBHandler(this);
    private GoogleMap mMap;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    ToggleButton parkTbt;
    Button bProfile;
    Double myLatitude = null;
    Double myLongitude = null;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    protected static final String TAG = "MapsActvity";
    Marker marker;
    String username;
    private HashMap<Marker, Car> carMarkerMap;
    int carId;

    private MapWrapperLayout mapWrapperLayout;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Get username passed down from loginActivity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        carId = handler.getCarId(username);

        //On click of PROFILE button, open EditProfile and send username
        bProfile = (Button) findViewById(R.id.bProfile);

        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent profileIntent = new Intent(MapsActivity.this, EditProfile.class);
                profileIntent.putExtra("username", username); //pass the username to EditProfile
                MapsActivity.this.startActivity(profileIntent);
            }
        });

        infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.info_window, null);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(15*1000);
        locationRequest.setFastestInterval(5*1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //set to balanced power accuracy on real device



        //add the onClickInfoWindowListener

        /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {



            @Override

            public void onInfoWindowClick(Marker marker) {

                Car car = carMarkerMap.get(marker);

                Toast.makeText(getBaseContext(),

                        car.getMake() + " " + car.getModel(),

                        Toast.LENGTH_LONG).show();



            }

        });*/

        parkTbt = (ToggleButton) findViewById(R.id.tbtPark);
        parkTbt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //If park button is toggled
                    //LatLng myLocation = new LatLng(myLatitude, myLongitude);
                    //marker = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
                    //int carId = handler.getCarId(username);
                    if (carId == -1) { //if car does not exist update profile
                        Toast.makeText(getApplicationContext(), "Please update your profile before proceeding", Toast.LENGTH_LONG).show();
                        isChecked = false;
                    }else { //if profile updated, that means car exists, put marker on map and update car in db
                        LatLng myLocation = new LatLng(myLatitude, myLongitude);
                        marker = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
                        handler.updateCar(carId, myLatitude, myLongitude);
                    }

                } else {
                    //Todo figure out a way to click and drag marker
                    if(marker != null) {
                        marker.remove();
                        handler.updateCar(carId, -1, -1); //Car is not parked, set location to default
                    }
                }

            }
        });

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
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }

        //MapWrapperLayout initialization
        mapWrapperLayout.init(mMap, getPixelsFromDp(MapsActivity.this, 39 + 20));

        //Create association between marker and car using HashMap
        carMarkerMap = new HashMap<Marker, Car>();
        List<Car> carList = new ArrayList<Car>();
        carList = handler.getAllCars();
        int i = 0;
        for (Car car : carList) {
            //if ((car.getLatitude() != -1 || car.getLongitude() != -1) && i != carId) {
            if (car.getLatitude() != -1 || car.getLongitude() != -1) {
                Marker newMarker = placeMarker(car);
                carMarkerMap.put(newMarker, car);
            }
            i++;
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Car car = carMarkerMap.get(marker);
                setInfoWindow(marker,car);
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void requestLocationUpdates() { //Start the process of collecting location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { //permission check
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) { //if client is connected, request location
            requestLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    /*public void parkAll() {
        List<Car> carList = new ArrayList<Car>();
        List<Marker> markers = new ArrayList<Marker>();
        carList = handler.getAllCars();
        int i = 0;
        for (Car car : carList){
            //marker = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
            double lat = car.getLatitude();
            double lng = car.getLongitude();
            LatLng myLocation;
            if (lat != -1 && lng != -1){
                myLocation = new LatLng(lat, lng);
                Marker marker = mMap.addMarker(new MarkerOptions().position(myLocation));
            }

        }

    }*/

    public void parkAll() {
        carMarkerMap = new HashMap<Marker, Car>();
        List<Car> carList = new ArrayList<Car>();
        carList = handler.getAllCars();
        for (Car car : carList) {
            Marker newMarker = placeMarker(car);
            carMarkerMap.put(newMarker, car);
        }
    }

    public Marker placeMarker(Car car) {
        double lat = car.getLatitude();
        double lng = car.getLongitude();
        LatLng carLocation = new LatLng(lat,lng);
        Marker m = mMap.addMarker(new MarkerOptions().position(carLocation));
        return m;
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void setInfoWindow (final Marker marker, final Car car)
            throws NullPointerException {
        if (car.getMake() != null && car.getModel() != null && car.getColour() != null) {
            ((TextView) infoWindow.findViewById(R.id.title))
                    .setText(car.getColour() + " " + car.getMake() + " " + car.getModel());
        }
        if (car.getLicence_plate() != null) {
            ((TextView) infoWindow.findViewById(R.id.snippet))
                    .setText(car.getLicence_plate());
        }

        //handle dispatched touch event for view click
        infoWindow.findViewById(R.id.bAlert).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        //Log.d(TAG,"any_view clicked" );
                        Toast.makeText(getApplicationContext(), "Thank you", Toast.LENGTH_LONG).show();
                        String message = "Your " + car.getMake() + car.getModel() + " is marked.";
                        try {
                            handler.pushFCMNotification(car.getToken(), message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });
    }

}
