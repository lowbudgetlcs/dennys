DO $$
DECLARE 
    -- team2 won this series 2-0
    div_id INT;
    team1_id INT;
    team2_id INT;
    captain1_id INT;
    captain2_id INT;
    series_id INT;
BEGIN
-- Create Divisions
    INSERT INTO divisions (name,tournament_id) VALUES ('ECONOMY_TEST',7250970) RETURNING id INTO div_id;

-- Create Teams
    INSERT INTO teams (name,division_id) VALUES ('TEAM 1', div_id) RETURNING id INTO team1_id;
    INSERT INTO teams (name,division_id) VALUES ('TEAM 2', div_id) RETURNING id INTO team2_id;

-- Create Players
    INSERT INTO players (riot_puuid,summoner_name,team_id) VALUES
        ('Xb6LgvUVf4lN5krpDuCBy5Oymsq0XztbkqOTvs1qjQsvf43PwwK3EElHPdqhHLpgMiEMbdo9ZlD7YQ', 'BabaAjam#psg', team1_id),
        ('WnThCeqcy3oJreXRWCy05nM2SNVlIKIu5xSKtsz_5MJWY-vkoiMJ-lFkN_KoTwy6G-YXtKCaogaXlQ', 'Effervescence#Elegy', team1_id),
        ('3ydYxvBw4zTCxBRDZoboq_-yk8PxlSt6nDn73UehOR04QoaNPfoglFLWy5RslaSMaq7FbS5945pOsA', 'wxyZED#420', team1_id),
        ('snkxmzjHoIwjnwpft5nxuT-b5RqSHsixetxsnP8Y5ZHat23Iw6ETMgZluAgZT4LsWTdf-P7l-5zrEQ', 'Torden#Panth', team1_id),
        ('2aGk9vOXmlKzZ9luCZgTZRq2YccbJtZCfDSfGXILql01tVE6C9ZmMvxqygwh2l95tHmvUVIbuWWkBQ', 'Funnydud99#NA1', team1_id),
        ('KX0P4mgDxOnieDqXuAg2c1lpsRnfnktRa_MZLLVa2uWWXHnFCONYOFG9MB555Vukvg_zttsJYJNUrw', 'JK Slapgames#NA1', team1_id),
        ('jRLThwSAZFG8UQ0OEXGcC_cvDiTPS4yuc5lzv_5uA3w-4FQiWf1EuppRhgRL8M0aGHg3OQdgyi55yg', 'Huia#MEOW', team1_id),
        ('Q-0oxpNCd3veJKzECpHzYGcZy2b69hj_ZaIRb243GVXUYQPVAMtwEqVBXLIUZqJNvJc4ksCrCMN2Ng', 'Verticallity#Cho', team1_id),

        ('0E9z21601_4mFpwq7BbPK7xdlb-6RNblykv9Ty2L1YJX2mY21mB-shkuzmojtWuF_B2gbYktPqhl3g', 'ELLO COMRAD #NA1', team2_id),
        ('w0-3jUYSPD0JnUj0ySuud3oNFgabkK-LVJQOpaPMkG5xEP-SvQcP-WV_mnIWx1T2YwpCfsSY5U5Wrg', 'NinPenguinn#NA1', team2_id),
        ('ICSrAlBKj11dxrSuwaQhZDT2CowS58bjjLl8EeIlmliV18HCIs6yZDJkCaT0iUhaIjvA4hzExZnCWQ', 'Jisp#bolt', team2_id),
        ('5e0Cbu3OQUbYTcfHFXCguGnfkFQ5VgbFMNqwV1yHpGvSK09BtVgR-bkbEwTqASAyv6Su8Oi7NLY17Q', 'IcarussFeather#NA1', team2_id),
        ('1EtsasRKADNt0CkGhSWfCLxX79_OC8UfbWirp9npQ6OSiVOdaJyY9tF20V3hzUHGvjj1e9YkLg8D3Q', 'J Chino#Simp', team2_id),
        ('SzrwW_YpAjNqZLHY0uPqrS2kVvRyGfjEiffv6Xta-xVSKCiSQWB-blQP0L0M0SOW8ESSyUbCNDCVfg', 'SnowflakeGecko#NA1', team2_id),
        ('UZSg5bpH82c2KFdKNLTHraPfyn9GWbPlXinZvbC653aVhDx2KN01ncvADK5Rzf5Ip_3-kG4PLtQc2A', 'VoiidAtomic#9195', team2_id);

-- Set Captains
    UPDATE teams SET captain_id = (SELECT id FROM players WHERE summoner_name = 'Funnydud99#NA1') WHERE id = team1_id;
    UPDATE teams SET captain_id = (SELECT id FROM players WHERE summoner_name = 'Jisp#bolt') WHERE id = team2_id;

-- Create series
    INSERT INTO series (division_id,winner_id,loser_id) VALUES (div_id,team2_id,team1_id) RETURNING id INTO series_id;

-- Create team to series mapping
    INSERT INTO team_to_series VALUES (DEFAULT, team1_id, series_id), (DEFAULT, team2_id, series_id);

-- Create games
    INSERT INTO games (shortcode,series_id,game_num) VALUES ('NA04e01-e599e041-7e3e-48fc-bb53-9e3ebb127555',series_id,1);
    INSERT INTO games (shortcode,series_id,game_num) VALUES ('NA04e01-7704f893-f01f-4f1d-be97-cb4468e91a4d',series_id,2);

END $$;

