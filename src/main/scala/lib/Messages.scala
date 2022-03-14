package lib

import ujson.{Arr, Obj}

object Messages {
  def message(`type`: String, content: Obj): Obj = Obj("type" -> `type`, "content" -> content)

  def requestPipeline(assignment_id: String, pipe_id: String, time_frame: Int): Obj = Obj(
    "assignment_id"         -> assignment_id,
    "pipe_id"               -> pipe_id,
    "time_frame"            -> time_frame,
    "values_per_time_frame" -> 0
  )
  def outputPipeline: Obj = Obj("type" -> "output")

  def emptyAssignment(id: String): Obj = Obj("id" -> id)
  def fullAssignment(id: String, processing: Arr, pipelines: Obj): Obj =
    Obj("id" -> id, "processing" -> processing, "pipelines" -> pipelines)

  def processingNode(func_name: String, kwargs: Obj, code: String): Obj =
    Obj("func_name" -> func_name, "kwargs" -> kwargs, "code" -> code)

  val SensorReadNodePythonCode: String =
    """
      |def sensor_read(out0, sensor, read_delay_ms=0, storage=None):
      |    if 'last_valid' not in storage:
      |        storage['last_valid'] = None
      |        storage['last_read'] = 0
      |
      |    if read_delay_ms == 0:  # old behaviour. read is faster than the sensor can provide values.
      |        if sensor.value is not None:
      |            out0.append(sensor.value)
      |    else:  # new behaviour. sensor is faster than the framework can process the values further down the line.
      |        if sensor.value is not None:
      |            storage['last_valid'] = sensor.value
      |
      |        if ticks_ms_diff_to_current(storage['last_read']) > read_delay_ms and storage['last_valid'] is not None:
      |            storage['last_read'] = ticks_ms()
      |            out0.append(storage['last_valid'])
      |""".stripMargin
}
