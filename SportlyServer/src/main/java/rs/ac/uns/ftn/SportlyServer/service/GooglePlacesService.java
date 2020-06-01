package rs.ac.uns.ftn.SportlyServer.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by gori8 on 30-April-20.
 */
/*
 * Klasa koja opisuje koji tj mapira putanju servisa
 * opisuje koji metod koristimo ali i sta ocekujemo kao rezultat
 * */
public interface GooglePlacesService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("json")
    Call<ResponseBody> search(@Query("key") String apiKey, @Query("location") String location, @Query("radius") Long radius,
                              @Query("keyword") String keyword);
}
