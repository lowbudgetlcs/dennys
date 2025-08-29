#!/usr/bin/env python3
# pyright: basic

import dennys
import args
import json
from argparse import ArgumentParser

parser = ArgumentParser()
parser = args.args(parser)
url = args.baseUrl(parser)
dataPath = args.dataPath(parser)

prefix = "Season 15"
with open(f"{dataPath}/divisions.json") as f:
    data = json.load(f)
    EVENTS = dennys.getEvents(url)
    if EVENTS is None:
        print("Failed to fetch events.")
        exit(1)
    TEAMS= dennys.getTeams(url)
    if TEAMS is None:
        print("Failed to fetch teams.")
        exit(1)
    for div, teams in data.items():
        # Create each team
        division = f"{prefix} {div}"
        print(division)
        eventId = dennys.findEventId(division, EVENTS)
        if eventId is None:
            print(f"Failed to find id for {division}.")
            continue
        for teamName in teams:
            print(teamName)
            teamId = dennys.findTeamId(teamName, TEAMS)
            if teamId is None:
                print(f"Failed to find id for {teamName}.")
                continue
            if dennys.addTeamToEvent(url, eventId, teamId) is None:
                print(f"Failed to add {teamName} to {division}.")
