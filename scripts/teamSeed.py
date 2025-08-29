#!/usr/bin/env python3
# pyright: basic

import dennys
import args
import json
from argparse import ArgumentParser

parser = ArgumentParser()
parser = args.args(parser)
base = args.baseUrl(parser)
dataPath = args.dataPath(parser)
url = f"{base}/team"

with open(f"{dataPath}/teams.json") as f:
    data = json.load(f)
    for team in data:
        # Create each team
        if dennys.createTeam(url, team) is None:
            print(f"Failed to create {team}.")
