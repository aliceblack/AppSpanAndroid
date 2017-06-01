package com.appspan.appspan;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OptionsActivity extends Activity{

    DataBaseHelper DataBase=null;
    String pkg=null;

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public OptionsActivity() {
        this.DataBase=new DataBaseHelper(this);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        setPkg(pkg);


        Intent mainIntent = getIntent();
        final String pkg = mainIntent.getExtras().getString("package options");//key of pair
        Long limit = DataBase.getLimit(pkg);

        final EditText editText=(EditText)findViewById(R.id.edit_limit);
        final Button btSet= (Button) findViewById(R.id.button_set);



        //back button
        final Button btBack= (Button) findViewById(R.id.button_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(getApplication(),"Back",Toast.LENGTH_SHORT);
                toast.show();

                Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);//context, class
                startActivity(intentMain);

            }});



        if( limit != -1 ){//limit already exists
            final TextView textViewCurrent = (TextView)findViewById(R.id.options_current);

            textViewCurrent.setText("Current daily limit for " + pkg + ": " + limit + " minutes");



            //set limit update existing limit
            btSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String newLimitString = String.valueOf(editText.getText());
                    final Long newlimit = Long.parseLong(newLimitString);

                    DataBase.updateLimit(pkg, newlimit);
                    textViewCurrent.setText("Current daily limit for " + pkg + ": " + newlimit.toString() + " minutes");
                    Toast toast=Toast.makeText(getApplication(),"Limit updated",Toast.LENGTH_SHORT);
                    toast.show();
                }});



            //delete
            final Button btDelete= (Button) findViewById(R.id.button_delete);
            btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBase.deleteLimit(pkg);
                    Toast toast=Toast.makeText(getApplication(),"Limit deleted",Toast.LENGTH_SHORT);
                    toast.show();
                    btDelete.setVisibility(View.INVISIBLE);
                    textViewCurrent.setText("Limit Deleted");
                }});
            btDelete.setVisibility(View.VISIBLE);


        }
        else{//limit does not exist, equals -1

            //set limit adds a new limit in db
            btSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String newLimitString = String.valueOf(editText.getText());
                    final Long newlimit = Long.parseLong(newLimitString);

                    final TextView textViewCurrent = (TextView)findViewById(R.id.options_current);
                    textViewCurrent.setText("Current daily limit for " + pkg + ": " + newlimit.toString() + " minutes");

                    DataBase.addLimit(pkg, newlimit);
                    Toast toast=Toast.makeText(getApplication(),"Limit set",Toast.LENGTH_SHORT);
                    toast.show();
                }});
        }


    }
}
