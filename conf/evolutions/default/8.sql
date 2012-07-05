# --- !Ups

START TRANSACTION;
DELETE FROM `user` WHERE id=1;
DELETE FROM `tour` WHERE id=1;
COMMIT;

START TRANSACTION;
UPDATE tour_state SET description='This tour state is still pending. We will try to confirm the tour 30min before it is supposed to start.' WHERE id=1;
UPDATE tour_state SET description='This tour has been confirmed.' WHERE id=2;
UPDATE tour_state SET description='This tour has been cancelled. It will NOT take place.' WHERE id=3;
DELETE FROM tour_state WHERE id=4;

COMMIT;

# --- !Downs

INSERT INTO `user` (`id`, `email`, `firstname`, `lastname`, `cellphone`, `password`) VALUES (1, 'test.test@carmeq.com', 'test', 'test', '0177123456', '7c4a8d09ca3762af61e59520943dc26494f8941b');
INSERT INTO `tour` VALUES (1, NOW(), NOW(), 2, 1, 1, 1, 0);

