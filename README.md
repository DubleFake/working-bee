<h1 align="center">🐝🐝🐝Task managing system🐝🐝🐝<h1/>


## Description
This is an application created for ascertaining skills and capabilities for an internship position at The Bee Company 🐝. 
### This application has the following endpoints:
- POST /register -> Endpoint to register new user. (Public)
- POST /login -> Endpoint to log in. (Public)
- POST /logout -> Endpoint to log out. (Secured)
- POST /tasks -> Create a new task. (Secured)
- PUT /tasks/{id} -> Update an existing record. (Secured)
- GET /tasks/{id} -> Get more info about a task. (Secured)
- GET /tasks?status={status} -> Get tasks, that were filtered by status. (Secured)


All data is stored in H2 database and **IS LOST** after the aplication is shutdown.

## Demonstartion

This section will demonstarte the basic functionality. Everything will be done on a local machine via terminal.

### 1) Start the application.

Once the application starts, we can start issuing the commands.

### 2) Register

Before we can create record we have to register. We can do this by POSTing to `/register` endpoint. You can replace username and password with your preferd ones.

```curl -i -X POST http://localhost:8080/register -H "Content-Type: application/json" -d '{"username": "user", "password": "user"}'```

If everything goes well, we should see response with code 201 Created.
![Standalone](https://i.imgur.com/lwvVDpA.png)

### 3) Log in

In order for us to issue requests to protected endpoints, we need to have a valid authorization token. To get it, we simply log in by POSTing out registered user details to `/login` endpoint.

```curl -i -X POST http://localhost:8080/login -H "Content-Type: application/json" -d '{"username": "user", "password": "user"}'```

If all is done correctly, we should get response with code 200 OK and our token for future use. One account can have only 1 token at one time, newly created tokens will overwrite old ones.
![Standalone](https://i.imgur.com/PNhyg1H.png)

### 4) Create task

Now that we have our token, we can access all secured endpoints by adding it to the `Authorization` header. Let's create a new simple task.

```curl -i -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"status": "ACTIVE","name": "Test Task","description": "This is a test task."}'```

If the operation succseeds, we should get code 201 Created. If the gotten code is 401 or 403, then `Authorization` header is likely missing, if the code is 400, then the request has a mistake in it.  
![Standalone](https://i.imgur.com/K6qsm1b.png)

### 5) Get created task

To check if our task was actually created and saved, we can try to get it. In this case we know the `ID` of our task, so we can reach it directly via `GET /tasks/{id}` endpoint.

```curl -i -X GET http://localhost:8080/tasks/1 -H "Authorization: Bearer <token>"```

We can see our task is indeed created.
![Standalone](https://i.imgur.com/wARfhpj.png)

### 6) Update already created task

If we made a mistake or just want to change some field of our tasks, we can use `PUT /tasks/{id}` endpoint. If task was created by us, we can edit it.

```curl -i -X PUT http://localhost:8080/tasks/2 -H "Content-Type: application/json" -H "Authorization: Bearer <token> -d '{"status": "ACTIVE","name": "Updated Test Task","description": "This is an updated test task description."}'```

If we get code 200 OK our update went through.
![Standalone](https://i.imgur.com/lADRozy.png)

To check if our update record is indeed live, we can use previous `GET` endpoint.

```curl -i -X GET http://localhost:8080/tasks/2 -H "Authorization: Bearer <token>"```

We indeed get our updated record.
![Standalone](https://i.imgur.com/Ns3EK9f.png)

### 7) Get tasks by status

If we don't know task's `ID` or even want to filter by `status` of our tasks, we can use `GET /tasks?=status{status}` endpoint. To demnostarte it better, let's add a few more tasks.

```curl -i -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"status": "INACTIVE","name": "Test Task 2","description": "This is a test task 2."}'```


```curl -i -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"status": "ACTIVE","name": "Test Task 3","description": "This is a test task 3."}'```

Here we added 2 more tasks. Main difference is that task 2 has `INACTIVE` status and task 3 has `ACTIVE`. When we filter by `ACTIVE` status, our task 2 should NOT be present.

```curl -i -X GET http://localhost:8080/tasks?status=ACTIVE -H "Authorization: Bearer <token>"```

Indeed we get only task 1 and 3.
![Standalone](https://i.imgur.com/bGCpaqF.png)

### 8) Log out

Once our work is done we can POST a request to our `/logout` endpoint. This will invalidate our token and to get a new one we would have to log in again. It's important to know, that tokens expire after 10 hours.

```curl -i -X POST http://localhost:8080/logout -H "Authorization: Bearer <token>"```

If we get code 200 OK, that means our token is invalidated.
![Standalone](https://i.imgur.com/YsCniWc.png)

To check if our token is indeed invalidated, we can try to access any secured endpoint using it. For example, let's check task with `ID` 1.
```curl -i -X GET http://localhost:8080/tasks/1 -H "Authorization: Bearer <token>"```

We indeed cannot reach our task with `ID` 1.
![Standalone](https://i.imgur.com/IP4FZhH.png)
