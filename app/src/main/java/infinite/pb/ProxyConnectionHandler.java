package infinite.pb;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

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
         /*   ByteBuffer buf = ByteBuffer.wrap(bytes);
            TCP_IP TCP_debug = new TCP_IP(buf);
            TCP_debug.debug();
            String destIP = TCP_debug.getDestination();

            //  Log.d("Host:", TCP_debug.getHostname());
            InetAddress address = InetAddress.getByName(destIP);
            System.out.println("Host address:" + address.getHostAddress()); // Gaunamas IP (185.11.24.36)
            System.out.println("Host name:" + address.getHostName()); // www.15min.lt
*/
            String request = new String(bytes);
            String host = extractHost(request);
            MyProxyServer.addToBag(host);
            int port = request.startsWith("CONNECT") ? 443 : 80;
            Log.d("**~~~** Request Port: ", ""+port);
            Log.d("**~~~** Request: ", request);


            if(host.contains("facebook.com"))
            {
                OutputStream proxyOutputStream = mProxySocket.getOutputStream();
                byte[] responseArray = new byte[BUFFER_SIZE];
                proxyOutputStream.write(responseArray, 0, bytesRead);
               //do nothing
            }

            else {
                if (port == 443) {
                    Log.d("***:", "443");
                    new Https443RequestHandler(mProxySocket).handle(host);
                } else {

                    Log.d("***:", "80");
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
                         //   Log.d("Outside IPS Response: ", response);
                        }
                    } while (bytesRead > 0);


                    proxyOutputStream.flush();
                    mOutsideSocket.close();
                }
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
