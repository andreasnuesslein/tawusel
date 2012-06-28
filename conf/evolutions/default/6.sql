# --- !Ups'

ALTER TABLE user ADD `extension` VARCHAR(10);

# --- !Downs

ALTER TABLE user DROP `extension`;
