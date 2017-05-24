package com.example.saketh.marked;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    final DBHandler handler = new DBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUserName = (EditText) findViewById(R.id.etUserName);
        final EditText etPass = (EditText) findViewById(R.id.etPass);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterHere);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final String username = etUserName.getText().toString();
                final String password = etPass.getText().toString();

                String pass = handler.searchPass(username);
                if(password.equals(pass)) { //if password is correct
                    //TODO we need to create a new intent
                    Intent loginIntent = new Intent(LoginActivity.this, MapsActivity.class);
                    loginIntent.putExtra("username", username); //pass the username to mapsActivity
                    LoginActivity.this.startActivity(loginIntent);
                }else {
                    Toast.makeText(LoginActivity.this, "Your login or password don't match, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
