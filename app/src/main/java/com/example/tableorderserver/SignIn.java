package com.example.tableorderserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tableorderserver.Common.Common;
import com.example.tableorderserver.Module.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignIn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        //initialise database
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        });
    }

    private void signInUser(final String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please Wait....");
        mDialog.show();

        final String localPhone = phone;
        final  String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(phone).exists()){
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff())){
                        if (user.getPassword().equals(localPassword)){
                            Intent login = new Intent(SignIn.this, Home.class);
                            Common.currentUser = user;
                            startActivity(login);
                        }
                        else {
                            Toast.makeText(SignIn.this,"Wrong password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(SignIn.this, "Please login with correct details", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this,"User do not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
