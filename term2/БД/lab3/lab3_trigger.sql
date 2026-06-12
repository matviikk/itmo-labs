CREATE OR REPLACE FUNCTION update_population_on_new_scene() RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT this_condition FROM people WHERE id = NEW.people_id) = 'живой' THEN
        RAISE NOTICE 'Updating population for city ID %', NEW.cities_id;
        UPDATE cities SET population_city = population_city + 1 WHERE id = NEW.cities_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_population_after_scene_insert ON scene;
CREATE TRIGGER trigger_update_population_after_scene_insert
AFTER INSERT ON scene
FOR EACH ROW
EXECUTE FUNCTION update_population_on_new_scene();


--DELETE FROM scene WHERE people_id IN (SELECT id FROM people WHERE full_name = 'Новый Тестовый Житель');
--DELETE FROM people WHERE full_name = 'Новый Тестовый Житель';

INSERT INTO people (full_name, date_of_birth, this_superpower, this_sex, this_condition)
VALUES ('Новый Тестовый Житель3', '2000-01-02', 'невидимость', 'м', 'живой');

INSERT INTO scene (people_id, cities_id, actions_mc, actions_mc_with_mc, aim_mc, date_of_scene)
VALUES ((SELECT id FROM people WHERE full_name = 'Новый Тестовый Житель3'), 1, 'Пример действия', 'Пример действия с MC', 'Пример цели', '2024-04-27');

SELECT * FROM cities WHERE id = 1;
