DROP TABLE IF EXISTS transaction CASCADE;
DROP TABLE IF EXISTS account CASCADE;

CREATE TABLE account (
  account_id BIGSERIAL,
  balance numeric(19, 2) NOT NULL,
  PRIMARY KEY (account_id)
);

CREATE TABLE transaction (
  transaction_id BIGSERIAL,
  account_id BIGINT,
  amount numeric(19, 2) NOT NULL,
  finished BOOLEAN,
  updated TIMESTAMP DEFAULT now(),
  PRIMARY KEY (transaction_id)
);

CREATE INDEX ON transaction(finished, updated);
