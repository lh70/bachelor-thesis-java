package lib

import scala.io.Source
import ujson.Obj


object PythonUserNodes {

  case class PythonUserNode(name: String, filepath: String = "", kwargs: Obj = Obj()) {
    var code: String = ""

    if (filepath.nonEmpty) {
      val file = scala.io.Source.fromFile(filepath)
      code = try file.mkString finally file.close()
    }
  }

  val PYTHON_USER_NODES_PATH: String = "submodules/s-connect-python/user_nodes/"

  def ButtonFilter(flip_threshold: Int=5, initial_state: Boolean=false): PythonUserNode =
    PythonUserNode("ButtonFilter", PYTHON_USER_NODES_PATH + "button_filter.py", Obj("flip_threshold" -> flip_threshold, "initial_state" -> initial_state))

  def ButtonToSingleEmit(): PythonUserNode =
    PythonUserNode("ButtonToSingleEmit", PYTHON_USER_NODES_PATH + "button_to_single_emit.py")

  def CaseStudyDelayObserver(filepath: String=""): PythonUserNode =
    PythonUserNode("CaseStudyDelayObserver", PYTHON_USER_NODES_PATH + "case_study_delay_observer.py", Obj("filepath" -> filepath))

  def Duplicate(): PythonUserNode =
    PythonUserNode("Duplicate", PYTHON_USER_NODES_PATH + "duplicate.py")

  def Filter(eval_str: String="x > 0"): PythonUserNode =
    PythonUserNode("Filter", PYTHON_USER_NODES_PATH + "filter.py", Obj("eval_str" -> eval_str))

  def Join(eval_str: String="x + y"): PythonUserNode =
    PythonUserNode("Join", PYTHON_USER_NODES_PATH + "join.py", Obj("eval_str" -> eval_str))

  def JoinWithDupFilter(eval_str: String="x + y"): PythonUserNode =
    PythonUserNode("JoinWithDupFilter", PYTHON_USER_NODES_PATH + "join_with_dup_filter.py", Obj("eval_str" ->eval_str))

  def Map(eval_str: String="x"): PythonUserNode =
    PythonUserNode("Map", PYTHON_USER_NODES_PATH + "map.py", Obj("eval_str" -> eval_str))

  def Mean(time_frame: Int=0): PythonUserNode =
    PythonUserNode("Mean", PYTHON_USER_NODES_PATH + "mean.py", Obj("time_frame" -> time_frame))

  def Monitor(time_frame: Int=100): PythonUserNode =
    PythonUserNode("Monitor", PYTHON_USER_NODES_PATH + "monitor.py", Obj("time_frame" -> time_frame))

  def MonitorLatest(): PythonUserNode =
    PythonUserNode("MonitorLatest", PYTHON_USER_NODES_PATH + "monitor_latest.py")

  def PassThrough(): PythonUserNode =
    PythonUserNode("PassThrough", PYTHON_USER_NODES_PATH + "pass_through.py")

  def PrintItems(format_str: String="{}", time_frame: Int=0): PythonUserNode =
    PythonUserNode("PrintItems", PYTHON_USER_NODES_PATH + "print_items.py", Obj("format_str" -> format_str, "time_frame" -> time_frame))

  def PrintQueue(format_str: String="{}", time_frame: Int=0): PythonUserNode =
    PythonUserNode("PrintQueue", PYTHON_USER_NODES_PATH + "print_queue.py", Obj("format_str" -> format_str, "time_frame" -> time_frame))

  def SensorRead(sensor_class_name: String, read_delay_ms: Int=0): PythonUserNode =
    PythonUserNode("SensorRead", PYTHON_USER_NODES_PATH + "sensor_read.py", Obj("sensor_class_name" -> sensor_class_name, "read_delay_ms" -> read_delay_ms))

  def Sum(time_frame: Int=0): PythonUserNode =
    PythonUserNode("Sum", PYTHON_USER_NODES_PATH + "sum.py", Obj("time_frame" -> time_frame))

  def ThroughputObserver(filepath: String=""): PythonUserNode =
    PythonUserNode("ThroughputObserver", PYTHON_USER_NODES_PATH + "throughput_observer.py", Obj("filepath" -> filepath))

  def ToggleState(eval_str: String="x > 0", initial_state: Boolean=false): PythonUserNode =
    PythonUserNode("ToggleState", PYTHON_USER_NODES_PATH + "toggle_state.py", Obj("eval_str" -> eval_str, "initial_state" -> initial_state))
}
