import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import infinite.pb.MyProxyServer;
import infinite.pb.R;


public class Vpn extends VpnService implements Handler.Callback, Runnable {

    private static final String TAG = "Vpn";
    private String mServerAddress;
    private String mServerPort;

    private Handler mHandler;
    private Thread mThread;

    private ParcelFileDescriptor mInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread.interrupt();
        }

        mServerAddress = "127.0.0.1";
        mServerPort = "8088";  //Practically any would do here, TODO: Add the case if chosen port is busy.

        // Start a new session by creating a new thread.
        mThread = new Thread(this, "VpnSession");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    //VPN Session thread runs
    @Override
    public synchronized void run() {

        try {

            Log.i(TAG, "Starting");
            InetSocketAddress server = new InetSocketAddress(mServerAddress, Integer.parseInt(mServerPort));
            run(server);
            Thread.sleep(3000);
            Log.i(TAG, "Server set okay !");

        } catch (Exception e) {

            Log.e(TAG, "Got " + e.toString());

        }
    }


    private boolean run(InetSocketAddress server) throws Exception {
        final int BUFFER_SIZE = 8192;  //largest size of MTU
        boolean connected= false;
        try {

            configure();
            connected=true;
            mHandler.sendEmptyMessage(R.string.connected);

            // Packets to be sent are queued in this input stream.
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
            // Packets received need to be written to this output stream.
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());
            // Allocate the buffer for a single packet.

            Log.i(TAG, "Starting forwarding packets !");

            while (true) {

                ByteBuffer packet = ByteBuffer.allocate(BUFFER_SIZE);
                int length;

                try
                {
                    // Read the outgoing packet from the input stream.
                    while ((length = in.read(packet.array())) > 0) {

                        /* echoSocket must be replaced by protected socket
                        Socket echoSocket = new Socket("::1", 8090);
                        OutputStream inputToProxy =
                                (echoSocket.getOutputStream());

                        //    InputStream outputFromProxy =
                        //          (echoSocket.getInputStream());

                        */
                        packet.limit(length);
                        TCP_IP TCP_debug = new TCP_IP(packet);
                        TCP_debug.debug();
                        String destIP = TCP_debug.getDestination();
                        InetAddress address = InetAddress.getByName(destIP);
                        System.out.println("Host address:" + address.getHostAddress());
                        System.out.println("Host name:" + address.getHostName());
                        //inputToProxy.write(packet.array(), 0, length);

                        packet.clear();

                        Thread.sleep(100);
                        //InputStream outputFromProxy = (echoSocket.getInputStream());

                        /* To be implemented
                        int len=0;
                        while((len=outputFromProxy.read(packet.array()))>0)
                        {


                            String response = new String(packet.array(), 0, len);
                            //byte[] bufferOutput = new byte[32767];
                            //inBuffer.read(bufferOutput);
                            // if (bufferOutput.length > 0) {
                            //      String recPacketString = new String(packet,0,packet.array().length,"UTF-8");
                            Log.d(TAG, "recPacketString");
                            //    FileOutputStream o = new FileOutputStream(mInterface.getFileDescriptor());
                            byte [] arr= {'O','K'};
                            out.write(arr);
                            //     out.write(packet.array());
                            Log.d("vpn fd op:", response);
                            packet.clear();

                            Thread.sleep(100);
                        }
                        */
                       // echoSocket.close();
                    }




                }


                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return connected;
    }

    //This method configures the VPN builder interface on device

    private void configure() throws Exception {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null ) {
            Log.i(TAG, "Using the previous interface");
            return;
        }

        // Create a new interface using the builder and save the parameters.
        // Configure a builder while parsing the parameters.
        Builder builder = new Builder();
        builder.setMtu(1500);
        builder.addAddress("10.0.0.2", 24);
        builder.addRoute("0.0.0.0", 0);

        //  builder.addDnsServer("8.8.8.8");

        mInterface = builder.establish();

        Log.i(TAG, "New interface set");
    }

    //This method works with proper http/s requests
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
            Log.d("TAG", request);
            return "No Host";
        }

    }

}

class TCP_IP extends Vpn {

    private ByteBuffer packet;
    private String hostname;
    private String destIP;
    private String sourceIP;
    private int version;
    private int protocol;
    private int port;


    public TCP_IP(ByteBuffer pack) {
        this.packet = pack;
    }

    public void debug() {


        int buffer = packet.get();
        int headerlength;
        int temp;

        version = buffer >> 4;
        headerlength = buffer & 0x0F;
        headerlength *= 4;
        System.out.println("IP Version:"+version);
        System.out.println("Header Length:"+headerlength);
        String status = "";
        status += "Header Length:"+headerlength;

        buffer = packet.get();      //DSCP + EN
        buffer = packet.getChar();  //Total Length

        System.out.println( "Total Length:"+buffer);

        buffer = packet.getChar();  //Identification
        buffer = packet.getChar();  //Flags + Fragment Offset
        buffer = packet.get();      //Time to Live
        buffer = packet.get();      //Protocol

        protocol = buffer;
        System.out.println( "Protocol:"+buffer);

        status += "  Protocol:"+buffer;

        buffer = packet.getChar();  //Header checksum


        byte buff = (byte)buffer;

        sourceIP  = "";
        buff = packet.get();  //Source IP 1st Octet
        temp = ((int) buff) & 0xFF;
        sourceIP += temp;
        sourceIP += ".";

        buff = packet.get();  //Source IP 2nd Octet
        temp = ((int) buff) & 0xFF;
        sourceIP += temp;
        sourceIP += ".";

        buff = packet.get();  //Source IP 3rd Octet
        temp = ((int) buff) & 0xFF;
        sourceIP += temp;
        sourceIP += ".";

        buff = packet.get();  //Source IP 4th Octet
        temp = ((int) buff) & 0xFF;
        sourceIP += temp;

        System.out.println( "Source IP:"+sourceIP);

        status += "   Source IP:"+sourceIP;


        destIP  = "";


        buff = packet.get();  //Destination IP 1st Octet
        temp = ((int) buff) & 0xFF;
        destIP += temp;
        destIP += ".";

        buff = packet.get();  //Destination IP 2nd Octet
        temp = ((int) buff) & 0xFF;
        destIP += temp;
        destIP += ".";

        buff = packet.get();  //Destination IP 3rd Octet
        temp = ((int) buff) & 0xFF;
        destIP += temp;
        destIP += ".";

        buff = packet.get();  //Destination IP 4th Octet
        temp = ((int) buff) & 0xFF;
        destIP += temp;

        System.out.println( "Destination IP:" + destIP);
        status += "   Destination IP:"+destIP;




    }


    public String getDestination() {
        return destIP;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getIPversion()
    {
        return version;
    }

}

