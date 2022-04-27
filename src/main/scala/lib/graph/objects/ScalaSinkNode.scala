package lib.graph.objects

import lib.PythonFunctions.PythonFunction
import ujson.Obj

class ScalaSinkNode(device: Device, inputs: List[Node]) extends Node(device, PythonFunction("dummy"), inputs: _*)
