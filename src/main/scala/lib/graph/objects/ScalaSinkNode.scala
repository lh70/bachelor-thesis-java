package lib.graph.objects

import ujson.Obj

class ScalaSinkNode(device: Device, inputs: List[Node]) extends Node(device, "dummy-sink", "dummy-sink-code", inputs, Obj())
