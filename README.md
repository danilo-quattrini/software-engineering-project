# software-engineering-project

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=spring)
![Gradle](https://img.shields.io/badge/Gradle-%E2%89%A5%208.5-4DC9C0?logo=gradle&logoColor=209BC4)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.8-blue?logo=postgresql)
![License](https://img.shields.io/badge/License-MIT-A31F34?logo=license)

This project is a team-based task management application developed as part of a university group assignment. Built using Java and leveraging the Spring Boot framework for the backend.

# Description

The project consists of a marketplace selling goods from an agricultural supply chain, such as typical products and bundles of food.<br>
Various actors are involved, each representing the following roles in the platform:

- Producer
- Transformer
- Distributor
- Trustee
- Entertainer
- Buyer
- Admin

# Setup

## Requirements

- JDK 21
- PostgreSQL
- Gradle 8.5 and after (if the web application is executed with the cli)

## Clone repository

```sh
$ git clone https://github.com/danilo-quattrini/software-engineering-project.git
$ cd software-engineering-project
```

## Configuration

### Creating and connecting to a PostgreSQL DB

- `$ psql` to enter PostgreSQL
- `$ \l` to list all db
- `$ CREATE DATABASE {db_name};` to create a db
- `$ \du` to see the roles
- `$ GRANT ALL PRIVILEGES ON DATABASE "{db_name}" TO {user_name}` (give also all privileges to user postgres)
- `$ \c {db_name}` to connect to a db
- `$ \d` to see the relations inside a db
- `$ \d {table_name}` to describe a table
- `$ SELECT * FROM {table_name}` to retrieve all elemets from a table

### Writing the `application.properties` file

See the `app/resources/application.properties.example` file to see the configuration of the application.<br>
A postgre database is recommended.<br>
The project is also compatible with h2.<br>
Specify the db driver.<br>
Use `secured` as security profile.<br>
After everything is setup, delete `example` from `app/resources/application.properties.example` (`app/resources/application.properties`)

## Build and run

```sh
$ ./gradlew build
$ ./gradlew bootRun
```

# Team Members

- [Danilo Quattrini](https://github.com/danilo-quattrini)
- [Christian De Vincentis](https://github.com/PPathfinderIV)
- [Carlo Alberto Savi](https://github.com/olracrafter)

# Contributing

If you wish to contribute, please fork the repository and create a pull request with your changes.

# License

This project is licensed under the MIT license.
