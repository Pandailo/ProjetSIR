DROP TABLE SIR_SALAIRE;
DROP TABLE SIR_EMP;
DROP TABLE SIR_PROJET;
DROP TABLE SIR_ASSIGN;

CREATE TABLE SIR_SALAIRE
(titre VARCHAR2(25),
 id integer CONSTRAINT pk_salaire PRIMARY KEY,
 salaire integer
);

CREATE TABLE SIR_EMP
(ENO integer CONSTRAINT pk_eno PRIMARY KEY,
 nom VARCHAR2(25),
 prenom VARCHAR2(25),
 age integer,
 ville VARCHAR2(25),
 titre VARCHAR2(25)
);

CREATE TABLE SIR_PROJET
(PNO integer CONSTRAINT pk_pno PRIMARY KEY,
 name VARCHAR2(25),
 budget integer,
 location VARCHAR2(25),
 mois VARCHAR2(25),
 annee integer
);

CREATE TABLE SIR_ASSIGN
(ENO integer,
 PNO integer,
 resp VARCHAR2(25),
 dur integer,
 CONSTRAINT pk_assign PRIMARY KEY (ENO,PNO)
);

