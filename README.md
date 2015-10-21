  Pigeon
=========

Pigeon is a transaction log streamer for MariaDB Galera Cluster.

Its goal is to propagate the information written to the transaction log in a kafka topic.

---


Main features
-------------

- Every sql statement is a message.
- In addition to the data, also shows fields name.
- Messages are serialized using avro format in according to [schema](#schema).
- Messages in the same transaction are identified by the field transactionId.
- Pigeon can be running on every node on the galera cluster. Only one instance is elected to streaming messages.
	If an error occurs, a new instance is elected to work.
- When a new instance is elected to run, it retrieves the last transaction position number from the last message sent.
- Avro schema version  is managed by confluent schema registry.


---

Quick Start
-----------

 Build project using maven. 

 Configure project by:

	1. db.properties
	2. pigeon.properties


Run main class `eu.unicredit.StreamingLog`













Schema
------

     {
	"namespace": "transactionlog",
	"type": "record",
	"name": "Record",
	"fields": 
	[
		{
			"name": "database",
			"type": "string"
		},

		{
			"name": "table",
			"type": "string"
		},

		{
			"name": "rdbmsType",
			"type": 
			[
				"string",
				"null"
			]
		},

		{
			"name": "eventType",
			"type": 
			{
				"type": "enum",
				"name": "EventTypeTransactionLog",
				"symbols": 
				[
					"WRITE_ROWS",
					"DELETE_ROWS",
					"UPDATE_ROWS"
				]
			}
		},

		{
			"name": "totalCountTransactionEvent",
			"type": "int"
		},

		{
			"name": "currentEventIndex",
			"type": "int"
		},

		{
			"name": "lastPositionNumber",
			"type": 
			[
				"long",
				"null"
			]
		},

		{
			"name": "logFileName",
			"type": 
			[
				"string",
				"null"
			]
		},

		{
			"name": "transactionSequenceNumber",
			"type": 
			[
				"long",
				"null"
			]
		},

		{
			"name": "timestampOperation",
			"type": "long"
		},

		{
			"name": "transactionId",
			"type": "string"
		},

		{
			"name": "afterValue",
			"type": 
			{
				"type": "array",
				"items": 
				[
					"string",
					"null"
				]
			}
		},

		{
			"name": "beforeValue",
			"type": 
			{
				"type": "array",
				"items": 
				[
					"string",
					"null"
				]
			}
		},

		{
			"name": "columnName",
			"type": 
			{
				"type": "array",
				"items": 
				[
					"string",
					"null"
				]
			}
		}
	]
}








