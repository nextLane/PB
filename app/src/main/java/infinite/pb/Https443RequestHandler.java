package infinite.pb;

import android.util.Log;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Https443RequestHandler implements RequestHandler {

    private static final int BUFFER_SIZE = 8192;
    private static final String CRLF = "\r\n";

    Socket mProxySocket;
    Socket mOutsideSocket;

    public Https443RequestHandler(Socket proxySocket) {
        this.mProxySocket = proxySocket;
    }

    @Override
    public void handle(String hostUrl) throws Exception {

        String host = hostUrl;
        int port = 443;

        mOutsideSocket = new Socket();
        mOutsideSocket.setKeepAlive(true);

        try{
            mOutsideSocket.connect(new InetSocketAddress(host, port));
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        OutputStream proxyOutputStream = mProxySocket.getOutputStream();
        proxyOutputStream.write(("HTTP/1.1 200 Connection established" + CRLF + CRLF).getBytes());
        proxyOutputStream.flush();

        DirectionalConnectionHandler client = new DirectionalConnectionHandler(mProxySocket, mOutsideSocket);
        client.start();
        DirectionalConnectionHandler server = new DirectionalConnectionHandler(mOutsideSocket, mProxySocket);
        server.start();

        client.join();
        server.join();

        mOutsideSocket.close();
        mProxySocket.close();
    }

}
