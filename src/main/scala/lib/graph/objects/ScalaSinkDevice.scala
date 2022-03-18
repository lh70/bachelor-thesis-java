package lib.graph.objects
import lib.Connection
import ujson.Obj

import java.net.Socket
import scala.collection.mutable

class ScalaSinkDevice(maxTimeFrame: Int = 100, maxValuesPerTimeFrame: Int = 0) extends Device(port = -1, maxTimeFrame = maxTimeFrame, maxValuesPerTimeFrame = maxValuesPerTimeFrame) {

  val pipelines: mutable.Map[String, Connection] = mutable.Map()

  override def removeAssignment(assignmentId: String): Unit = {
    // Do nothing
  }

  override def addAssignment(distribution: mutable.Map[Device, Obj]): Unit = {
    val assignment = distribution(this)

    val assignmentId = assignment("id").value.asInstanceOf[String]

    for ((pipelineId, info) <- assignment("pipelines").obj) {
      val pipelineHost = info("host").str
      val pipelinePort = info("port").num.toInt
      val pipelineTimeFrame = info("time_frame").num.toInt

      val conn = new Connection(new Socket(pipelineHost, pipelinePort))

      conn.sendControlMessage(ujson.write(
        Obj(
          "type" -> "pipeline_request",
          "content" -> Obj(
            "assignment_id"         -> assignmentId,
            "pipe_id"               -> pipelineId,
            "time_frame"            -> pipelineTimeFrame,
            "values_per_time_frame" -> 0
          )
        )
      ))

      pipelines(pipelineId) = conn
    }
  }
}
