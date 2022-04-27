package lib.graph

import lib.graph.objects.{Device, Edge, Node}
import ujson.{Arr, Obj}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Graph(val devices: List[Device], val nodes: List[Node]) {
  val edges: List[Edge] = nodes.flatMap(_.edges.values).distinct

  def buildDistribution(assignmentId: String): (List[objects.Device], mutable.Map[objects.Device, Obj]) = {
    val distribution: mutable.Map[objects.Device, Obj] = mutable.Map()

    for (device <- devices) {
      distribution(device) = Obj("id" -> assignmentId, "pipelines" -> Obj(), "processing" -> Arr())
    }

    // distribution gets enriched
    val orderedDevices = _buildProcessing(distribution)
    _buildPipelines(distribution)

    (orderedDevices, distribution)
  }

  private def _buildProcessing(distribution: mutable.Map[objects.Device, Obj]): List[objects.Device] = {
    // get a topological correct order of the nodes
    val (orderedDevices, orderedNodes) = Ordering.topologicalSort(nodes)

    for (node <- orderedNodes) {
      distribution(node.device)("processing").arr += node.getSerializable
    }

    orderedDevices
  }

  private def _buildPipelines(distribution: mutable.Map[objects.Device, Obj]): Unit = {
    for (edge <- edges) {
      val (deviceFrom, pipelineFrom, deviceTo, pipelineTo) = edge.getSerializable

      distribution(deviceFrom)("pipelines").obj(edge.id) = pipelineFrom
      distribution(deviceTo)("pipelines").obj(edge.id) = pipelineTo
    }
  }

}
