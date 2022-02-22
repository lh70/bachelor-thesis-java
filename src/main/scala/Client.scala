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
    conn.send(Messages.getPipelineRequestMessage)
    // There will now be a steady stream of values to this socket connection
    conn
  }

  /** Convenience method to send a control message the sets the assignment */
  def distributeAssignment(sensorKind: String): Unit = sendControlMessage(Messages.getAssignmentMessage(sensorKind))

  /** Convenience method to send a control message that removes the assignment */
  def removeAssignment(): Unit = sendControlMessage(Messages.getRemoveAssignmentMessage)

  /** Sends a json control message
    *
    * @param message valid json object string
    * @throws IOException on any communication error
    */
  def sendControlMessage(message: String): Unit = {
    val conn = new Connection(new Socket(ip, port))
    conn.send(message)
    val responseString = conn.receive()
    val response       = upickle.default.read[Response](responseString)
    if (response.ack != 1) throw new IOException("Ack failed. Return object is unexpected: " + response)
    // connection is not needed afterwards
    // counting on intelligent java garbage collector to close socket on error
    conn.close()
  }
}
