package rs.ac.uns.ftn.SportlyServer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.SportlyServer.dto.PlaceDTO;
import rs.ac.uns.ftn.SportlyServer.service.GooglePlacesServiceUtils;
import com.fasterxml.jackson.core.type.TypeReference;


import javax.annotation.PostConstruct;
import java.util.ArrayList;

@SpringBootApplication
public class SportlyServerApplication {

	private final String API_KEY_PLACES = "AIzaSyD1xhjBoYoxC_Jz1t7cqlbWV-Q1m0p979Q";

	Logger logger = LoggerFactory.getLogger(SportlyServerApplication.class);



	public static void main(String[] args) {
		SpringApplication.run(SportlyServerApplication.class, args);
	}

	private String locationToString(double latitude, double longitude){
		return latitude+","+longitude;
	}

	@PostConstruct
	public void init(){

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


		//Basketball
		Call<ResponseBody> call = GooglePlacesServiceUtils.placesService.search(API_KEY_PLACES,locationToString(45.253513,19.829127),10000L,"basketball court");

		call.enqueue(new Callback<ResponseBody>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				if (response.code() == 200){
					JsonNode responseJSON = objectMapper.readTree(response.body().string());
					JsonNode resultsListJSON = responseJSON.get("results");
					ArrayList<PlaceDTO> placesList = objectMapper.readValue(resultsListJSON.toString(), new TypeReference<ArrayList<PlaceDTO>>() {});
					logger.info("MapFragment","***** LISTA SPISAK LISTA SPISAK *****");
					//save to DB
				}else{
					logger.error("MapFragment","Meesage recieved: "+response.code());
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				logger.error("MapFragment", t.getMessage() != null?t.getMessage():"error");
			}
		});

		//Football
		Call<ResponseBody> footballCall = GooglePlacesServiceUtils.placesService.search(API_KEY_PLACES,locationToString(45.253513,19.829127),10000L,"football court");

		footballCall.enqueue(new Callback<ResponseBody>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				if (response.code() == 200){
					JsonNode responseJSON = objectMapper.readTree(response.body().string());
					JsonNode resultsListJSON = responseJSON.get("results");
					ArrayList<PlaceDTO> placesList = objectMapper.readValue(resultsListJSON.toString(), new TypeReference<ArrayList<PlaceDTO>>() {});
					logger.info("MapFragment","***** LISTA SPISAK LISTA SPISAK *****");
					//save to DB
				}else{
					logger.error("MapFragment","Meesage recieved: "+response.code());
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				logger.error("MapFragment", t.getMessage() != null?t.getMessage():"error");
			}
		});

		//Tennis
		Call<ResponseBody> tennisCall = GooglePlacesServiceUtils.placesService.search(API_KEY_PLACES,locationToString(45.253513,19.829127),10000L,"tennis court");

		tennisCall.enqueue(new Callback<ResponseBody>() {
			@SneakyThrows
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
				if (response.code() == 200){
					JsonNode responseJSON = objectMapper.readTree(response.body().string());
					JsonNode resultsListJSON = responseJSON.get("results");
					ArrayList<PlaceDTO> placesList = objectMapper.readValue(resultsListJSON.toString(), new TypeReference<ArrayList<PlaceDTO>>() {});
					logger.info("MapFragment","***** LISTA SPISAK LISTA SPISAK *****");
					//save to DB
				}else{
					logger.error("MapFragment","Meesage recieved: "+response.code());
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				logger.error("MapFragment", t.getMessage() != null?t.getMessage():"error");
			}
		});

	}

}
