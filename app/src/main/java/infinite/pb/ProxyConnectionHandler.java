package infinite.pb;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ProxyConnectionHandler implements Runnable {
    private static final int BUFFER_SIZE = 8192;

    Socket mProxySocket;
    Socket mOutsideSocket;


    public ProxyConnectionHandler(Socket proxySocket) {
        mProxySocket = proxySocket;
    }



    @Override
    public void run() {
        try{
            long startTimestamp = System.currentTimeMillis();

            InputStream proxyInputStream = mProxySocket.getInputStream();

            byte[] bytes = new byte[BUFFER_SIZE];
            int bytesRead = proxyInputStream.read(bytes, 0, BUFFER_SIZE);
            String request = new String(bytes);
            String host = extractHost(request);
            Log.d("**~~~** Request Host: ", host);
             MyProxyServer.addToBag(host);
            int port = request.startsWith("CONNECT") ? 443 : 80;

            if (port == 443) {
                Log.d("***:","443");
                new Https443RequestHandler(mProxySocket).handle(host);
            } else {

                Log.d("***:","80");
                mOutsideSocket = new Socket(host, port);
                OutputStream outsideOutputStream = mOutsideSocket.getOutputStream();
                outsideOutputStream.write(bytes, 0, bytesRead);
                outsideOutputStream.flush();

                InputStream outsideSocketInputStream = mOutsideSocket.getInputStream();
                OutputStream proxyOutputStream = mProxySocket.getOutputStream();
                byte[] responseArray = new byte[BUFFER_SIZE];

                do {
                    bytesRead = outsideSocketInputStream.read(responseArray, 0, BUFFER_SIZE);
                    if (bytesRead > 0) {
                        proxyOutputStream.write(responseArray, 0, bytesRead);
                        String response = new String(bytes, 0, bytesRead);
                        Log.d("Outside IPS Response: ", response);
                    }
                } while (bytesRead > 0);


                proxyOutputStream.flush();
                mOutsideSocket.close();
            }
            mProxySocket.close();

            Log.d("ACHTUNG", "Cycle: " + (System.currentTimeMillis() - startTimestamp));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String extractHost(String request) {
        if (request.contains("Host: ")) {
            int hStart = request.indexOf("Host: ") + 6;
            int hEnd = request.indexOf(':', hStart)+1;
            if(hEnd< hStart)
            {
                hEnd = request.indexOf('\n', hStart);
            }
            return request.substring(hStart, hEnd -1 );
        }
        else {
            Log.d("PPPPPP:", request);
            return "No Host";
        }

    }

}
