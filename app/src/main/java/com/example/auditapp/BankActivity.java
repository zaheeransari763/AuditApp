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

public class BankActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private EditText bankamt;
    private Button bnkupdate;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("MySavings").child(currentUserId);

        loadingBar = new ProgressDialog(this);

        bankamt = (EditText) findViewById(R.id.bankamount);
        bnkupdate = (Button) findViewById(R.id.bankamountbtn);

        bnkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatebank();
            }
        });
    }

    private void updatebank() {

        final String bnk = bankamt.getText().toString();

        if (TextUtils.isEmpty(bnk)) {
            Toast.makeText(this, "Amount is Mandatory...", Toast.LENGTH_SHORT).show();
        } else {

            /*FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String ClientID = firebaseUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference("Bank Amount").child(ClientID);*/

            HashMap<String, Object> ClientMap = new HashMap();
            ClientMap.put("Bank Amount", bnk);

            mUserDatabase.updateChildren(ClientMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Intent DashMainIntent = new Intent(BankActivity.this,Dashboard.class);
                        DashMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(DashMainIntent);
                        Toast.makeText(BankActivity.this, "Updated Successfully...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                     else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(BankActivity.this, "Error Occurred ;" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }


    }

}





