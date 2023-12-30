## Resilience
I chose a `limited non-blocking retry policy` and a `DLT topic` but there are many other options.

## Scalability & Performance
For scalability the app could be broke down into microservices, splitting the topic consumer and the DB access
In this case, for data synchronization, keeping an eye on the offset of the topic would be crucial.

For performance, using a framework like `Spring WebFlux` could improve it by allowing tasks to run in parallel
like reading and deserializing the message from the topic while writing to the DB.

## Observations
- I had to update the version from the images on the `docker-compose.yml` file due to compatibilities with Apple Silicon
chip (ARM) as the app was crashing even with no code implemented. Both `kafka` and `postgres` images have arm-based images.
This was not the case for the `aambertin/kafka-cli:3.3.1` image, but Docker was able to emulate with lesser performance
- Although the package structure of `controller` `service` `model` `repository` is no longer considered the best approach
for the sake of the simplicity of the exercise I built it that way.

## Resilience

## Testing
Used `Jacoco` for code coverage. Omitted `KubernetesController` since it's a demo class, not strictly related to the challenge
