# Blog-server
![Travis build](https://api.travis-ci.com/jaguar51/Blog-server.svg?token=eeiUqii3zoxH4p569Lqh&branch=master)

The server part of the blogging system implemented with Spring Boot/JPA + Hibernate + MySQL.

## Install
1. Clone the project
2. Build project

	`gradlew build`
3. Create MySQL database with default params

    `dbName` = `spring_blog_db`   
    `Username` = `user`   
    `Password` = `1234`
4. Run jar file 

    ```bash
    java ${RUN_PARAMETERS} -jar blog-api-CURRENT_VERSION.jar
    ```
    
    RUN_PARAMETERS:
    ```bash
    -Dspring.datasource.url=jdbc:mysql://localhost:3306/spring_blog_db
    -Dspring.datasource.username=user
    -Dspring.datasource.password=1234
    -Dspring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
    -Dspring.jpa.hibernate.ddl-auto=update
    -Dspring.jpa.generate-ddl=true
    -Dspring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
    
    -Dlogging.level.root=INFO
    -Dlogging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=ERROR
    ```
    
## Api Documentation
You can check the documentation in [wiki](https://github.com/jaguar51/Blog-server/wiki)

## Contribute
Contributions are always welcome!

## License
> You can check out the full license [here](LICENSE)

This project is licensed under the terms of the **MIT** license.
