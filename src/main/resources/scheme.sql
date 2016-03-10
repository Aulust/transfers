CREATE TABLE IF NOT EXISTS account (
  account_id BIGSERIAL,
  balance numeric(19, 2) NOT NULL,
  PRIMARY KEY (account_id)
);

CREATE TABLE IF NOT EXISTS transaction (
  transaction_id BIGSERIAL,
  account_id BIGINT,
  amount numeric(19, 2) NOT NULL,
  finished BOOLEAN,
  updated DATETIME default now(),
  PRIMARY KEY (transaction_id)
);

CREATE INDEX ON transaction(finished, updated);
