INSERT INTO users (username, password) VALUES
    ('user1', '$2a$10$hD51u7KprdS7PCKDH/x1aOS8O7XADX0jYvG9gKjgDLqlq54Elif8K'), --password123
    ('user2', '$2a$10$kDHupV3lLANp49COx0RlleZ.Gem59A0dqUNR6V.aVfV3UzAd3I/12');  --password456

INSERT INTO tours (name, description, from_location, to_location, transport_type, distance_km, estimated_time, route, user_id) VALUES
    ('Wiener Stadtwanderung', 'Gemütliche Wanderung durch die Wiener Innenstadt', 'Wien Stephansplatz', 'Wien Schönbrunn', 'WALKING', 6.5, 90, NULL, 1),
    ('Donauradweg Etappe 1', 'Radtour entlang der Donau von Krems nach Wien', 'Krems an der Donau', 'Wien', 'BICYCLE', 85.0, 300, NULL, 1),
    ('Salzburg nach Hallstatt', 'Tagesausflug mit dem Auto durch das Salzkammergut', 'Salzburg', 'Hallstatt', 'CAR', 75.0, 70, NULL, 2),
    ('Innsbruck Nordkette', 'Bergwanderung zur Nordkette über den Innsbrucker Weg', 'Innsbruck Altstadt', 'Nordkette Hafelekar', 'WALKING', 12.0, 240, NULL, 2);

INSERT INTO tour_logs (date_time, comment, difficulty, total_distance, total_time, rating, tour_id, user_id) VALUES
    ('2025-04-10 09:00:00', 'Schönes Wetter, viele Touristen unterwegs', 'EASY', 6.5, 85, 4, 1, 1),
    ('2025-04-15 08:30:00', 'Leichter Gegenwind, aber tolle Aussicht', 'MEDIUM', 85.0, 310, 5, 2, 1),
    ('2025-05-01 10:00:00', 'Stau bei der Anfahrt, Hallstatt war überfüllt', 'EASY', 75.0, 90, 3, 3, 2),
    ('2025-05-03 07:00:00', 'Anstrengend aber die Aussicht lohnt sich', 'HARD', 12.0, 260, 5, 4, 2),
    ('2025-05-10 09:30:00', 'Zweiter Versuch, diesmal weniger Touristen', 'EASY', 6.3, 80, 5, 1, 1);
