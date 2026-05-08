CREATE TABLE financial_profile (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE REFERENCES app_user(id),
  monthly_income NUMERIC(19, 4) NOT NULL,
  monthly_expenses NUMERIC(19, 4) NOT NULL,
  current_debt NUMERIC(19, 4) NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE loan_application (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES app_user(id),
  requested_amount NUMERIC(19, 4) NOT NULL,
  approved_amount NUMERIC(19, 4) NOT NULL,
  term_months INTEGER NOT NULL,
  annual_interest_rate NUMERIC(8, 4) NOT NULL,
  estimated_installment NUMERIC(19, 4) NOT NULL,
  status VARCHAR(20) NOT NULL,
  risk_snapshot_json VARCHAR(4000) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_financial_profile_user_id ON financial_profile(user_id);
CREATE INDEX idx_loan_application_user_id ON loan_application(user_id);
CREATE INDEX idx_loan_application_status ON loan_application(status);
CREATE INDEX idx_loan_application_created_at ON loan_application(created_at);
