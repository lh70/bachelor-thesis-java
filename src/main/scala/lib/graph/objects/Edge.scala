package lib.graph.objects

import ujson.Obj

import scala.collection.mutable.ListBuffer


class Edge(val nodeFrom: Node, val nodeTo: Node) {
  val id: String = Edge.idCounter.toString
  Edge.idCounter += 1

  Edge.instances += this

  def getSerializable: (Device, Obj, Device, Obj) = {
    val pipelineFromDevice = Obj()
    val pipelineToDevice = Obj()

    if (nodeFrom.device == nodeTo.device) {
      pipelineFromDevice("type") = "local"
      pipelineToDevice("type") = "local"
    } else if (nodeFrom.device.host == nodeTo.device.host) {
      pipelineFromDevice("type") = "output"
      pipelineToDevice("type") = "input"
      pipelineToDevice("host") = "localhost"
      pipelineToDevice("port") = nodeFrom.device.port
      pipelineToDevice("time_frame") = nodeTo.device.maxTimeFrame
      pipelineToDevice("values_per_time_frame") = nodeTo.device.maxValuesPerTimeFrame
    } else {
      pipelineFromDevice("type") = "output"
      pipelineToDevice("type") = "input"
      pipelineToDevice("host") = nodeFrom.device.host
      pipelineToDevice("port") = nodeFrom.device.port
      pipelineToDevice("time_frame") = nodeTo.device.maxTimeFrame
      pipelineToDevice("values_per_time_frame") = nodeTo.device.maxValuesPerTimeFrame
    }

    (nodeFrom.device, pipelineFromDevice, nodeTo.device, pipelineToDevice)
  }
}
object Edge {
  val instances: ListBuffer[Edge] = ListBuffer()
  private var idCounter: Int = 0
}
