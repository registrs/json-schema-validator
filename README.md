# JSON schema validation service


## Startup

At project directory run command (runs on port 8090). Database (SQLLite) will and initial schema will be created after startup automatically.
```bash
sbt run
```

## Run tests
At project directory run
```bash
sbt test
```

## API

Example requests of retrieval of schema, saving new schema and validating json against schema.
```bash
curl http://localhost:8090/schema/schemaId
```
```bash
curl -d '{"$schema":"http://json-schema.org/draft-03/schema#","patternProperties":{"f.*o":{"type":"integer"}}}' -H 'Content-Type: application/json' http://localhost:8090/schema/schemaId
```
```bash
curl -d '{"quux":3}' -H 'Content-Type: application/json' http://localhost:8090/validate/3
```




