# --- !Ups


START TRANSACTION;
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (20, 3, 'Hauptbahnhof', 'Bahnhofstr. 8, 85051 Ingolstadt');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (21, 4, 'Hauptbahnhof', 'Arnulf-Klett-Platz, 70173 Stuttgart');
INSERT INTO `location` (`id`, `town_id`, `name`, `address`) VALUES (22, 5, 'Hauptbahnhof', 'Wilsonova 300/8, 120 00 Praha');
COMMIT;


