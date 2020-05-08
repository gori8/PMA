package rs.ac.uns.ftn.sportly.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.dto.PlaceDTO;
import rs.ac.uns.ftn.sportly.model.Event;
import rs.ac.uns.ftn.sportly.service.GooglePlacesServiceUtils;
import rs.ac.uns.ftn.sportly.ui.dialogs.LocationDialog;
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;
import rs.ac.uns.ftn.sportly.ui.event.create_event.CreateEventActivity;
import rs.ac.uns.ftn.sportly.ui.friends.FriendsAdapter;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private SlidingUpPanelLayout slidingPanel;
    private LocationManager locationManager;
    private String provider;
    private SupportMapFragment mMapFragment;
    private AlertDialog dialog;
    private Marker myLoc;
    private GoogleMap map;
    private final String API_KEY_PLACES = "AIzaSyD1xhjBoYoxC_Jz1t7cqlbWV-Q1m0p979Q";
    private String placeName;

    public static MapFragment newInstance() {

        MapFragment mpf = new MapFragment();

        return mpf;
    }

    /**
     * Prilikom kreidanja fragmenta preuzimamo sistemski servis za rad sa lokacijama
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Kada zelmo da dobijamo informacije o lokaciji potrebno je da specificiramo
     * po kom kriterijumu zelimo da dobijamo informacije GSP, MOBILNO(WIFI, MObilni internet), GPS+MOBILNO
     * **/
    private void createMapFragmentAndInflate() {
        if(map==null) {
            //specificiramo krijterijum da dobijamo informacije sa svih izvora
            //ako korisnik to dopusti
            Criteria criteria = new Criteria();

            //sistemskom servisu prosledjujemo taj kriterijum da bi
            //mogli da dobijamo informacje sa tog izvora
            provider = locationManager.getBestProvider(criteria, true);

            //kreiramo novu instancu fragmenta
            mMapFragment = SupportMapFragment.newInstance();

            //i vrsimo zamenu trenutnog prikaza sa prikazom mape
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.map_container, mMapFragment).commit();

            //pozivamo ucitavnje mape.
            //VODITI RACUNA OVO JE ASINHRONA OPERACIJA
            //LOKACIJE MOGU DA SE DOBIJU PRE MAPE I OBRATNO
            mMapFragment.getMapAsync(this);


        }
    }

    private void showLocatonDialog() {
        if (dialog == null) {
            dialog = new LocationDialog(getActivity()).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        createMapFragmentAndInflate();

        slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean wifi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps && !wifi) {
            showLocatonDialog();
        } else {
            if (checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                    locationManager.requestLocationUpdates(provider, 30*1000, 300, this);

                }else if(ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    //Request location updates:
                    locationManager.requestLocationUpdates(provider, 30*1000, 300, this);
                }
            }
        }


        //Dummy Button samo da prikazem Event Overview
        Button eventButton = getActivity().findViewById(R.id.eventButton);

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) getContext();

                Intent intent = new Intent(mainActivity, EventActivity.class);
                intent.putExtra("location", "Đačko igralište");
                intent.putExtra("name", "FTN Friday Basketball");
                intent.putExtra("time", "18:00 - 20:00");
                intent.putExtra("people", "2/6 people");
                intent.putExtra("date", "17/05/2020");
                intent.putExtra("price", "Free");
                intent.putExtra("creator", "Pera Perić");
                intent.putExtra("description", "Basket, petak uveče. Dođite posle predavanja na koju partiju 3 na 3 basketa.");
                intent.putExtra("imageView", R.drawable.djacko);
                intent.putExtra("isCreator",false);
                mainActivity.startActivity(intent);
            }
        });

        Button myEventButton = getActivity().findViewById(R.id.myEventButton);

        myEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) getContext();

                Intent intent = new Intent(mainActivity, EventActivity.class);
                intent.putExtra("location", "Sportski centar Spens");
                intent.putExtra("name", "Mali fudbal za programere");
                intent.putExtra("time", "20:00 - 21:30");
                intent.putExtra("people", "1/10 people");
                intent.putExtra("date", "20/05/2020");
                intent.putExtra("price", "2$");
                intent.putExtra("creator", "Marko Markovic");
                intent.putExtra("description", "Programeri koji zele posle posla da se druze uz mali fudbal su dobrodosli.");
                intent.putExtra("imageView", R.drawable.spens);
                intent.putExtra("isCreator",true);
                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_map, vg, false);
        slidingPanel = view.findViewById(R.id.sliding_layout);
        return view;
    }

    /**
     * Svaki put kada uredjaj dobijee novu informaciju o lokaciji ova metoda se poziva
     * i prosledjuje joj se nova informacija o kordinatamad
     * */
    @Override
    public void onLocationChanged(Location location) {
        //todo Kad odes u Novi Sad majmune
       if (map != null) {
            //addMarker(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Allow user location")
                        .setMessage("To continue working we need your locations....Allow now?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 30*1000, 300, this);
                    }

                } else if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 30*1000, 300, this);
                    }

                }
                return;
            }

        }
    }


    private String locationToString(LatLng loc){
        return loc.latitude+","+loc.longitude;
    }

    /**
     * KAda je mapa spremna mozemo da radimo sa njom.
     * Mozemo reagovati na razne dogadjaje dodavanje markera, pomeranje markera,klik na mapu,...
     * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Location location = null;

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                location = locationManager.getLastKnownLocation(provider);
            }else if(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                //Request location updates:
                location = locationManager.getLastKnownLocation(provider);
            }
        }

        LatLng latLngForSearch = new LatLng(45.253513,19.829127);

        location.setLatitude(latLngForSearch.latitude);
        location.setLongitude(latLngForSearch.longitude);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


        Call<ResponseBody> call = GooglePlacesServiceUtils.placesService.search(API_KEY_PLACES,locationToString(latLngForSearch),10000L,"basketball court");

        call.enqueue(new Callback<ResponseBody>() {
            @SneakyThrows
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200){
                    JsonNode responseJSON = objectMapper.readTree(response.body().string());
                    JsonNode resultsListJSON = responseJSON.get("results");
                    ArrayList<PlaceDTO> placesList = objectMapper.readValue(resultsListJSON.toString(), new TypeReference<ArrayList<PlaceDTO>>() {});
                    Log.i("MapFragment","***** LISTA SPISAK LISTA SPISAK *****");
                    for (PlaceDTO place : placesList) {
                        Marker marker = addMarker(new LatLng(place.getGeometry().getLocation().getLat(),place.getGeometry().getLocation().getLng()),place.getName(),BitmapDescriptorFactory.HUE_RED);
                        marker.setTag(place);
                    }
                }else{
                    Log.e("MapFragment","Meesage recieved: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MapFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });




        //ako zelmo da reagujemo na klik markera koristimo marker click listener
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getId()!=myLoc.getId()) {
                    PlaceDTO placeDTO = (PlaceDTO) marker.getTag();
                    TextView tvPlaceName = getView().findViewById(R.id.place_info_name);
                    tvPlaceName.setText(placeDTO.getName());
                    placeName = placeDTO.getName();
                    slidingPanel.setAnchorPoint(0.5f);
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
                return true;
            }
        });



        if (location != null) {
            if (myLoc != null) {
                myLoc.remove();
            }
            LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
            myLoc = addMarker(loc,"My position", BitmapDescriptorFactory.HUE_BLUE);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(loc).zoom(14).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private Marker addMarker(LatLng loc, String title, float color) {

        Marker ret = map.addMarker(new MarkerOptions()
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(color))
                .position(loc));
        ret.setFlat(true);

        return ret;
    }

    /**
     *
     * Rad sa lokacja izuzetno trosi bateriju.Obavezno osloboditi kada vise ne koristmo
     * */
    @Override
    public void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Event> events = new ArrayList<>();

        Event event1 = new Event();
        event1.setName("FTN Friday Basketball");
        event1.setDescription("Basket, petak uveče. Dođite posle predavanja na koju partiju 3 na 3 basketa.");
        event1.setFrom("18:00");
        event1.setTo("20:00");
        event1.setSignedUpPlayers((short)2);
        event1.setTotalPlayers((short)6);
        event1.setSport("basketball");

        Event event2 = new Event();
        event2.setName("Mali fudbal za programere");
        event2.setDescription("Programeri koji zele posle posla da se druze uz mali fudbal su dobrodosli.");
        event2.setFrom("20:00");
        event2.setTo("21:30");
        event2.setSignedUpPlayers((short)1);
        event2.setTotalPlayers((short)10);
        event2.setSport("football");

        events.add(event1);
        events.add(event2);

        EventsAdapter adapter = new EventsAdapter(getContext(), events);

        ListView listView = (ListView) getActivity().findViewById(R.id.events_list);
        listView.setAdapter(adapter);

        Button createButton = getActivity().findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) getContext();

                Intent intent = new Intent(mainActivity, CreateEventActivity.class);
                intent.putExtra("location", placeName);

                mainActivity.startActivity(intent);
            }
        });
    }
}
