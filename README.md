# Albarapp #

CRM web app focused on delivery notes management. It is targeted for small business with simple distribution channel and aimed to be deployed in the cloud.

---
### Tehcnology Stack ###  
Frontend:
* Vue
* Vuetify
* Axios   

Backend: 
* Spring Boot 
* Spring Data Rest
* SQL Database

---
### Build App ###  
There are three maven profiles. You can build with any group of them:
- development: Add development dependencies
- production: Add production dependencies
- front-end: Add frontend maven plugin

        ./mvnw clean install -P development,production,frontend
---
### Run App ###  
There are two spring profiles. You can specify the active profile with -Dspring.profiles.active
- Development:

        java -jar -Dspring.profiles.active=development target/*.jar
        ./mvnw spring-boot:run -P development -Dspring-boot.run.profiles=development

- Production:

        java -jar -Dspring.profiles.active=production,frontend target/*.jar
        ./mvnw spring-boot:run -P production,frontend -Dspring-boot.run.profiles=production

- Frontend development (with hot reload). In albarap-frontend directory:

        npm run serve

---
### Docker ###  
Build docker image with the app and run a container:
- Development:

        ./mvnw clean install -P development,frontend
        docker build -t albarapp/albarapp .
        docker run -d -p 80:8085 -v ~/logs:/logs "SPRING_PROFILES_ACTIVE=development" albarapp/albarapp

- Production:
        
        ./mvnw clean install
        docker build -t albarapp/albarapp:VERSION_TAG .
        docker run -d -p 80:8085 -v /home/ec2-user/logs-albarapp:/logs --env-file /home/ec2-user/env.albarapp albarapp/albarapp:VERSION_TAG
