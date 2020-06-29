CREATE TABLE transactions (
  user_id VARCHAR(64),
  created_at TIMESTAMP,
  symbol VARCHAR(8),
  quantity INTEGER,
  unit_price DECIMAL(16, 2)
);

CREATE INDEX transactions_user_idx ON transactions(user_id);

