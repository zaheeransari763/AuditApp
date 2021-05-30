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

public class ExpenseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    String ClientID;

    private EditText entex,fex,cthex,bilex;
    private Button exupdate, checkExpenseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        ClientID = firebaseUser.getUid();

        checkExpenseBtn = (Button) findViewById(R.id.checkExpenses);
        checkExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToCalculateExpense();
            }
        });

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("MySavings").child(ClientID).child("Expenses");
        ProgressDialog loadingbar = new ProgressDialog(this);

        entex = (EditText) findViewById(R.id.entertainex);
        fex = (EditText) findViewById(R.id.foodex);
        cthex = (EditText) findViewById(R.id.clothex);
        bilex = (EditText) findViewById(R.id.billex);

        exupdate = (Button) findViewById(R.id.expenseamountbtn);

        exupdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                expenseupdate();
            }
        });
    }

    private void SendToCalculateExpense() {
        Intent calculate = new Intent(ExpenseActivity.this,CalculateExpenseActivity.class);
        startActivity(calculate);
    }



    private void expenseupdate()
    {
        final String eex = entex.getText().toString();
        final String foex = fex.getText().toString();
        final String coex = cthex.getText().toString();
        final String bex = bilex.getText().toString();

        if (TextUtils.isEmpty(eex))
        {
            Toast.makeText(this, " Entertainmrnt Amount is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(foex))
        {
            Toast.makeText(this, " Food Amount is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(coex))
        {
            Toast.makeText(this, " Cloth Amount is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(bex))
        {

            Toast.makeText(this, " Bill Amount is Mandatory...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, String> ClientMap = new HashMap();
            ClientMap.put("Entertainment Expense", eex);
            ClientMap.put("Food Expense", foex);
            ClientMap.put("Cloth Expense", coex);
            ClientMap.put("Bill Expense", bex);
            mUserDatabase.setValue(ClientMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        /*Intent DashMainIntent = new Intent(ExpenseActivity.this,CheckBalanceActivity.class);
                        //DashMainIntent.putExtra("Total Expenses",String.valueOf());
                        DashMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(DashMainIntent);*/
                        Toast.makeText(ExpenseActivity.this, "Updated Successfully...", Toast.LENGTH_SHORT).show();
                        ///finish();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(ExpenseActivity.this, "Error Occurred ;" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
