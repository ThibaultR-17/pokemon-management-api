-- Ids générés par la base dans l'ordre d'insertion : Sacha = 1, Ondine = 2
-- type : nom de la constante TypePokemon (@Enumerated STRING)
INSERT INTO dresseur (nom, region) VALUES ('Sacha', 'Kanto');
INSERT INTO dresseur (nom, region) VALUES ('Ondine', 'Azuria');

-- Sacha : 3 Pokémon ; Ondine : aucun
INSERT INTO pokemon (nom, type, niveau, dresseur_id) VALUES ('Pikachu', 'ELECTRIK', 55, (SELECT id FROM dresseur WHERE nom = 'Sacha'));
INSERT INTO pokemon (nom, type, niveau, dresseur_id) VALUES ('Salamèche', 'FEU', 30, (SELECT id FROM dresseur WHERE nom = 'Sacha'));
INSERT INTO pokemon (nom, type, niveau, dresseur_id) VALUES ('Carapuce', 'EAU', 20, (SELECT id FROM dresseur WHERE nom = 'Sacha'));
