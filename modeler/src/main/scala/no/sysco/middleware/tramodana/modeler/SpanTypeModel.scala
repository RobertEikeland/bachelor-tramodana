/*
The purpose of this file is to define what changes from trace to trace,
and what information remains the same.
We are interested in information on the topological level

 */
final case class Span(
                       traceId: String,          // changes
                       spanId: String,	         // changes
                       spanHash: Long,           // changes
                       duration: Long,           // changes
                       flags: Int,		           // ??
                       logs: Option[List[Log]],  // see below
                       operationName: String,    // DOESNT CHANGE
                       parentId:String,          // changes
                       process:Option[Process],  // see below
                       refs: Option[List[Ref]],  // ??
                       startTime: Long,          // changes
                       tags: Option[List[Field]] // changes
                     )
/*
A SpanNode's id should be unique in a workflow, as it will be used as the base
for creating node-to-node relationships.
Therefore we need to make sure and avoid hash collision between two nodes
in the same workflow.
What makes a node unique is a composition of
- its name,
- the service it is a part of,
- the kind of span (server or client),
- and its parent node.

First suggestion is to build it as composition of its values joined by '#'.
E.g:
  nodeHash = s"$spanKind#$serviceName#$operationName#$parentHash"
A good test would be to check on spans with very common operation names,
like REST request operations ('GET', 'POST', etc...)
 */
final case class SpanNode(
                         nodeId: String,              // should be decided out of rest of values
                         nodeHash: String,
                         parentId: String,
                         
                         operationName: String,         // like in Span
                         serviceName: String,           // under process > service_name
                         spanKind: String,               // under tags > [key: span.kind] > value_string
                         handlerClass: Option[String],  // under logs >
                                                        //      fields >
                                                        //      handler.class_simple_name >
                                                        //      value_string
                         )
/*
"trace_id": "0x00000000000000008cdfd0b8468dc387",
"span_id": "-8295682498716909000",
"span_hash": 7083069031972214000,
"duration": 164851,
"flags": 1,
"logs": [
{
  "ts": 1520845538311000,
  "fields": [
  { "key": "handler.class_simple_name", "value_string": "GreetingController", },
  { "key": "handler", "value_string": "public org.springframework.http.ResponseEntity ru.zhenik.spring.rest.hello.one.controller.GreetingController.buyBookMethod()", },
  { "key": "event", "value_string": "preHandle", },
  { "key": "handler.method_name", "value_string": "buyBookMethod", }
  ]
},
{
  "ts": 1520845538475000,
  "fields": [
  { "key": "event", "value_string": "afterCompletion", },
  { "key": "handler", "value_string": "public org.springframework.http.ResponseEntity ru.zhenik.spring.rest.hello.one.controller.GreetingController.buyBookMethod()", }
  ]
}
],
"operation_name": "buyBookMethod",
"parent_id": "0",
"process": {
  "service_name": "Human",
  "tags": [
      { "key": "hostname",      "value": "nikita-lenovo-yoga-710-14ikb" ...},
      { "key": "jaeger.version","value": "Java-0.20.0" ...},
      { "key": "ip",            "value": "127.0.1.1" ...},
   ]
},
"refs": [],
"start_time": 1520845538310000,
"tags": [
   { "key": "http.status_code", "value": 200 ...},
   { "key": "component",        "value": "java-web-servlet"  ...},
   { "key": "span.kind",        "value": "server" ...},
   { "key": "sampler.type",     "value": "probabilistic" ...},
   { "key": "sampler.param",    "value": ??? ...},
   { "key": "http.url",         "value": "http://localhost:10081/buybook" ...},
   { "key": "http.method",      "value": "GET" ...},

 ]
*/

final case class Log(
                      ts: Long,                     // changes
                      fields: Option[List[Field]])  // see below

final case class Field(
                        key: String,
                        valueType: String,
                        valueString: String,
                        valueBool: Boolean,
                        valueLong: Long,
                        valueDouble: Double,
                        valueBinary: Option[String])

final case class Process(
                          serviceName: String,
                          tags: Option[List[Field]])

final case class Ref(
                      refType: String,
                      traceId: String,
                      spanId: String)


case class SpanTree(value: Span, children: List[SpanTree])
final case class Trace(traceId:Option[String], spans: Option[List[Span]])