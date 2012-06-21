# --- !Ups

CREATE  TABLE IF NOT EXISTS `user` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `email` VARCHAR(45) NOT NULL ,
  `firstname` VARCHAR(45) NOT NULL ,
  `lastname` VARCHAR(45) NOT NULL ,
  `cellphone` VARCHAR(20) NOT NULL ,
  `password` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) )
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `town` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `location` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `town_id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `address` VARCHAR(80) NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `town_id` (`town_id` ASC) ,
  CONSTRAINT `town_id`
    FOREIGN KEY (`town_id` )
    REFERENCES `town` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- This is the dirtiest hack in history but needed for mother***ing Anorm
CREATE VIEW location2 AS
SELECT * FROM location;

CREATE  TABLE IF NOT EXISTS `tour_state` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NULL ,
  `description` MEDIUMTEXT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `tour` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `departure` DATETIME NOT NULL ,
  `arrival` DATETIME NOT NULL ,
  `dep_location` INT NOT NULL ,
  `arr_location` INT NOT NULL ,
  `tour_state` INT NOT NULL ,
  `mod_id` INT NULL ,
  `checked_by_timer` TINYINT(1) DEFAULT 0, 
  PRIMARY KEY (`id`) ,
  INDEX `location_tour_dep_location` (`dep_location` ASC) ,
  INDEX `location_tour_arr_location` (`arr_location` ASC) ,
  INDEX `tour_state_tour_tour_state` (`tour_state` ASC) ,
  INDEX `user_tour_mod_id` (`mod_id` ASC) ,
  CONSTRAINT `location_tour_dep_location`
    FOREIGN KEY (`dep_location` )
    REFERENCES `location` (`id` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `location_tour_arr_location`
    FOREIGN KEY (`arr_location` )
    REFERENCES `location` (`id` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `tour_state_tour_tour_state`
    FOREIGN KEY (`tour_state` )
    REFERENCES `tour_state` (`id` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `user_tour_mod_id`
    FOREIGN KEY (`mod_id` )
    REFERENCES `user` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `user_has_tour` (
  `user_id` INT NOT NULL ,
  `tour_id` INT NOT NULL ,
  PRIMARY KEY (`user_id`, `tour_id`) ,
  INDEX `fk_user_has_tour_tour1` (`tour_id` ASC) ,
  INDEX `fk_user_has_tour_user1` (`user_id` ASC) ,
  CONSTRAINT `fk_user_has_tour_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_tour_tour1`
    FOREIGN KEY (`tour_id` )
    REFERENCES `tour` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS `sms_api_message` (
  `id` INT NOT NULL ,
  `message` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;

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

CREATE VIEW favorites AS
SELECT tour.departure AS departure, tour.arrival AS arrival, tour.dep_location AS dep_location, tour.arr_location AS arr_location, user_has_tour.user_id AS user_id
	FROM tour
	JOIN user_has_tour ON tour.id = user_has_tour.tour_id
	GROUP BY RIGHT(tour.departure, 8), RIGHT(tour.arrival, 8), tour.dep_location, tour.arr_location
	HAVING tour.departure < NOW()
	ORDER BY COUNT(*) DESC LIMIT 8;

-- -----------------------------------------------------
-- Data for table `initial_template`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (1, "2012-06-20 17:05:00", "2012-06-20 17:17:00", "Wolfsburg", "Carmeq Wolfsburg, Autovision", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (2, "2012-06-20 17:55:00", "2012-06-20 18:07:00", "Wolfsburg", "Carmeq Wolfsburg, Autovision", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (3, "2012-06-20 16:40:00", "2012-06-20 16:55:00", "Wolfsburg", "Volkswagen FE", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (4, "2012-06-20 17:55:00", "2012-06-20 18:10:00", "Wolfsburg", "Volkswagen FE", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (5, "2012-06-20 16:40:00", "2012-06-20 16:52:00", "Wolfsburg", "Volkswagen LKW Wache", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (6, "2012-06-20 17:55:00", "2012-06-20 18:07:00", "Wolfsburg", "Volkswagen LKW Wache", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (7, "2012-06-20 16:40:00", "2012-06-20 16:54:00", "Wolfsburg", "Volkswagen TE, Rübenkamp", "Hauptbahnhof");
INSERT INTO initial_template (id, departure, arrival, town, dep_location, arr_location) VALUES (8, "2012-06-20 17:55:00", "2012-06-20 18:09:00", "Wolfsburg", "Volkswagen TE, Rübenkamp", "Hauptbahnhof");

-- -----------------------------------------------------
-- Data for table `user`
-- -----------------------------------------------------
 START TRANSACTION;
 INSERT INTO `user` (`id`, `email`, `firstname`, `lastname`, `cellphone`, `password`) VALUES (1, 'test.test@carmeq.com', 'test', 'test', '0177123456', '7c4a8d09ca3762af61e59520943dc26494f8941b');

 COMMIT;

-- -----------------------------------------------------
-- Data for table `town`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `town` (`id`, `name`) VALUES (1, 'Berlin');
INSERT INTO `town` (`id`, `name`) VALUES (2, 'Wolfsburg');
INSERT INTO `town` (`id`, `name`) VALUES (3, 'Ingolstadt');
INSERT INTO `town` (`id`, `name`) VALUES (4, 'Stuttgart');
INSERT INTO `town` (`id`, `name`) VALUES (5, 'Prag');

COMMIT;

-- -----------------------------------------------------
-- Data for table `Location`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (1, 1, 'Carmeq Berlin', 'Carnotstraße 4, 10587 Berlin');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (18, 1, 'Hauptbahnhof', 'Europaplatz 1, 10557 Berlin');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (2, 2, 'Carmeq Wolfsburg, Autovision', 'Major-Hirst-Straße 11, 38442 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (19, 2, 'Hauptbahnhof', 'Willy-Brandt-Platz 2, 38440 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (3, 2, 'Volkswagen FE', 'Nordstraße, 38440 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (4, 2, 'Volkswagen TE, Hopfengarten', 'Hopfengarten 37-47, 38442 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (5, 2, 'Volkswagen TE, Rübenkamp', 'Rübenkamp 2, 38442 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (6, 2, 'Volkswagen AutoUni', 'Berliner Ring 2, 38440 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (7, 2, 'Volkswagen LKW Wache', 'Stellfelder Straße 46, 33442 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (8, 2, 'Volkswagen HMI, Fa. Volke', 'Daimlerstraße 38, 38446 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (9, 2, 'Volkswagen Isenbüttel', 'Poststraße 28, 38440 Wolfsburg');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (10, 2, 'Volkswagen Prüfgelände Ehra', 'Prüfgelände, 38468 Ehra-Lessien');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (11, 3, 'Carmeq Ingolstadt', 'Sachsstraße 14b, 85080 Gaimersheim');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (12, 3, 'Audi TE', 'Tor 9, 85055 Ingolstadt');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (13, 3, 'Audi Forum', 'Ettinger Straße, 85057 Ingolstadt');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (14, 4, 'Carmeq Stuttgart', 'Königstraße 43b, 70173 Stuttgart');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (15, 4, 'Porsche Weissach', 'Porschestraße, 71287 Weissach');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (16, 5, 'e4t', 'Novodvorska 994/138, CZ-142 21 Praha 4');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (17, 5, 'Skoda Werk', 'Nuslova 2515/4, CZ-158 00 Praha 13');

COMMIT;

-- -----------------------------------------------------
-- Data for table `tour_state`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `tour_state` (`id`, `name`, `description`) VALUES (1,'pending', 'if a taxi was not yet called');
INSERT INTO `tour_state` (`id`, `name`, `description`) VALUES (2,'success', 'if a taxi was called');
INSERT INTO `tour_state` (`id`, `name`, `description`) VALUES (3,'fail', 'if a taxi could not be called');
INSERT INTO `tour_state` (`id`, `name`, `description`) VALUES (4,'done', 'if the tour is finished');

COMMIT;

-- -----------------------------------------------------
-- Data for table `tour`
-- -----------------------------------------------------
START TRANSACTION;

INSERT INTO `tour` VALUES (1, NOW(), NOW(), 2, 1, 1, 1, 0);

COMMIT;

-- -----------------------------------------------------
-- Data for table `sms_api_message`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (100, 'SMS wurde erfolgreich verschickt.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (101, 'Versand an mindestens einen Empfänger fehlgeschlagen.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (201, 'Ländercode für diesen SMS-Typ nicht gültig. Bitte als Basic SMS verschicken.'); 
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (202, 'Empfängernummer ungültig.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (300, 'Bitte Benutzer/Passwort angeben.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (301, 'Variable to nicht gesetzt.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (304, 'Variable type nicht gesetzt.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (305, 'Variable text nicht gesetzt.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (306, 'Absendernummer ungültig. Diese muss vom Format 0049... sein und eine gültige Handynummer darstellen.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (307, 'Variable url nicht gesetzt.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (400, 'Variable type ungültig. Siehe erlaubte Werte oben..');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (401, 'Variable text ist zu lang.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (402, 'Reloadsperre – diese SMS wurde bereits innerhalb der letzten 90 Sekunden verschickt.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (500, 'Zu wenig Guthaben vorhanden.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (600, 'Carrier Zustellung misslungen');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (700, 'Unbekannter Fehler.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (801, 'Logodatei nicht angegeben.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (802, 'Logodatei existiert nicht.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (803, 'Klingelton nicht angegeben.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (900, 'Benutzer/Passwort-Kombination falsch.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (902, 'http API für diesen Account deaktiviert.');
INSERT INTO `sms_api_message` (`id`, `message`) VALUES (903, 'Server IP ist falsch.');

COMMIT;

# --- !Downs

DROP TABLE IF EXISTS `sms_api_message` ;
DROP TABLE IF EXISTS `user_has_tour` ;
DROP TABLE IF EXISTS `tour` ;
DROP TABLE IF EXISTS `tour_state` ;
DROP VIEW IF EXISTS `location2` ;
DROP TABLE IF EXISTS `location` ;
DROP TABLE IF EXISTS `town` ;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `initial_template`;
DROP VIEW IF EXISTS `favorites`;
