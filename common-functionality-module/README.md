# Common Functionality Module

This module contains common functionality that can be shared across the application.

## Features

### Firestore Initialization

The `FirestoreInitializer` class provides a common way to initialize and access the Firestore database.

```java
@Autowired
private FirestoreInitializer firestoreInitializer;

// Get the Firestore instance
Firestore db = firestoreInitializer.getDb();
```

### Request Context

The module provides a way to add basic context to each request through the `RequestContextInterceptor`.
This interceptor adds the following information to each request:

- Request ID (UUID)
- Request Timestamp
- User Agent
- Request Path

You can access this information from anywhere in the application using the `RequestContextUtil`:

```java
@Autowired
private RequestContextUtil requestContextUtil;

// Get the request ID
String requestId = requestContextUtil.getRequestId();

// Get the request timestamp
Object timestamp = requestContextUtil.getRequestTimestamp();

// Get the user agent
String userAgent = requestContextUtil.getUserAgent();

// Get the request path
String path = requestContextUtil.getRequestPath();
```

## Configuration

The module includes a `common.properties` file with configuration options:

```properties
# Logging configuration
common.logging.enabled=true
common.logging.level=INFO

# Request context configuration
common.request.context.enabled=true
common.request.context.include-headers=true
```

## Usage

To use this module in your application, add it as a dependency in your build.gradle file:

```gradle
dependencies {
    implementation project(':common-functionality-module')
}
```

Then import the configuration in your application:

```java
@SpringBootApplication
@Import(CommonModuleConfiguration.class)
public class YourApplication {
    // ...
}
