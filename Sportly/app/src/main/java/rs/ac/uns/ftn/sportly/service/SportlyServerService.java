package rs.ac.uns.ftn.sportly.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rs.ac.uns.ftn.sportly.dto.BundleRatingDTO;
import rs.ac.uns.ftn.sportly.dto.EventDTO;
import rs.ac.uns.ftn.sportly.dto.EventRequestDTO;
import rs.ac.uns.ftn.sportly.dto.EventRequestRequest;
import rs.ac.uns.ftn.sportly.dto.FacebookRequestDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.GoogleRequestDTO;
import rs.ac.uns.ftn.sportly.dto.ParticipationDTO;
import rs.ac.uns.ftn.sportly.dto.PeopleDTO;
import rs.ac.uns.ftn.sportly.dto.SportsFieldDTO;
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

    @POST("/event/eventRequest/create/participant")
    Call<EventRequestDTO> applyForEvent(@Header("Authorization") String authHeader, @Body EventRequestRequest request);

    @POST("/event/eventRequest/create/creator")
    Call<EventRequestDTO> inviteFriendOnEvent(@Header("Authorization") String authHeader, @Body EventRequestRequest request);

    @POST("/event/create")
    Call<EventDTO> createEvent(@Header("Authorization") String authHeader, @Body EventDTO event);

    @PUT("/event/edit")
    Call<EventDTO> editEvent(@Header("Authorization") String authHeader, @Body EventDTO event);

    @PUT("/event/eventRequest/{id}/accept")
    Call<EventRequestDTO> acceptApplicationForEvent(@Header("Authorization") String authHeader, @Path("id") Long id);

    @DELETE("/event/eventRequest/{id}/delete")
    Call<EventRequestDTO> deleteApplicationForEvent(@Header("Authorization") String authHeader, @Path("id") Long id);

    @DELETE("/event/participation/{id}/delete")
    Call<ParticipationDTO> deleteParticipationForEvent(@Header("Authorization") String authHeader, @Path("id") Long id);

    @DELETE("/event/eventRequest/{id}/reject")
    Call<EventRequestDTO> declineApplicationForEvent(@Header("Authorization") String authHeader, @Path("id") Long id);

    @POST("/favouriteFields/{fieldId}")
    Call<SportsFieldDTO> addToFavorites(@Header("Authorization") String authHeader, @Path("fieldId") Long fieldId);

    @DELETE("/favouriteFields/{fieldId}")
    Call<SportsFieldDTO> removeFromFavorites(@Header("Authorization") String authHeader, @Path("fieldId") Long fieldId);

    @DELETE("/event/{id}/delete")
    Call<EventDTO> deleteEvent(@Header("Authorization") String authHeader, @Path("id") Long id);

    @POST("/fieldRatings/bundle")
    Call<Long> rateEverything(@Header("Authorization") String authHeader, @Body BundleRatingDTO bundle);

    @PUT("/user")
    Call<UserDTO> editUser(@Header("Authorization") String authHeader, @Body UserDTO request);

}