package rs.ac.uns.ftn.sportly.sync;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.EventDTO;
import rs.ac.uns.ftn.sportly.dto.FriendDTO;
import rs.ac.uns.ftn.sportly.dto.SportsFieldDTO;
import rs.ac.uns.ftn.sportly.dto.SyncDataDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.utils.SportlyUtils;

public class SyncDataService extends Service {

    public static String RESULT_CODE = "RESULT_CODE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent ints = new Intent(MainActivity.SYNC_DATA);
        int status = SportlyUtils.getConnectivityStatus(getApplicationContext());
        ints.putExtra(RESULT_CODE, status);


        Log.d("SERVICE", "--------------SERVICE--------------");
        //ima konekcije ka netu skini sta je potrebno i sinhronizuj bazu
        if(status == SportlyUtils.TYPE_WIFI || status == SportlyUtils.TYPE_MOBILE){
            Log.d("SERVICE", "--------------SYNC--------------");

            SharedPreferences sharedpreferences = getSharedPreferences("Sportly.xml", Context.MODE_PRIVATE);
            String jwt = sharedpreferences.getString("jwt","DEFAULT");
            String authHeader = "Bearer " + jwt;

            Call<SyncDataDTO> call = SportlyServerServiceUtils.sportlyServerService.sync(authHeader);

            call.enqueue(new Callback<SyncDataDTO>() {
                @Override
                public void onResponse(Call<SyncDataDTO> call, Response<SyncDataDTO> response) {
                    if (response.code() == 200){
                        SyncDataDTO syncDataDTO = response.body();
                        Log.d("SERVICE", "SYNC IS OK");
                        for(FriendDTO friendDTO : syncDataDTO.getFriends()){

                            System.out.println("---FRIEND---");
                            System.out.println("FIRST NAME:"+friendDTO.getFirstName());
                            System.out.println("LAST NAME:"+friendDTO.getLastName());
                            System.out.println("EMAIL:"+friendDTO.getEmail());
                            System.out.println("USERNAME:"+friendDTO.getUsername());
                            System.out.println("SERVER ID:"+friendDTO.getId());

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.FRIENDS_FIRST_NAME,friendDTO.getFirstName());
                            values.put(DataBaseTables.FRIENDS_LAST_NAME,friendDTO.getLastName());
                            values.put(DataBaseTables.FRIENDS_EMAIL,friendDTO.getEmail());
                            values.put(DataBaseTables.FRIENDS_USERNAME,friendDTO.getUsername());
                            values.put(DataBaseTables.SERVER_ID,friendDTO.getId());

                           getContentResolver().insert(
                                   Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_FRIENDS),
                                   values);
                        }

                        for(SportsFieldDTO sportsFieldDTO : syncDataDTO.getAllSportsFields()){

                            System.out.println("---SPORTSFIELD---");
                            System.out.println("NAME:"+sportsFieldDTO.getName());
                            System.out.println("DESCRIPTION:"+sportsFieldDTO.getDescription());
                            System.out.println("LATITUDE:"+sportsFieldDTO.getLatitude());
                            System.out.println("LONGITUDE:"+sportsFieldDTO.getLongitude());
                            System.out.println("SERVER ID:"+sportsFieldDTO.getId());

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.SPORTSFIELDS_NAME,sportsFieldDTO.getName());
                            values.put(DataBaseTables.SPORTSFIELDS_DESCRIPTION,sportsFieldDTO.getDescription());
                            values.put(DataBaseTables.SPORTSFIELDS_LATITUDE,sportsFieldDTO.getLatitude());
                            values.put(DataBaseTables.SPORTSFIELDS_LONGITUDE,sportsFieldDTO.getLongitude());
                            values.put(DataBaseTables.SERVER_ID,sportsFieldDTO.getId());

                            if(syncDataDTO.getFavorite().contains(sportsFieldDTO.getId())){
                                values.put(DataBaseTables.SPORTSFIELDS_FAVORITE,1);
                            }else{
                                values.put(DataBaseTables.SPORTSFIELDS_FAVORITE,0);
                            }

                            Uri sfUri = getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_SPORTSFIELDS),
                                    values);

                            for(EventDTO eventDTO : sportsFieldDTO.getEvents()){

                                System.out.println("---EVENT---");
                                System.out.println("CURR:"+eventDTO.getCurr());
                                System.out.println("NUMBER OF PEOPLE:"+eventDTO.getNumbOfPpl());
                                System.out.println("PRICE:"+eventDTO.getPrice());
                                System.out.println("DESCRIPTION:"+eventDTO.getDescription());
                                System.out.println("TIME FROM:"+eventDTO.getTimeFrom());
                                System.out.println("TIME TO:"+eventDTO.getTimeTo());
                                System.out.println("SPORTS FIELD ID:"+sfUri.getLastPathSegment());
                                System.out.println("SERVER ID:"+eventDTO.getId());

                                ContentValues valuesEvent = new ContentValues();
                                valuesEvent.put(DataBaseTables.EVENTS_NAME,eventDTO.getName());
                                valuesEvent.put(DataBaseTables.EVENTS_CURR,eventDTO.getCurr());
                                valuesEvent.put(DataBaseTables.EVENTS_NUMB_OF_PPL,eventDTO.getNumbOfPpl());
                                valuesEvent.put(DataBaseTables.EVENTS_PRICE,eventDTO.getPrice());
                                valuesEvent.put(DataBaseTables.EVENTS_DESCRIPTION,eventDTO.getDescription());
                                valuesEvent.put(DataBaseTables.EVENTS_DATE_FROM,eventDTO.getDateFrom().toString());
                                valuesEvent.put(DataBaseTables.EVENTS_DATE_TO,eventDTO.getDateTo().toString());
                                valuesEvent.put(DataBaseTables.EVENTS_TIME_FROM,eventDTO.getTimeFrom().toString());
                                valuesEvent.put(DataBaseTables.EVENTS_TIME_TO,eventDTO.getTimeTo().toString());
                                valuesEvent.put(DataBaseTables.EVENTS_SPORTS_FILED_ID,sfUri.getLastPathSegment());
                                valuesEvent.put(DataBaseTables.SERVER_ID,eventDTO.getId());

                                if(syncDataDTO.getCreatorEvents().contains(eventDTO.getId())){
                                    valuesEvent.put(DataBaseTables.EVENTS_CREATOR,1);
                                    valuesEvent.put(DataBaseTables.EVENTS_PARTICIPATING,0);
                                }
                                else if(syncDataDTO.getParticipantEvents().contains(eventDTO.getId())){
                                    valuesEvent.put(DataBaseTables.EVENTS_PARTICIPATING,1);
                                    valuesEvent.put(DataBaseTables.EVENTS_CREATOR,0);
                                }
                                else{
                                    valuesEvent.put(DataBaseTables.EVENTS_CREATOR,0);
                                    valuesEvent.put(DataBaseTables.EVENTS_PARTICIPATING,0);
                                }

                                getContentResolver().insert(
                                        Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_EVENTS),
                                        valuesEvent);
                            }
                        }

                    }else{
                        Log.d("REZ","Meesage recieved: "+response.code());
                    }
                }

                @Override
                public void onFailure(Call<SyncDataDTO> call, Throwable t) {
                    Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
                    Log.d("SERVICE", "SYNC FAILED");
                }
            });

        }

        //sendBroadcast(ints);

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}
