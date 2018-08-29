package com.sunxy.sunhermes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sunxy.hermes.core.SunHermes;
import com.sunxy.sunhermes.dao.FileManager;
import com.sunxy.sunhermes.dao.IFileManager;
import com.sunxy.sunhermes.dao.IUserManager;
import com.sunxy.sunhermes.dao.Person;
import com.sunxy.sunhermes.dao.UserManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SunHermes.getDefault().register(UserManager.class);
        SunHermes.getDefault().register(FileManager.class);
    }

    public void start(View view){
        FileManager.getInstance().setPath("--11223344556677889900--");
        startActivity(new Intent(this, SecondActivity.class));

    }

    public void getInfo(View view){
        Person person = UserManager.getInstance().getPerson();
        if (person != null){
            Toast.makeText(this, person.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
