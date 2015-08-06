# Description

This program should receive json formated messages from NxLog, convert the Json Fields and store the messages in an Elasticsearch Index. You can use Kibana for the message analysis.

Using the following technologies for maximum performance und scalibility:

* Akka actors: distribute the load to multiple cpus
* Akka cluster: distribute the load to multiple server
* Akka IO: enable reactive operation
* play-json: parse and transform the json messages

# Status

Very early development state, but working.

# ToDo

* Substitute the SimpleSender ( to Elasticsearch ) with a BulkLoader.
* Own ThreadPool for the BulkLoader Http actors.
* RoundRobin Router for parser actors ( scale up and out ).
* Dynamic ES index creation ( nxlog-2015-01-01 ) per day.
* Akka configuration file: application.configuration.
* sbt native builder configuration for rpm and zip/tar.
* Dynamic parsing for various windows events ( split the Message field ).