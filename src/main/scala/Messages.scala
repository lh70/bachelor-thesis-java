import upickle.default.ReadWriter

object Messages {

  case class AssignmentMessage(`processing-assignment`: Assignment, processing: List[Processing]) derives ReadWriter
  case class Assignment(`assignment-id`: String, pipelines: Map[String, PipelineType] = Map.empty) derives ReadWriter
  case class PipelineType(`type`: String) derives ReadWriter
  case class Processing(`class`: String, kwargs: ProcessingArgs) derives ReadWriter
  case class ProcessingArgs(sensor: String, read_delay_ms: Int, out0: String) derives ReadWriter

  case class RemoveMessage(`remove-assignment`: Assignment) derives ReadWriter

  case class RequestMessage(`pipeline-request`: AssignmentReq) derives ReadWriter
  case class AssignmentReq(`assignment-id`: String, `pipe-id`: String, `time-frame`: Int, `values-per-time-frame`: Int) derives ReadWriter

  /** Returns a message string for assigning a SensorRead with choosable sensor kind.
    *
    * Assignment ID is 0
    * Pipeline ID is 0
    * Processing node getting executed is SensorRead
    * SensorRead arg 'read_delay_ms' is set 0 -> old behaviour -> throw out at many values as possible, no duplicate check
    *
    * @param sensorKind The kind of sensor which should be read.
    * @return the json encoded message string.
    */
  def getAssignmentMessage(sensorKind: String): String = upickle.default.write(AssignmentMessage(
    Assignment("0", Map("0" -> PipelineType("output"))),
    List(Processing("SensorRead", ProcessingArgs(sensorKind, 0, "0")))
  ))

  /** Returns a message string to remove the SensorRead assignment.
    *
    * Assignment ID is 0
    *
    * @return the json encoded message string
    */
  def getRemoveAssignmentMessage: String = upickle.default.write(RemoveMessage(Assignment("0")))

  /** Returns a message to request the pipeline outputting the SensorRead values.
    *
    * Assignment ID is 0
    * Pipeline ID is 0
    * Time-Frame is 100 -> send aggregated value list every 100 milliseconds
    * Values-Per-Time-Frame is 0 -> unused value in the framework at the moment -> ignore
    *
    * @return the json encoded message string
    */
  def getPipelineRequestMessage: String = upickle.default.write(RequestMessage(AssignmentReq("0", "0", 100, 0)))
}
