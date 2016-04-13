restful scala reactive backend
======================

DDD DOMAIN VALIDATION: http://codemozzer.me/domain,validation,action,composable,messages/2015/09/26/domain_validation.html

This is rest application seed using Scala 2.11, PlayFramework 2.4, Slick 3.1, Postgres 9.4, Akka Actors, FlyWay DB migrations, JWT Token Authentication and Deep Domain validation

- introduced working generic and reusable Slick Repository
- introduced generic domain service
- Seed is having multiple developed tools for deep domain validation. Deep Domain Validation is representing custom validation of Domain (or any others) objects that have simple or complex validation rules, from simple ones like notNullNorEmpty, lengthIsBiggerOrEqualTo, validEmail to complex ones like unique in DB some other complex dependencies between objects. It is providing simple solution of how to write structured ItemValidators[T] .
- Domain Validation is populating `Messages` which can have INFO, WARNING or ERROR messages, which can later be presented to API user, also populated `Messages` can be used to decide what to do if i.e. WARNING is present, then we can decide to go in some direction like retry our attempt, or if ERROR is present then we will revert multiple actions.
- in application is implementing deep validation where all ERRORS, WARNING and INFO messages are collected and returned in unified response

- all rest responses are unified and response has same structure every time so it is easier to handle errors, warning and information messages and also it is easier to handle specific data on pages.
Response is structured to have GLOBAL and LOCAL messages. LOCAL messages are messages that are coupled to some field i.e. = "username is too log. Allowed length is 80 chars". Global messages are messages that are reflecting state of whole data on page, i.e. "User will not be active until is approved". Local and Global messages are having three levels: ERROR, WARNING and INFORMATION.
example response: 

- GLOBAL messages:

```json
{
    "messages" : {
        "global" : {
            "info": ["User successfully created."],
            "warnings": ["User will not be available for login until is activated"],
            "errors": []
        },
        "local" : []
    },
	"data":{
	    "id": 2,
	    "firstName": "Mister",
	    "lastName": "Sir",
	    "username": "mistersir",
	    "email": "mistersir@example.com"
    }
}
```

- LOCAL messages:

```json
{
    "messages" : {
        "global" : {
            "info": [],
            "warnings": [],
            "errors": []
        },
        "local" : [
            {
                "formId" : "username",
                "errors" : ["User with this username already exists."],
                "warnings" : [],
                "info" : []
            }
        ]
    },
	"data":{
	    "id": 2,
	    "firstName": "Mister",
	    "lastName": "Sir",
	    "username": "mistersir",
	    "email": "mistersir@example.com"
    }
}
```

- JSON Web Tokens (JWT) is used for user identification and authentication

- application is divided into modules i.e. user module, user module etc. Each module have dao, domain, validation, service packages.

- Database migrations:
  - in `db_init` directory is initial postgres script that will create database `luxuryakka` with user `luxuryakka` and password `luxuryakka` . That can be easily done manually.
  - when application is started db migrations are available on : `http://localhost:9000/@flyway/default` where pending migrations can be applied as described here: [Play 2.4 FlayWay](https://github.com/flyway/flyway-play)

