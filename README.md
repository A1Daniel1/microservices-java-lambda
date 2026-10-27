# Microservices with Java, AWS Lambda y API Gateway

Laboratorio de introducción a los microservicios con **Java** usando **AWS Lambda** y **Amazon API Gateway**.

Se implementa un servicio REST que, partiendo del ejemplo clásico de calcular el cuadrado de un entero, evoluciona hacia el manejo de entradas y salidas más complejas mediante objetos POJO (JSON).

## Contenido

- [Descripción](#descripción)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Compilar y empaquetar](#compilar-y-empaquetar)
- [Crear la función Lambda](#crear-la-función-lambda)
- [Exponer el servicio con API Gateway](#exponer-el-servicio-con-api-gateway)
- [Publicar el servicio](#publicar-el-servicio)
- [Entradas y salidas más complejas](#entradas-y-salidas-más-complejas)
- [Limpieza de recursos](#limpieza-de-recursos)

## Descripción

Este laboratorio muestra el flujo completo para publicar un servicio en AWS:

1. Crear una clase que implemente el servicio deseado.
2. Crear una función **Lambda** y subir las clases empaquetadas en un JAR.
3. Configurar **API Gateway** para exponer la función como un servicio REST.
4. Probar y publicar el servicio para que quede disponible por internet.

## Estructura del proyecto

```
services/
├── pom.xml
└── src
    ├── main/java/co/edu/escuelaing
    │   ├── App.java
    │   └── services
    │       ├── MathServices.java   # Función que calcula el cuadrado
    │       ├── Handler.java        # Handler con POJOs de entrada/salida
    │       ├── Peticion.java       # POJO de entrada (nombre, a, b)
    │       └── Respuesta.java      # POJO de salida (mensaje, suma)
    └── test/java/co/edu/escuelaing
        └── AppTest.java
```

### `MathServices.java`

Función sencilla que recibe un entero y devuelve su cuadrado. No tiene dependencias adicionales:

```java
package co.edu.escuelaing.services;

public class MathServices {

    public static Integer square(Integer i) {
        return i * i;
    }
}
```

### Entradas y salidas más complejas

Para ilustrar el manejo de objetos JSON, el proyecto incluye el handler y sus POJOs:

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

- `Peticion` (entrada): campos `nombre` (String), `a` y `b` (int).
- `Respuesta` (salida): campos `mensaje` (String) y `suma` (int).

AWS Lambda serializa y deserializa automáticamente el JSON de entrada/salida según el tipo POJO definido.

## Requisitos

- Java (el proyecto compila con `maven.compiler.release` 21).
- [Maven](https://maven.apache.org/).
- Una cuenta de AWS (en AWS Academy se usa el rol `LabRole`).

## Compilar y empaquetar

```bash
mvn package
```

El JAR resultante (por ejemplo `target/services-1.0-SNAPSHOT.jar`) es el que se sube a la función Lambda. El proyecto usa el `maven-shade-plugin` para generar un JAR autocontenido.

## Crear la función Lambda

1. En la consola de AWS, abre el servicio **Lambda**.
2. Haz clic en **Create function** y selecciona **Author from scratch**.
3. Asigna un nombre a la función (por ejemplo `square`).
4. Selecciona **Java** como runtime.
5. En **Permissions**, elige *Use an existing role* y selecciona el rol de ejecución (en AWS Academy, `LabRole`). Si no existe, crea uno o deja que AWS lo cree automáticamente.
6. En **Code**, sube el JAR generado.
7. En el campo **Handler**, escribe la clase y el método en el formato:

   ```
   {nombre completo de la clase}::{método}
   ```

   Por ejemplo:

   ```
   co.edu.escuelaing.services.MathServices::square
   ```

8. Haz clic en **Save**.

### Probar la función

1. Arriba, abre **Select a test event** → **Configure test events**.
2. Asigna un nombre al evento (por ejemplo `testSquare`).
3. Borra el JSON mostrado y escribe únicamente el número `5`.
4. Haz clic en **Create** y luego en **Test**.

> Observa que la función se probó con el valor único `5`, no con un objeto JSON. Esta distinción importa al mapear la entrada en el siguiente paso.

## Exponer el servicio con API Gateway

1. Abre el servicio **API Gateway**.
2. Haz clic en **Getting Started**.
3. Selecciona **REST API** y **New API**.
4. Asigna un nombre a la API (por ejemplo `mathServices`).
5. Selecciona **Regional** como tipo de endpoint.

### Crear el método GET

1. En la API `mathServices`, abre el menú **Actions** junto a *Resources* y selecciona **Create Method**.
2. Selecciona **GET** y confirma.
3. En el campo **Lambda Function**, escribe el nombre de la función creada y guarda.

### Configurar la petición y el mapeo

1. Haz clic en **Method Request** y agrega un parámetro llamado `value` en *URL Query String Parameters*.
2. Regresa a **Method Execution** y abre **Integration Request**.
3. En *URL Query String Parameters*, crea un parámetro llamado `value` mapeado a:

   ```
   method.request.querystring.value
   ```

4. Configura una **mapping template** para indicar cómo se pasan los parámetros a la función Lambda.

> Muchas opciones por defecto y ejemplos en línea pasan los parámetros dentro de un objeto JSON. Para este laboratorio, solo se pasa un número.

Para pasar parámetros reales, usa en la plantilla de mapeo:

```
$input.params("value")
```

### Probar el método

1. Regresa a la configuración del método.
2. Abre **Test** y ejecútalo.
3. Incluye un parámetro `value` con un entero. Si lo omites, se reportará un error.

## Publicar el servicio

1. Abre el menú **Actions** y selecciona **Deploy API**.
2. Selecciona **[New Stage]** y asigna un nombre (por ejemplo `Beta`). Aquí también se pueden gestionar otros entornos (pruebas, producción).

La URL que aparece arriba permite invocar el servicio. Por ejemplo:

```
https://24uiyf4bn6.execute-api.us-east-1.amazonaws.com/Beta?value=14
```

## Limpieza de recursos

Recuerda eliminar la función Lambda y la API al terminar para evitar cargos innecesarios.

## Referencias

- [API Gateway Mapping Template Reference](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)
- [aws-lambda-java-events](https://github.com/aws/aws-lambda-java-libs)
