import java.io.{IOException, InputStream, OutputStream}
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.nio.{ByteBuffer, ByteOrder}

class Connection(socket: Socket) {
  private val in = new InputStreamBuffer(socket.getInputStream)
  private val out = socket.getOutputStream

  def toBytes(i: Int): Array[Byte] = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.BIG_ENDIAN).putInt(i).array()
  def toInt(array: Array[Byte]): Int = ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN).getInt

  /** Sends a message with correct encoding and so on */
  def send(message: String): Unit = {
    // message must be utf-8 encoded
    val encodedMessage = message.getBytes(StandardCharsets.UTF_8)
    // length cannot exceed signed int length, so max-unsigned-int is not checked atm
    out.write(toBytes(encodedMessage.length))
    out.write(encodedMessage)
    out.flush()
  }

  /** Receives a message with correct encoding and so on */
  def receive(): String = {
    // preamble is 4 bytes long
    in.readNBytes(4)
    val length = toInt(in.buffer.slice(0, 4))
    // cannot allocate more than Integer.MAX_VALUE array space
    // this is only half of what a message can theoretically be
    if (Integer.compareUnsigned(length, Integer.MAX_VALUE) > 0) { // cannot handle this atm
      throw new IOException("Message is longer than " + Integer.MAX_VALUE + " bytes, which is Java array maximum.")
    }
    // message is utf-8 encoded
    in.readNBytes(length)
    new String(in.buffer.slice(0, length), StandardCharsets.UTF_8)
  }

  def close(): Unit = socket.close()
}

class InputStreamBuffer(val in: InputStream) {
  var buffer = new Array[Byte](1)

  def readNBytes(length: Int): Unit = {
    if (buffer.length < length) {
      buffer = new Array[Byte](length)
    }

    var totalBytesRead = 0
    var currentBytesRead = 0
    while (totalBytesRead < length) {
      currentBytesRead = in.read(buffer, totalBytesRead, length-totalBytesRead)

      if (currentBytesRead == -1) {
        throw new Exception("socket closed")
      }

      totalBytesRead = totalBytesRead + currentBytesRead
    }
  }
}

