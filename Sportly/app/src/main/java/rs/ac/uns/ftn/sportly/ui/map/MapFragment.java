package rs.ac.uns.ftn.sportly.ui.map;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;


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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.PlaceDTO;
import rs.ac.uns.ftn.sportly.dto.SportsFieldDTO;
import rs.ac.uns.ftn.sportly.model.Event;
import rs.ac.uns.ftn.sportly.service.GooglePlacesServiceUtils;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.adapters.EventsAdapter;
import rs.ac.uns.ftn.sportly.ui.adapters.EventsCursorAdapter;
import rs.ac.uns.ftn.sportly.ui.dialogs.LocationDialog;
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;
import rs.ac.uns.ftn.sportly.ui.event.create_event.CreateEventActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

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
    private HashMap<String,Boolean> filterChecks = new HashMap<>();
    private HashMap<String,ArrayList<Marker>> markersMap = new HashMap<>();
    private Cursor data;
    private EventsCursorAdapter adapter;
    private Long selectedSportsFieldServerId;
    private Long selectedSportsFieldId;


    public static MapFragment newInstance() {

        MapFragment mpf = new MapFragment();

        return mpf;
    }

    public class MarkerData{

        public Long sportsFieldId;
        public Long sportsFieldServerId;
        public Integer isFavorite;

        public MarkerData(Long sportsFieldId, Long sportsFieldServerId, Integer isFavorite) {
            this.sportsFieldId = sportsFieldId;
            this.sportsFieldServerId = sportsFieldServerId;
            this.isFavorite = isFavorite;
        }
    }

    /**
     * Prilikom kreidanja fragmenta preuzimamo sistemski servis za rad sa lokacijama
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        setHasOptionsMenu(true);
        markersMap.put("basketball",new ArrayList<>());
        markersMap.put("football",new ArrayList<>());
        markersMap.put("tennis",new ArrayList<>());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.message));
        searchAutoComplete.setTextColor(ContextCompat.getColor(getActivity(), R.color.message));
        View searchplate = (View)searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchplate.setBackgroundResource(R.drawable.background_search);
        ImageView searchClose = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_clear_24dp);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /**
     * Kada zelmo da dobijamo informacije o lokaciji potrebno je da specificiramo
     * po kom kriterijumu zelimo da dobijamo informacije GSP, MOBILNO(WIFI, MObilni internet), GPS+MOBILNO
     * **/
    private void createMapFragmentAndInflate(View view) {
        if(map==null) {
            //specificiramo krijterijum da dobijamo informacije sa svih izvora
            //ako korisnik to dopusti
            Criteria criteria = new Criteria();

            //sistemskom servisu prosledjujemo taj kriterijum da bi
            //mogli da dobijamo informacje sa tog izvora
            provider = locationManager.getBestProvider(criteria, true);

            //kreiramo novu instancu fragmenta
            mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


            //i vrsimo zamenu trenutnog prikaza sa prikazom mape
            //FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            //transaction.replace(R.id.map_container, mMapFragment).commit();

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
                intent.putExtra("sportsFieldServerId", MapFragment.this.selectedSportsFieldServerId);
                intent.putExtra("sportsFieldId", MapFragment.this.selectedSportsFieldId);


                mainActivity.startActivity(intent);
            }
        });

        Button btnAll = view.findViewById(R.id.btn_all);
        filterChecks.put("all",true);

        Button btnBasketball = view.findViewById(R.id.btn_basketball);
        filterChecks.put("basketball",true);

        Button btnFootball = view.findViewById(R.id.btn_football);
        filterChecks.put("football",true);

        Button btnTennis = view.findViewById(R.id.btn_tennis);
        filterChecks.put("tennis",true);

        btnAll.setOnClickListener(v -> {

            if(filterChecks.get("all")) {
                uncheckButton(btnAll,"all");
                uncheckButton(btnBasketball,"basketball");
                uncheckButton(btnFootball,"football");
                uncheckButton(btnTennis,"tennis");
            }else{
                checkButton(btnAll,"all");
                checkButton(btnBasketball,"basketball");
                checkButton(btnFootball,"football");
                checkButton(btnTennis,"tennis");
            }
        });

        btnBasketball.setOnClickListener(v -> {

            if(filterChecks.get("basketball")) {
                uncheckButton(btnAll,"all");
                uncheckButton(btnBasketball,"basketball");
            }else{
                checkButton(btnBasketball,"basketball");
                if(checkIfAllChecked()){
                    checkButton(btnAll,"all");
                }
            }
        });

        btnFootball.setOnClickListener(v -> {

            if(filterChecks.get("football")) {
                uncheckButton(btnAll,"all");
                uncheckButton(btnFootball,"football");
            }else{
                checkButton(btnFootball,"football");
                if(checkIfAllChecked()){
                    checkButton(btnAll,"all");
                }
            }
        });

        btnTennis.setOnClickListener(v -> {

            if(filterChecks.get("tennis")) {
                uncheckButton(btnAll,"all");
                uncheckButton(btnTennis,"tennis");
            }else{
                checkButton(btnTennis,"tennis");
                if(checkIfAllChecked()){
                    checkButton(btnAll,"all");
                }
            }
        });

    }

    private boolean checkIfAllChecked(){
        boolean ret = true;
        for(String key : filterChecks.keySet()){
            if(key.equals("all")){
                continue;
            }
            boolean check = filterChecks.get(key);
            if(!check){
                ret = false;
                break;
            }
        }

        return ret;
    }

    private void checkButton(Button btn, String key){
        btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        btn.setTextColor(Color.WHITE);
        filterChecks.put(key,true);
        if(!key.equals("all")) {
            ArrayList<Marker> markers = markersMap.get(key);
            for (Marker marker : markers) {
                marker.setVisible(true);
            }
        }
    }

    private void uncheckButton(Button btn, String key){
        btn.setBackgroundColor(Color.WHITE);
        btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        filterChecks.put(key,false);
        if(!key.equals("all")) {
            ArrayList<Marker> markers = markersMap.get(key);
            for (Marker marker : markers) {
                marker.setVisible(false);
            }
        }
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
        createMapFragmentAndInflate(view);

        ListView listView = view.findViewById(R.id.events_list);

        listView.setOnItemClickListener((parent, vieww, positionLv, idLv) -> {
            TextView textView = view.findViewById(R.id.place_info_name);
            String placeInfoName = (String) textView.getText();


            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy");
            Cursor cursor = (Cursor)adapter.getItem(positionLv);
            String location = placeInfoName;
            String name = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_NAME));
            String timeFrom = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_TIME_FROM));
            String timeTo = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_TIME_TO));
            String numOfPeople = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_NUMB_OF_PPL));
            String numOfParticipants = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS));
            String date = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_DATE_FROM));
            String price = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_PRICE));
            String description = cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_DESCRIPTION));
            Long eventId = cursor.getLong(cursor.getColumnIndex(DataBaseTables.SERVER_ID));
            Boolean flagIsCreator=false;
            if(cursor.getString(cursor.getColumnIndex(DataBaseTables.EVENTS_APPLICATION_STATUS)).equals("CREATOR")){
                flagIsCreator=true;
            }

            Intent intent = new Intent(getActivity(), EventActivity.class);
            intent.putExtra("eventId", eventId);
            /*intent.putExtra("location", location);
            intent.putExtra("name", name);
            intent.putExtra("time", timeFrom+" - "+timeTo);
            intent.putExtra("people", numOfParticipants+"/"+numOfPeople+" people");
            intent.putExtra("date", date);

            intent.putExtra("price", price);
            intent.putExtra("creator", "Pera Perić");
            intent.putExtra("description", description);
            intent.putExtra("imageView", R.drawable.djacko);
            intent.putExtra("isCreator",flagIsCreator);*/
            getActivity().startActivity(intent);

        });


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

        String mockLocationProvider = LocationManager.GPS_PROVIDER;

        location = new Location(mockLocationProvider);

        location.setLatitude(latLngForSearch.latitude);
        location.setLongitude(latLngForSearch.longitude);

        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.SPORTSFIELDS_DESCRIPTION,
                DataBaseTables.SPORTSFIELDS_LATITUDE,
                DataBaseTables.SPORTSFIELDS_LONGITUDE,
                DataBaseTables.SPORTSFIELDS_NAME,
                DataBaseTables.SPORTSFIELDS_FAVORITE,
                DataBaseTables.SPORTSFIELDS_CATEGORY,
                DataBaseTables.SERVER_ID
        };

        data = getActivity().getContentResolver().query(Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_SPORTSFIELDS),allColumns,null,null,null);

        data.moveToFirst();

        while (!data.isAfterLast()) {
            String category = data.getString(data.getColumnIndex(DataBaseTables.SPORTSFIELDS_CATEGORY));
            String name = data.getString(data.getColumnIndex(DataBaseTables.SPORTSFIELDS_NAME));
            Float lat = data.getFloat(data.getColumnIndex(DataBaseTables.SPORTSFIELDS_LATITUDE));
            Float lng = data.getFloat(data.getColumnIndex(DataBaseTables.SPORTSFIELDS_LONGITUDE));
            Long sportsFieldServerId = data.getLong(data.getColumnIndex(DataBaseTables.SERVER_ID));
            Integer isFavorite = data.getInt(data.getColumnIndex(DataBaseTables.SPORTSFIELDS_FAVORITE));
            Long id = data.getLong(data.getColumnIndex(DataBaseTables.ID));


            Marker marker = null;

            if(category.equals("basketball")) {
                marker = addMarker(new LatLng(lat, lng), name, bitmapDescriptorFromVector(getActivity(), R.drawable.marker_basketball));
            }else if (category.equals("football")){
                marker = addMarker(new LatLng(lat, lng), name, bitmapDescriptorFromVector(getActivity(), R.drawable.marker_football));
            }else if (category.equals("tennis")){
                marker = addMarker(new LatLng(lat, lng), name, bitmapDescriptorFromVector(getActivity(), R.drawable.marker_tennis));
            }

            MarkerData markerData = new MarkerData(id,sportsFieldServerId,isFavorite);
            marker.setTag(markerData);

            markersMap.get(category).add(marker);
            data.moveToNext();
        }




        //ako zelmo da reagujemo na klik markera koristimo marker click listener
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTag()!=null) {

                    MarkerData markerData = (MarkerData)marker.getTag();

                    ImageButton addFavoriteButton = getView().findViewById(R.id.addFavoriteButton);
                    ImageButton removeFavoriteButton = getView().findViewById(R.id.removeFavoriteButton);
                    if(markerData.isFavorite == 1){
                        addFavoriteButton.setVisibility(View.GONE);
                        removeFavoriteButton.setVisibility(View.VISIBLE);
                    }else if(markerData.isFavorite == 0){
                        removeFavoriteButton.setVisibility(View.GONE);
                        addFavoriteButton.setVisibility(View.VISIBLE);
                    }

                    Bundle args = new Bundle();
                    args.putLong("sportsFieldsId",markerData.sportsFieldId);
                    MapFragment.this.selectedSportsFieldServerId = markerData.sportsFieldServerId;
                    MapFragment.this.selectedSportsFieldId = markerData.sportsFieldId;


                    if(getLoaderManager().getLoader(0)!=null){
                        getLoaderManager().restartLoader(0, args, MapFragment.this);
                    }else{
                        getLoaderManager().initLoader(0, args, MapFragment.this);
                    }


                    String[] from = new String[] {
                            DataBaseTables.EVENTS_NAME,
                            DataBaseTables.EVENTS_DESCRIPTION,
                            DataBaseTables.EVENTS_NUMB_OF_PPL,
                            DataBaseTables.EVENTS_TIME_FROM,
                            DataBaseTables.EVENTS_TIME_TO,
                            DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS
                    };
                    int[] to = new int[] {
                            R.id.list_event_name,
                            R.id.list_event_description,
                            R.id.list_event_ppl,
                            R.id.list_event_time_from,
                            R.id.list_event_time_to,
                            R.id.list_event_participants
                    };
                    adapter = new EventsCursorAdapter(getActivity(), R.layout.event_item, null, from,
                            to);

                    ListView listView = (ListView) getView().findViewById(R.id.events_list);
                    listView.setAdapter(adapter);


                    TextView tvPlaceName = getView().findViewById(R.id.place_info_name);
                    tvPlaceName.setText((String)marker.getTitle());
                    slidingPanel.setAnchorPoint(0.5f);
                    slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

                    String jwt = JwtTokenUtils.getJwtToken(MapFragment.this.getContext());
                    String authHeader = "Bearer " + jwt;

                    ProgressBar loadingCircle = getView().findViewById(R.id.loadingCircle);

                    addFavoriteButton.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            addFavoriteButton.setVisibility(View.GONE);
                            loadingCircle.setVisibility(View.VISIBLE);

                            Call<SportsFieldDTO> call = SportlyServerServiceUtils.sportlyServerService.addToFavorites(authHeader,markerData.sportsFieldServerId);

                            call.enqueue(new Callback<SportsFieldDTO>() {
                                @Override
                                public void onResponse(Call<SportsFieldDTO> call, Response<SportsFieldDTO> response) {
                                    if (response.code() == 201){

                                        Log.i("ADD TO FAVORITES", "CALL TO SERVER SUCCESSFUL");

                                        ContentValues values = new ContentValues();
                                        values.put(DataBaseTables.SPORTSFIELDS_FAVORITE,1);

                                        MapFragment.this.getContext().getContentResolver().update(
                                                Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_SPORTSFIELDS),
                                                values,
                                                DataBaseTables.SERVER_ID + " = " + markerData.sportsFieldServerId,
                                                null
                                        );

                                        loadingCircle.setVisibility(View.GONE);
                                        removeFavoriteButton.setVisibility(View.VISIBLE);

                                    }else{
                                        Log.i("ADD TO FAVORITES", "CALL TO SERVER RESPONSE CODE: "+response.code());
                                        loadingCircle.setVisibility(View.GONE);
                                        addFavoriteButton.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFailure(Call<SportsFieldDTO> call, Throwable t) {
                                    Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                                    Log.i("ADD TO FAVORITES", "CALL TO SERVER FAILED");
                                    loadingCircle.setVisibility(View.GONE);
                                    addFavoriteButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

                    removeFavoriteButton.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            removeFavoriteButton.setVisibility(View.GONE);
                            loadingCircle.setVisibility(View.VISIBLE);

                            Call<SportsFieldDTO> call = SportlyServerServiceUtils.sportlyServerService.removeFromFavorites(authHeader,markerData.sportsFieldServerId);

                            call.enqueue(new Callback<SportsFieldDTO>() {
                                @Override
                                public void onResponse(Call<SportsFieldDTO> call, Response<SportsFieldDTO> response) {
                                    if (response.code() == 200){

                                        Log.i("REMOVE FROM FAVORITES", "CALL TO SERVER SUCCESSFUL");

                                        ContentValues values = new ContentValues();
                                        values.put(DataBaseTables.SPORTSFIELDS_FAVORITE,0);

                                        MapFragment.this.getContext().getContentResolver().update(
                                                Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_SPORTSFIELDS),
                                                values,
                                                DataBaseTables.SERVER_ID + " = " + markerData.sportsFieldServerId,
                                                null
                                        );

                                        loadingCircle.setVisibility(View.GONE);
                                        addFavoriteButton.setVisibility(View.VISIBLE);

                                    }else{
                                        Log.i("REMOVE FROM FAVORITES", "CALL TO SERVER RESPONSE CODE: "+response.code());
                                        loadingCircle.setVisibility(View.GONE);
                                        removeFavoriteButton.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFailure(Call<SportsFieldDTO> call, Throwable t) {
                                    Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                                    Log.i("REMOVE FROM", "CALL TO SERVER FAILED");
                                    loadingCircle.setVisibility(View.GONE);
                                    removeFavoriteButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
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

            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

   /* public void onActivityCreated(Bundle savedInstanceState) {
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


    }*/





    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.EVENTS_NAME,
                DataBaseTables.EVENTS_CURR,
                DataBaseTables.EVENTS_DESCRIPTION,
                DataBaseTables.EVENTS_NUMB_OF_PPL,
                DataBaseTables.EVENTS_PRICE,
                DataBaseTables.EVENTS_SPORTS_FILED_ID,
                DataBaseTables.EVENTS_DATE_FROM,
                DataBaseTables.EVENTS_DATE_TO,
                DataBaseTables.EVENTS_TIME_FROM,
                DataBaseTables.EVENTS_TIME_TO,
                DataBaseTables.EVENTS_APPLICATION_STATUS,
                DataBaseTables.SERVER_ID,
                DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS
        };



        Uri uri = Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.SPORTSFIELDS_EVENTS+"/"+args.getLong("sportsFieldsId"));


        return new CursorLoader(getActivity(), uri, allColumns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
