# Product-Api

The project consists of a **Spring Boot / Data REST** backend and a very simpel **React** client that allows a user to view data (writes are not supported in the client).
The backend stores data in an in-memory instance of **H2**.
The frontend uses **Bootstrap** for layout and basic style.

To run: `spring-boot:run -Drun.profiles=dev`\
To run tests: `test -Dspring.profiles.active=test`

To query from command line:
* `curl localhost:8080/api/products` *- view all currently persisted products*
* `curl localhost:8080/api/products/{id}` *- view the product with the given id*

Please refer to *'ProductApiApplicationTests'* for a more complete overview of the supported operations.

*Note: The client will warn that Bootstrap requires JQuery - this can be safely ignored.*# Product-Api
