package lib.graph.objects

import lib.Connection
import ujson.Obj

import java.net.Socket
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class Device(val host: String = "localhost", val port: Int = 8090, val maxTimeFrame: Int = 100, val maxValuesPerTimeFrame: Int = 0) {
  val id: Int = Device.counter
  Device.counter += 1

  for (device <- Device.instances) {
    if (device.host == host && device.port == port) {
      throw Exception("Devices " + device.id +" and " + id + "have the same host:port (" + host + ":" + port + ") configuration")
    }
  }

  Device.instances += this

  override def toString: String = {
    "Device " + id + " " + host + ":" + port
  }

  def removeAssignment(assignmentId: String): Unit = {
    val conn = new Connection(new Socket(host, port))

    conn.sendControlMessage(ujson.write(
      lib.Messages.message("remove_assignment", lib.Messages.emptyAssignment(assignmentId))
    ))

    conn.close()
  }

  def addAssignment(distribution: mutable.Map[Device, Obj]): Unit = {
    val conn = new Connection(new Socket(host, port))

    conn.sendControlMessage(ujson.write(
      lib.Messages.message("add_assignment", distribution(this))
    ))

    conn.close()
  }
}
object Device {
  val instances: ListBuffer[Device] = ListBuffer()
  private var counter: Int = 0
}
