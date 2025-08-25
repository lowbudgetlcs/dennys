#!/usr/bin/env python3

# pyright: basic
from argparse import ArgumentParser
import json
import url
import requests
import filters


parser = ArgumentParser()
parser = url.args(parser)
url = url.base(parser)
header = {"Content-Type": "application/json"}
prefix = "Season 15"

res = requests.get(f"{url}/event", headers=header)
res.raise_for_status()
EVENTS = res.json()

with open("data/series.json") as f:
    data = json.load(f)
    for d, matchups in data.items():
        division = f"{prefix} {d}"
        eventId = filters.getEventId(division, EVENTS)
        res = requests.get(f"{url}/event/{eventId}/teams")
        res.raise_for_status()
        teams = res.json()["teams"]
        for team, opps in matchups.items():
            lhsId = filters.getTeamId(team, teams)
            for opp in opps:
                rhsId = filters.getTeamId(opp, teams)
                payload = {"team1Id": lhsId, "team2Id": rhsId, "gamesToWin": 2}
                res = requests.post(
                    f"{url}/event/{eventId}/series",
                    headers=header,
                    data=json.dumps(payload),
                )
                res.raise_for_status()
