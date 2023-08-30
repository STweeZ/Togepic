# Togepic

## Instruction de lancement

### Back

>Effectuer un `docker-compose up` à la racine du projet.
>
>Vous bénéficierez :
>    - D'un accès aux logs de l'application **localhost:8000/kibana**
>    - D'un accès à l'API **localhost:8000/togepic**
>    - D'un accès au contrat de service **localhost:8000/swagger/**
>    - D'un accès à la base de données **localhost:5431/togepic** avec les accès suivants :
>        - name=*postgres*
>        - password=*z021d9f-48CoXt9*

## UML persistance concept

```plantuml
hide empty members
skinparam classAttributeIconSize 0


class User {
    - long id
    - long version
    - string name
    - string email
    - string password
    - byte[] picture
    - string description
    - string token
    - timestamp lastConnexion
    - timestamp registration
    - boolean emailVerified
    - Verification verification
    - List<Member> groupes
    - List<Post> posts
}

class Verification {
    - long id
    - string token
    - timestamp expiration
    - User user
}

class Groupe {
    - long id
    - long version
    - string name
    - string description
    - byte[] picture
    - timestamp creation
    - boolean isPrivate
    - List<Member> users
    - List<Post> posts
}

class Post {
    - long id
    - string text
    - timestamp creation
    - User user
    - Groupe groupe
    - Post parent
    - List<Post> childrens
    - List<Picture> pictures
}

class Picture {
    - int id
    - byte[] text
    - string xGeolocate
    - string yGeolocate
    - Post post
}

class Invitation {
    - long id
    - boolean isRequest
    - Groupe groupe
    - User user
}

class Member {
    - long id
    - Role role
    - Groupe groupe
    - User user
}

enum Role {
    READER, EDITOR, ADMIN
}

Verification "1" <--> "1" User
User "1" <--> "0..*" Member
Member "0..*" <--> "1" Groupe
User "1" <--> "0..*" Post
Groupe "1" <--> "0..*" Post
Post "1" <--> "0..*" Picture
Post "0..*" <--> "0..1" Post
Invitation "0..*" --> "1" User
Invitation "0..*" --> "1" Groupe
```
