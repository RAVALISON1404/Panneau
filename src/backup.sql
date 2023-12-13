CREATE TABLE secteur
(
    id       SERIAL PRIMARY KEY,
    nom      VARCHAR,
    panneau  DOUBLE PRECISION,
    batterie DOUBLE PRECISION
);

CREATE TABLE salle
(
    id         SERIAL PRIMARY KEY,
    nom        VARCHAR,
    secteur_id INT REFERENCES secteur (id)
);

CREATE TABLE pointage
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
select secteur.id                     as secteur_id,
       besoin.date                    as date,
       (besoin.puissance * (sum(am))) as am,
       (besoin.puissance * (sum(pm))) as pm
from secteur
         join besoin on secteur.id = besoin.secteur_id
         join salle on secteur.id = salle.secteur_id
         join pointage on salle.id = pointage.salle_id and pointage.date = besoin.date
group by secteur.id, besoin.date, besoin.puissance;

create or replace view consommation_necessaire as
select secteur.id                              as secteur,
       luminosite.date,
       luminosite.heure,
       luminosite.niveau * secteur.panneau / 10 as panneau,
       CASE
           WHEN EXTRACT(HOUR FROM luminosite.heure) >= 12 THEN sum(pm) - luminosite.niveau * secteur.panneau / 10
           ELSE sum(am) - luminosite.niveau * secteur.panneau / 10
           END                                 AS batterie
from luminosite
         join besoin_necessaire on luminosite.date = besoin_necessaire.date
         join secteur on besoin_necessaire.secteur_id = secteur.id
group by luminosite.date, luminosite.heure, secteur.id, luminosite.niveau, secteur.panneau;
