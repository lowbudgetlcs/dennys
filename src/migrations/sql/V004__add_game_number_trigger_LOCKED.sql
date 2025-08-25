START TRANSACTION;
SET search_path = dennys;

CREATE OR REPLACE FUNCTION assign_game_number()
RETURNS TRIGGER AS $$
BEGIN
    -- If NUMBER is null, compute the next number for this series
    IF NEW.number IS NULL THEN
        SELECT COALESCE(MAX(number), 0) + 1
        INTO NEW.number
        FROM dennys.games
        WHERE series_id = NEW.series_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_game_number
BEFORE INSERT ON dennys.games
FOR EACH ROW
EXECUTE FUNCTION assign_game_number();

COMMIT;