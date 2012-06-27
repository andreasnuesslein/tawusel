# --- !Ups

CREATE  TABLE IF NOT EXISTS `notification` (
  `userid` INTEGER NOT NULL,
  sbdy_joined_email BOOLEAN,
  sbdy_joined_sms BOOLEAN,
  sbdy_left_email BOOLEAN,
  sbdy_left_sms BOOLEAN,
  xmin_email BOOLEAN,
  xmin_sms BOOLEAN,
  must_call_email BOOLEAN,
  must_call_sms BOOLEAN,
  status_changed_email BOOLEAN,
  status_changed_sms BOOLEAN,
  FOREIGN KEY (`userid`) REFERENCES user(id)
)

# --- !Downs

DROP TABLE IF EXISTS `notification` ;
