package rs.ac.uns.ftn.sportly.ui.favorites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.SportsFieldDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.map.MapFragment;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class FavoriteCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;

    public FavoriteCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to) {
        super(context,layout,c,new String[]{from[0]},new int[]{to[0]});
        this.layout=layout;
        this.mContext = context;
        this.inflater=LayoutInflater.from(context);
        this.cr=c;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        //set Image of selected SportsField
        ImageView imageView = view.findViewById(R.id.favorite_image);
        String imageRef = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.SPORTSFIELDS_IMAGE_REF));
        String imageUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference="+imageRef+"&key=AIzaSyD1xhjBoYoxC_Jz1t7cqlbWV-Q1m0p979Q";
        Log.i("IMAGE_REF","IMAGE REF: "+imageRef);
        Picasso.get().load(imageUri)
                .placeholder(R.drawable.default_avatar).into(imageView);

        RatingBar ratingBar = view.findViewById(R.id.favorite_ratingBar);

        int ratingIndex=cursor.getColumnIndexOrThrow(DataBaseTables.SPORTSFIELDS_RATING);

        Long sportsFieldServerId = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID));

        ratingBar.setRating(cursor.getFloat(ratingIndex));

        String jwt = JwtTokenUtils.getJwtToken(context);
        String authHeader = "Bearer " + jwt;

        ProgressBar loadingCircle = view.findViewById(R.id.loadingCircle);

        ImageButton removeFavoriteButton = view.findViewById(R.id.imageButton);
        removeFavoriteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeFavoriteButton.setVisibility(View.GONE);
                loadingCircle.setVisibility(View.VISIBLE);

                Call<SportsFieldDTO> call = SportlyServerServiceUtils.sportlyServerService.removeFromFavorites(authHeader, sportsFieldServerId);

                call.enqueue(new Callback<SportsFieldDTO>() {
                    @Override
                    public void onResponse(Call<SportsFieldDTO> call, Response<SportsFieldDTO> response) {
                        if (response.code() == 200) {

                            Log.i("REMOVE FROM FAVORITES", "CALL TO SERVER SUCCESSFUL");

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.SPORTSFIELDS_FAVORITE, 0);

                            context.getContentResolver().update(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_SPORTSFIELDS),
                                    values,
                                    DataBaseTables.SERVER_ID + " = " + sportsFieldServerId,
                                    null
                            );
                        } else {
                            Log.i("REMOVE FROM FAVORITES", "CALL TO SERVER RESPONSE CODE: " + response.code());
                            loadingCircle.setVisibility(View.GONE);
                            removeFavoriteButton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<SportsFieldDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null ? t.getMessage() : "error");
                        Log.i("REMOVE FROM", "CALL TO SERVER FAILED");
                        loadingCircle.setVisibility(View.GONE);
                        removeFavoriteButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}
