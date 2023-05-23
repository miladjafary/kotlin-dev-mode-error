[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=goflink_payment-service&metric=alert_status&token=68af1364d405d62cdb6f74c530ced8d53600846f)](https://sonarcloud.io/summary/new_code?id=goflink_payment-service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=goflink_payment-service&metric=coverage&token=68af1364d405d62cdb6f74c530ced8d53600846f)](https://sonarcloud.io/summary/new_code?id=goflink_payment-service)
# Payment Service 

This service will be main use for dealing with whatever is related to handling a payment transaction. 
It is developed in Kotlin and uses Quarkus framework, the Supersonic Subatomic Java Framework. 

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/payment-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): A JAX-RS implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

# Adyen Webhook
The [Adyen webhook](https://github.com/goflink/payment-service/blob/e58b470c1f37a958e91404cfdac95b70fb865140/src/main/kotlin/com/goflink/infrastructure/psp/adyen/webhook/TerminalEventNotificationResource.kt) 
has been added to the `com.goflink.infrastructure.psp.adyen.webhook` package, as its payload has a lot off commons class with Adyen Request/Response. 
In addition, the Ayden dependencies will be isolated from other parts of the projects.  


# Send local telemetry to Datadog
1. Launch the datadog agent container. The agent is preconfigured in [docker-compose.yml](https://github.com/goflink/payment-service/blob/main/docker-compose.yml#L3).
```
docker-compose up
```
2. Download the latest version of data-dog java tracer from https://dtdg.co/latest-java-tracer. It will be called **dd-java-agent.jar** after it downloads.
3. Run the app 
```
java -javaagent:/path/to/dd-java-agent.jar -Ddd.env=dev -Ddd.service=payment-service -Ddd.logs.injection=true -jar target/quarkus-app/quarkus-run.jar
```
For more info about how to monitor a JAVA Application, refer to the following links:
- [Tracing Java Applications](https://docs.datadoghq.com/tracing/trace_collection/dd_libraries/java/?tab=containers)
- [Connecting Java Logs and Traces](https://docs.datadoghq.com/tracing/other_telemetry/connect_logs_and_traces/java/?tab=log4j2)

## Integration test with BDD and cucumber
The integration tests (or e2e tests) are written in Behavioural style. Our ambition is to adopt Behaviour Driven 
Design ([BDD](https://cucumber.io/docs/bdd/)).  
BDD will help us to have our tests in a way that can be read and write by 
non-technical people (PMs, QAs etc.). It also enables us to have more and more testing scenarios only by reordering
the steps or modifying their values without touching the codes.  
We use [Cucumber](https://cucumber.io/) for implementing Behavioural tests.  
The tests have end to end setup and all the infrastructures likes database and broker run on test containers and tests 
are run against the real infrastructure. The only exceptions are some 3rd party APIs (Adyen for example) which are not 
possible to interact with them during the tests. Such APIs are stubs using Wiremock.

The feature files are located at `test/resource/features` and the step definitions are located at `test/it/steps/`.  
In order to add a new feature create `test/resource/features/foo.feature` and if it requires to define new steps add a 
respective `test/it/steps/foo.kt` file.
Once you annotate a function with `@given` ,`@when` or `@then` you can use it as a step in the feature file.

Following is a sample feature file:
```
# test/resource/features/greeting.feature

Feature: Greeting Clients
  Scenario: Greet new client with their firstname
    Given Client with email "j.doe@mail.com" is not our user
    When Client registers with following the info:
      | first name      | John      |
      | last name       | Doe       |
    Then Client receives the message: Welcome "John"
```
And the steps definition file for this feature is as below:
```kotlin
// test/it/steps/greeting.kt

@Given("Client with email {string} is not our user")
fun verifyUserIsNotRegistered(username: String) {
    // assert username can't be found in our DB
}

@When("Client registers with following the info:")
fun registerNewClient(userInfo: DataTable) {
    // register new client with the userInfo 
}

@Then("Client receives the message: {string}")
fun responseIsOk(greetingMessage: String) {
    // assert greetingMessage
}
```

Once we run the tests via `mvn test` the cucumber tests are automatically included, but we have the option to run them
separately using  
`mvn test -Dtest="CucumberResourceTest"`

