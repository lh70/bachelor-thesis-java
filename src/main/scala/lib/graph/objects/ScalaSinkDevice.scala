package lib.graph.objects
import lib.Connection
import ujson.Obj

import java.net.Socket
import scala.collection.mutable

class ScalaSinkDevice(maxTimeFrame: Int  = 100, maxValuesPerTimeFrame: Int  = 0)
    extends Device(port = -1, maxTimeFrame = maxTimeFrame, maxValuesPerTimeFrame = maxValuesPerTimeFrame) {

  val pipelines: mutable.Map[String, Connection] = mutable.Map()

  override def removeAssignment(assignmentId: String): Unit = {
    // Do nothing
    // On a remote device this removes the assignment object referenced by assignmentId
    // We do not track assignments by id locally currently
  }

  override def addAssignment(assignment: Obj): Unit = {
    // The scala sink device can hav an arbitrary amount of incoming pipelines, as build by the graph.
    // (As the pipeline-ids are created generically by the graph, we cannot distinguish between them.
    //  As a result, more than one incoming pipeline makes no sense currently)
    //
    // On addAssignment we open those pipeline connections for further use of the values in scala

    val assignmentId = assignment("id").value.asInstanceOf[String]

    for ((pipelineId, info) <- assignment("pipelines").obj) {
      val pipelineHost      = info("host").str
      val pipelinePort      = info("port").num.toInt
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

      conn.sendControlMessage(ujson.write(
        Obj(
          "type" -> "assignment_initialization",
        )
      ))

      pipelines(pipelineId) = conn
    }
  }
}
