#!/usr/bin/env python3
# pyright: basic

import url
import json
import requests
from argparse import ArgumentParser

parser = ArgumentParser()
parser = url.args(parser)
base = url.base(parser)
url = f"{base}/team"

with open("data/teams.json") as f:
    data = json.load(f)
    for team in data:
        # Create each team
        payload = {"name": team}
        res = requests.post(
            f"{url}",
            headers={"Content-Type": "application/json"},
            data=json.dumps(payload),
        )
        try:
            res.raise_for_status()
        except requests.HTTPError as e:
            print(f"Failed to create team: {team}")
            print(e)
