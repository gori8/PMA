package rs.ac.uns.ftn.sportly.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rs.ac.uns.ftn.sportly.dto.FacebookRequestDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.GoogleRequestDTO;
import rs.ac.uns.ftn.sportly.dto.PeopleDTO;
import rs.ac.uns.ftn.sportly.dto.SyncDataDTO;
import rs.ac.uns.ftn.sportly.dto.UserDTO;

/**
 * Created by gori8 on 06-June-20.
 */

public interface SportlyServerService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/sync")
    Call<SyncDataDTO> sync(@Header("Authorization") String authHeader);

    @POST("/auth/google/login")
    Call<UserDTO> postGoogleToken(@Body GoogleRequestDTO googleRequestDTO);

    @POST("/auth/facebook/login")
    Call<UserDTO> postFacebookToken(@Body FacebookRequestDTO facebookRequestDTO);

    @GET("/friendship/people/{filterText}")
    Call<List<PeopleDTO>> searchPeople(@Header("Authorization") String authHeader, @Path("filterText") String filterText);

    @PUT("/friendship/confirmRequest")
    Call<FriendshipDTO> confirmRequest(@Header("Authorization") String authHeader, @Body FriendshipRequestDto request);

    @POST("/friendship/sendRequest")
    Call<FriendshipDTO> addFriend(@Header("Authorization") String authHeader, @Body FriendshipRequestDto request);

    @HTTP(method = "DELETE", path = "/friendship/deleteFriendship", hasBody = true)
    Call<FriendshipDTO> deleteFriend(@Header("Authorization") String authHeader, @Body FriendshipRequestDto request);


}