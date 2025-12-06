-- Insertion des utilisateurs de test
INSERT INTO utilisateurs (nom, email, mot_de_passe, role, cree_le, modifie_le) VALUES
('Admin Principal', 'admin@cinema.com', 'MotdepasseAdmin123', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jean Dupont', 'jean.dupont@studio.com', 'mdpRealisateur1', 'REALISATEUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sophie Martin', 'sophie.martin@studio.com', 'mdpRealisateur2', 'REALISATEUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Marc Leroy', 'marc.leroy@studio.com', 'mdpScenariste1', 'SCENARISTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Claire Dubois', 'claire.dubois@studio.com', 'mdpScenariste2', 'SCENARISTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Hugo Bernard', 'hugo.bernard@studio.com', 'mdpProducteur1', 'PRODUCTEUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Laura Petit', 'laura.petit@studio.com', 'mdpMonteur1', 'MONTEUR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING; 