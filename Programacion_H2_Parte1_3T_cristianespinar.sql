CREATE DATABASE cine_cristianespinar;
USE cine_cristianespinar;

CREATE TABLE tipo (
    id_tipo VARCHAR(10) PRIMARY KEY,
    nombre_tipo VARCHAR(50) NOT NULL
);

CREATE TABLE peliculas (
    id_pelicula VARCHAR(10) PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    director VARCHAR(100),
    duracion INT,
    clasificacion VARCHAR(10),
    id_tipo VARCHAR(10),
    FOREIGN KEY (id_tipo) REFERENCES tipo(id_tipo)
);
INSERT INTO tipo (id_tipo, nombre_tipo) VALUES 
('T1', 'Animación'),
('T2', 'Documental'),
('T3', 'Drama'),
('T4', 'Comedia');
INSERT INTO peliculas (id_pelicula, titulo, director, duracion, clasificacion, id_tipo) VALUES 
('P001', 'Soul', 'Pete Docter', 100, 'A', 'T1'),
('P002', '13th', 'Ava DuVernay', 100, 'B', 'T2'),
('P003', 'The Pursuit of Happyness', 'Gabriele Muccino', 117, 'A', 'T3'),
('P004', 'The Hangover', 'Todd Phillips', 100, 'B15', 'T4');
