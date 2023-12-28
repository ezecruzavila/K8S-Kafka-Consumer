# The challenge:
You must establish an abstraction for a collection of apps running on a Kubernetes cluster. The real challenge is that this abstraction should have the capability to adapt to different formats apart from Kubernetes.
For example, envision a scenario where certain applications are running as K8s Deployments while others are running on AWS Lambda. We desire the capability to have a level of abstraction that allows us to consolidate both environments into a single format.

## Now let's code:

We are providing a docker-compose file which contains:

- A Kafka broker which has a topic `k8s-resources` with a bunch od preloaded data
- A Postgres DB
- The base for the app you are developing

The objective is to create an app which is going to consume the messages from the topic and store them into the DB following the abstraction you've defined.
It's important to consider that an App is composed by many Kubernetes' resources.

## Kubernetes data structure
The provided data is from a real Kubernetes Cluster, if you are not familiar with Kubernetes here are some tips on how data is store and linked.

1. In this set of data you have two type of applications `Deployment` and `StatefulSet`
2. Child resources are linked to their parent by a field called `ownerReferences`
    1. The exception on this example is the `Service`, which is related by a `spec.selector` to a `Deployment` or `StatefulSet`

## Considerations

You are free to structure the projects, choose frameworks and/or libraries as you see fit to deliver on this project. Also feel free to update the `docker-compose` file as you see necessary. The only condition is that it has to work with a simple `docker-compose up`.

We will be evaluating data structures, synchronization, resiliency and performance strategies.
And be mindful that this is your presentation letter! Make sure it talks highly of you!

The project is ready to be implemented using Kotlin. However, if you believe that using Java or Scala would result in a better outcome, we are open to accepting those as well. Similarly, if you prefer to use Maven instead of Gradle, we are flexible with that choice too.

## What are we evaluating?
The overall idea is to understand how you analyze the challenge, the decisions you make and the final solution.
To evaluate the final solution, we will focus in the following key points:

- App resiliency
- App scalability and data synchronization
- Messages consumption handling
- Data structures and abstractions
- Language understanding
- Tests
- Documentation
- Solution improvements

## Do you have any questions?
Don´t be shy, send us an email with your questions and we will try to give our best.

