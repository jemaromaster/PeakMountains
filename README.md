## Crowdsourcing Mountain Identification webapp. 

- The system allows two types of users (campaign admins, annotators). 
- Admins are allow to create campaigns and upload peaks registry files. 
- Annotators can confirm the information of each peak and suggest edition to these. 
- Admins have a dashboard where they can filter information about their campaigns.

### Techonologies
- Deploying environment
    - Java 1.8
    - Postgres 9.0 (or change the JDBC version in pom.xml)
        - There is an script called 'import.sql' that should be automatically execute when creating the database.

- thymeleaf v 1.5
- SpringBoot
    - spring security 
    - spring storage

- Template HTML/Bootstrap used : Gentella Allela. https://colorlib.com/polygon/gentelella/index.html