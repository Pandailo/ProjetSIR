INSERT INTO SIR_SALAIRE values ('Elec. Eng',1, 40000);
INSERT INTO SIR_SALAIRE values ('Sys. Anal.',2, 34000);
INSERT INTO SIR_SALAIRE values ('Mech.Eng.',3, 27000);
INSERT INTO SIR_SALAIRE values ('Programmeur',4, 24000);
INSERT INTO SIR_SALAIRE values ('Secrétaire',5, 20000);
INSERT INTO SIR_SALAIRE values ('Technicien',6, 32000);
INSERT INTO SIR_SALAIRE values ('Chef de projet',7, 45000);


DECLARE
	nom SIR_EMP.nom%TYPE;
	prenom SIR_EMP.prenom%TYPE;
	age SIR_EMP.age%TYPE;
	ville SIR_EMP.ville%TYPE;
	t_titre SIR_EMP.titre%TYPE;
	rand integer;
BEGIN
	FOR i IN 1 .. 50 LOOP
		nom:=DBMS_RANDOM.STRING('l',10);
		prenom:=DBMS_RANDOM.STRING('l',8);
		age:=DBMS_RANDOM.VALUE(18,80);
		ville:=DBMS_RANDOM.STRING('l',8);
		rand:=DBMS_RANDOM.VALUE(1,7);
		SELECT titre INTO t_titre FROM SIR_SALAIRE WHERE rand=id;
		INSERT INTO SIR_EMP values (i, nom, prenom, age, ville, t_titre);
	END LOOP; 
END;
/

DECLARE
	name SIR_PROJET.name%TYPE;
	budget SIR_PROJET.budget%TYPE;
	location SIR_PROJET.location%TYPE;
	mois SIR_PROJET.mois%TYPE;
	annee  SIR_PROJET.annee%TYPE;
	rand integer;
BEGIN
	FOR i IN 1 .. 50 LOOP
		name:=DBMS_RANDOM.STRING('l',10);
		budget:=DBMS_RANDOM.VALUE(100000,1000000);
		location:=DBMS_RANDOM.STRING('l',8);
		annee:=FLOOR(DBMS_RANDOM.value(2000,2100));
		rand:=FLOOR(DBMS_RANDOM.value(1,12));
		IF rand=1 then
			mois:='Janvier';
		ELSIF rand=2 then
			mois:='Fevrier';
		ELSIF rand=3 then
			mois:='Mars';
		ELSIF rand=4 then
			mois:='Avril';
		ELSIF rand=5 then
			mois:='Mai';
		ELSIF rand=6 then
			mois:='Juin';
		ELSIF rand=7 then
			mois:='Juillet';
		ELSIF rand=8 then
			mois:='Aout';
		ELSIF rand=9 then
			mois:='Septembre';
		ELSIF rand=10 then
			mois:='Octobre';
		ELSIF rand=11 then
			mois:='Novembre';
		ELSIF rand=12 then
			mois:='Decembre';
		END IF;  
		INSERT INTO SIR_PROJET values (i, name, budget, location, mois, annee);
	END LOOP; 
END;
/

DECLARE
	resp SIR_ASSIGN.resp%TYPE;
	dur SIR_ASSIGN.dur%TYPE;
	rand integer;
BEGIN
	FOR i IN 1 .. 50 LOOP
		rand:=DBMS_RANDOM.VALUE(1,50);
		resp:=DBMS_RANDOM.STRING('l',10);
		dur:=DBMS_RANDOM.VALUE(6,48);
		INSERT INTO SIR_ASSIGN values (rand, i, resp, dur);
	END LOOP; 
END;
/
