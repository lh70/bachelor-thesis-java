package lib.graph.objects

import lib.Connection
import ujson.Obj

import java.net.Socket
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Device protected (
    val host: String = "localhost",
    val port: Int = 8090,
    val maxTimeFrame: Int = 100,
    val maxValuesPerTimeFrame: Int = 0
) {

  val id = Device.nextID

  override def toString: String = {
    "Device " + id + " " + host + ":" + port
  }

  def removeAssignment(assignmentId: String): Unit = {
    val conn = new Connection(new Socket(host, port))

    conn.sendControlMessage(ujson.write(
      Obj("type" -> "remove_assignment", "content" -> Obj("id" -> assignmentId))
    ))

    conn.close()
  }

  def addAssignment(distribution: Obj): Unit = {
    val conn = new Connection(new Socket(host, port))

    conn.sendControlMessage(ujson.write(
      Obj("type" -> "add_assignment", "content" -> distribution)
    ))

    conn.close()
  }
}

object Device {

  def apply(
      host: String = "localhost",
      port: Int = 8090,
      maxTimeFrame: Int = 100,
      maxValuesPerTimeFrame: Int = 0
  ): Device = register(new Device(host, port, maxTimeFrame, maxValuesPerTimeFrame))

  def register(d: Device): d.type =
    for (device <- Device.instances) {
      if (device.host == d.host && device.port == d.port) {
        throw Exception(
          "Devices " + device.id + " and " + d.id + "have the same host:port (" + d.host + ":" + d.port + ") configuration"
        )
      }
    }
    instances += d
    d

  val instances: ListBuffer[Device] = ListBuffer()

  private var counter: Int = 0
  def nextID: Int =
    counter += 1
    counter - 1

}
