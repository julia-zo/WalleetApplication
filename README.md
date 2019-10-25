{
	"email":"lalalo@email.com",
	"transactionId": 37,
	"transactionAmount": 0.75
}

{
    "email": "lalalo@email.com",
    "transactionAmount": 0.75,
    "transactionId": 37,
    "transactionResult": "SUCCESS",
    "transactionReason": "Player has sufficient balance."
}

{
    "email": "lalalo@email.com",
    "transactionAmount": 100.75,
    "transactionId": 77,
    "transactionResult": "FAIL",
    "transactionReason": "Player has insufficient balance."
}

{
    "email": "lalalo@email.com",
    "transactionAmount": 0.75,
    "transactionId": 37,
    "transactionResult": "FAIL",
    "transactionReason": "Transaction ID is invalid."
}
---
{
	"email":"lalalo@email.com",
	"transactionId": 3,
	"transactionAmount": 2
}

{
    "email": "lalalo@email.com",
    "transactionAmount": 2.0,
    "transactionId": 3,
    "transactionResult": "SUCCESS",
    "transactionReason": "New funds credited successfully."
}

{
    "email": "lalalo@email.com",
    "transactionAmount": 2.0,
    "transactionId": 3,
    "transactionResult": "FAIL",
    "transactionReason": "Transaction ID is invalid."
}

---

{
    "email": "lalalo@email.com",
    "currentBalance": 9.5
}

--

{
    "email": "lalalo@email.com",
    "history": [
        {
            "transactionID": 6,
            "transactionType": "DEPOSIT",
            "transactionAmount": 10.0
        },
        {
            "transactionID": 3,
            "transactionType": "DEPOSIT",
            "transactionAmount": 2.0
        },
        {
            "transactionID": 67,
            "transactionType": "WITHDRAWL",
            "transactionAmount": 1.0
        },
        {
            "transactionID": 37,
            "transactionType": "WITHDRAWL",
            "transactionAmount": 0.75
        },
        {
            "transactionID": 77,
            "transactionType": "WITHDRAWL",
            "transactionAmount": 0.75
        }
    ]
}

