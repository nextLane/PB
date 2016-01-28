package infinite.pb;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MyProxyServer extends Service {

    private final IBinder mBinder = new MyBinder();
    ServerSocket serverSocket = null;

    //TODO: Handle all the cases of server /socket connectivity with suitable toasts

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        Log.d("*******","On service creation!");
        int port = 8091;	//default

        try {
            serverSocket = new ServerSocket(port);
            Log.d("Started on: ", ""+port);
        } catch (IOException e) {
            System.err.println("Could not listen on port!");
            System.exit(-1);
        }

    //TODO: Check if this affects the internet connectivity of the device in general
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean listening = true;
        int res=Service.START_NOT_STICKY;
        try
        {
            new serverWorks().execute(serverSocket);
        }

        catch(Exception e)
        {
            res= Service.START_FLAG_RETRY;
        }

        return res;
    }


    public class MyBinder extends Binder {
        MyProxyServer getService() {
            return MyProxyServer.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}


class serverWorks extends AsyncTask <ServerSocket, Void, Integer> {

    @Override
    protected Integer doInBackground(ServerSocket... params) {
        boolean listening=true;

        while(listening) {
            try {
                Socket proxySocket = params[0].accept();
                ProxyConnectionHandler proxyConnectionHandler = new ProxyConnectionHandler(proxySocket);
                new Thread(proxyConnectionHandler).start();

             } catch (IOException e) {
                e.printStackTrace();
                return Service.START_FLAG_RETRY;
            }

        }
        return Service.START_NOT_STICKY;
    }
}