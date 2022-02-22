import java.io.{DataInputStream, IOException, OutputStream}
import java.lang.Integer.toUnsignedLong
import java.net.Socket
import java.nio.charset.StandardCharsets

class Connection(socket: Socket) {

  /** Sends a message with correct encoding and so on */
  def send(message: String) = { // message must be utf-8 encoded
    val encodedMessage = message.getBytes(StandardCharsets.UTF_8)
    // length cannot exceed signed int length, so max-unsigned-int is not checked atm
    val length = encodedMessage.length
    // build unsigned int, as Java does not support unsigned variables

    val uInt = new Array[Byte](4)
    uInt(0) = ((length & 0x00000000ff000000L) >> 24).toByte
    uInt(1) = ((length & 0x0000000000ff0000L) >> 16).toByte
    uInt(2) = ((length & 0x000000000000ff00L) >> 8).toByte
    uInt(3) = (length & 0x00000000000000ffL).toByte
    val out = socket.getOutputStream
    out.write(uInt)
    out.write(encodedMessage)
    out.flush()
  }

  /** Receives a message with correct encoding and so on */
  def receive() = { // DataInputStream has some convenience methods
    val in = new DataInputStream(socket.getInputStream)
    // preamble is 4 bytes long
    val uInt = in.readInt
    // convert to long for correct unsigned integer value
    val length = toUnsignedLong(uInt)
    // cannot allocate more than Integer.MAX_VALUE array space
    // this is only half of what a message can theoretically be
    if (length > Integer.MAX_VALUE) { // cannot handle this atm
      throw new IOException("Message is longer than " + Integer.MAX_VALUE + " bytes, which is Java array maximum.")
    }
    val intLength     = length.toInt
    val responseBytes = new Array[Byte](intLength)
    in.readFully(responseBytes)
    // message is utf-8 encoded
    new String(responseBytes, StandardCharsets.UTF_8)
  }
  
  def close() = socket.close()
}
