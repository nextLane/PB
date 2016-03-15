package infinite.pb;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class MyProxyServer extends Service {

    private final IBinder mBinder = new MyBinder();
    ServerSocket serverSocket = null;
    static UrlsDirectory udir;
    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        Log.d("*******","On service creation!");
        int port = 8090;	//default

        try {
            serverSocket = new ServerSocket(port);
            //the value of port is presently hard-coded  make it random

            Log.d("Started on: ", ""+port);
        } catch (IOException e) {
            System.err.println("Could not listen on port!");
            System.exit(-1);
        }

        udir=new UrlsDirectory(getBaseContext());
        udir.fillUrlsBag();   //get all the existant urlsdata in the db
      // udir.printOutBag();
    //TODO: Check if this affects the internet connectivity of the device in general
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
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

//TODO: OnStop print url bag for now, make sure data gets entered in db later on.
// TODO: The hashmap has to be emptied with time. ? Ponder.

    public class MyBinder extends Binder {
        MyProxyServer getService() {
            return MyProxyServer.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static void addToBag(String url)
    {
        udir.dropUrl(url);
    }

    public static void printOutBag()
    {
        udir.printOutBag();
    }

}



class serverWorks extends AsyncTask <ServerSocket, Void, Integer> {

    @Override
    protected Integer doInBackground(ServerSocket... params) {
        boolean listening=true;

        while(listening) {
            try {
                Log.d("count:","New connection made");
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