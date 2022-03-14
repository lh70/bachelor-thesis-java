package lib.graph

import ujson.{Arr, Obj}

import scala.collection.mutable

object Graph {

  def clear(): Unit = {
    objects.Device.instances.clear()
    objects.Edge.instances.clear()
    objects.Node.instances.clear()
  }

  def buildDistribution(assignmentId: String): (List[objects.Device], mutable.Map[objects.Device, Obj]) = {
    val distribution: mutable.Map[objects.Device, Obj] = mutable.Map()

    for (device <- objects.Device.instances) {
      distribution(device) = Obj("id" -> assignmentId, "pipelines" -> Obj(), "processing" -> Arr())
    }

    // distribution gets enriched
    val orderedDevices = _buildProcessing(distribution)
    _buildPipelines(distribution)

    (orderedDevices, distribution)
  }

  private def _buildProcessing(distribution: mutable.Map[objects.Device, Obj]): List[objects.Device] = {
    // get a topological correct order of the nodes
    val (orderedDevices, orderedNodes) = Ordering.topologicalSort

    for (node <- orderedNodes) {
      distribution(node.device)("processing").arr += node.getSerializable
    }

    orderedDevices
  }

  private def _buildPipelines(distribution: mutable.Map[objects.Device, Obj]): Unit = {
    for (edge <- objects.Edge.instances) {
      val (deviceFrom, pipelineFrom, deviceTo, pipelineTo) = edge.getSerializable

      distribution(deviceFrom)("pipelines").obj(edge.id) = pipelineFrom
      distribution(deviceTo)("pipelines").obj(edge.id) = pipelineTo
    }
  }

}
