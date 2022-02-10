import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class Client {
    /**
     * Client to communicate with the python framework.
     * Invalid requests can fail on the python side, which will produce an error (logged) there.
     * Currently, the framework then closes the connection without sending any failure information back!
     */

    public static final long MAX_UNSIGNED_INT = 4294967295L;

    private final String ip;
    private final int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Requests the pipeline, which is in the assignment
     *
     * @return Connection which the framework sends its values to
     * @throws IOException on any communication error
     */
    public Connection requestPipeline() throws IOException {
        Connection conn = new Connection(new Socket(ip, port));

        // Will elevate the current connection to a pipeline connection
        conn.send(Messages.getPipelineRequestMessage());

        // There will now be a steady stream of values to this socket connection
        return conn;
    }

    /**
     * Convenience method to send a control message the sets the assignment
     */
    public void distributeAssignment(String sensorKind) throws IOException {
        sendControlMessage(Messages.getAssignmentMessage(sensorKind));
    }

    /**
     * Convenience method to send a control message that removes the assignment
     */
    public void removeAssignment() throws IOException {
        sendControlMessage(Messages.getRemoveAssignmentMessage());
    }

    /**
     * Sends a json control message
     *
     * @param message valid json object string
     * @throws IOException on any communication error
     */
    public void sendControlMessage(String message) throws IOException {
        Connection conn = new Connection(new Socket(ip, port));

        conn.send(message);

        String responseString = conn.receive();

        try {
            JSONObject response = new JSONObject(responseString);

            if (!((int) response.get("ack") == 1)) {
                throw new IOException("Ack failed. Return object is unexpected: " + response);
            }
        } catch (JSONException e) {
            throw new IOException("Ack failed: " + responseString);
        }

        // connection is not needed afterwards
        // counting on intelligent java garbage collector to close socket on error
        conn.close();
    }

}
