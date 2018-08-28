package com.sunxy.hermes.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.sunxy.hermes.core.service.HermesService;
import com.sunxy.hermes.core.service.Request;
import com.sunxy.hermes.core.service.Responce;
import com.sunxy.hermes.core.service.SunHermeService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class ServiceConnectionManager {
    private static final ServiceConnectionManager ourInstance = new ServiceConnectionManager();

    public static ServiceConnectionManager getInstance() {
        return ourInstance;
    }

    private final ConcurrentHashMap<Class<? extends HermesService>, SunHermeService> mHermesServices = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends HermesService>, HermesServiceConnection> mHermesServiceConnections = new ConcurrentHashMap();


    private ServiceConnectionManager() {}

    public void bind(Context context, String packageName, Class<? extends HermesService> service){
        HermesServiceConnection connection = new HermesServiceConnection(service);
        mHermesServiceConnections.put(service, connection);
        Intent intent;
        if (TextUtils.isEmpty(packageName)){
            intent = new Intent(context, service);
        }else{
            intent = new Intent();
            intent.setClassName(packageName,service.getName());
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    public Responce request(Class<HermesService> sunHermeServiceClass, Request request){
        SunHermeService sunHermeService = mHermesServices.get(sunHermeServiceClass);
        if (sunHermeService != null){
            try {
                return sunHermeService.send(request);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private class HermesServiceConnection implements ServiceConnection{

        private Class<? extends HermesService> mClass;

        public HermesServiceConnection(Class<? extends HermesService> mClass) {
            this.mClass = mClass;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SunHermeService sunHermeService = SunHermeService.Stub.asInterface(service);
            mHermesServices.put(mClass, sunHermeService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mHermesServices.remove(mClass);
        }
    }

}
