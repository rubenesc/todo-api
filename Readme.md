
##Todo Rest API

###Description

###Live Demo

###Stack

	Back-end: 
		Java, Jersey, Spring
		
		APIs (curl):
			//Create a todo
			curl -i -X POST -H 'Content-Type: application/json' -d '{"title":"Todo Title", "description":"Todo Desc", "done": false}' http://localhost:8080/todo-api/v1/todo

			//Retrive all todo's
			curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo

			//find todo by id
			curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af

			//update todo
			curl -i -X PUT -H 'Content-Type: application/json' -d '{"title":"Todo Title update", "description":"Todo Desc update", "done": true}' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af

			//find updated todo by id
			curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af

			//delete todo
			curl -i -X DELETE -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af

			//verify todo doesn't exist
			curl -i -X GET -H 'Content-Type: application/json' http://localhost:8080/todo-api/v1/todo/543ec1eb0364f8ca5dd372af


	Database: MongoDB

###Tests

A series of tests were created using "jUnit". The tests are aimed at the backend, testing the API and the database CRUD operations. All tests execute the application in a "test" environment (different database)

```bash
  $ ./run_tests.sh
```


#### Installation
