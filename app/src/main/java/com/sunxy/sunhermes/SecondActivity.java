package com.sunxy.sunhermes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sunxy.hermes.core.SunHermes;
import com.sunxy.hermes.core.service.HermesService;
import com.sunxy.sunhermes.dao.IUserManager;
import com.sunxy.sunhermes.dao.Person;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SunHermes.getDefault().connect(this, HermesService.class);
    }

    public void start(View view){

    }

    public void getInfo(View view){
        IUserManager instance = SunHermes.getDefault().getInstance(IUserManager.class);
        instance.setPerson(new Person("sunxiaoyu", "123456"));
    }
}
