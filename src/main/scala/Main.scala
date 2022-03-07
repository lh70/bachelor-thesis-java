import java.io.IOException

import rescala.default.*

object Main {
  def main(args: Array[String]): Unit = {
    val ip         = "192.168.2.124"
    val port       = 8090
    val sensorKind = "dummy"
    val python     = new Client(ip, port)
    python.removeAssignment()
    python.distributeAssignment(sensorKind)
    val pipeline = python.requestPipeline()

    val evt = Evt[String]()
    evt.observe(println)

    while true do
      val msg = pipeline.receive()
      evt.fire(msg)
  }
}
