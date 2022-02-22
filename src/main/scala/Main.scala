import java.io.IOException

object Main {
  def main(args: Array[String]): Unit = {
    val ip         = "127.0.0.1"
    val port       = 8090
    val sensorKind = "dummy"
    val python     = new Client(ip, port)
    python.removeAssignment()
    python.distributeAssignment(sensorKind)
    val pipeline = python.requestPipeline
    while ({ true }) System.out.println(pipeline.receive)
  }
}
