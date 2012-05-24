# --- !Ups

-- -----------------------------------------------------
-- Table `User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User` ;

CREATE  TABLE IF NOT EXISTS `User` (
  `user_id` INT NOT NULL AUTO_INCREMENT ,
  `email` VARCHAR(45) NOT NULL ,
  `forename` VARCHAR(45) NOT NULL ,
  `lastname` VARCHAR(45) NOT NULL ,
  `cellphone` BIGINT NOT NULL ,
  `password` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`user_id`) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Town`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Town` ;

CREATE  TABLE IF NOT EXISTS `Town` (
  `town_id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`town_id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Location`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Location` ;

CREATE  TABLE IF NOT EXISTS `Location` (
  `location_id` INT NOT NULL AUTO_INCREMENT ,
  `town_location_id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `address` VARCHAR(80) NULL ,
  PRIMARY KEY (`location_id`) ,
  INDEX `town_location_id` (`town_location_id` ASC) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  CONSTRAINT `town_location_id`
    FOREIGN KEY (`town_location_id` )
    REFERENCES `Town` (`town_id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Tour_State`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Tour_State` ;

CREATE  TABLE IF NOT EXISTS `Tour_State` (
  `state_id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(20) NULL ,
  `description` MEDIUMTEXT NULL ,
  PRIMARY KEY (`state_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Tour`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Tour` ;

CREATE  TABLE IF NOT EXISTS `Tour` (
  `tour_id` INT NOT NULL AUTO_INCREMENT ,
  `date` DATE NOT NULL ,
  `departure` TIME NOT NULL ,
  `arrival` TIME NOT NULL ,
  `dep_location` INT NOT NULL ,
  `arr_location` INT NOT NULL ,
  `comment` MEDIUMTEXT NULL ,
  `meetingpoint` VARCHAR(255) NULL ,
  `authentification` VARCHAR(45) NULL ,
  `tour_state` INT NOT NULL ,
  `mod_user_id` INT NULL ,
  PRIMARY KEY (`tour_id`) ,
  INDEX `location_tour_dep_location` (`dep_location` ASC) ,
  INDEX `location_tour_arr_location` (`arr_location` ASC) ,
  INDEX `tour_state_tour_tour_state` (`tour_state` ASC) ,
  INDEX `user_tour_mod_user_id` (`mod_user_id` ASC) ,
  CONSTRAINT `location_tour_dep_location`
    FOREIGN KEY (`dep_location` )
    REFERENCES `Location` (`location_id` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `location_tour_arr_location`
    FOREIGN KEY (`arr_location` )
    REFERENCES `Location` (`location_id` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `tour_state_tour_tour_state`
    FOREIGN KEY (`tour_state` )
    REFERENCES `Tour_State` (`state_id` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `user_tour_mod_user_id`
    FOREIGN KEY (`mod_user_id` )
    REFERENCES `User` (`user_id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `User_has_Tour`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User_has_Tour` ;

CREATE  TABLE IF NOT EXISTS `User_has_Tour` (
  `user_id` INT NOT NULL ,
  `tour_id` INT NOT NULL ,
  PRIMARY KEY (`user_id`, `tour_id`) ,
  INDEX `fk_User_has_Tour_Tour1` (`tour_id` ASC) ,
  INDEX `fk_User_has_Tour_User1` (`user_id` ASC) ,
  CONSTRAINT `fk_User_has_Tour_User1`
    FOREIGN KEY (`user_id` )
    REFERENCES `User` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_User_has_Tour_Tour1`
    FOREIGN KEY (`tour_id` )
    REFERENCES `Tour` (`tour_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Data for table `Town`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `Town` (`town_id`, `name`) VALUES (1, 'Berlin');
INSERT INTO `Town` (`town_id`, `name`) VALUES (2, 'Wolfsburg');
INSERT INTO `Town` (`town_id`, `name`) VALUES (3, 'Ingolstadt');
INSERT INTO `Town` (`town_id`, `name`) VALUES (4, 'Stattgart');
INSERT INTO `Town` (`town_id`, `name`) VALUES (5, 'Prag');

COMMIT;

-- -----------------------------------------------------
-- Data for table `Location`
-- -----------------------------------------------------
START TRANSACTION;
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (1, 1, 'Carmeq Berlin', 'Carnotstraße 4, 10587 Berlin');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (2, 2, 'Carmeq Wolfsburg, Autovision', 'Major-Hirst-Straße 11, 38442 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (3, 2, 'Volkswagen FE', 'Nordstraße, 38440 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (4, 2, 'Volkswagen TE, Hopfengarten', 'Hopfengarten 37-47, 38442 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (5, 2, 'Volkswagen TE, Rübenkamp', 'Rübenkamp 2, 38442 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (6, 2, 'Volkswagen AutoUni', 'Berliner Ring 2, 38440 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (7, 2, 'Volkswagen LKW Wache', 'Stellfelder Straße 46, 33442 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (8, 2, 'Volkswagen HMI, Fa. Volke', 'Daimlerstraße 38, 38446 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (9, 2, 'Volkswagen Isenbüttel', 'Poststraße 28, 38440 Wolfsburg');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (10, 2, 'Volkswagen Prüfgelände Ehra', 'Prüfgelände, 38468 Ehra-Lessien');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (11, 3, 'Carmeq Ingolstadt', 'Sachsstraße 14b, 85080 Gaimersheim');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (12, 3, 'Audi TE', 'Tor 9, 85055 Ingolstadt');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (13, 3, 'Audi Forum', 'Ettinger Straße, 85057 Ingolstadt');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (14, 4, 'Carmeq Stuttgart', 'Königstraße 43b, 70173 Stuttgart');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (15, 4, 'Porsche Weissach', 'Porschestraße, 71287 Weissach');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (16, 5, 'e4t', 'Novodvorská 994/138, CZ-142 21 Praha 4');
INSERT INTO `Location` (`location_id`, `town_location_id`, `name`, `address`) VALUES (17, 5, 'Ŝkoda Werk', 'Nušlova 2515/4, CZ-158 00 Praha 13');

COMMIT;

# --- !Downs

DROP TABLE user;
