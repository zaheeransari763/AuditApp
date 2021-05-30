package com.example.auditapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SavingsActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private EditText savamt;
    private Button savupdate;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("MySavings").child(currentUserId);

        savamt=(EditText)findViewById(R.id.savingsamount);
        savupdate=(Button)findViewById(R.id.savingsamountbtn);

        savupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savings();
            }
        });

    }

    private void savings() {


        final String saveamt = savamt.getText().toString();

        if (TextUtils.isEmpty(saveamt)) {
            Toast.makeText(this, "Amount is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else {

            /*FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String ClientID = firebaseUser.getUid();

            mUserDatabase = FirebaseDatabase.getInstance().getReference("Savings Amount").child(ClientID);*/

            HashMap<String, Object> ClientMap = new HashMap();
            ClientMap.put("Savings Amount", saveamt);

            mUserDatabase.updateChildren(ClientMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Intent DashMainIntent = new Intent(SavingsActivity.this,Dashboard.class);
                        DashMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(DashMainIntent);
                        Toast.makeText(SavingsActivity.this, "Updated Successfully...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SavingsActivity.this, "Error Occurred ;" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }

    }
}
