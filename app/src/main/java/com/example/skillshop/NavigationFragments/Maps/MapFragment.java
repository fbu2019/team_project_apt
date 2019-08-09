package com.example.skillshop.NavigationFragments.Maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skillshop.Adapters.CustomWindowAdapter;
import com.example.skillshop.ClassDescription.ClassDetailsActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment{

    protected ArrayList<Workshop> mWorkshops;
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SupportMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear(); //clear old markers
                setupCameraPosition();
                setupInfoWindow();
                getClasses(mMap);
            }
        });
        return rootView;
    }

    private void setupInfoWindow() {
        mMap.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater(), getContext()));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Workshop workshop = (Workshop) marker.getTag();
                final Intent profileDetailsIntent = new Intent(getContext(), ClassDetailsActivity.class);
                //pass in class that was selected
                profileDetailsIntent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(workshop));
                startActivity(profileDetailsIntent);
            }
        });
    }

    private void setupCameraPosition() {
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(37.4530,-122.1817))
                .zoom(10)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void getClasses(GoogleMap mMap)
    {
        mWorkshops = new ArrayList<>();
        Query parseQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        parseQuery.getAllClasses().withItems();

        parseQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Workshop workshopItem = objects.get(i);
                        mWorkshops.add(workshopItem);

                        //Creates point from workshop location to be used for the position of the marker
                        ParseGeoPoint workshopLocation = workshopItem.getLocation();
                        double lat = workshopLocation.getLatitude();
                        double lon = workshopLocation.getLongitude();
                        LatLng point = new LatLng(lat, lon);


                        //sets the default marker color
                        BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
                        //sets the marker color based on category
                        String category = workshopItem.getCategory();
                        defaultMarker = setMarkerColor(defaultMarker, category);

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(point)
                                .icon(defaultMarker)
                                .title(workshopItem.getObjectId())
                        );

                        //adds each workshop item as a tag to be passed to the class details activity when the info window is pressed
                        marker.setTag(workshopItem);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }


    private BitmapDescriptor setMarkerColor(BitmapDescriptor marker, String category) {
        switch (category){
            case ("Culinary"): {
                marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                break;
            }
            case ("Education"): {
                marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                break;
            }
            case ("Fitness"): {
                marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                break;
            }
            case ("Arts/Crafts"): {
                marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                break;
            }
            case ("Other"): {
                marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                break;
            }
            default: {
                marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
                break;
            }
        }
        return marker;
    }

}
