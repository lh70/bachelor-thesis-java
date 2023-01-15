package lib.graph.objects

import lib.Connection
import ujson.Obj

import java.net.Socket
import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Device(
    val host: String = "localhost",
    val port: Int = 8090,
    val maxTimeFrameMs: Int = 100,
    val maxHeartbeatMs: Int = 100
) {

  val id: Int = Device.nextID

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

  @tailrec
  def assertAllDiffer(devices: List[Device]): Unit = devices match
    case d1 :: rest =>
      rest.foreach(assertDifferent(d1))
      assertAllDiffer(rest)
    case Nil =>

  def assertDifferent(d1: Device)(d2: Device): Unit = {
    if (d1.host == d2.host && d1.port == d2.port) {
      throw Exception(
        "Devices " + d1.id + " and " + d2.id + "have the same host:port (" + d2.host + ":" + d2.port + ") configuration"
      )
    }
  }

  private var counter: Int = 0
  def nextID: Int =
    counter += 1
    counter - 1

}
