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

-- This is the dirtiest hack in history but needed for mother***ing Anorm
CREATE VIEW location2 AS
SELECT * FROM location;

-- This is being used for getting the favorite tours
CREATE VIEW favorites AS
SELECT tour.departure AS departure, tour.arrival AS arrival, tour.dep_location AS dep_location, tour.arr_location AS arr_location, user_has_tour.user_id AS user_id
	FROM tour
	JOIN user_has_tour ON tour.id = user_has_tour.tour_id
	GROUP BY RIGHT(tour.departure, 8), RIGHT(tour.arrival, 8), tour.dep_location, tour.arr_location
	HAVING tour.departure < NOW()
	ORDER BY COUNT(*) DESC LIMIT 8;

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
