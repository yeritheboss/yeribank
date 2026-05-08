CREATE TABLE app_user (
  id UUID PRIMARY KEY,
  email VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE account (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES app_user(id),
  account_number VARCHAR(34) NOT NULL UNIQUE,
  balance NUMERIC(19, 4) NOT NULL,
  status VARCHAR(20) NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE transfer (
  id UUID PRIMARY KEY,
  from_account_id UUID NOT NULL REFERENCES account(id),
  to_account_id UUID NOT NULL REFERENCES account(id),
  amount NUMERIC(19, 4) NOT NULL,
  status VARCHAR(30) NOT NULL,
  risk_score INTEGER,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_account_user_id ON account(user_id);
CREATE INDEX idx_transfer_from_account_id ON transfer(from_account_id);
CREATE INDEX idx_transfer_to_account_id ON transfer(to_account_id);
