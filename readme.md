# Read Me First

**IoT-Simulator-Service API**

* Generate mock IoT data and publish it to Kafka.

# Prerequisite - Environment Setup
1. JRE 17
2. Maven 4.0+
3. IDE of your choice
4. Docker Desktop

# Prerequisite - Dependency on another applications
* IoT-Simulator-Service API is dependent on
    1. data-ingestion-service

# Getting Started - Build & Run Application
* Execute command - docker compose up -d to start kafka & zookeeper container's locally on Docker Desktop
* Verify if the kafka & zookeeper container's properly started with command - docker ps
* Build application with command - mvn clean install
* Start iot-simulator-service application with command - mvn spring-boot:run
* Verify if the application started smoothly on port 8081
* Check application logs for successful message being sent to Kafka.