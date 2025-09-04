#!/usr/bin/env python3
# pyright: basic

import dennys
import args
import json
from argparse import ArgumentParser
import logging

logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

parser = ArgumentParser()
parser = args.args(parser)
base = args.base_url(parser)
data_path = args.data_path(parser)
url = f"{base}/team"

with open(f"{data_path}/teams.json") as f:
    data = json.load(f)
    for team in data:
        # Create each team
        logger.info(f"Creating team '{team}'...")
        if dennys.create_team(url, team) is None:
            print(f"Failed to create {team}.")
