
##Todo Rest API

###Description

Application that exposes a series of REST services to manage a "Todo List". The data is persisted in MongoDB, if a resource is marked as "done" a text message will be sent using a Twilio rest client. The backend has search service that runs on top of Elastic Search (Hosted on searchly.com) using Jest as a rest client.

###Live Demo

	http://blooming-forest-7969.herokuapp.com/v1/todo
	
	#list items, by default it lists 10 items per page
	http://blooming-forest-7969.herokuapp.com/v1/todo?p=1
	http://blooming-forest-7969.herokuapp.com/v1/todo?p=2
	
	#limit the items per page
	http://blooming-forest-7969.herokuapp.com/v1/todo?p=1&l=40
	
	#search
	http://blooming-forest-7969.herokuapp.com/v1/todo/search?q=cat	
	

###Stack

	Java 1.7, Maven, Jersey 2, Spring 4, Twilio, Elastic Search (jest), MongoDB, Jetty

###API's

Method | URI                        | Resource       | Example
------ | ---------------------------|----------------|----------
POST   | /v1/todo                   | create todo    | curl -i -X POST -H 'Content-Type: application/json' -d '{"title":"Todo Title", "description":"Todo Desc", "done": false}' http://localhost:8080/todo-api/v1/todo
GET    | /v1/todo?p={page}&l={limit}| list todos     | curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo?p=1
GET    | /v1/todo/{id}              | find a todo    | curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af
GET    | /v1/todo/search?q={query}  | search todos   | curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/search?q=cat
GET    | /v1/todo/{id}/done         | mark done      | curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af/done
GET    | /v1/todo/{id}/undone       | mark undone    | curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af/undone
PUT    | /v1/todo/{id}              | update a todo  | curl -i -X PUT -H 'Content-Type: application/json' -d '{"title":"Todo Title update", "description":"Todo Desc update", "done": true}' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af
PATCH  | /v1/todo/{id}              | partial update | curl -i -X PATCH -H 'Content-Type: application/json' -d '{"title":"Todo Title update"}' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af
DELETE | /v1/todo/{id}              | delete a todo  | curl -i -X DELETE -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af


###Tests

A series of tests were created using "jUnit".

Run service tests aimed at the operations of the backend.

```bash
  $ mvn clean package
```

Run Integration tests aimed at the CRUD operations of the Rest API

```bash
  $ mvn verify
```

#### Installation
```bash
  $ git https://github.com/rubenesc/todo-api.git
  (configure ./src/main/resources/spring/config.properties)
  (configure ./src/test/resources/config.properties)
  $ mvn clean package
  $ mvn jetty:run
```

Test: http://localhost:8080/todo-api/v1/todo
