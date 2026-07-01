DROP TABLE IF EXISTS Respuesta CASCADE;
DROP TABLE IF EXISTS Opcion CASCADE;
DROP TABLE IF EXISTS Pregunta CASCADE;
DROP TABLE IF EXISTS Detalleaplicacion CASCADE;
DROP TABLE IF EXISTS Test CASCADE;
DROP TABLE IF EXISTS Sujeto CASCADE;
DROP TABLE IF EXISTS Usuario CASCADE;

DROP SEQUENCE IF EXISTS detalleaplicacion_detalleaplicacionid_seq;
DROP SEQUENCE IF EXISTS opcion_opcionid_seq;
DROP SEQUENCE IF EXISTS pregunta_preguntaid_seq;
DROP SEQUENCE IF EXISTS respuesta_respuestaid_seq;
DROP SEQUENCE IF EXISTS sujeto_sujetoid_seq;
DROP SEQUENCE IF EXISTS test_testid_seq;
DROP SEQUENCE IF EXISTS usuario_usuarioid_seq;

CREATE SEQUENCE detalleaplicacion_detalleaplicacionid_seq INCREMENT 1 START 1;
CREATE SEQUENCE opcion_opcionid_seq INCREMENT 1 START 1;
CREATE SEQUENCE pregunta_preguntaid_seq INCREMENT 1 START 1;
CREATE SEQUENCE respuesta_respuestaid_seq INCREMENT 1 START 1;
CREATE SEQUENCE sujeto_sujetoid_seq INCREMENT 1 START 1;
CREATE SEQUENCE test_testid_seq INCREMENT 1 START 1;
CREATE SEQUENCE usuario_usuarioid_seq INCREMENT 1 START 1;

CREATE TABLE Usuario
(
	UsuarioID integer NOT NULL DEFAULT NEXTVAL('usuario_usuarioid_seq'::regclass),
	Apellidos varchar(255) NOT NULL,
	Email varchar(255) NULL,
	Estudios varchar(255) NULL,
	Fechanac date NULL,
	Nombres varchar(255) NOT NULL,
	Password varchar(255) NOT NULL,
	Profesion varchar(255) NULL,
	Sexo char(1) NULL,
	Username varchar(255) NOT NULL,
	createdAt timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updatedAt timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deletedAt timestamp with time zone NULL
)
;

CREATE TABLE Sujeto
(
	SujetoID integer NOT NULL
)
;

CREATE TABLE Test
(
	TestID integer NOT NULL DEFAULT NEXTVAL('test_testid_seq'::regclass),
	Estado boolean NOT NULL DEFAULT true,
	Instrucciones text NOT NULL,
	Nombre varchar(255) NOT NULL,
	Tiempomax numeric NOT NULL,
	createdAt timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updatedAt timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deletedAt timestamp with time zone NULL
)
;

CREATE TABLE Detalleaplicacion
(
	DetalleaplicacionID integer NOT NULL DEFAULT NEXTVAL('detalleaplicacion_detalleaplicacionid_seq'::regclass),
	Aciertos integer NOT NULL DEFAULT 0,
	Desaciertos integer NOT NULL DEFAULT 0,
	Fecha date NOT NULL DEFAULT CURRENT_DATE,
	Horafin time without time zone NOT NULL,
	Horainicio time without time zone NOT NULL,
	TestID integer NOT NULL,
	SujetoID integer NOT NULL
)
;

CREATE TABLE Pregunta
(
	PreguntaID integer NOT NULL DEFAULT NEXTVAL('pregunta_preguntaid_seq'::regclass),
	Imagen varchar(255) NOT NULL,
	TestID integer NOT NULL,
	createdAt timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updatedAt timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deletedAt timestamp with time zone NULL
)
;

CREATE TABLE Opcion
(
	OpcionID integer NOT NULL DEFAULT NEXTVAL('opcion_opcionid_seq'::regclass),
	Acierto boolean NOT NULL,
	Respuesta integer NOT NULL,
	PreguntaID integer NOT NULL
)
;

CREATE TABLE Respuesta
(
	RespuestaID integer NOT NULL DEFAULT NEXTVAL('respuesta_respuestaid_seq'::regclass),
	Opcion integer NOT NULL,
	PreguntaID integer NOT NULL,
	DetalleaplicacionID integer NOT NULL
)
;

ALTER TABLE Usuario ADD CONSTRAINT PK_Usuario PRIMARY KEY (UsuarioID);
ALTER TABLE Sujeto ADD CONSTRAINT PK_Sujeto PRIMARY KEY (SujetoID);
ALTER TABLE Test ADD CONSTRAINT PK_Test PRIMARY KEY (TestID);
ALTER TABLE Detalleaplicacion ADD CONSTRAINT PK_Detalleaplicacion PRIMARY KEY (DetalleaplicacionID);
ALTER TABLE Pregunta ADD CONSTRAINT PK_Pregunta PRIMARY KEY (PreguntaID);
ALTER TABLE Opcion ADD CONSTRAINT PK_Opcion PRIMARY KEY (OpcionID);
ALTER TABLE Respuesta ADD CONSTRAINT PK_Respuesta PRIMARY KEY (RespuestaID);

ALTER TABLE Sujeto ADD CONSTRAINT FK_Sujeto_Usuario
	FOREIGN KEY (SujetoID) REFERENCES Usuario (UsuarioID) ON DELETE CASCADE ON UPDATE No Action
;

ALTER TABLE Detalleaplicacion ADD CONSTRAINT FK_DetalleAplicacion_Test
	FOREIGN KEY (TestID) REFERENCES Test (TestID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Detalleaplicacion ADD CONSTRAINT FK_DetalleAplicacion_Sujeto
	FOREIGN KEY (SujetoID) REFERENCES Sujeto (SujetoID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Pregunta ADD CONSTRAINT FK_Pregunta_Test
	FOREIGN KEY (TestID) REFERENCES Test (TestID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Opcion ADD CONSTRAINT FK_Opcion_Pregunta
	FOREIGN KEY (PreguntaID) REFERENCES Pregunta (PreguntaID) ON DELETE CASCADE ON UPDATE No Action
;

ALTER TABLE Respuesta ADD CONSTRAINT FK_Respuesta_Pregunta
	FOREIGN KEY (PreguntaID) REFERENCES Pregunta (PreguntaID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Respuesta ADD CONSTRAINT FK_Respuesta_Detalleaplicacion
	FOREIGN KEY (DetalleaplicacionID) REFERENCES Detalleaplicacion (DetalleaplicacionID) ON DELETE CASCADE ON UPDATE No Action
;

ALTER TABLE usuario ADD COLUMN dtype VARCHAR(31);

ALTER TABLE pregunta ALTER COLUMN imagen TYPE BYTEA USING imagen::bytea;
