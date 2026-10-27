# Microservices with Java, AWS Lambda, and API Gateway

Introductory lab on microservices with **Java** using **AWS Lambda** and **Amazon API Gateway**.

It implements a REST service that, starting from the classic example of calculating the square of an integer, evolves into handling more complex inputs and outputs through POJO objects (JSON).

## Table of Contents

- [Description](#description)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Build and Package](#build-and-package)
- [Create the Lambda Function](#create-the-lambda-function)
- [Expose the Service with API Gateway](#expose-the-service-with-api-gateway)
- [Publish the Service](#publish-the-service)
- [More Complex Inputs and Outputs](#more-complex-inputs-and-outputs)
- [Clean Up Resources](#clean-up-resources)

## Description

This lab walks through the complete flow for publishing a service on AWS:

1. Create a class that implements the desired service.
2. Create a **Lambda** function and upload the classes packaged in a JAR.
3. Configure **API Gateway** to expose the function as a REST service.
4. Test and publish the service so it is available over the internet.

## Project Structure

```
services/
├── pom.xml
└── src
    ├── main/java/co/edu/escuelaing
    │   ├── App.java
    │   └── services
    │       ├── MathServices.java   # Function that calculates the square
    │       ├── Handler.java        # Handler with input/output POJOs
    │       ├── Peticion.java       # Input POJO (nombre, a, b)
    │       └── Respuesta.java      # Output POJO (mensaje, suma)
    └── test/java/co/edu/escuelaing
        └── AppTest.java
```

### `MathServices.java`

A simple function that receives an integer and returns its square, with no extra dependencies:

```java
package co.edu.escuelaing.services;

public class MathServices {

    public static Integer square(Integer i) {
        return i * i;
    }
}
```

### More complex inputs and outputs

To illustrate handling JSON objects, the project includes a handler and its POJOs:

```java
public class Handler {
    public Respuesta handleRequest(Peticion peticion) {
        String nombre = (peticion.getNombre() == null || peticion.getNombre().isBlank())
                ? "mundo" : peticion.getNombre();
        int suma = peticion.getA() + peticion.getB();
        return new Respuesta("Hola " + nombre, suma);
    }
}
```

- `Peticion` (input): fields `nombre` (String), `a`, and `b` (int).
- `Respuesta` (output): fields `mensaje` (String) and `suma` (int).

AWS Lambda automatically serializes and deserializes the JSON input/output according to the defined POJO type.

## Requirements

- Java (the project compiles with `maven.compiler.release` 21).
- [Maven](https://maven.apache.org/).
- An AWS account (in AWS Academy, the `LabRole` role is used).

## Build and Package

```bash
mvn package
```

The resulting JAR (for example `target/services-1.0-SNAPSHOT.jar`) is the one uploaded to the Lambda function. The project uses the `maven-shade-plugin` to generate a self-contained JAR.

## Create the Lambda Function

1. In the AWS console, open the **Lambda** service.
2. Click **Create function** and select **Author from scratch**.
3. Give the function a name (for example `square`).
4. Select **Java** as the runtime.
5. Under **Permissions**, choose *Use an existing role* and select the execution role (in AWS Academy, `LabRole`). If it does not exist, create one or let AWS create it automatically.
6. In **Code**, upload the generated JAR.
7. In the **Handler** field, enter the class and method in this format:

   ```
   {fully qualified class name}::{method name}
   ```

   For example:

   ```
   co.edu.escuelaing.services.MathServices::square
   ```

8. Click **Save**.

### Test the function

1. At the top, open **Select a test event** → **Configure test events**.
2. Give the event a name (for example `testSquare`).
3. Delete the JSON shown and enter only the number `5`.
4. Click **Create** and then **Test**.

> Note that the function is tested with the single value `5`, not with a JSON object. This distinction matters when mapping the input in the next step.

## Expose the Service with API Gateway

1. Open the **API Gateway** service.
2. Click **Getting Started**.
3. Select **REST API** and **New API**.
4. Give the API a name (for example `mathServices`).
5. Select **Regional** as the endpoint type.

### Create the GET method

1. In the `mathServices` API, open the **Actions** menu next to *Resources* and select **Create Method**.
2. Select **GET** and confirm.
3. In the **Lambda Function** field, enter the name of the function you created and save.

### Configure the request and mapping

1. Click **Method Request** and add a parameter named `value` under *URL Query String Parameters*.
2. Go back to **Method Execution** and open **Integration Request**.
3. Under *URL Query String Parameters*, create a parameter named `value` mapped to:

   ```
   method.request.querystring.value
   ```

4. Configure a **mapping template** to specify how parameters are passed to the Lambda function.

> Many default options and online examples pass parameters inside a JSON object. For this lab, only a number is passed.

To pass real parameters, use the following in the mapping template:

```
$input.params("value")
```

### Test the method

1. Go back to the method configuration.
2. Open **Test** and run it.
3. Include a `value` parameter with an integer. If you omit it, an error is reported.

## Publish the Service

1. Open the **Actions** menu and select **Deploy API**.
2. Select **[New Stage]** and give it a name (for example `Beta`). Other stages (such as testing and production) can also be managed here.

The URL shown at the top lets you invoke the service. For example:

```
https://24uiyf4bn6.execute-api.us-east-1.amazonaws.com/Beta?value=14
```

## Clean Up Resources

Remember to delete the Lambda function and the API when you are done to avoid unnecessary charges.

## References

- [API Gateway Mapping Template Reference](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)
- [aws-lambda-java-events](https://github.com/aws/aws-lambda-java-libs)
