#!/usr/bin/env python3
# pyright: basic

import url
import json
import requests
from argparse import ArgumentParser
from filters  import getEventId, getTeamId

parser = ArgumentParser()
parser = url.args(parser)
url = url.base(parser)
header = {"Content-Type": "application/json"}

res = requests.get(f"{url}/event", headers=header)
res.raise_for_status()
EVENTS = res.json()

res = requests.get(f"{url}/team", headers=header)
res.raise_for_status()
TEAMS = res.json()

prefix = "Season 15"
with open("data/divisions.json") as f:
    data = json.load(f)
    for div, teams in data.items():
        # Create each team
        division = f"{prefix} {div}"
        eventId = getEventId(division, EVENTS)
        if eventId is None:
            continue
        for teamName in teams:
            try:
                teamId = getTeamId(teamName, TEAMS)
                if teamId is None:
                    continue
                payload = {"teamId": teamId}
                res = requests.post(
                    f"{url}/event/{eventId}/teams",
                    headers=header,
                    data=json.dumps(payload),
                )
                res.raise_for_status()
            except requests.HTTPError as e:
                print(f"Failed to create team: {teamName}")
                print(e)
