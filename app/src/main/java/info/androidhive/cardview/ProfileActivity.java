package info.androidhive.cardview;

import android.*;
import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import info.androidhive.cardview.helper.SQLiteHandler;
import info.androidhive.cardview.helper.SessionManager;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,editProfileFragment.OnFragmentInteractionListener{


    private ProgressBar pg;

    private static final int logoutCode = 4;
    RelativeLayout intent;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_PHOTO = 2;
    private static final int REQUEST_PERMISSION = 1054;
    FirebaseUser user;
    private String mCurrentPhotoPath;
    ImageView im;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    FloatingActionButton fab;
    private int choice = 0;
    SessionManager session;

    private showProfileFragment profileFragment;
    private editProfileFragment editProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // SQLite database handler
        session = new SessionManager(getApplicationContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("profile_photos");


        initCollapsingToolbar();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(editProfile != null){
                editProfile.editDetails();
            }
            }
        });

        intent = (RelativeLayout) findViewById(R.id.intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);

        de.hdodenhof.circleimageview.CircleImageView imageView = hView.findViewById(R.id.imageView);
        TextView eText = hView.findViewById(R.id.EtextView);
        TextView dnTxt = hView.findViewById(R.id.dnTxt);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            eText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            dnTxt.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            try {
                Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        pg = findViewById(R.id.progress);

        profileFragment = (showProfileFragment) getSupportFragmentManager().findFragmentByTag("SHOW_PROFILE");
        editProfile = new editProfileFragment();


        FloatingActionButton changeImg = findViewById(R.id.changeImg);
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ProfileActivity.this,CameraActivity.class));
                intent.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
        });


        /*nam.setText(name.getText().toString());
        ema.setText(email.getText().toString());
        phone.setText(phone.getText().toString());
        addr.setText(address.getText().toString());*/

        FloatingActionButton camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 2;

                checkPermissions(choice);
            }
        });

        FloatingActionButton gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 1;

                checkPermissions(choice);
            }
        });

        FloatingActionButton remove = findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this,"I was Clicked",Toast.LENGTH_LONG).show();
            }
        });

        hideDialog();
        //checkNewUser();
    }

    private void checkPermissions(int choice){
        //Toast.makeText(ProfileActivity.this,"I got called", Toast.LENGTH_SHORT).show();
        if ( ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
        else {
            if(choice == 1) {
                choosePhotoFromGallary();
            }
            else if(choice == 2){
                takePhotoFromCamera();
            }
        }
    }

    private void enableEditing(){
        getSupportFragmentManager().beginTransaction().replace(R.id.prof_cont,editProfile).commit();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if(choice == 1) {
                        choosePhotoFromGallary();
                    }
                    else if(choice == 2){
                        takePhotoFromCamera();
                    }
                }
                else {
                    Toast.makeText(ProfileActivity.this,"You need to grant this app permission to use this feature",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void choosePhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_PICK_PHOTO);
        }

    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "info.androidhive.cardview.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        intent.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
            galleryAddPic();
            File f = new File(mCurrentPhotoPath);
            File compressed = compress(f);
            upLoadImage(compressed);
        }
        else if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String [] proj={MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query( selectedImageUri,
                    proj, // Which columns to return
                    null,       // WHERE clause; which rows to return (all rows)
                    null,       // WHERE clause selection arguments (none)
                    null); // Order-by clause (ascending by name)
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            String filePath = cursor.getString(column_index);
            File f = new File(filePath);
            File compressed = compress(f);
            upLoadImage(compressed);
        }
    }

    public File compress(File f){


        try {
            File compressedImage = new Compressor(this)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(f);

            return compressedImage;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ProfileActivity.this,"could not compress image",Toast.LENGTH_SHORT).show();
            Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

        }
        return f;
    }

    public void upLoadImage(File f){
        Uri contentUri = Uri.fromFile(f);
        StorageReference photoRef = mChatPhotosStorageReference.child(contentUri.getLastPathSegment());
        photoRef.putFile(contentUri).addOnSuccessListener
                (this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();


                        UpdateProfile(downloadUrl);
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this,"could not upload Image",Toast.LENGTH_SHORT).show();
                Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        TextView name = (TextView)findViewById(R.id.nam);
        im = (ImageView) findViewById(R.id.backdrop);
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        try {
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(im);
        } catch (Exception e) {
            e.printStackTrace();
        }
        collapsingToolbar.setBackground(ContextCompat.getDrawable(this,R.drawable.side_nav_bar));
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(intent.getVisibility() == View.VISIBLE){
            intent.setVisibility(View.GONE);
            //fab.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void UpdateProfile(final Uri uri){


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this,"Profile Updated",Toast.LENGTH_LONG).show();
                            try {
                                Glide.with(ProfileActivity.this).load(uri).into(im);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trans, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            enableEditing();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Wallet) {
            // Handle the camera action
            Intent intent = new Intent(ProfileActivity.this, accountActivity.class);
            //intent.putExtra("Address",getAddress(location.getLatitude(),location.getLongitude()));
            startActivity(intent);

        } else if (id == R.id.Order) {
            // Handle the camera action
            Intent intent = new Intent(ProfileActivity.this, TransActivity.class);
            //intent.putExtra("Address",getAddress(location.getLatitude(),location.getLongitude()));
            startActivity(intent);

        }
        else if (id == R.id.Home) {
            // Handle the camera action
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            //intent.putExtra("Address",getAddress(location.getLatitude(),location.getLongitude()));
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            logout.setLogoutCode(4);
            DialogFragment exit = new exit();
            exit.show(getFragmentManager(),"Exit");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        session.setLogin(false);
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        Toast.makeText(ProfileActivity.this,"logout",Toast.LENGTH_SHORT).show();
                    }
                });
    }



    protected void showDialog() {
        if (pg.getVisibility() == View.GONE)
            pg.setVisibility(View.VISIBLE);
    }

    protected void hideDialog() {
        if (pg.getVisibility() == View.VISIBLE)
            pg.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentInteraction() {
        //showProfileFragment profileFragment = (showProfileFragment) getSupportFragmentManager().findFragmentByTag("SHOW_PROFILE");
        //getSupportFragmentManager().beginTransaction().replace(R.id.prof_cont,profileFragment).commit();
        this.onBackPressed();
        fab.setVisibility(View.GONE);
    }
}
