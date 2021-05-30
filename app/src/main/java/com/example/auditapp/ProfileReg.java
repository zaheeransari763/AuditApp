package com.example.auditapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileReg extends AppCompatActivity
{
    private CircleImageView SetUserImage;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private static final int GALLERY_PICK = 1;
    private EditText Name, Phone;
    private Button register;

    // Storage Firebase
    private StorageReference mImageStorage;
    private Uri imageUri;
    private StorageTask uploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_reg);

        mAuth = FirebaseAuth.getInstance();
        Name = (EditText) findViewById(R.id.name_reg);
        Phone = (EditText) findViewById(R.id.phone_reg);

        SetUserImage = (CircleImageView) findViewById(R.id.profile_photo);

        register = (Button) findViewById(R.id.reg_button);

        SetUserImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegUser();
            }
        });


        SetUserImage = (CircleImageView) findViewById(R.id.profile_photo);
        loadingBar = new ProgressDialog(this);

        String currentUserID = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        mUserDatabase.keepSynced(true);

        /*mUserDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
               final String image = dataSnapshot.child("image").getValue().toString();
                if(!image.equals("default"))
                {
                    Picasso.with(ProfileReg.this).load(image).placeholder(R.drawable.profile).into(SetUserImage);
                    Picasso.with(ProfileReg.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(SetUserImage, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(ProfileReg.this).load(image).placeholder(R.drawable.profile).into(SetUserImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        mImageStorage = FirebaseStorage.getInstance().getReference();
    }
    private void RegUser()
    {
        final String name = Name.getText().toString();
        final String contact = Phone.getText().toString();
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Enter The Name", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(contact))
        {
            Toast.makeText(this, "Enter The Phone number", Toast.LENGTH_SHORT).show();
        }
        else
            {
            loadingBar.setTitle("Sign Up Status");
            loadingBar.setMessage("please wait");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

                HashMap<String, String> shopMap = new HashMap();
                shopMap.put("Name", name);
                shopMap.put("Contact", contact);
                shopMap.put("image", "default");
                mUserDatabase.setValue(shopMap).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            SendUserToDashBoard();
                            Toast.makeText(ProfileReg.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(ProfileReg.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

            }
    }

    private void SendUserToDashBoard()
    {
        startActivity(new Intent(this,Profile_upload.class));
        finish();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress())
            {
                Toast.makeText(this, "Upload is in progress", Toast.LENGTH_SHORT).show();
            } else
            {
                uploadImage();
            }
        }
    }*/

    /*private void uploadImage() {
        loadingBar.setMessage("Uploading");
        loadingBar.show();
        if (imageUri != null)
        {
            String current_uid = mAuth.getUid();
            final StorageReference fileReference = mImageStorage.child("images").child(current_uid + "." + getFileExtension(imageUri));
            //final StorageReference fileReference = mImageStorage.child("images" + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        String current_uid = mAuth.getUid();
                        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(current_uid);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", mUri);
                        mUserDatabase.updateChildren(map);
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(ProfileReg.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(ProfileReg.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }*/


}


