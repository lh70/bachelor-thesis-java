package lib.graph.objects

import lib.PythonUserNodes.PythonUserNode
import ujson.Obj

import scala.collection.mutable

object Node {
  def scalaSink(device: Device, inputs: Node*): Node = Node(device, PythonUserNode("dummy"), inputs: _*)
}

// kwargs are not allowed to use parameter names that begin with "in" or "out".
class Node(val device: Device, val pythonUserNode: PythonUserNode, inputs: Node*) {

  val edges: mutable.Map[String, Edge] = mutable.Map()

  // could add a rudimentary kwargs names check in the future

  for (node <- inputs) {
    // node is from-node, this is to-node
    val edge = Edge(node, this)

    // define param string for new input kwarg of this node
    var param = findNextUnusedKey("in", edges)

    edges(param) = edge

    // define param string for new output kwargs of other node
    param = findNextUnusedKey("out", node.edges)

    node.edges(param) = edge
  }

  private def findNextUnusedKey(string: String, map: mutable.Map[String, Edge]): String = {
    var idx = 0
    while (map.contains(string + idx.toString)) {
      idx += 1
    }
    string + idx.toString
  }

  def getSerializable: Obj = {
    val serializable_kwargs = ujson.copy(pythonUserNode.kwargs)

    // kwargs are not allowed to use parameter names that begin with "in" or "out". "storage" is also a reserved parameter.
    for ((k, v) <- edges) {
      serializable_kwargs(k) = v.id
    }

    Obj("class_name" -> pythonUserNode.name, "kwargs" -> serializable_kwargs, "code" -> pythonUserNode.code)
  }
}

