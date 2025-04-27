# CCTV Video Service

## 📹 Video API Documentation

**Base URL:** `/video`

---

### GET `/video/{videoId}`

**Description:**  
Retrieve metadata of a single video by its ID.

**Path Parameters:**

- `videoId` (string, required): UUID of the video.

**Responses:**

- `200 OK`: Returns the video metadata.
- `404 Not Found`: Video not found.
- `500 Internal Server Error`: If retrieval fails.

---

### DELETE `/video/{videoId}`

**Description:**  
Delete a video by its ID.

**Path Parameters:**

- `videoId` (string, required): UUID of the video.

**Responses:**

- `200 OK`: Video successfully deleted.
- `404 Not Found`: Video not found.
- `500 Internal Server Error`: If the deletion fails.

---

### GET `/video/videos`

**Description:**  
Retrieve a set of videos. Optionally filter by uploader.

**Query Parameters:**

- `uploader` (string, optional): Filter videos by uploader name.
- `search` (string, optional): Filter videos by title containing search.

**Responses:**

- `200 OK`: Returns a set of video metadata.

---

### POST `/video`

**Description:**  
Store a new video.

**Request Body:**

- `Video` (JSON): The video object to store.

**Responses:**

- `201 Created`: Video stored successfully.
- `500 Internal Server Error`: If storing fails.

### POST `/{videoId}/view`

**Description:**  
Increment the view count on a video

**Request Body:**

- empty

**Responses:**

- `200 Ok`: Video view incremented successfully
- `404 Not Found`: Video not found
- `500 Internal Server Error`: If storing fails.

---

## 📦 Example `Video` Schema

```json
{
  "uuid": "123e4567-e89b-12d3-a456-426614174000",
  "title": "Mall CCTV Footage",
  "description": "Surveillance footage from the north entrance",
  "tags": [
    "security",
    "entrance",
    "night"
  ],
  "creationDate": "2025-04-22T14:30:00",
  "uploader": "security_team"
}
```

## Micronaut 4.7.5 Documentation

- [User Guide](https://docs.micronaut.io/4.7.5/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.7.5/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.7.5/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Deployment with GraalVM

If you want to deploy to AWS Lambda as a GraalVM native image, run:

```bash
./mvnw package -Dpackaging=docker-native -Dmicronaut.runtime=lambda -Pgraalvm
```

This will build the GraalVM native image inside a docker container and generate the `function.zip` ready for the deployment.


## Handler

Handler: io.micronaut.function.aws.proxy.payload2.APIGatewayV2HTTPEventFunction

[AWS Lambda Handler](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)

- [Micronaut Maven Plugin documentation](https://micronaut-projects.github.io/micronaut-maven-plugin/latest/)
## Feature http-client-jdk documentation

- [Micronaut HTTP Client Jdk documentation](https://docs.micronaut.io/latest/guide/index.html#jdkHttpClient)

- [https://openjdk.org/groups/net/httpclient/intro.html](https://openjdk.org/groups/net/httpclient/intro.html)


## Feature maven-enforcer-plugin documentation

- [https://maven.apache.org/enforcer/maven-enforcer-plugin/](https://maven.apache.org/enforcer/maven-enforcer-plugin/)


## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)


## Feature amazon-api-gateway-http documentation

- [Micronaut Amazon API Gateway HTTP documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/index.html#amazonApiGateway)

- [https://docs.aws.amazon.com/apigateway/](https://docs.aws.amazon.com/apigateway/)


## Feature aws-lambda-custom-runtime documentation

- [Micronaut Custom AWS Lambda runtime documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/index.html#lambdaCustomRuntimes)

- [https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html)


## Feature aws-lambda-events-serde documentation

- [Micronaut AWS Lambda Events Serde documentation](https://micronaut-projects.github.io/micronaut-aws/snapshot/guide/#eventsLambdaSerde)

- [https://github.com/aws/aws-lambda-java-libs/tree/main/aws-lambda-java-events](https://github.com/aws/aws-lambda-java-libs/tree/main/aws-lambda-java-events)


## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)


## Feature aws-lambda documentation

- [Micronaut AWS Lambda Function documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/index.html#lambda)


