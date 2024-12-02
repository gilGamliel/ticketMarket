# ticketMarket
tickets exchange 
I designed and developed a dynamic tickets exchange management application using a modern tech stack, ensuring an efficient and seamless user experience for event creation and interaction.

Technologies and Features
Backend with Spring Boot:

Leveraged Spring Boot for backend development, using its powerful dependency injection and Spring MVC capabilities for routing and request handling.
Designed controllers, such as createEventController, to handle event creation and processing with structured logic.
Database Integration with Spring Data JPA:

Mapped the Event entity to a MySQL database using Hibernate annotations like @Entity and @Column.
Implemented CRUD operations using JpaRepository, alongside custom logic with a repository implementation to check for duplicate event names.
Dynamic Frontend with Thymeleaf:

Used Thymeleaf as the templating engine for rendering dynamic HTML pages, including forms and personalized content.
Designed templates such as createEventForm for creating events and success_event_created for confirmation, dynamically binding backend data using th:text and th:action.
JavaScript and Geolocation:

Integrated geolocation functionality via the navigator.geolocation API to fetch the user's location dynamically.
Used the OpenCage Geocoding API to convert latitude and longitude into city names, enhancing the user's personalized experience.
Session Management:

Used HttpSession to store user details post-login, enabling session-based personalization such as displaying "Hello, [User’s Name]" on the homepage.
Error Handling and Validation:

Incorporated backend validation to check if an event already exists, notifying users of conflicts through dynamic error messages on the frontend.
Database and Entity Design:

Structured the Event entity with fields like event_name, event_date, event_desc, and event_owner, ensuring all necessary event details were stored efficiently in the database.
RESTful API Integration:

Designed RESTful endpoints for handling form submissions and processing data seamlessly, following clean architectural principles.
Frontend Design with HTML and CSS:

Created user-friendly forms and interfaces using HTML5 for structure and CSS3 for styling, ensuring a responsive and visually appealing design.
Project Workflow
Frontend Interaction:

Users interact with the application by creating events via a clean and responsive interface, validated client-side and server-side.
Backend Processing:

Form submissions trigger HTTP POST requests to Spring Boot controllers, which validate and process the data before storing it in the database.
Data Persistence and Retrieval:

Events are stored in the database and retrieved using repository methods. Custom logic ensures duplicate events are not created.
Location-Based Personalization:

The user's location is dynamically fetched and displayed on the homepage, adding a layer of interactivity and personalization.
Skills and Concepts Demonstrated
Custom Repository Implementation: Created a custom JPA repository to extend default functionality with domain-specific logic.
Dynamic Templates: Used Thymeleaf to bind backend data directly to the frontend, minimizing redundant code and enhancing scalability.
Session Management: Implemented session-based data storage to personalize user experiences.
API Integration: Seamlessly integrated geolocation and external APIs to enhance functionality.
Through this project, I demonstrated proficiency in Java, Spring Boot, JPA, Thymeleaf, and API integration while ensuring a modular, maintainable, and user-centric design.

