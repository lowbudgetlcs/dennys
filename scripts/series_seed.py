#!/usr/bin/env python3

# pyright: basic
from argparse import ArgumentParser
import json
import dennys
import args

parser = ArgumentParser()
parser = args.args(parser)
url = args.base_url(parser)
event_prefix = "Season 15"
data_path = args.data_path(parser)


with open(f"{data_path}/series.json") as f:
    data = json.load(f)
    events = dennys.get_events(url)
    if events is None:
        print("Failed to fetch events.")
        exit(1)
    for d, matchups in data.items():
        division = f"{event_prefix} {d}"
        event_id = dennys.find_event_id(division, events)
        if event_id is None:
            print(f"Failed to find id for {division}.")
            continue
        divisions = dennys.get_event_with_teams(url, event_id)
        if divisions is None:
            print(f"Failed to get event with teams {division}.")
            continue
        teams = divisions["teams"]
        for team, opps in matchups.items():
            team1 = dennys.find_team_id(team, teams)
            if team1 is None:
                print(f"Failed to find teamId for {team}")
                continue
            for opp in opps:
                team2 = dennys.find_team_id(opp, teams)
                if team2 is None:
                    print(f"Failed to find teamId for {opp}")
                    continue
                series = dennys.create_series(url, event_id, (team1, team2), 2)
                if series is None:
                    print(f"Failed to create series between {team1}, {team2}")
