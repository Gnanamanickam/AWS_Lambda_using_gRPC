## Gnanamanickam Arumugaperumal

## Overview

The project creates a solution for solving a distributed computational problem using cloud computing technologies by designing and implementing a RESTful service and a lambda function that are accessed from clients using gRPC and PLAY framework .

## Prerequisites

* Install SBT to build the jar
* Terminal to SSH and SCP into VM to execute Hadoop commands
* Install the curl command to run the APi call or else install POSTMAN in the system.
* AWS account to create a EC2 instance to run the file and transfer the log to S3 bucket

## Installation

* Clone the GIT repository by using git clone https://github.com/Gnanamanickam/AWS_Lambda_using_gRPC
* Run the following commands in the console

```
sbt clean compile test
```
```
sbt clean compile run
```
* It builds and compiles the project
* If you are using IntellIj clone the repository by using "Check out from Version Control and then Git."

* The scala version should be set in the Global Libraries under Project Structure in Files .
* The SBT configuration should be added by using Edit Configurations and then simulations can be ran in the IDE .

## Execution

Created a python code which gets two input inputTime and inputDifferentialTime which checks whether the time stamp between those time is present in log file deployed in s3 bucket.
Write a binary search algorithm to find whether the timeStamp is present and if present return a MD5 hashcode of the output.


Deploy the code in the AWS Lambda functionality and add a policy which gives full access to the S3 bucket to read the file .

Now create a API gateway in the AWS and add the lambda code to it with required policies which will create a URL on the stage and that can be used in GRPC and Rest API framework to make calls and get the result .

Also create a EC2 instance which can be used to run the LOGFileGenerator and directly transfer the log file to the s3 bucket.

## gRPC

gRPC is a open source remote procedure call framework which uses Protobuf and can run in any environment .
To define the gRPC service and the method request and response types we use .proto file which auto generates four files in the target folder that can be used by the server and client code to communicate with AWS lambda function using API gateway to make communications .

Then we define rpc methods inside our service definition, specifying their request and response types. gRPC lets you define four kinds of service methods

A simple RPC where the client sends a request to the server using the stub and waits for a response to come back
```
rpc GetFeature(input) returns (Feature) {}
```
A server-side streaming RPC where the client sends a request to the server and gets a stream to read a sequence of messages back. The client reads from the returned stream until there are no more messages
```
rpc ListFeatures(input) returns (stream Feature) {}
```
A client-side streaming RPC where the client writes a sequence of messages and sends them to the server, again using a provided stream. Once the client has finished writing the messages, it waits for the server to read them all and return its response.
```
rpc Route(stream input) returns (output) {}
```
.proto file also contains protocol buffer message type definitions for all the request and response types used in the service.

To generate the gRPC client and server interfaces from our .proto service definition, we use the proto3 compiler which generates grpc services .
```
Nov 05, 2021 8:27:45 PM gRPC.gRPCClient response
INFO: Time Stamp Present: {"isTimeStampPresent": "True", "hashValue": "<md5 HASH object @ 0x7f5cec3a1090>"}
```


## PLAY Framework

Play is a framework build on Akka which is lightweight and used to create highly-scalable applications. 

To create a new Play framework for scala we use the following below :
```
sbt new playframework/play-scala-seed.g8
```

And then give the required names and package names which creates the template for making API calls.

Change the controller to call API gateway and change the routes in resources as required and then run
```
sbt run
```

which starts the server on the localhost in port number 9000 which is default .

To check whether the API call is working properly we can use the curl command

```
🚀❯ curl -v localhost:9000/logAPI
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 9000 (#0)
> GET /logAPI HTTP/1.1
> Host: localhost:9000
> User-Agent: curl/7.55.1
> Accept: */*
>
< HTTP/1.1 200 OK
< Referrer-Policy: origin-when-cross-origin, strict-origin-when-cross-origin
< X-Frame-Options: DENY
< X-XSS-Protection: 1; mode=block
< X-Content-Type-Options: nosniff
< X-Permitted-Cross-Domain-Policies: master-only
< Date: Sat, 06 Nov 2021 00:31:32 GMT
< Content-Type: application/json
< Content-Length: 27
<
"{\"isTimeStampPresent\": \"True\", \"hashValue\": \"<md5 HASH object @ 0x7f5cec359410>\"}
```

