CREATE TABLE IF NOT EXISTS currencies
(
    id        INTEGER
        primary key autoincrement,
    code      VARCHAR(255)
        unique,
    full_name VARCHAR(255),
    sign      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS exchange_rates
(
    id                 INTEGER
        primary key autoincrement,
    base_currency_id   INTEGER
        references currencies,
    target_currency_id INTEGER
        references currencies,
    rate               DECIMAL(10, 6),
    unique (base_currency_id, target_currency_id)
);