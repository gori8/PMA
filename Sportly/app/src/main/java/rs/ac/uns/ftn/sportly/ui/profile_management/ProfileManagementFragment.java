package rs.ac.uns.ftn.sportly.ui.profile_management;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import lombok.SneakyThrows;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.BuildConfig;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.dto.UserDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.login.LoginActivity;
import rs.ac.uns.ftn.sportly.ui.map.MapFragment;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;
import rs.ac.uns.ftn.sportly.utils.RealPathUtil;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileManagementFragment extends Fragment {
    private String name;
    private String surname;
    private String gender;
    private String username;
    private int photoUrl;
    private Uri mImageUri;

    private String currentPhotoPath;

    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    private Long current_user_id;

    private ProgressDialog mProgressDialog;

    private String[] galleryPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int CAMERA = 101;
    private static final int GALLERY = 102;
    ImageView profilePhoto;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile_management, container, false);

        profilePhoto = (ImageView)root.findViewById(R.id.edit_profile_icon);
        profilePhoto.setOnClickListener((View.OnClickListener) view -> {
            selectImage();
        });

        TextView changePhoto = root.findViewById(R.id.edit_profile_text);
        changePhoto.setOnClickListener((View.OnClickListener) view -> {
            selectImage();
        });

        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button saveButton = getView().findViewById(R.id.edit_profile_save_button);
        setSaveButtonClickEvent(saveButton);

        current_user_id = JwtTokenUtils.getUserId(ProfileManagementFragment.this.getContext());

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id.toString());

        fillDataBasedOnEmail();

        setFieldsOnView();
    }

    public void setFieldsOnView(){
        ImageView img = getView().findViewById(R.id.edit_profile_icon);
        img.setImageResource(photoUrl);

        EditText nameEdit = getView().findViewById(R.id.edit_profile_name);
        EditText surnameEdit = getView().findViewById(R.id.edit_profile_surname);

        String nameAndSurname = JwtTokenUtils.getName(this.getContext());
        String[] parts = nameAndSurname.split(" ");
        nameEdit.setText(parts[0]);
        String surname = "";
        for(int i=1; i<parts.length;i++)
            surname += parts[i] + " ";

        surnameEdit.setText(surname);

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(JwtTokenUtils.getUserId(this.getContext()).toString());
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.get().load(image)
                        .placeholder(R.drawable.default_avatar).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setMessage(String text){
        TextView msg = getView().findViewById(R.id.edit_profile_message);
        msg.setText(text);
    }

    private void setSaveButtonClickEvent(Button button){
        // Register a callback to respond to the user
        button.setOnClickListener(new View.OnClickListener() {
            private boolean Validate(){
                EditText nameEdit = getView().findViewById(R.id.edit_profile_name);
                EditText surnameEdit = getView().findViewById(R.id.edit_profile_surname);

                String nameHelper = nameEdit.getText().toString();
                String surnameHelper = surnameEdit.getText().toString();

                if(nameHelper.equals("") || surnameHelper.equals(""))
                    return false;

                return true;
            }
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.edit_profile_save_button:
                        if(Validate()){
                            EditText nameEdit = getView().findViewById(R.id.edit_profile_name);
                            EditText surnameEdit = getView().findViewById(R.id.edit_profile_surname);
                            String name = nameEdit.getText().toString();
                            String surname = surnameEdit.getText().toString();

                            UserDTO dto = new UserDTO();
                            dto.setIme(name);
                            dto.setPrezime(surname);

                            String jwt = JwtTokenUtils.getJwtToken(ProfileManagementFragment.this.getContext());
                            String authHeader = "Bearer " + jwt;

                            Call<UserDTO> call = SportlyServerServiceUtils.sportlyServerService.editUser(authHeader, dto);
                            call.enqueue(new Callback<UserDTO>() {
                                @Override
                                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                                    if (response.code() == 200){
                                        UserDTO userDTO = response.body();
                                        JwtTokenUtils.setName(userDTO.getIme() + " " + userDTO.getPrezime(), ProfileManagementFragment.this.getContext());

                                        EditText nameEdit = getView().findViewById(R.id.edit_profile_name);
                                        EditText surnameEdit = getView().findViewById(R.id.edit_profile_surname);
                                        nameEdit.setText(userDTO.getIme());
                                        surnameEdit.setText(userDTO.getPrezime());

                                        JwtTokenUtils.setName(userDTO.getIme() + " " + userDTO.getPrezime(),ProfileManagementFragment.this.getContext());

                                        showCreatePopup();

                                        //TODO: PREBACI SE NA MAP FRAGMENT
                                    }else{
                                        Log.d("REZ","Meesage recieved: "+response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserDTO> call, Throwable t) {
                                    Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
                                }
                            });
                        }
                        else{
                            String msg = "Validation failed. Please try again.";
                            setMessage(msg);
                        }
                        break;
                }
            }
        });
    }

    private void showCreatePopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("You successfully updated profile.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        setFieldsOnView();
                    }
                });

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit profile photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (EasyPermissions.hasPermissions(ProfileManagementFragment.this.getContext(), galleryPermissions)) {
                    if (options[item].equals("Take Photo"))
                    {

                        File file = createImageFile();
                        mImageUri = FileProvider.getUriForFile(ProfileManagementFragment.this.getContext(), BuildConfig.APPLICATION_ID + ".fileprovider",file);

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, CAMERA);
                        }
                    }
                    else if (options[item].equals("Choose from Gallery"))
                    {
                        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY);
                    }
                    else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }else {
                    EasyPermissions.requestPermissions(ProfileManagementFragment.this,
                            "You need to grant access for storage and camera.",
                            101,
                            galleryPermissions);
                }
            }
        });
        builder.show();
    }

    @SneakyThrows
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap thumb_bitmap=null;
        Bitmap image_bitmap=null;

        if (resultCode == getActivity().RESULT_OK) {

            mProgressDialog = new ProgressDialog(this.getContext());
            mProgressDialog.setTitle("Uploading Image...");
            mProgressDialog.setMessage("Please wait while we upload and process the image.");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            String realPath = "";

            if (requestCode == CAMERA) {
                galleryAddPic();
                realPath = currentPhotoPath;
                System.out.println("PAAAAAAAAAAATH "+realPath);
            } else if (requestCode == GALLERY) {
                mImageUri = data.getData();
                realPath = RealPathUtil.getRealPathFromURI(this.getContext(), mImageUri);
            }
            File thumb_file = new File(realPath);
            thumb_bitmap = compressImage(thumb_file);
            profilePhoto.setImageBitmap(thumb_bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] thumb_byte = baos.toByteArray();

            StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
            final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

            filepath.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        final String[] download_url = new String[1];

                         task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                         {
                             @Override
                             public void onSuccess(Uri downloadUrl)
                             {
                                 download_url[0] =downloadUrl.toString();
                             }
                         });

                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {



                                if(thumb_task.isSuccessful()){

                                    final String[] thumb_downloadUrl = new String[1];


                                    thumb_task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                        public void onSuccess(Uri downloadUrl)
                                        {
                                            thumb_downloadUrl[0] =downloadUrl.toString();

                                            mUserDatabase.child("image").setValue(download_url[0]);
                                            mUserDatabase.child("thumb_image").setValue(thumb_downloadUrl[0]);

                                            mProgressDialog.dismiss();
                                            Toast.makeText(ProfileManagementFragment.this.getContext(), "Success Uploading.", Toast.LENGTH_LONG).show();
                                        }
                                    });



                                } else {
                                    Toast.makeText(ProfileManagementFragment.this.getContext(), "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
                                    mProgressDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ProfileManagementFragment.this.getContext(), "Error in uploading.", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }

    public Bitmap compressImage(File imageFile) {
        Bitmap compressedImage = null;
        try {
            compressedImage = new Compressor(this.getContext())
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(75)
                    .compressToBitmap(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedImage;
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView toolbar = getActivity().findViewById(R.id.nav_view);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        BottomNavigationView toolbar = getActivity().findViewById(R.id.nav_view);
        toolbar.setVisibility(View.VISIBLE);
    }

    //TEMP FUNCTION FOR FILLING DATA
    public void fillDataBasedOnEmail(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
    );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void galleryAddPic() {
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
        this.getContext().sendBroadcast(mediaScanIntent);
    }
}
