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
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.ApplierDTO;
import rs.ac.uns.ftn.sportly.dto.EventDTO;
import rs.ac.uns.ftn.sportly.dto.FriendDTO;
import rs.ac.uns.ftn.sportly.dto.NotificationDTO;
import rs.ac.uns.ftn.sportly.dto.PeopleDTO;
import rs.ac.uns.ftn.sportly.dto.SportsFieldDTO;
import rs.ac.uns.ftn.sportly.dto.SyncDataDTO;
import rs.ac.uns.ftn.sportly.model.firebase.Friends;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.login.LoginActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;
import rs.ac.uns.ftn.sportly.utils.SportlyUtils;

public class SyncDataService extends Service {

    public static String RESULT_CODE = "RESULT_CODE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int status = SportlyUtils.getConnectivityStatus(getApplicationContext());


        Log.d("SERVICE", "--------------SERVICE--------------");
        //ima konekcije ka netu skini sta je potrebno i sinhronizuj bazu
        if(status == SportlyUtils.TYPE_WIFI || status == SportlyUtils.TYPE_MOBILE){
            Log.d("SERVICE", "--------------SYNC--------------");

            String jwt = JwtTokenUtils.getJwtToken(this);
            String authHeader = "Bearer " + jwt;

            Call<SyncDataDTO> call = SportlyServerServiceUtils.sportlyServerService.sync(authHeader);

            call.enqueue(new Callback<SyncDataDTO>() {
                @Override
                public void onResponse(Call<SyncDataDTO> call, Response<SyncDataDTO> response) {
                    if (response.code() == 200){
                        SyncDataDTO syncDataDTO = response.body();
                        Log.d("SERVICE", "SYNC IS OK");

                        List<Long> allFriends = new ArrayList<>();
                        List<Long> allEvents = new ArrayList<>();
                        List<String> allApplicationLists = new ArrayList<>();
                        List<Long> allNotifications = new ArrayList<>();

                        //FRIENDS
                        for(FriendDTO friendDTO : syncDataDTO.getFriends()){

                            System.out.println("---FRIEND---");
                            System.out.println("FIRST NAME:"+friendDTO.getFirstName());
                            System.out.println("LAST NAME:"+friendDTO.getLastName());
                            System.out.println("EMAIL:"+friendDTO.getEmail());
                            System.out.println("USERNAME:"+friendDTO.getUsername());
                            System.out.println("FRIEND TYPE:"+friendDTO.getFriendType());
                            System.out.println("SERVER ID:"+friendDTO.getId());

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.FRIENDS_FIRST_NAME,friendDTO.getFirstName());
                            values.put(DataBaseTables.FRIENDS_LAST_NAME,friendDTO.getLastName());
                            values.put(DataBaseTables.FRIENDS_EMAIL,friendDTO.getEmail());
                            values.put(DataBaseTables.FRIENDS_USERNAME,friendDTO.getUsername());
                            values.put(DataBaseTables.FRINEDS_TYPE,friendDTO.getFriendType());
                            values.put(DataBaseTables.SERVER_ID,friendDTO.getId());

                           getContentResolver().insert(
                                   Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_FRIENDS),
                                   values);

                           allFriends.add(friendDTO.getId());
                        }

                        deleteIfNotOnServer(
                                Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_FRIENDS),
                                allFriends,
                                "LONG"
                        );

                        //NOTIFICATIONS
                        for(NotificationDTO notificationDTO : syncDataDTO.getNotifications()){
                            System.out.println("---NOTIFICATION---");
                            System.out.println("TITTLE:"+notificationDTO.getTitle());
                            System.out.println("TYPE:"+notificationDTO.getType());
                            System.out.println("MESSAGE:"+notificationDTO.getMessage());
                            System.out.println("DATE:"+notificationDTO.getDate());
                            System.out.println("SERVER ID:"+notificationDTO.getId());

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.NOTIFICATIONS_TITTLE,notificationDTO.getTitle());
                            values.put(DataBaseTables.NOTIFICATIONS_TYPE,notificationDTO.getType());
                            values.put(DataBaseTables.NOTIFICATIONS_MESSAGE,notificationDTO.getMessage());
                            values.put(DataBaseTables.NOTIFICATIONS_DATE,notificationDTO.getDate());
                            values.put(DataBaseTables.SERVER_ID,notificationDTO.getId());

                            getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_NOTIFICATIONS),
                                    values);

                            allNotifications.add(notificationDTO.getId());
                        }

                        deleteIfNotOnServer(
                                Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_NOTIFICATIONS),
                                allNotifications,
                                "LONG"
                        );

                        //SPORTSFIELDS
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
                            values.put(DataBaseTables.SPORTSFIELDS_RATING,sportsFieldDTO.getRating());
                            values.put(DataBaseTables.SPORTSFIELDS_CATEGORY,sportsFieldDTO.getCategory());
                            values.put(DataBaseTables.SERVER_ID,sportsFieldDTO.getId());

                            if(sportsFieldDTO.isFavorite()){
                                values.put(DataBaseTables.SPORTSFIELDS_FAVORITE,1);
                            }else{
                                values.put(DataBaseTables.SPORTSFIELDS_FAVORITE,0);
                            }

                            Uri sfUri = getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_SPORTSFIELDS),
                                    values);

                            //EVENTS
                            for(EventDTO eventDTO : sportsFieldDTO.getEvents()){

                                System.out.println("---EVENT---");
                                System.out.println("CURR:"+eventDTO.getCurr());
                                System.out.println("NUMBER OF PEOPLE:"+eventDTO.getNumbOfPpl());
                                System.out.println("PRICE:"+eventDTO.getPrice());
                                System.out.println("DESCRIPTION:"+eventDTO.getDescription());
                                System.out.println("DATE FROM:"+eventDTO.getDateFrom());
                                System.out.println("DATE TO:"+eventDTO.getDateTo());
                                System.out.println("TIME FROM:"+eventDTO.getTimeFrom());
                                System.out.println("TIME TO:"+eventDTO.getTimeTo());
                                System.out.println("SPORTS FIELD ID:"+sfUri.getLastPathSegment());
                                System.out.println("APPLICATION STATUS"+eventDTO.getApplicationStatus());
                                System.out.println("SERVER ID:"+eventDTO.getId());
                                System.out.println("EVENT CREATOR:"+eventDTO.getCreator());


                                ContentValues valuesEvent = new ContentValues();
                                valuesEvent.put(DataBaseTables.EVENTS_NAME,eventDTO.getName());
                                valuesEvent.put(DataBaseTables.EVENTS_CURR,eventDTO.getCurr());
                                valuesEvent.put(DataBaseTables.EVENTS_NUMB_OF_PPL,eventDTO.getNumbOfPpl());
                                valuesEvent.put(DataBaseTables.EVENTS_PRICE,eventDTO.getPrice());
                                valuesEvent.put(DataBaseTables.EVENTS_DESCRIPTION,eventDTO.getDescription());
                                valuesEvent.put(DataBaseTables.EVENTS_DATE_FROM,eventDTO.getDateFrom());
                                valuesEvent.put(DataBaseTables.EVENTS_DATE_TO,eventDTO.getDateTo());
                                valuesEvent.put(DataBaseTables.EVENTS_TIME_FROM,eventDTO.getTimeFrom().toString());
                                valuesEvent.put(DataBaseTables.EVENTS_TIME_TO,eventDTO.getTimeTo().toString());
                                valuesEvent.put(DataBaseTables.EVENTS_SPORTS_FILED_ID,sfUri.getLastPathSegment());
                                valuesEvent.put(DataBaseTables.SERVER_ID,eventDTO.getId());
                                valuesEvent.put(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS,eventDTO.getNumOfParticipants());
                                valuesEvent.put(DataBaseTables.EVENTS_APPLICATION_STATUS,eventDTO.getApplicationStatus());
                                valuesEvent.put(DataBaseTables.EVENTS_CREATOR,eventDTO.getCreator());


                                getContentResolver().insert(
                                        Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_EVENTS),
                                        valuesEvent);

                                allEvents.add(eventDTO.getId());

                                //APPLICATION LIST
                                for(ApplierDTO applier : eventDTO.getApplicationList()){

                                    System.out.println("---APPLICATION LIST---");
                                    System.out.println("EVENT SERVER ID:"+eventDTO.getId());
                                    System.out.println("APPLIER SERVER ID:"+applier.getId());
                                    System.out.println("FIRST NAME:"+applier.getFirstName());
                                    System.out.println("LAST NAME:"+applier.getLastName());
                                    System.out.println("USERNAME:"+applier.getUsername());
                                    System.out.println("EMAIL:"+applier.getEmail());
                                    System.out.println("STATUS:"+applier.getStatus());
                                    System.out.println("SERVER ID:"+"E"+eventDTO.getId()+"A"+applier.getId());

                                    ContentValues valuesApplicationList = new ContentValues();
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EVENT_SERVER_ID,eventDTO.getId());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_APPLIER_SERVER_ID,applier.getId());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_FIRST_NAME,applier.getFirstName());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_LAST_NAME,applier.getLastName());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_USERNAME,applier.getUsername());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EMAIL,applier.getEmail());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_STATUS,applier.getStatus());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_REQUEST_ID,applier.getRequestId());
                                    valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID, applier.getParticipationId());
                                    valuesApplicationList.put(DataBaseTables.SERVER_ID,"E"+eventDTO.getId()+"A"+applier.getId());

                                    getContentResolver().insert(
                                            Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_APPLICATION_LIST),
                                            valuesApplicationList);

                                    allApplicationLists.add("E"+eventDTO.getId()+"A"+applier.getId());
                                }
                            }
                        }

                        deleteIfNotOnServer(
                                Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_APPLICATION_LIST),
                                allApplicationLists,
                                "STRING"
                        );

                        deleteIfNotOnServer(
                                Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_EVENTS),
                                allEvents,
                                "LONG"
                        );

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

        if(intent.getStringExtra("flagStarted")!=null) {
            if (intent.getStringExtra("flagStarted").equals("LoginActivity")) {

                Intent ints = new Intent(LoginActivity.SYNC_DATA_FINISHED);

                ints.putExtra("authType", intent.getStringExtra("authType"));

                sendBroadcast(ints);
            }
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void deleteIfNotOnServer(Uri uri, List<?> list, String idType){
        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{DataBaseTables.SERVER_ID},
                null,
                null,
                null
        );

        cursor.moveToFirst();

        while(!cursor.isAfterLast()){

            Long serverId = -1L;
            String serverIdString = "";
            if(idType.equals("LONG")){
                serverId = cursor.getLong(cursor.getColumnIndex(DataBaseTables.SERVER_ID));
                if(!((List<Long>) list).contains(serverId)){
                    getContentResolver().delete(
                            uri,
                            DataBaseTables.SERVER_ID + "=" + serverId,
                            null);
                }
            }else if(idType.equals("STRING")){
                serverIdString = cursor.getString(cursor.getColumnIndex(DataBaseTables.SERVER_ID));

                if(!((List<Long>) list).contains(serverIdString)){
                    getContentResolver().delete(
                            uri,
                            DataBaseTables.SERVER_ID + " = '"+serverIdString+"'",
                            null);
                }
            }

            cursor.moveToNext();
        }
    }
}

