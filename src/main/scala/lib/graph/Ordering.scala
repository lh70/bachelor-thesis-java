package lib.graph

import lib.graph.objects.Node
import lib.graph.objects

import scala.collection.mutable

object Ordering {

  def topologicalSort(nodes: List[Node]): (List[objects.Device], List[objects.Node]) = {
    // contains the current nodes to traverse -> if empty -> total graph was traversed
    val traversalNodeList: mutable.ListBuffer[objects.Node] = mutable.ListBuffer()

    // filled with all unique edges
    var edgesSet: Set[objects.Edge] = Set()

    // get all source nodes to start traversal
    for (node <- nodes) {
      var isSource: Boolean = true

      for (edge <- node.edges) {
        if (edge._1.startsWith("in")) {
          isSource = false
          // no break in scala?
        }
      }

      if (isSource) {
        traversalNodeList += node
        edgesSet ++= node.edges.values
      }
    }

    // starts with all unique edges -> should be empty after total graph traversal
    val edges: mutable.ListBuffer[objects.Edge] = mutable.ListBuffer.empty ++ edgesSet.toList

    // nodes are added in topologically correct order (from sources to sinks)
    val resultNodeList: mutable.ListBuffer[objects.Node] = mutable.ListBuffer()

    // move through whole graph
    while (traversalNodeList.nonEmpty) {
      val node: objects.Node = traversalNodeList.remove(0)
      resultNodeList += node

      // potential next nodes to add to traversal list are the current nodes outgoing edge nodes
      var idxOut: Int = 0
      while (node.edges.contains("out" + idxOut)) {
        val nextEdge: objects.Edge = node.edges("out" + idxOut)

        // skip this next-node if this branch was already covered
        if (edges.contains(nextEdge)) {
          // first occurrence is only occurrence due to Set filtering
          edges.remove(edges.indexOf(nextEdge))

          val nextNode: objects.Node = nextEdge.nodeTo

          // also skip node if this is not the longest way to this node and there is another ingoing edge that will reach it
          var noOtherIncomingEdges: Boolean = true
          var idxIn: Int = 0
          while (nextNode.edges.contains("in" + idxIn)) {
            if (edges.contains(nextNode.edges("in" + idxIn))) {
              noOtherIncomingEdges = false
            }

            idxIn += 1
          }

          if (noOtherIncomingEdges) {
            traversalNodeList += nextNode
          }
        }

        idxOut += 1
      }

    }

    if (edges.isEmpty) {
      (getDeviceList(resultNodeList.toList), resultNodeList.toList)
    } else {
      throw new Exception("circular dependencies in graph")
    }
  }

  private def getDeviceList(sortedNodeList: List[Node]): List[objects.Device] = {
    val result: mutable.ListBuffer[objects.Device] = mutable.ListBuffer()

    // this algorithm is far from optimal, but it works
    for (node <- sortedNodeList) {
      if (result.contains(node.device)) {
        val devicesInFront: mutable.ListBuffer[objects.Device] = mutable.ListBuffer()
        val devicesInBack: mutable.ListBuffer[objects.Device] = mutable.ListBuffer()

        for (i <- result.indexOf(node.device)+1 until result.length) {
          if (isDeviceInSourcesOf(node, result(i))) {
            devicesInFront += result(i)
          } else {
            devicesInBack += result(i)
          }
        }

        result.take(result.indexOf(node.device))  // exclusive node.device
        result ++= devicesInFront
        result += node.device
        result ++= devicesInBack
      } else {
        result += node.device
      }
    }

    result.toList
  }

  private def isDeviceInSourcesOf(node: objects.Node, device: objects.Device): Boolean = {
    if (node.device == device) {
      return true
    }

    var isDeviceInSources: Boolean = false

    var idxIn: Int = 0
    while (node.edges.contains("in" + idxIn)) {
      val nodeFrom: objects.Node = node.edges("in" + idxIn).nodeFrom
      isDeviceInSources |= isDeviceInSourcesOf(nodeFrom, device)

      idxIn += 1
    }

    isDeviceInSources
  }
}
