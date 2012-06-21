# --- !Ups

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

