package lib

import scala.io.Source
import ujson.Obj


object PythonFunctions {

  case class PythonFunction(name: String, filepath: String = "", kwargs: Obj = Obj()) {
    var code: String = ""

    if (filepath.nonEmpty) {
      val file = scala.io.Source.fromFile(filepath)
      code = try file.mkString finally file.close()
    }
  }

  val PYTHON_FUNCTIONS_PATH: String = "submodules/s-connect-python/user_nodes/"

  def ButtonFilter(flip_threshold: Int=5, initial_state: Boolean=false): PythonFunction =
    PythonFunction("button_filter", PYTHON_FUNCTIONS_PATH + "button_filter.py", Obj("flip_threshold" -> flip_threshold, "initial_state" -> initial_state))

  def ButtonToSingleEmit(): PythonFunction =
    PythonFunction("button_to_single_emit", PYTHON_FUNCTIONS_PATH + "button_to_single_emit.py")

  def CaseStudyDelayObserver(filepath: String=""): PythonFunction =
    PythonFunction("case_study_delay_observer", PYTHON_FUNCTIONS_PATH + "case_study_delay_observer.py", Obj("filepath" -> filepath))

  def Duplicate(): PythonFunction =
    PythonFunction("duplicate", PYTHON_FUNCTIONS_PATH + "duplicate.py")

  def Filter(eval_str: String="x > 0"): PythonFunction =
    PythonFunction("filter", PYTHON_FUNCTIONS_PATH + "filter.py", Obj("eval_str" -> eval_str))

  def Join(eval_str: String="x + y"): PythonFunction =
    PythonFunction("join", PYTHON_FUNCTIONS_PATH + "join.py", Obj("eval_str" -> eval_str))

  def JoinWithDupFilter(eval_str: String="x + y"): PythonFunction =
    PythonFunction("join_with_dup_filter", PYTHON_FUNCTIONS_PATH + "join_with_dup_filter.py", Obj("eval_str" ->eval_str))

  def Map(eval_str: String="x"): PythonFunction =
    PythonFunction("map", PYTHON_FUNCTIONS_PATH + "map.py", Obj("eval_str" -> eval_str))

  def Mean(time_frame: Int=0): PythonFunction =
    PythonFunction("mean", PYTHON_FUNCTIONS_PATH + "mean.py", Obj("time_frame" -> time_frame))

  def Monitor(time_frame: Int=100): PythonFunction =
    PythonFunction("monitor", PYTHON_FUNCTIONS_PATH + "monitor.py", Obj("time_frame" -> time_frame))

  def MonitorLatest(): PythonFunction =
    PythonFunction("monitor_latest", PYTHON_FUNCTIONS_PATH + "monitor_latest.py")

  def PassThrough(): PythonFunction =
    PythonFunction("pass_through", PYTHON_FUNCTIONS_PATH + "pass_through.py")

  def PrintItems(format_str: String="{}", time_frame: Int=0, values_per_time_frame: Int=0): PythonFunction =
    PythonFunction("print_items", PYTHON_FUNCTIONS_PATH + "print_items.py", Obj("format_str" -> format_str, "time_frame" -> time_frame, "values_per_time_frame" -> values_per_time_frame))

  def PrintQueue(format_str: String="{}", time_frame: Int=0, values_per_time_frame: Int=0): PythonFunction =
    PythonFunction("print_queue", PYTHON_FUNCTIONS_PATH + "print_queue.py", Obj("format_str" -> format_str, "time_frame" -> time_frame, "values_per_time_frame" -> values_per_time_frame))

  def SensorRead(sensor: String, read_delay_ms: Int=0): PythonFunction =
    PythonFunction("sensor_read", PYTHON_FUNCTIONS_PATH + "sensor_read.py", Obj("sensor" -> sensor, "read_delay_ms" -> read_delay_ms))

  def Sum(time_frame: Int=0): PythonFunction =
    PythonFunction("sum", PYTHON_FUNCTIONS_PATH + "sum.py", Obj("time_frame" -> time_frame))

  def ThroughputObserver(filepath: String=""): PythonFunction =
    PythonFunction("throughput_observer", PYTHON_FUNCTIONS_PATH + "throughput_observer.py", Obj("filepath" -> filepath))

  def ToggleState(eval_str: String="x > 0", initial_state: Boolean=false): PythonFunction =
    PythonFunction("toggle_state", PYTHON_FUNCTIONS_PATH + "toggle_state.py", Obj("eval_str" -> eval_str, "initial_state" -> initial_state))
}
