package infinite.pb;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Https443RequestHandler implements RequestHandler {

    private static final int BUFFER_SIZE = 8192;
    private static final String CRLF = "\r\n";

    Socket mProxySocket;
    Socket mOutsideSocket;

    public Https443RequestHandler(Socket proxySocket) {
        this.mProxySocket = proxySocket;
    }

    @Override
    public void handle(String request) throws Exception {
        byte[] bytes = request.getBytes();
        int bytesRead = bytes.length;

        String host = extractHost(request);
        int port = 443;

        mOutsideSocket = new Socket();
        mOutsideSocket.setKeepAlive(true);
        mOutsideSocket.connect(new InetSocketAddress(host, port));

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

    private String extractHost(String request) {
        int hStart = request.indexOf("Host: ") + 6;
        int hEnd = request.indexOf('\n', hStart);
        return request.substring(hStart, hEnd - 1);
    }

}
