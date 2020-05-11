package rs.ac.uns.ftn.sportly.ui.map;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
import rs.ac.uns.ftn.sportly.ui.adapters.EventsAdapter;
import rs.ac.uns.ftn.sportly.ui.dialogs.LocationDialog;
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;
import rs.ac.uns.ftn.sportly.ui.event.create_event.CreateEventActivity;

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

    private void updateDateLabel(TextView edittext, Calendar myCalendar){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("sr","RS"));
        edittext.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });

        setUpDatePicker();

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

    private void setUpDatePicker(){

        final Calendar myCalendar = Calendar.getInstance();

        TextView edittext= (TextView) getView().findViewById(R.id.events_date_filter);

        updateDateLabel(edittext,myCalendar);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(edittext,myCalendar);
            }

        };

        edittext.setOnClickListener(v -> {

            new DatePickerDialog(getActivity(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        });

        ImageView calendarImage = (ImageView) getView().findViewById(R.id.calendar_image);

        calendarImage.setOnClickListener(v -> {

            new DatePickerDialog(getActivity(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        });

    }

    @Override
    public void onResume() {
        super.onResume();

        createMapFragmentAndInflate();



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
                        Marker marker = addMarker(new LatLng(place.getGeometry().getLocation().getLat(),place.getGeometry().getLocation().getLng()),place.getName(),bitmapDescriptorFromVector(getActivity(), R.drawable.marker_basketball));
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
                if(marker.getTag()!=null) {
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
            myLoc = addMarker(loc,"My position", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(loc).zoom(14).build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private Marker addMarker(LatLng loc, String title, BitmapDescriptor bitmapDescriptor) {

        Marker ret = map.addMarker(new MarkerOptions()
                .title(title)
                .icon(bitmapDescriptor)
                .position(loc));
        ret.setFlat(true);

        return ret;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
        event1.setLocation("Đačko igralište");
        event1.setName("FTN Friday Basketball");
        event1.setDescription("Basket, petak uveče. Dođite posle predavanja na koju partiju 3 na 3 basketa.");
        event1.setFrom("18:00");
        event1.setTo("20:00");
        event1.setSignedUpPlayers((short)2);
        event1.setTotalPlayers((short)6);
        event1.setSport("basketball");

        Event event2 = new Event();
        event1.setLocation("Đačko igralište");
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

        listView.setOnItemClickListener((parent, view, position, id) -> {

            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy");

            Event event = (Event) listView.getAdapter().getItem(position);

            MainActivity mainActivity = (MainActivity) getContext();

            Intent intent = new Intent(mainActivity, EventActivity.class);
            intent.putExtra("location", placeName);
            intent.putExtra("name", event.getName());
            intent.putExtra("time", event.getFrom()+" - "+event.getTo());
            intent.putExtra("people", event.getSignedUpPlayers()+"/"+event.getTotalPlayers()+" people");
            intent.putExtra("date", formater.format(event.getDate()));
            intent.putExtra("price", "Free");
            intent.putExtra("creator", position%2==0?"Stevan Vulić":"Pera Perić");
            intent.putExtra("description", event.getDescription());
            intent.putExtra("imageView", R.drawable.djacko);
            intent.putExtra("isCreator",position%2==0?true:false);
            mainActivity.startActivity(intent);

        });


    }
}
