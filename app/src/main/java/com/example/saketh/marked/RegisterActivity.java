package com.example.saketh.marked;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    final DBHandler handler = new DBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etFullName = (EditText) findViewById(R.id.etFullName);
        final EditText etUserName = (EditText) findViewById(R.id.etUserName);
        final EditText etPass = (EditText) findViewById(R.id.etPass);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);

        final Button bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etFullName.getText().toString();
                final String username = etUserName.getText().toString();
                final String password = etPass.getText().toString();
                final String email = etEmail.getText().toString();

                boolean exists = handler.searchUser(username);
                if(exists == false) { //username does not already exist so create a new user and insert into DB
                    User newUser = new User(username,name,password,email);
                    handler.insertUser(newUser);
                    Toast.makeText(RegisterActivity.this, "You are now registered, please log in", Toast.LENGTH_LONG).show();

                    Intent registeredIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(registeredIntent);
                }else {
                    //popup msg
                    Toast user_already_exists = Toast.makeText(RegisterActivity.this, "This username already exists, please try another", Toast.LENGTH_SHORT);
                    user_already_exists.show();

                }
            }
        });
    }
}
