# --- !Ups

DROP TABLE `initial_template`;
CREATE TABLE IF NOT EXISTS `initial_template` (
	`id` INT NOT NULL,
	`town` INT,
	`departure` DATETIME NOT NULL,
	`arrival` DATETIME NOT NULL,
	`dep_location` INT,
	`arr_location` INT,
	PRIMARY KEY (`id`)
	)
	ENGINE = InnoDB;

START TRANSACTION;
DELETE FROM initial_template;
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (1, "2012-06-20 17:05:00", "2012-06-20 17:17:00", 2, 2, 19);
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (2, "2012-06-20 17:55:00", "2012-06-20 18:07:00", 2, 2, 19);
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (3, "2012-06-20 16:40:00", "2012-06-20 16:55:00", 2, 3, 19);
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (4, "2012-06-20 17:55:00", "2012-06-20 18:10:00", 2, 3, 19);
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (5, "2012-06-20 16:40:00", "2012-06-20 16:52:00", 2, 7, 19);
COMMIT;

CREATE VIEW initials AS
SELECT initial_template.id AS id, departure, arrival,
  town.id as townid, town.name,
  l1.id as l1id, l1.name as l1name, l1.address as l1address,
  l2.id as l2id, l2.name as l2name, l2.address as l2address
FROM initial_template
JOIN location AS l1 ON dep_location = l1.id
JOIN location2 AS l2 ON arr_location = l2.id
JOIN town ON town.id = l1.town_id
ORDER BY initial_template.id ASC;

# --- !Downs

DROP VIEW initials;

DROP TABLE `initial_template`;
CREATE TABLE IF NOT EXISTS `initial_template` (
	`id` INT NOT NULL,
	`town` VARCHAR(45),
	`departure` DATETIME NOT NULL,
	`arrival` DATETIME NOT NULL,
	`dep_location` VARCHAR(45),
	`arr_location` VARCHAR(45),
	PRIMARY KEY (`id`)
	)
	ENGINE = InnoDB;

START TRANSACTION;
DELETE FROM initial_template;
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (1, "2012-06-20 17:05:00", "2012-06-20 17:17:00", "Wolfsburg", "Carmeq Wolfsburg, Autovision", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (2, "2012-06-20 17:55:00", "2012-06-20 18:07:00", "Wolfsburg", "Carmeq Wolfsburg, Autovision", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (3, "2012-06-20 16:40:00", "2012-06-20 16:55:00", "Wolfsburg", "Volkswagen FE", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (4, "2012-06-20 17:55:00", "2012-06-20 18:10:00", "Wolfsburg", "Volkswagen FE", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (5, "2012-06-20 16:40:00", "2012-06-20 16:52:00", "Wolfsburg", "Volkswagen LKW Wache", "Hauptbahnhof");
COMMIT;
