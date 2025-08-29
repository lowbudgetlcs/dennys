#!/usr/bin/env python3

# pyright: basic
from argparse import ArgumentParser
import json
import dennys
import args

parser = ArgumentParser()
parser = args.args(parser)
url = args.baseUrl(parser)
prefix = "Season 15"
dataPath = args.dataPath(parser)


with open(f"{dataPath}/series.json") as f:
    data = json.load(f)
    events = dennys.getEvents(url)
    if events is None:
        print("Failed to fetch events.")
        exit(1)
    for d, matchups in data.items():
        division = f"{prefix} {d}"
        eventId = dennys.findEventId(division, events)
        if eventId is None:
            print(f"Failed to find id for {division}.")
            continue
        divisions = dennys.getEventWithTeams(url, eventId)
        if divisions is None:
            print(f"Failed to get event with teams {division}.")
            continue
        teams = divisions["teams"]
        for team, opps in matchups.items():
            lhsId = dennys.findTeamId(team, teams)
            if lhsId is None:
                print(f"Failed to find teamId for {team}")
                continue
            for opp in opps:
                rhsId = dennys.findTeamId(opp, teams)
                if rhsId is None:
                    print(f"Failed to find teamId for {opp}")
                    continue
                series = dennys.createSeries(url, eventId, lhsId, rhsId, 2)
                if series is None:
                    print(f"Failed to create series between {lhsId}, {rhsId}")
