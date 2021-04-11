# AI
## Installation 
- Download [zookeeper](https://www.apache.org/dyn/closer.cgi/zookeeper/)
- Download [kafka](https://kafka.apache.org/downloads)

In the terminal launch
```sh
$ ./apache-zookeeper-3.5.9-bin/bin/zkServer.sh start
$ ./kafka_2.13-2.7.0/bin/kafka-server-start.sh -daemon ../config/server.properties
```

## Usage 

You can lauch the projet with intellij. 

To generate the rdf file will need the following part of the code.
```scala
// Write file in resources    
listOfPersons().foreach(person => {  
  
 PersonModel.addAll(model, map, person.toString)})  
println("rdf dataset size = " + model.size())  
ModelFileWriter.write("/modelVaccine.rdf", model, "RDF/XML-ABBREV")  
```

## Contributors

-   Ailton LOPES MENDES
-   Jonathan CHU
-   Fabien LAMBERT--DELAVAQUERIE
-   Gérald LIN
