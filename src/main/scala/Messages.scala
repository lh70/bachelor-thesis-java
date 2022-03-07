import Messages.ProcessingNode
import upickle.default.ReadWriter

object Messages {

  /** Deploys and runs an Assignment
   *
   * @param `processing-assignment` the fully defined assignment (processing and pipelines)
   */
  case class DeployAssignmentMessage(`processing-assignment`: Assignment) derives ReadWriter

  /** Removes a deployed assignment. Produces a no-operation if there is no such assignment on the device
   *
   * @param `remove-assignment` the assignment that should be cancelled and removed. Only assignment-id must be defined
   */
  case class RemoveAssignmentMessage(`remove-assignment`: Assignment) derives ReadWriter

  /** Serialized form of a full assignment for one device. An assignment may span over multiple devices (currently not supported by this port).
   *
   * @param `assignment-id` a unique identifier for this assignment
   * @param processing a list of processing steps to execute in this assignment. Steps are executed in list order.
   * @param pipelines map(id->pipeline) defines the pipelines interconnecting the processing steps and the outward and inward pipelines.
   */
  case class Assignment(`assignment-id`: String, processing: List[ProcessingNode], pipelines: Map[String, OutputPipeline]) derives ReadWriter
  object Assignment {
    def apply(`assignment-id`: String): Assignment = Assignment(`assignment-id`, List.empty, Map.empty)
  }

  /** Defines an output pipeline in an assignment. There can be input, local and output pipelines. input and local are currently not supported by this port.
   *
   */
  case class OutputPipeline(`type`: String) derives ReadWriter
  object OutputPipeline {
    def apply(): OutputPipeline = OutputPipeline("output")
  }

  /** Defines a node with python code that should be executed
   *
   *
   * @param func_name name of the function, defined in the code string, that should be imported as the node
   * @param kwargs keyword arguments, map(parameter-name -> value), of the function
   * @param code the code string, containing the python function definition
   */
  case class ProcessingNode(func_name: String, kwargs: SensorReadKwargs, code: String) derives ReadWriter

  /** Map(parameter -> value) for the sensor_read function
   *
   * @param sensor kind of sensor to read
   * @param read_delay_ms minimum rate at which to read messages. 0 -> read as fast as possible.
   * @param out0 id of the output pipeline to connect to this node.
   */
  case class SensorReadKwargs(out0: String, sensor: String, read_delay_ms: Int) derives ReadWriter



  /** Requests the output pipeline on the other device to connect to this device as an input pipeline
   *
   * @param `pipeline-request` the pipeline to be requested
   */
  case class RequestInputPipelineMessage(`pipeline-request`: RequestInputPipelineDef) derives ReadWriter

  /** Defines the pipeline properties to request. Pipeline properties are defined by the receiving (input) side of the pipeline
   *
   * @param `assignment-id` the id of the assignment where the output pipeline was defined
   * @param `pipe-id` the id of the output pipeline
   * @param `time-frame` the time interval at which aggregated values should be pushed into the pipeline
   */
  case class RequestInputPipelineDef(`assignment-id`: String, `pipe-id`: String, `time-frame`: Int, `values-per-time-frame`: Int) derives ReadWriter
  object RequestInputPipelineDef {
    def apply(`assignment-id`: String, `pipe-id`: String, `time-frame`: Int): RequestInputPipelineDef = RequestInputPipelineDef(`assignment-id`, `pipe-id`, `time-frame`, 0)
  }



  /** Returns a message string for assigning a sensor_read node with choosable sensor kind.
    *
    * Assignment ID is A0
    * Pipeline ID is P0
    * Processing node getting executed is sensor_read
    * sensor_read arg 'read_delay_ms' is set 0 -> old behaviour -> read as many values as possible, no duplicate check
    *
    * @param sensorKind The kind of sensor which should be read.
    * @return the json encoded message string.
    */
  def getAssignmentMessage(sensorKind: String): String = upickle.default.write(
    DeployAssignmentMessage(
      Assignment(
        "A0",
        List(
          ProcessingNode("sensor_read", SensorReadKwargs("P0", sensorKind, 0), SensorReadNodePythonCode)
        ),
        Map(
          "P0" -> OutputPipeline()
        )
      )
    )
  )

  /** Returns a message string to remove the SensorRead assignment.
    *
    * Assignment ID is A0
    *
    * @return the json encoded message string
    */
  def getRemoveAssignmentMessage: String = upickle.default.write(RemoveAssignmentMessage(Assignment("A0")))

  /** Returns a message to request the pipeline outputting the SensorRead values.
    *
    * Assignment ID is A0
    * Pipeline ID is P0
    * Time-Frame is 100 -> send aggregated value list every 100 milliseconds
    *
    * @return the json encoded message string
    */
  def getPipelineRequestMessage: String = upickle.default.write(RequestInputPipelineMessage(RequestInputPipelineDef("A0", "P0", 100)))



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

