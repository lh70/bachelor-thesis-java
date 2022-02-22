import java.io.{DataInputStream, DataOutputStream, IOException, OutputStream}
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.nio.{ByteBuffer, ByteOrder}

class Connection(socket: Socket) {


  // alternative methods to read/write ints and bytes
  // byte buffer is a newer api (java.nio) than DataInput/OutputStream ... but its never clear if that is good or bad?
  def toBytes(i: Int): Array[Byte] = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN).putInt(i).array()
  def toInt(array: Array[Byte]): Int = ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN).getInt

  /** Sends a message with correct encoding and so on */
  def send(message: String): Unit = {
    // message must be utf-8 encoded
    val encodedMessage = message.getBytes(StandardCharsets.UTF_8)
    // length cannot exceed signed int length, so max-unsigned-int is not checked atm
    val length = encodedMessage.length
    val out = socket.getOutputStream
    DataOutputStream(out).writeInt(length)
    out.write(encodedMessage)
    out.flush()
  }

  /** Receives a message with correct encoding and so on */
  def receive(): String = {
    // DataInputStream has some convenience methods
    val in = new DataInputStream(socket.getInputStream)
    // preamble is 4 bytes long

    val length = in.readInt()
    // cannot allocate more than Integer.MAX_VALUE array space
    // this is only half of what a message can theoretically be
    if (Integer.compareUnsigned(length, Integer.MAX_VALUE) > 0) { // cannot handle this atm
      throw new IOException("Message is longer than " + Integer.MAX_VALUE + " bytes, which is Java array maximum.")
    }
    // message is utf-8 encoded
    new String(in.readNBytes(length), StandardCharsets.UTF_8)
  }

  def close(): Unit = socket.close()
}
