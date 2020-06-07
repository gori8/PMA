package rs.ac.uns.ftn.sportly.sync;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
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
import rs.ac.uns.ftn.sportly.dto.FriendDTO;
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
            Call<SyncDataDTO> call = SportlyServerServiceUtils.sportlyServerService.sync();

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

                            Uri uri = getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_FRIENDS),
                                    values);
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
