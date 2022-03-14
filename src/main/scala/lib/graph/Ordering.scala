package lib.graph

import scala.collection.mutable

object Ordering {
  // holds sorted node list in reverse order
  private val node_stack: mutable.ListBuffer[objects.Node] = mutable.ListBuffer()
  // device order is inherited from the node order
  private val device_stack: mutable.ListBuffer[objects.Device] = mutable.ListBuffer()
  // dict to store visited information about each node
  private val visited: mutable.Map[objects.Node, Boolean] = mutable.Map()

  def topologicalSort: (List[objects.Device], List[objects.Node]) = {
    node_stack.clear()
    device_stack.clear()
    visited.clear()

    for (node <- objects.Node.instances) visited(node) = false

    // simply iterate over all nodes. Gives one possible topologically correct order.
    for (node <- objects.Node.instances) {
      if (!visited(node)) {
        recursiveUtil(node)
      }
    }

    // reversed stack is correct order where input nodes to the graph are first
    (device_stack.reverse.toList, node_stack.reverse.toList)
  }

  // recursive function for depth first behaviour
  private def recursiveUtil(node: objects.Node): Unit = {
    visited(node) = true

    var idx = 0
    while (node.edges.contains("out" + idx)) {
      if (!visited(node.edges("out" + idx).nodeTo)) {
        recursiveUtil(node.edges("out" + idx).nodeTo)
      }
      idx += 1
    }

    // add node after each following node is already added
    node_stack += node

    // a device may be the same for multiple nodes, so only add device if not already in stack
    // warning: currently there is no check for circular device dependencies
    if (!device_stack.contains(node.device)) {
      device_stack += node.device
    }
  }
}
