object Messages {

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
  def getAssignmentMessage(sensorKind: String) =
    "{\n" + "  \"processing-assignment\": {\n" + "    \"assignment-id\": \"0\",\n" + "    \"pipelines\": {\n" + "      \"0\": {\"type\": \"output\"}\n" + "    },\n" + "    \"processing\": [\n" + "      {\"class\": \"SensorRead\", \"kwargs\": {\"sensor\": \"" + sensorKind + "\", \"read_delay_ms\": 0, \"out0\": \"0\"}}\n" + "    ]\n" + "  }\n" + "}"

  /** Returns a message string to remove the SensorRead assignment.
    *
    * Assignment ID is 0
    *
    * @return the json encoded message string
    */
  def getRemoveAssignmentMessage =
    "{\n" + "  \"remove-assignment\": {\n" + "    \"assignment-id\": \"0\"\n" + "  }\n" + "}\n"

  /** Returns a message to request the pipeline outputting the SensorRead values.
    *
    * Assignment ID is 0
    * Pipeline ID is 0
    * Time-Frame is 100 -> send aggregated value list every 100 milliseconds
    * Values-Per-Time-Frame is 0 -> unused value in the framework at the moment -> ignore
    *
    * @return the json encoded message string
    */
  def getPipelineRequestMessage =
    "{\n" + "  \"pipeline-request\": {\n" + "    \"assignment-id\": \"0\",\n" + "    \"pipe-id\": \"0\",\n" + "    \"time-frame\": 100,\n" + "    \"values-per-time-frame\": 0\n" + "  }\n" + "}\n"
}
