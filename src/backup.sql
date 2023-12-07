CREATE TABLE secteur
(
    id  SERIAL PRIMARY KEY,
    nom VARCHAR
);

CREATE TABLE source
(
    id         SERIAL PRIMARY KEY,
    panneau    DOUBLE PRECISION,
    batterie   DOUBLE PRECISION,
    secteur_id INT REFERENCES secteur (id)
);

CREATE TABLE salle
(
    id         SERIAL PRIMARY KEY,
    nom        VARCHAR,
    secteur_id INT REFERENCES secteur (id)
);

CREATE TABLE nbr_pers
(
    id       SERIAL PRIMARY KEY,
    date     DATE,
    salle_id INT REFERENCES salle (id),
    am       INT,
    pm       INT
);

CREATE TABLE delestage
(
    id         SERIAL PRIMARY KEY,
    date       DATE,
    debut      TIME,
    fin        TIME,
    secteur_id INT REFERENCES secteur (id)
);

CREATE TABLE besoin
(
    id         SERIAL PRIMARY KEY,
    date       DATE,
    puissance  DOUBLE PRECISION DEFAULT 60,
    secteur_id INT REFERENCES secteur (id)
);

CREATE TABLE luminosite
(
    id     SERIAL PRIMARY KEY,
    date   DATE,
    heure  TIME,
    niveau DOUBLE PRECISION
);

create or replace view besoin_necessaire as
select besoin.date as date, (besoin.puissance * (sum(am))) as am, (besoin.puissance * (sum(pm))) as pm
from secteur
         join besoin on secteur.id = besoin.secteur_id
         join salle on secteur.id = salle.secteur_id
         join nbr_pers on salle.id = nbr_pers.salle_id
group by besoin.date, besoin.puissance;

create or replace view consommation_necessaire as
select secteur.id            as secteur,
       luminosite.date,
       heure,
       niveau * panneau / 10 as panneau,
       CASE
           WHEN EXTRACT(HOUR FROM heure) >= 12 THEN pm - niveau * panneau / 10
           ELSE am - niveau * panneau / 10
           END               AS batterie
from luminosite,
     secteur
         join source on secteur.id = source.secteur_id
         natural join besoin_necessaire;