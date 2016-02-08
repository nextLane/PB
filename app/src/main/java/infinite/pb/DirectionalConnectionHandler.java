package infinite.pb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class DirectionalConnectionHandler extends Thread {
    private final InputStream in;
    private final OutputStream out;

    DirectionalConnectionHandler(final Socket sin, final Socket sout) throws IOException
    {
        in = sin.getInputStream();
        out = sout.getOutputStream();
    }

    @Override
    public void run() {
        final byte[] buf = new byte[4096];
        int count;

        try {
            while ((count = in.read(buf, 0, buf.length)) != -1) {
                String stream = new String(buf);
                out.write(buf, 0, count);
            }
            out.flush();
        } catch (final IOException e) {
            // Just swallow as we can't recover from this
        }
    }
}
