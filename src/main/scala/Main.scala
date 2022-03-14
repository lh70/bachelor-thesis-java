import java.io.IOException
import rescala.default.*

import java.net.Socket
import lib.graph.Graph
import lib.graph.objects.{Device, Node, ScalaSinkDevice, ScalaSinkNode}
import ujson.Obj

object Main {
  def main(args: Array[String]): Unit = {
    val pc = Device()
    val scalaSink = ScalaSinkDevice()

    val n1 = Node(pc, "sensor_read", lib.Messages.SensorReadNodePythonCode, List(), Obj("sensor" -> "dummy", "read_delay_ms" -> 0))

    val n2 = ScalaSinkNode(scalaSink, List(n1))

    val (orderedDevices, distribution) = Graph.buildDistribution("A0")

    for (device <- orderedDevices) {
      device.removeAssignment("A0")
      device.addAssignment(distribution)
    }

    // we only have one pipeline (currently there would be also no way of knowing which pipeline is which on multiple input pipelines)
    val conn = scalaSink.pipelines.values.head


    val evt = Evt[List[Int]]()
    evt.observe((e:List[Int]) => println(e.length))

    while true do
      val msg = conn.receive()

      val values = upickle.default.read[List[Int]](msg)

      evt.fire(values)
  }
}
