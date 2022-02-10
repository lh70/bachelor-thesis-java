import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connection {

    private final Socket socket;

    public Connection(Socket socket) {
        this.socket = socket;
    }

    /**
     * Sends a message with correct encoding and so on
     */
    public void send(String message) throws IOException {
        // message must be utf-8 encoded
        byte[] encodedMessage = message.getBytes(StandardCharsets.UTF_8);

        // length cannot exceed signed int length, so max-unsigned-int is not checked atm
        long length = encodedMessage.length;

        // build unsigned int, as Java does not support unsigned variables
        byte[] uInt = new byte[4];
        uInt[0] = (byte) ((length & 0x00000000FF000000L) >> 24);
        uInt[1] = (byte) ((length & 0x0000000000FF0000L) >> 16);
        uInt[2] = (byte) ((length & 0x000000000000FF00L) >> 8);
        uInt[3] = (byte) ((length & 0x00000000000000FFL));

        OutputStream out = socket.getOutputStream();

        out.write(uInt);
        out.write(encodedMessage);
        out.flush();
    }

    /**
     * Receives a message with correct encoding and so on
     */
    public String receive() throws IOException {
        // DataInputStream has some convenience methods
        DataInputStream in = new DataInputStream(socket.getInputStream());

        // preamble is 4 bytes long
        int uInt = in.readInt();

        // convert to long for correct unsigned integer value
        long length = uInt & 0xffffffffL;

        // cannot allocate more than Integer.MAX_VALUE array space
        // this is only half of what a message can theoretically be
        if (length > Integer.MAX_VALUE) {
            // cannot handle this atm
            throw new IOException("Message is longer than " + Integer.MAX_VALUE + " bytes, which is Java array maximum.");
        }
        int intLength = (int) length;

        byte[] responseBytes = new byte[intLength];
        in.readFully(responseBytes);

        // message is utf-8 encoded
        return new String(responseBytes, StandardCharsets.UTF_8);
    }

    public void close() throws IOException {
        socket.close();
    }
}
