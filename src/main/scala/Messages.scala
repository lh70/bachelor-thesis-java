import ujson.{Obj, Arr}

object Messages {

  def getRemoveAssignmentMessage(id: String): String = ujson.write(
    message("remove_assignment", emptyAssignment(id))
  )

  def getAddAssignmentMessage(id: String, sensorKind: String): String = ujson.write(
    message("add_assignment", fullAssignment(
      id=id,
      processing=Arr(
        processingNode("sensor_read", Obj("out0" -> "P0", "sensor" -> sensorKind, "read_delay_ms" -> 0), SensorReadNodePythonCode)
      ),
      pipelines=Obj(
        "P0" -> outputPipeline
      )
    ))
  )

  def getRequestPipelineMessage(id: String, time_frame: Int): String = ujson.write(
    message("pipeline_request", requestPipeline(id, "P0", time_frame))
  )


  private def message(`type`: String, content: Obj) = Obj("type" -> `type`, "content" -> content)

  private def requestPipeline(assignment_id: String, pipe_id: String, time_frame: Int) = Obj("assignment_id" -> assignment_id, "pipe_id" -> pipe_id, "time_frame" -> time_frame, "values_per_time_frame" -> 0)
  private def outputPipeline = Obj("type" -> "output")

  private def emptyAssignment(id: String) = Obj("id" -> id)
  private def fullAssignment(id: String, processing: Arr, pipelines: Obj) = Obj("id" -> id, "processing"-> processing, "pipelines" -> pipelines)

  private def processingNode(func_name: String, kwargs: Obj, code: String) = Obj("func_name" -> func_name, "kwargs" -> kwargs, "code" -> code)


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






