INSERT INTO connexion VALUES (1, '2017-03-01', '06:46:00', 901);
INSERT INTO connexion VALUES (2, '2017-03-01', '08:39:05', 902);
INSERT INTO connexion VALUES (3, '2017-03-01', '13:55:46', 901);

INSERT INTO connexion VALUES (4, '2017-03-02', '08:39:05', 902);
INSERT INTO connexion VALUES (5, '2017-03-02', '08:45:00', 902);
INSERT INTO connexion VALUES (6, '2017-03-02', '10:39:05', 903);
INSERT INTO connexion VALUES (7, '2017-03-02', '16:42:50', 901);

INSERT INTO connexion VALUES (8, '2017-03-03', '08:39:05', 902);
INSERT INTO connexion VALUES (9, '2017-03-03', '08:40:05', 903);
INSERT INTO connexion VALUES (10, '2017-03-03', '08:47:05', 903);
INSERT INTO connexion VALUES (11, '2017-03-03', '09:39:05', 903);
INSERT INTO connexion VALUES (12, '2017-03-03', '10:39:05', 902);

SELECT uid FROM connexion WHERE time_con BETWEEN '08:00:00' AND '10:00:00';