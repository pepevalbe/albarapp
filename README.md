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
### Run App ###  

- Development\
./mvnw spring-boot:run -Dspring-boot.run.profiles=development

- Development only frontend (hot reload)\
cd albarapp-frontend    
npm run serve

- Production\
 ./mvnw spring-boot:run -Dspring-boot.run.profiles=production
