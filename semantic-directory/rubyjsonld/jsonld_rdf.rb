require 'json/ld'
require 'rdf/nquads'

# {"@context":"https://www.w3.org/2019/wot/td/v1","id":"urn:dev:ops:32473-WoTLamp-1234","title":"MyLampThing","securityDefinitions":{"basic_sc":{"scheme":"basic","in":"header"}},"security":["basic_sc"],"properties":{"status":{"type":"string","forms":[{"href":"https://mylamp.example.com/status"}]}},"actions":{"toggle":{"name":"toggle","forms":[{"href":"https://mylamp.example.com/toggle"}]}},"events":{"overheating":{"data":{"type":"string"},"forms":[{"href":"https://mylamp.example.com/oh","subprotocol":"longpoll"}]}}}

baseIri = (0..0).map { ARGV.shift }

input_json = gets.chomp
parsed_json = JSON.parse(input_json)

preloaded_wot_context = JSON::LD::Context.new.parse(File.join(__dir__, "wot.jsonld"))
JSON::LD::Context.add_preloaded('https://www.w3.org/2019/wot/td/v1', preloaded_wot_context)
graph = RDF::Graph.new << JSON::LD::API.toRdf(parsed_json, base: baseIri )

puts graph.dump(:nquads)