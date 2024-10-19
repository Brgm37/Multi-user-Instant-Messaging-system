# Conceptual module
The following diagram holds the Entity-Relationship for the information represented by the system.
## Domain
### model
The following umls represents the main entities of the system.

---
#### channels
- This uml represents channels, which are the main entities of the system. They are the main way to communicate with other users.

<img src="../umls/domain/model/ChannelsUml.png">

---
#### messages
- This uml represents messages, which is how users communicate with each other.

<img src="../umls/domain/model/MessagesUml.png">

---
#### users
- This uml represents users, which are the entities that use the system.

<img src="../umls/domain/model/UsersUml.png">

---

### errors
- This uml represents errors, which are the entities that are used to represent errors that occur in the system.

<img src="../umls/domain/errors/ErrorUml.png">

---
## Services
### interfaces
- This uml represents the interfaces that are defined on service module.

<img src="../umls/service/interface/InterfacesUml.png">

---
### services
- This uml represents the services that are implemented on service module.

<img src="../umls/service/services/ServicesUml.png">

---

## Repository
- This uml represents the repositories that are implemented on repository module.

<img src="../umls/repository/RepositoryUml.png">

---

## Repository_jdbc
- This uml represents the repositories that are implemented on repository_jdbc module.

<img src="../umls/repository_jdbc/JdbcUml.png">

---

## HttpApi
### controllers
- This uml represents the controllers that are implemented on httpApi module.

<img src="../umls/controller/ControllerUml.png">

---