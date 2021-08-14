/*
TRUNCATE TABLE manga;
TRUNCATE TABLE chapter;
TRUNCATE TABLE files;


ALTER TABLE manga AUTO_INCREMENT = 1;
ALTER TABLE chapter AUTO_INCREMENT = 1;
ALTER TABLE files AUTO_INCREMENT = 1;
UPDATE links SET STATUS = NULL;
*/




SELECT * FROM links;
SELECT * FROM links_copy LIMIT 10;

SELECT link FROM links_copy where link not like '%chapter%' AND STATUS is NULL LIMIT 10;

SELECT * FROM links where link not like '%chapter%' AND STATUS is NULL LIMIT 10;

SELECT COUNT(*) FROM links where link not like '%chapter%' AND STATUS is NOT NULL LIMIT 10;
SELECT COUNT(*) FROM links where link not like '%chapter%' AND STATUS is NOT NULL LIMIT 10;


select * from links where STATUS IN (1,2,3);


SET GLOBAL max_connections = 5000;

SELECT COUNT(*) FROM files;

SELECT COUNT(*) FROM chapter WHERE content IS NOT NULL;

SELECT COUNT(*) FROM chapter WHERE status IS NOT NULL;

SELECT * FROM chapter WHERE SOURCE = 'https://readmanganato.com/manga-bn978870/chapter-1072';

SELECT * FROM chapter WHERE id = 1088;

SELECT COUNT(*) FROM manga;

SELECT * FROM manga;