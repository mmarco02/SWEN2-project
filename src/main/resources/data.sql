INSERT INTO users (username, password) VALUES
    ('user1', '$2a$10$hD51u7KprdS7PCKDH/x1aOS8O7XADX0jYvG9gKjgDLqlq54Elif8K'), --password123
    ('user2', '$2a$10$kDHupV3lLANp49COx0RlleZ.Gem59A0dqUNR6V.aVfV3UzAd3I/12');  --password456

INSERT INTO tours (name, description, from_location, to_location, transport_type, distance_km, estimated_time, route, user_id) VALUES
    ('Wiener Stadtwanderung', 'Gemütliche Wanderung durch die Wiener Innenstadt', 'Wien Stephansplatz', 'Wien Schönbrunn', 'WALKING', 6.5, 90, NULL, 1),
    ('Donauradweg Etappe 1', 'Radtour entlang der Donau von Krems nach Wien', 'Krems an der Donau', 'Wien', 'BICYCLE', 85.0, 300, NULL, 1),
    ('Salzburg nach Hallstatt', 'Tagesausflug mit dem Auto durch das Salzkammergut', 'Salzburg', 'Hallstatt', 'CAR', 75.0, 70, NULL, 2),
    ('Innsbruck Nordkette', 'Bergwanderung zur Nordkette über den Innsbrucker Weg', 'Innsbruck Altstadt', 'Nordkette Hafelekar', 'WALKING', 12.0, 240, NULL, 2),
    ('Wachau Weinwanderung', 'Wanderung durch die Weingärten der Wachau mit Verkostung', 'Dürnstein', 'Spitz an der Donau', 'WALKING', 15.0, 210, NULL, 1),
    ('Graz Schlossberg Runde', 'Stadtrundgang über den Schlossberg mit Aussicht auf Graz', 'Graz Hauptplatz', 'Graz Schlossberg', 'WALKING', 3.5, 60, NULL, 2),
    ('Neusiedlersee Radtour', 'Radtour rund um den Neusiedlersee im Burgenland', 'Neusiedl am See', 'Podersdorf am See', 'BICYCLE', 42.0, 150, NULL, 1),
    ('Grossglockner Hochalpenstrasse', 'Panoramafahrt über die höchste Passstrasse Österreichs', 'Bruck an der Grossglocknerstrasse', 'Heiligenblut', 'CAR', 48.0, 90, NULL, 2),
    ('Salzkammergut Radweg', 'Radtour durch das Salzkammergut von Bad Ischl nach Hallstatt', 'Bad Ischl', 'Hallstatt', 'BICYCLE', 30.0, 120, NULL, 1),
    ('Wiener Prater Laufrunde', 'Laufrunde durch den Wiener Prater entlang der Hauptallee', 'Wien Praterstern', 'Wien Lusthaus', 'WALKING', 8.0, 55, NULL, 1),
    ('Brenner Passfahrt', 'Fahrt über den Brennerpass von Innsbruck nach Sterzing', 'Innsbruck', 'Sterzing', 'CAR', 38.0, 45, NULL, 2),
    ('Donauinsel Radtour', 'Gemütliche Radtour auf der Wiener Donauinsel', 'Wien Donauinsel Nord', 'Wien Donauinsel Süd', 'BICYCLE', 21.0, 75, NULL, 1),
    ('Linz Kulturmeile', 'Stadtwanderung durch die Linzer Kulturmeile', 'Linz Hauptplatz', 'Linz Ars Electronica', 'WALKING', 4.0, 50, NULL, 2),
    ('Zillertal Wanderung', 'Bergwanderung im Zillertal mit Almhütten', 'Mayrhofen', 'Berliner Hütte', 'WALKING', 18.0, 360, NULL, 2),
    ('Busfahrt nach Bratislava', 'Tagesausflug mit dem Bus von Wien nach Bratislava', 'Wien Erdberg', 'Bratislava Hauptbahnhof', 'BUS', 65.0, 60, NULL, 1);

INSERT INTO tour_logs (date_time, comment, difficulty, total_distance, total_time, rating, tour_id, user_id) VALUES
    ('2025-04-10 09:00:00', 'Schönes Wetter, viele Touristen unterwegs', 'EASY', 6.5, 85, 4, 1, 1),
    ('2025-04-15 08:30:00', 'Leichter Gegenwind, aber tolle Aussicht', 'MEDIUM', 85.0, 310, 5, 2, 1),
    ('2025-05-01 10:00:00', 'Stau bei der Anfahrt, Hallstatt war überfüllt', 'EASY', 75.0, 90, 3, 3, 2),
    ('2025-05-03 07:00:00', 'Anstrengend aber die Aussicht lohnt sich', 'HARD', 12.0, 260, 5, 4, 2),
    ('2025-05-10 09:30:00', 'Zweiter Versuch, diesmal weniger Touristen', 'EASY', 6.3, 80, 5, 1, 1),
    ('2025-05-12 10:00:00', 'Wanderung durch die Weingärten war wunderschön, Verkostung am Ende top', 'EASY', 15.0, 200, 5, 5, 1),
    ('2025-05-14 14:00:00', 'Graz Schlossberg bei Sonnenuntergang, tolle Aussicht auf die Stadt', 'EASY', 3.5, 55, 4, 6, 2),
    ('2025-05-15 07:30:00', 'Radtour am Neusiedlersee, leichter Wind, flache Strecke perfekt zum Radfahren', 'EASY', 42.0, 145, 4, 7, 1),
    ('2025-05-16 09:00:00', 'Grossglockner Panoramafahrt bei klarem Wetter, Aussicht auf die Berge atemberaubend', 'MEDIUM', 48.0, 95, 5, 8, 2),
    ('2025-05-18 08:00:00', 'Salzkammergut Radtour, Hallstatt diesmal nicht so überfüllt wie bei der Autofahrt', 'MEDIUM', 30.0, 125, 4, 9, 1),
    ('2025-05-20 06:30:00', 'Laufrunde im Prater am frühen Morgen, kaum Leute, sehr entspannend', 'EASY', 8.0, 50, 5, 10, 1),
    ('2025-05-22 11:00:00', 'Brennerpass Fahrt, Stau am Tunnel aber Landschaft wunderschön', 'EASY', 38.0, 60, 3, 11, 2),
    ('2025-05-23 13:00:00', 'Donauinsel Radtour bei Sommerwetter, viele Radfahrer unterwegs', 'EASY', 21.0, 70, 4, 12, 1),
    ('2025-05-25 10:30:00', 'Linz Kulturmeile war interessant, Ars Electronica Museum sehr empfehlenswert', 'EASY', 4.0, 45, 4, 13, 2),
    ('2025-05-27 06:00:00', 'Zillertal Wanderung zur Berliner Hütte, anstrengend aber Aussicht grandios', 'HARD', 18.0, 370, 5, 14, 2),
    ('2025-05-28 08:00:00', 'Busfahrt nach Bratislava, gemütlich, Stadt ist einen Besuch wert', 'EASY', 65.0, 55, 4, 15, 1),
    ('2025-06-01 09:00:00', 'Wiener Stadtwanderung nochmal bei Regen, trotzdem schön mit weniger Touristen', 'EASY', 6.4, 95, 3, 1, 1),
    ('2025-06-02 07:00:00', 'Donauradweg zweite Etappe, diesmal mit Rückenwind, Aussicht auf die Donau fantastisch', 'MEDIUM', 85.0, 280, 5, 2, 1),
    ('2025-06-02 10:00:00', 'Nordkette Wanderung bei Nebel, Aussicht leider nicht so gut diesmal', 'HARD', 12.0, 250, 3, 4, 2),
    ('2025-06-03 08:30:00', 'Wachau Wanderung nochmal, diesmal die Weingärten in voller Blüte', 'EASY', 14.8, 195, 5, 5, 1),
    ('2025-06-03 11:00:00', 'Neusiedlersee Radtour bei starkem Wind, Radfahren war anstrengender als erwartet', 'MEDIUM', 42.0, 170, 3, 7, 1),
    ('2025-06-03 14:00:00', 'Salzkammergut nochmal, Hallstatt am Wochenende wieder viele Touristen', 'MEDIUM', 30.0, 130, 4, 9, 1);
