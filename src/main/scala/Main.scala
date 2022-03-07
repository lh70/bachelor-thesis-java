import java.io.IOException

import rescala.default.*

object Main {
  def main(args: Array[String]): Unit = {
    print(Messages.getAssignmentMessage("dummy"))  // default key "type":"output" is missing on OutputPipeline
    print("\n")

    print(Messages.getRemoveAssignmentMessage)  // default keys "processing":[] and "pipelines":{} are missing on Assignment
    print("\n")

    print(Messages.getPipelineRequestMessage)  // default key "values-per-time-frame":0 is missing on RequestInputPipelineDef
    print("\n")


    val t = """
    val ip         = "127.0.0.1"
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
    """
  }
}
