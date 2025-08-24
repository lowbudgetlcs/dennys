#!/usr/bin/env python3
# pyright: basic

import url
import json
import requests
from argparse import ArgumentParser

parser = ArgumentParser()
parser = url.args(parser)
url = url.base(parser)
header = {"Content-Type": "application/json"}

res = requests.get(f"{url}/event", headers=header)
res.raise_for_status()
EVENTS = res.json()


def getEventId(name):
    e = [x["id"] for x in EVENTS if x["name"] == name]
    count = len(e)
    if count > 1:
        print(f"More than one event matched {name}")
        print(e)
        return None
    elif count == 0:
        print(f"No event matched {name}")
        return None
    else:
        return e[0]


res = requests.get(f"{url}/team", headers=header)
res.raise_for_status()
TEAMS = res.json()


def getTeamId(name):
    t = [x["id"] for x in TEAMS if x["name"] == name]
    count = len(t)
    if count > 1:
        print(f"More than one team matched {name}")
        print(t)
        return None
    elif count == 0:
        print(f"No team matched {name}")
        return None
    else:
        return t[0]


prefix = "Season 15"
with open("data/divisions.json") as f:
    data = json.load(f)
    for div, teams in data.items():
        # Create each team
        division = f"{prefix} {div}"
        eventId = getEventId(division)
        print(eventId)
        if eventId is None:
            continue
        for teamName in teams:
            try:
                teamId = getTeamId(teamName)
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
