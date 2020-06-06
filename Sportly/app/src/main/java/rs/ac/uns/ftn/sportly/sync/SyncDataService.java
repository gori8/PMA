package rs.ac.uns.ftn.sportly.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
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

        //ima konekcije ka netu skini sta je potrebno i sinhronizuj bazu
        if(status == SportlyUtils.TYPE_WIFI || status == SportlyUtils.TYPE_MOBILE){

            Call<SyncDataDTO> call = SportlyServerServiceUtils.sportlyServerService.sync();

            call.enqueue(new Callback<SyncDataDTO>() {
                @Override
                public void onResponse(Call<SyncDataDTO> call, Response<SyncDataDTO> response) {
                    if (response.code() == 200){
                        /*
                        TODO: Save to DB
                         */
                        SyncDataDTO syncDataDTO = response.body();
                    }else{
                        Log.d("REZ","Meesage recieved: "+response.code());
                    }
                }

                @Override
                public void onFailure(Call<SyncDataDTO> call, Throwable t) {
                    Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
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
