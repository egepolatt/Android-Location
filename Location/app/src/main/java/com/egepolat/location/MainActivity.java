package com.egepolat.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Test
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ArrayList<BaseContract> mCityList;
    private ArrayList<BaseContract> mIlceList;
    private FupsAdapter mIlceAdapter;
    private FupsAdapter mCityAdapter;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private Marker markerCenter;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    //private ActivityMainBinding binding;
    TextView enlem, boylam, ulke, postkod,adres;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //val layoutInflater = parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // val bind: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.spinner_item, parent, false)
        // LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // binding= DataBindingUtil.inflate(layoutInflater, R.layout.spinner_item, null, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        enlem = findViewById(R.id.text_enlem);
        boylam = findViewById(R.id.text_boylam);
        ulke = findViewById(R.id.text_ulke);
        postkod = findViewById(R.id.text_postkod);
        adres = findViewById(R.id.text_adres);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initList();
        Spinner spinnerCity= findViewById(R.id.spinCity);
        mCityAdapter = new FupsAdapter(mCityList);
        spinnerCity.setAdapter(mCityAdapter);
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AdminArea clickedItem = (AdminArea) parent.getItemAtPosition(position);
                String clickedCityName = clickedItem.getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Spinner spinnerIlce = findViewById(R.id.spinIlce);
        mIlceAdapter = new FupsAdapter(mIlceList);
        spinnerIlce.setAdapter(mIlceAdapter);
        getFusedLocation();
    }

    private void initList() {
        mCityList = new ArrayList<>();
        mIlceList = new ArrayList<>();
    }

    private void getFusedLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {}
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                LatLng lastUserLocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
            }
        });
    }

    private void fillAddressDetails(LatLng location)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1);
            enlem.setText(Html.fromHtml(
                    "" + addresses.get(0).getLatitude()
            ));
            boylam.setText(Html.fromHtml(
                    "" + addresses.get(0).getLongitude()
            ));
            ulke.setText(Html.fromHtml(
                    "" + addresses.get(0).getCountryName()
            ));
            postkod.setText(Html.fromHtml(
                    "" + addresses.get(0).getPostalCode()
            ));

            mCityAdapter.insertItem(new AdminArea(addresses.get(0).getAdminArea()
            ));

            mIlceAdapter.insertItem(new SubAdminArea(addresses.get(0).getSubAdminArea()
            ));

            adres.setText(Html.fromHtml(
                    "" + addresses.get(0).getAddressLine(0)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        MarkerOptions markerOptions = new MarkerOptions()
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.fups));
        markerOptions.position(mMap.getCameraPosition().target);
        markerCenter = mMap.addMarker(markerOptions);
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                markerCenter.setPosition(mMap.getCameraPosition().target);
                fillAddressDetails(markerCenter.getPosition());
            }
        });
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void save(View view){
        mMap.clear();
        mCityList.clear();
        mIlceList.clear();

        LatLng konum = new LatLng(39.7467116,39.4888058);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    konum.latitude, konum.longitude, 1);
            enlem.setText(Html.fromHtml(
                    "" + addresses.get(0).getLatitude()
            ));
            boylam.setText(Html.fromHtml(
                    "" + addresses.get(0).getLongitude()
            ));
            ulke.setText(Html.fromHtml(
                    "" + addresses.get(0).getCountryName()
            ));
            postkod.setText(Html.fromHtml(
                    "" + addresses.get(0).getPostalCode()
            ));

            mCityAdapter.insertItem(new AdminArea(addresses.get(0).getAdminArea()
            ));

            mIlceAdapter.insertItem(new SubAdminArea(addresses.get(0).getSubAdminArea()
            ));

            adres.setText(Html.fromHtml(
                    "" + addresses.get(0).getAddressLine(0)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konum,15));
            MarkerOptions markerOptions = new MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fups));
            markerOptions.position(mMap.getCameraPosition().target);
            markerCenter = mMap.addMarker(markerOptions);
            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        markerCenter.setPosition(mMap.getCameraPosition().target);
                        fillAddressDetails(markerCenter.getPosition());
                    }
                });
    }
}