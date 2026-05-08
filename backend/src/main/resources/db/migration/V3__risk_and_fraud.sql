CREATE TABLE risk_profile (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE REFERENCES app_user(id),
  score INTEGER NOT NULL,
  last_assessment_score INTEGER NOT NULL,
  risk_level VARCHAR(20) NOT NULL,
  alert_count_90d BIGINT NOT NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE transfer_risk_assessment (
  id UUID PRIMARY KEY,
  transfer_id UUID NOT NULL UNIQUE REFERENCES transfer(id),
  user_id UUID NOT NULL REFERENCES app_user(id),
  score INTEGER NOT NULL,
  decision VARCHAR(20) NOT NULL,
  risk_level VARCHAR(20) NOT NULL,
  reasons_json VARCHAR(4000) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE fraud_alert (
  id UUID PRIMARY KEY,
  transfer_id UUID NOT NULL REFERENCES transfer(id),
  user_id UUID NOT NULL REFERENCES app_user(id),
  account_id UUID NOT NULL REFERENCES account(id),
  rule_code VARCHAR(80) NOT NULL,
  severity VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  details_json VARCHAR(4000) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  reviewed_at TIMESTAMP,
  reviewed_by UUID REFERENCES app_user(id)
);

CREATE INDEX idx_risk_profile_user_id ON risk_profile(user_id);
CREATE INDEX idx_transfer_risk_assessment_transfer_id ON transfer_risk_assessment(transfer_id);
CREATE INDEX idx_transfer_risk_assessment_user_id ON transfer_risk_assessment(user_id);
CREATE INDEX idx_fraud_alert_user_id ON fraud_alert(user_id);
CREATE INDEX idx_fraud_alert_transfer_id ON fraud_alert(transfer_id);
CREATE INDEX idx_fraud_alert_status ON fraud_alert(status);
CREATE INDEX idx_fraud_alert_created_at ON fraud_alert(created_at);
