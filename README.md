# Wallet Application
REST Application to hold monetary balance and make changes to such balance.

_Author: Júlia Zottis Moreira_

## Available Endpoints

| Operation | Path | Description |
|-----------|------|-------------|
| POST | /v1/deposit | Accepts or Rejects a deposit transaction, save the accepted transaction into the client's history |
| POST | /v1/withdrawl | Accepts or Rejects a withdrawl transaction, save the accepted transaction into the client's history |
| GET | /v1/history/{email} | Gets the transaction history for a given client, identified via email |
| GET | /v1/balance/{email} | Gets the current balance for a given client, identified via email |

### Requirements
* Payload must be on JSON format

## Prerequisites
* Java JDK 1.8
* Gradle 3.2.1

## Running the Application
Use Gradle tasks jettyRun and jettyStop to run and stop the application respectvely.

## Examples

### POST /v1/deposit

Case: Transaction is accepted

```
REQUEST
POST /v1/deposit HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
    "email":"lalalo@email.com",
    "transactionId": 6,
    "transactionAmount": 10.0
}
```

Fields:
* email (Required)
* transactionId (Required)
* transactionAmount (Required)

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
    "email": "lalalo@email.com",
    "transactionAmount": 10.0,
    "transactionId": 6,
    "transactionResult": "SUCCESS",
    "transactionReason": "New funds credited successfully."
}
```

Fields:
* email (Required)
* transactionId (Required)
* transactionAmount (Required)
* transactionResult (Required)
* transactionReason (Required)

Case: Transaction is rejected because the transaction id is not unique

```
REQUEST
POST /v1/deposit HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
    "email":"lalalo@email.com",
    "transactionId": 6,
    "transactionAmount": 2
}
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
    "email": "lalalo@email.com",
    "transactionAmount": 2.0,
    "transactionId": 6,
    "transactionResult": "FAIL",
    "transactionReason": "Transaction ID is invalid."
}
```

### POST /v1/withdrawl

Case: Transaction is accepted

```
REQUEST
POST /v1/withdrawl HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
    "email":"lalalo@email.com",
    "transactionId": 37,
    "transactionAmount": 0.75
}
```

Fields:
* email (Required)
* transactionId (Required)
* transactionAmount (Required)

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
    "email": "lalalo@email.com",
    "transactionAmount": 0.75,
    "transactionId": 37,
    "transactionResult": "SUCCESS",
    "transactionReason": "Player has sufficient balance."
}
```

Fields:
* email (Required)
* transactionId (Required)
* transactionAmount (Required)
* transactionResult (Required)
* transactionReason (Required)

Case: Transaction is rejected due to insufficient funds

```
REQUEST
POST /v1/withdrawl HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
    "email":"lalalo@email.com",
    "transactionId": 77,
    "transactionAmount": 100.75
}
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
    "email": "lalalo@email.com",
    "transactionAmount": 100.75,
    "transactionId": 77,
    "transactionResult": "FAIL",
    "transactionReason": "Player has insufficient balance."
}
```

Case: Transaction is rejected because the transaction id is not unique

```
REQUEST
POST /v1/withdrawl HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
    "email":"lalalo@email.com",
    "transactionId": 37,
    "transactionAmount": 0.75
}
```

```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json

{
    "email": "lalalo@email.com",
    "transactionAmount": 0.75,
    "transactionId": 37,
    "transactionResult": "FAIL",
    "transactionReason": "Transaction ID is invalid."
}
```

Case: Transaction is rejected because provided client does not exist

```
REQUEST
POST /v1/withdrawl HTTP/1.1
HOST: http://localhost:8080
Content-Type: application/json

{
    "email":"jane.doe@email.com",
    "transactionId": 37,
    "transactionAmount": 0.75
}
```

```
RESPONSE
HTTP/1.1 404 NOT FOUND
```


### GET /v1/history/{email}

Case: Get Transaction history for an existent client

```
REQUEST
GET /v1/history/lalalo@email.com HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```
```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
    "email": "lalalo@email.com",
    "history": [
        {
            "transactionID": 6,
            "transactionType": "DEPOSIT",
            "transactionAmount": 10.0
        },
        {
            "transactionID": 37,
            "transactionType": "WITHDRAWL",
            "transactionAmount": 0.75
        }
    ]
}
```

Fields:
* email (Required)
* history (Required)

Case: Get transaction history for a non-existent client

```
REQUEST
GET /v1/history/jane.doe@email.com HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```

```
RESPONSE
HTTP/1.1 404 NOT FOUND
```

### GET /v1/balance/{email}

Case: Get the current balance of a given client
```
REQUEST
GET /v1/balance/lalalo@email.com HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```
```
RESPONSE
HTTP/1.1 200 OK
Content-Type: application/json
{
    "email": "lalalo@email.com",
    "currentBalance": 9.25
}
```

Fields:
* email (Required)
* currentBalance (Required)

Case: Get current balance for a non-existent client

```
REQUEST
GET /v1/balance/jane.doe@email.com HTTP/1.1
Host: http://localhost:8080
Content-Type: application/json
```

```
RESPONSE
HTTP/1.1 404 NOT FOUND
```

## Assumptions
* Data persistence was not required, in-memory storage is being used;
* Transactions always have positive values;
* A client only exists after it had credit added to it's wallet for the first time.

## Improvements
* Data persistence could be added to improve reliability;
* Error handling can be improved to inform more details about the error;
* Separate the service logic from the Rest logic in order to facilitate changes on the Rest layer.

## Support
If you have any question, please send an [email](mailto:juliazottis@hotmail.com).
