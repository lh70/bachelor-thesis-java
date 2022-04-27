package lib.graph.objects

import ujson.Obj

import scala.collection.mutable.ListBuffer

case class Edge(nodeFrom: Node, nodeTo: Node, id: String) {

  def getSerializable: (Device, Obj, Device, Obj) = {
    val pipelineFromDevice = Obj()
    val pipelineToDevice   = Obj()

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

  def apply(nodeFrom: Node, nodeTo: Node): Edge = new Edge(nodeFrom, nodeTo, nextId)
  private var idCounter: Int      = 0

  def nextId =
    val res = Edge.idCounter.toString
    Edge.idCounter += 1
    res
}
