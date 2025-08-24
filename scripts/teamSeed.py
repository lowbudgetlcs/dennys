#!/usr/bin/env python3
# pyright: basic

import json
import requests
from argparse import ArgumentParser

parser = ArgumentParser(description="Seed dennys database with events.")
_ = parser.add_argument("environment")
args = parser.parse_args()
environment = args.environment
url = f"https://{environment}.lowbudgetlcs.com/api/v1/team"

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
