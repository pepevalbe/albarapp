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

        java -jar -Dspring.profiles.active=development target/albarapp-1.0.0.jar
        ./mvnw spring-boot:run -P development -Dspring-boot.run.profiles=development

- Production:

        java -jar -Dspring.profiles.active=production,frontend target/albarapp-1.0.0.jar           
        ./mvnw spring-boot:run -P production,frontend -Dspring-boot.run.profiles=production

- Frontend development (with hot reload). In albarap-frontend directory:

        npm run serve

