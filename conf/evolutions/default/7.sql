# -- !Ups

DROP VIEW IF EXISTS `favorites`;
-- This is being used for getting the favorite tours
CREATE VIEW favorites AS
SELECT tour.departure AS departure, tour.arrival AS arrival, tour.dep_location AS dep_location, tour.arr_location AS arr_location, user_has_tour.user_id AS user_id, user_has_tour.resetted_favorite AS resetted_favorite, tour.id
	FROM tour
	JOIN user_has_tour ON tour.id = user_has_tour.tour_id
	GROUP BY RIGHT(tour.departure, 8), RIGHT(tour.arrival, 8), tour.dep_location, tour.arr_location
	HAVING tour.departure < NOW()
	ORDER BY COUNT(*) DESC;

# --- !Downs

DROP VIEW IF EXISTS `favorites`;
