# load-balancer-round-robin

Components of project

naming-server - Eureka naming server that allows visibility between the microservices. Microservices register to this naming server and allows visibility of host, port, and app name.

round-robin-entry - API used for JSON validation logic. Run with multiple instances.

simple-api-entry - API used as the entry point for the POST request. Also contains the round robin load balancing implementation.
