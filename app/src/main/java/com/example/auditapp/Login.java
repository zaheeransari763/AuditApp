package com.example.auditapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private EditText mPhoneText;
    private EditText mCodeText;
    private Button mSendButton;
    private FirebaseAuth mAuth;
    private int btnType = 0;
    private String mVerificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPhoneText = (EditText) findViewById(R.id.phone_text);
        mCodeText = (EditText) findViewById(R.id.code_text);
        mSendButton = (Button) findViewById(R.id.SendButton);
        mAuth = FirebaseAuth.getInstance();


        mSendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(btnType == 0)
                {

                    mPhoneText.setEnabled(false);
                    mSendButton.setEnabled(false);
                    String phoneNo = mPhoneText.getText().toString();
                    String phonenumber = "+91" + phoneNo;
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phonenumber,
                            60,
                            TimeUnit.SECONDS,
                            Login.this,
                            mCallBacks
                    );
                }
                else
                {
                    mSendButton.setEnabled(false);
                    String verificationCode = mCodeText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(Login.this, "Error Occurred: Verification Failed...", Toast.LENGTH_SHORT).show();
                mPhoneText.setEnabled(true);
                mSendButton.setEnabled(true);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token)
            {
                mVerificationId = verificationId;
                mResendToken = token;

                btnType = 1;

                mSendButton.setText("Verify Code");
                mSendButton.setEnabled(true);
            }
        };
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();

        if (currentUser!=null){


            Intent intent=new Intent(Login.this,Dashboard.class);
            startActivity(intent);

        }
    }*/



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = task.getResult().getUser();
                            Intent mainintent = new Intent(Login.this,ProfileReg.class);
                            startActivity(mainintent);
                            finish();
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(Login.this, "Error Occurred:" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
