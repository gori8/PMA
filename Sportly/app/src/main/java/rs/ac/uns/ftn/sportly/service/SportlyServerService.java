package rs.ac.uns.ftn.sportly.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rs.ac.uns.ftn.sportly.dto.SyncDataDTO;

/**
 * Created by gori8 on 06-June-20.
 */

public interface SportlyServerService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/sync")
    Call<SyncDataDTO> sync();




}