# SW-WoT

SW-WoT is a University Project for the Semantic Web exam. It allow to semantically discover the things described by a WoT thing description (TD). It's composed by 3 sub-modules:

- core: contains the core functionality of SW-WoT, like things validation. Moreover this module contains two ontology: WoT and HomeOnto. The last one, is a custom ontology for describe some smart appliance in terms of WoT concept. HomeOnto is not to be considered a valid ontology for the description of Smart Applicance, it is only an example to show the potential of such an approach. (see the section of possible future works)
- semantic-directory: allow to register thing descriptions and search for it using SPARQL query.
- discovery-gateway: allow to make advanced search, like automatic collection of data retrieved from things. (e.g. all temperature values from temperature sensors).



## Installation

The project could be open using Intellij that supports Kotlin very well. The main libraries used are: Jena and Vertx.

Moreover, in order to parse JSONLD-1.1 thing description format, its required to install Ruby on Rails on system and setup a configuration file for semantic-directory module:

```json
{
  "host": "0.0.0.0",
  "port": 10000,
  "jsonld": {
    "type": "ruby",
    "ruby_exec": "ruby",
    "script": "semantic-directory/rubyjsonld/jsonld_rdf.rb"
  },
  "thing_folder": "semantic-directory/src/main/resources/things"
}
```

- `host` and `port` allow to configure the semantic directory webservice;
- `jsonld` allow to configure the parser for JSONLD-1.1. 
  - `type` defines which parser to use. Now, only `ruby` is supported.
  - `ruby_exec` defines the ruby executable path. If the ruby path is added to PATH variable, don't change this value.
  - `script` defines the ruby's script to use for parse the JSONLD-1.1.
- `thing_folder` its an optional parameter. Allow to define a folder from which to load some thing descriptions at boot.



## Usage

The project contains another module called `scenarios` that contains some useful examples of how to use the system.

1. How to start the semantic directory? ( after right setup of the configuration file in the `resources`)

    ```kotlin
    val config = JSONObject(Source.readFromResource("directory-config.json"))
    val jsonLdParser = JSONLDParserFactory.fromJson(config.getJSONObject("jsonld"))!!
    val parser = TDParser(jsonLdParser = jsonLdParser)
    val tdd = ThingDescriptionDirectory()
    val restVerticle = RestApiSemanticDiscovery(
                tdParser = parser,
                thingDescriptionDirectory = tdd,
                host = config.getString("host"),
                port = config.getInt("port"))

    val vertx = Vertx.vertx()
    vertx.deployVerticleAwait(restVerticle)
    ```

    Optionally, inside `scenarios` folder there is two utility bootstrap method for semantic directory:

    ```kotlin
    // Start the semantic directory using the config file in resources (directory-config.json)
    Bootstrap.defaultBoot(/*optionally, some thing description in json*/)

    // Start the semantic directory specifing a config file
    Bootstrap.bootSemanticDirectory(File(/*..*/), /*optionally, some thing description in json*/)
    ```

2. How to create a semantic gateway for advanced search?

   ```kotlin
   // need to specify host and port where semantic directory is running
   val discovery = DiscoveryGateway.fromDirectory("localhost", 10000)
   ```

3. How search some things?

   e.g. collect some values from things

   ```kotlin
   // this search things and collect data on specific property
   val query = thingCollectQuery {
       filter {
           canSense { FeatureProperty.Temperature }
       }
       collectOn { FeatureProperty.Temperature }
   }
   discovery.collectData(query).mapNotNull { it.asDouble() }.average()
   ```

   e.g. only discover things

   ```kotlin
   val query = thingQuery { 
       canSense { 
           feature { FeatureProperty.AmbientTemperature }
           feature { FeatureProperty.AmbientHumidity }
       }
       canActOn { 
           FeatureProperty.AmbientTemperature
       }
   }
   val things = discovery.searchThings(query)
   ```



## Possible Improvements

- align WoT ontology to SAREF ontology and describe the smart appliances in terms of both instead of my custom Home-WoT ontology;
- add support for dynamic discovery (e.g. find things that perceive a temperature > 20)
  - actually the system allow to collect data. This functionality could be used to achieve dynamic discovery
- improve the architectural layer of WoT that allow to achieve operational interoperability. Actually there is a mock implementation of the WoT stack inside `ConsumedThing.kt`
- allow to retrieve the things by id through semantic directory web service using a path like: `/td/{id}`

