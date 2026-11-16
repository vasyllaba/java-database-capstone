This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules.
The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). 
All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.
The app is based on the GOF patterns.

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer.
4. The service layer operates business logic and calls repository layer.
5. The repository layer calls the database directly (May be MySQL or MongoDB).
6. Spring binds the models using @Entity annotation for Relational DB or @Document for Mongo.
7. The populated Java models proceded by our business logic are sent back to the UI.
