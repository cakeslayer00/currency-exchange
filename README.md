# currency-exchange

This project is all about currency and exchange rates. Basically simple crud of creating currency/exchange rate, with only bussiness logic being is ability to exchange some amount of money.

Created this proj with sole purpose of discovering Java Servlets API, also implemented connection pool for database using HikariCP, database is SQLite.

That was pretty harsh, especially learning servlets and I think there is still more to learn about them. Hope as I grow bigger return here and complete it till perfect state.

# Table of content

[Technologies](#used-technologies)

[How to install](#how-to-install-and-run)

[How to use](#how-to-use-the-project)

[Credits](#credits)

# Used technologies

- Java Servlets API
- HikariCP
- SQlite
- Jackson Databind //for json wrapping
- ModelMapper //for mapping dtos

# How to install and run

for now you can clone repo, install maven and tomcat and run :)
I hope will come later and add docker compose with dockerfile, so it will be 2 commands or so. 

# How to use the project

### For currencies

#### GET `/currencies`

To get all currencies

```json
[
  {
    "id": 0,
    "code": "USD",
    "name": "United States Dollar",
    "sign": "$"
  },
  {
    "id": 1,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
  }
]
```

#### GET `/currency/{currency_code}`

To get specified currency
```json
{
    "id": 2,
    "code": "KZT",
    "name": "Kazakhstani Tenge",
    "sign": "₸"
}
```

#### POST `/currencies`

To create a currency. You must specify `name`, `code` and `sign`
You'll get 201 HTTP response code if successful

### For exchange rates

#### GET `/exchangeRates`

To get all exchange rates

```json
[
    {
        "id": 1,
        "baseCurrency": {
            "id": 2,
            "code": "USD",
            "name": "Dollar",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 1,
            "code": "EUR",
            "name": "Euro",
            "sign": "€"
        },
        "rate": 0.99
    },
    {
        "id": 2,
        "baseCurrency": {
            "id": 4,
            "code": "RUB",
            "name": "Russian Ruble",
            "sign": "₽"
        },
        "targetCurrency": {
            "id": 3,
            "code": "KZT",
            "name": "Kazakhstani Tenge",
            "sign": "₸"
        },
        "rate": 5.19
    }
]
```
#### GET `/exchangeRate/{exchange_rate}

To get specified exchange rate

```json
{
    "id": 1,
    "baseCurrency": {
        "id": 2,
        "code": "USD",
        "name": "Dollar",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "code": "EUR",
        "name": "Euro",
        "sign": "€"
    },
    "rate": 0.99
}
```

#### POST `/exchangeRates`

To create a exchange rate. You must specify `baseCurrencyCode`, `targetCurrencyCode` and `rate` parameters.
You'll get 201 HTTP response code if successful

#### PATCH `/exchangeRate/{exchange_rate}

To update an existing exchange rate. You must specify `rate` parameter

```json
{
    "id": 1,
    "baseCurrency": {
        "id": 2,
        "code": "USD",
        "name": "Dollar",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "code": "EUR",
        "name": "Euro",
        "sign": "€"
    },
    "rate": 22.22 ///here
}
```

### For exchange part

#### GET `/exchange`

To calculate final amount of money based on certain exchange rate. You need to specify `from` for base currency, `to` for target and `amount` to specify amount.

```json
{
    "baseCurrency": {
        "id": 2,
        "code": "USD",
        "name": "Dollar",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 3,
        "code": "KZT",
        "name": "Kazakhstani Tenge",
        "sign": "₸"
    },
    "rate": "530.97",
    "amount": "50000.00",
    "convertedAmount": "26548500.00"
}
```

# Credits

This project based on one of the from Sergey Zhukov's roadmap. 
Here's link:
https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange/
