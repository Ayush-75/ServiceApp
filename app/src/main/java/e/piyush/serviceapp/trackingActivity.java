package e.piyush.serviceapp;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import e.piyush.serviceapp.Model.MyLocation;
import e.piyush.serviceapp.utils.Common;

public class trackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    DatabaseReference trackingUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerEventRealtime();
    }

    private void registerEventRealtime() {
        trackingUserLocation= FirebaseDatabase.getInstance()
                .getReference(Common.PUBLIC_LOCATION)
                .child(Common.trackingUser.getUid());
        trackingUserLocation.addValueEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackingUserLocation.addValueEventListener(this);

    }

    @Override
    protected void onStop() {
        trackingUserLocation.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       //enable zoom ui
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //set skin for map

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() !=null)
        {
            MyLocation location =dataSnapshot.getValue(MyLocation.class);

            //marker
            LatLng userMarker = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userMarker)
            .title(Common.trackingUser.getEmail())
            .snippet(Common.getDateFormatted(Common.convertTimeStampsToDate(location.getTime()))));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker,16f));
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
