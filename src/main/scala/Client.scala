import java.io.*
import java.net.Socket

object Client {

  /** Client to communicate with the python framework.
    * Invalid requests can fail on the python side, which will produce an error (logged) there.
    * Currently, the framework then closes the connection without sending any failure information back!
    */
  val MAX_UNSIGNED_INT: Long = (1L << 32) - 1
}

case class Response(ack: Int) derives upickle.default.ReadWriter

class Client(val ip: String, val port: Int) {

  /** Requests the pipeline, which is in the assignment
    *
    * @return Connection which the framework sends its values to
    */
  def requestPipeline(): Connection = {
    val conn = new Connection(new Socket(ip, port))
    // Will elevate the current connection to a pipeline connection
    sendControlMessage(conn, Messages.getPipelineRequestMessage)
    // There will now be a steady stream of values to this socket connection
    conn
  }

  /** Convenience method to send a control message the sets the assignment */
  def distributeAssignment(sensorKind: String): Unit = {
    val conn = new Connection(new Socket(ip, port))

    sendControlMessage(conn, Messages.getAssignmentMessage(sensorKind))

    conn.close()
  }

  /** Convenience method to send a control message that removes the assignment */
  def removeAssignment(): Unit = {
    val conn = new Connection(new Socket(ip, port))

    sendControlMessage(conn, Messages.getRemoveAssignmentMessage)

    conn.close()
  }

  /** Sends a json control message
    *
    * @param message valid json object string
    * @throws IOException on any communication error
    */
  def sendControlMessage(conn: Connection, message: String): Unit = {
    conn.send(message)
    val responseString = conn.receive()
    val response       = upickle.default.read[Response](responseString)
    if (response.ack != 1) throw new IOException("Ack failed. Return object is unexpected: " + response)
  }
}
