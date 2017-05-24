package com.example.saketh.marked;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

public class EditProfile extends AppCompatActivity {

    final DBHandler handler = new DBHandler(this);
    TextView tvEmail, tvPassword, tvCar, tvMake, tvModel, tvColour, tvLicence;
    EditText etEmail, etPassword, etMake, etModel, etColour, etLicence;
    //final Button bSave = (Button) findViewById(R.id.bSave);
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final Button bSave = (Button) findViewById(R.id.bSave);

        //Get username passed down from loginActivity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        User user = new User();
        user = handler.getUser(username); //get user using username

        Car car = new Car();
        final int carId = handler.getCarId(username); //get car id using username
        if (carId != -1){ //if car exists, get car
            car = handler.getCar(carId); //get car using car id
        }



        //Reference textviews with findviews
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        //tvEmail.setPaintFlags(tvEmail.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvPassword = (TextView) findViewById(R.id.tvPassword);
        tvCar = (TextView) findViewById(R.id.tvCar);
        tvMake = (TextView) findViewById(R.id.tvMake);
        tvModel = (TextView) findViewById(R.id.tvModel);
        tvColour = (TextView) findViewById(R.id.tvColour);
        tvLicence = (TextView) findViewById(R.id.tvLicence);

        //SetText textviews
        tvEmail.setText(user.getEmail());
        tvPassword.setText(user.getPassword());
        tvMake.setText(car.getMake());
        tvModel.setText(car.getModel());
        tvColour.setText(car.getColour());
        tvLicence.setText(car.getLicence_plate());

        //Create alert dialogs
        //final AlertDialog.Builder dEmail = new AlertDialog.Builder(this);
        final AlertDialog dEmail = new AlertDialog.Builder(this).create();
        final AlertDialog dPassword = new AlertDialog.Builder(this).create();
        final AlertDialog dMake = new AlertDialog.Builder(this).create();
        final AlertDialog dModel = new AlertDialog.Builder(this).create();
        final AlertDialog dColour = new AlertDialog.Builder(this).create();
        final AlertDialog dLicence = new AlertDialog.Builder(this).create();
        //dEmail.create();

        //Create editTexts to place inside dialog
        etEmail = new EditText(this);
        etPassword = new EditText(this);
        etMake = new EditText(this);
        etModel = new EditText(this);
        etColour = new EditText(this);
        etLicence = new EditText(this);

        //Set layout for dialog
        dEmail.setView(etEmail);
        dPassword.setView(etPassword);
        dMake.setView(etMake);
        dModel.setView(etModel);
        dColour.setView(etColour);
        dLicence.setView(etLicence);

        //on clicking textbox, let user edit contents
        //***EMAIL***
        dEmail.setTitle("Please edit your Email");
        //dEmail.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
        dEmail.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvEmail.setText(etEmail.getText());
                dEmail.dismiss();

            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etEmail.setText(tvEmail.getText());
                dEmail.show();
            }
        });


        //on clicking textbox, let user edit contents
        //***PASSWORD***
        dPassword.setTitle("Please edit your password");
        dPassword.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvPassword.setText(etPassword.getText());
                dPassword.dismiss();
            }
        });

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPassword.setText(tvPassword.getText());
                dPassword.show();
            }
        });


        //on clicking textbox, let user edit contents
        //***MAKE***
        dMake.setTitle("Enter the make of your car");
        dMake.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvMake.setText(etMake.getText());
                dMake.dismiss();
            }
        });

        tvMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etMake.setText(tvMake.getText());
                dMake.show();
            }
        });


        //on clicking textbox, let user edit contents
        //***MODEL***
        dModel.setTitle("Enter the model of your car");
        dModel.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvModel.setText(etModel.getText());
                dModel.dismiss();
            }
        });

        tvModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etModel.setText(tvModel.getText());
                dModel.show();
            }
        });


        //on clicking textbox, let user edit contents
        //***COLOUR***
        dColour.setTitle("Enter the colour of your car");
        dColour.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvColour.setText(etColour.getText());
                dColour.dismiss();
            }
        });

        tvColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etColour.setText(tvColour.getText());
                dColour.show();
            }
        });

        //on clicking textbox, let user edit contents
        //***lICENCE PLATE***
        dLicence.setTitle("Enter your licence plate number");
        dLicence.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvLicence.setText(etLicence.getText());
                dLicence.dismiss();
            }
        });

        tvLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etLicence.setText(tvLicence.getText());
                dLicence.show();
            }
        });

        /*
        //NOW UPDATE DB
        handler.updateUser(username, tvEmail.getText().toString(), tvPassword.getText().toString());
        if(carId != -1) { // if car exists in db then update
            handler.updateCar(carId, tvMake.getText().toString(), tvModel.getText().toString(), tvColour.getText().toString(), tvLicence.getText().toString());
        }else { // if car does not exist in db, then add car
            long id = handler.insertCar(tvMake.getText().toString(), tvModel.getText().toString(), tvColour.getText().toString(), tvLicence.getText().toString(), -1, -1, 0);
            handler.insertOwnedBY(username, (int)id); //insert relationsip of carid and username
        }
        */

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NOW UPDATE DB
                String token = FirebaseInstanceId.getInstance().getToken();
                handler.updateUser(username, tvEmail.getText().toString(), tvPassword.getText().toString());
                if(carId != -1) { // if car exists in db then update
                    handler.updateCar(carId, tvMake.getText().toString(), tvModel.getText().toString(), tvColour.getText().toString(), tvLicence.getText().toString());
                    handler.registerToken(carId, token);
                }else { // if car does not exist in db, then add car
                    long id = handler.insertCar(tvMake.getText().toString(), tvModel.getText().toString(), tvColour.getText().toString(), tvLicence.getText().toString(), -1, -1, 0, token);
                    handler.insertOwnedBY(username, (int)id); //insert relationsip of carid and username
                }


                Intent exitIntent = new Intent(EditProfile.this, MapsActivity.class);
                exitIntent.putExtra("username", username); //pass the username to mapsActivity
                EditProfile.this.startActivity(exitIntent);
            }
        });
    }
}
