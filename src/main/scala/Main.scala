import java.io.IOException
import rescala.default.*

import java.net.Socket

object Main {
  def main(args: Array[String]): Unit = {
    val ip         = "192.168.2.163"
    val port       = 8090

    val conn = new Connection(new Socket(ip, port))

    conn.sendControlMessage(Messages.getRemoveAssignmentMessage("A0"))

    conn.sendControlMessage(Messages.getAddAssignmentMessage("A0", "dummy"))

    conn.sendControlMessage(Messages.getRequestPipelineMessage("A0", 100))

    val evt = Evt[List[Int]]()
    evt.observe((e:List[Int]) => println(e.length))

    while true do
      val msg = conn.receive()

      val values = upickle.default.read[List[Int]](msg)

      evt.fire(values)
  }
}
