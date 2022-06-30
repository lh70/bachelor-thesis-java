import lib.PythonUserNodes.{PYTHON_USER_NODES_PATH, PythonUserNode, SensorRead}
import lib.graph.{Graph, objects}
import lib.graph.objects.{Device, Edge, Node, ScalaSinkDevice}
import rescala.default.*
import ujson.Obj

import java.io.IOException
import java.net.Socket
import scala.util.Try

object Main {
  def main(args: Array[String]): Unit = {
    val pc        = Device()
    val scalaSink = ScalaSinkDevice()

    val devices = List(pc, scalaSink)
    Device.assertAllDiffer(devices)

    val n1 = Node(pc, SensorRead("Dummy"))
    val n2 = Node.scalaSink(scalaSink, n1)

    val distId = "A0"

    val (orderedDevices, assignments) =
      new Graph(devices, List(n1, n2))
        .buildDistribution(distId)

    for (device <- orderedDevices) {
      device.removeAssignment(distId)
      device.addAssignment(assignments(device))
    }

    // we only have one pipeline (currently there would be also no way of knowing which pipeline is which on multiple input pipelines)
    val conn = scalaSink.pipelines.values.head

    val evt = Evt[List[Int]]()
    evt.observe((e: List[Int]) => println(e.length))

    while true do
      val msg = conn.receive()

      val values = upickle.default.read[List[Int]](msg)

      evt.fire(values)
  }
}
