#!/usr/bin/env python3
# pyright: basic

import dennys
import args
import json
from argparse import ArgumentParser
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("event_seed")

parser = ArgumentParser()
parser = args.args(parser)
url = args.base_url(parser)
dataPath = args.data_path(parser)

prefix = "Season 15"
with open(f"{dataPath}/divisions.json") as f:
    data = json.load(f)
    EVENTS = dennys.get_events(url)
    if EVENTS is None:
        logger.error("Failed to fetch events.")
        exit(1)
    TEAMS = dennys.get_teams(url)
    if TEAMS is None:
        logger.error("Failed to fetch teams.")
        exit(1)
    for div, teams in data.items():
        # Create each team
        division = f"{prefix} {div}"
        eventId = dennys.find_event_id(division, EVENTS)
        if eventId is None:
            logger.warning(f"Failed to find id for {division}.")
            continue
        for teamName in teams:
            teamId = dennys.find_team_id(teamName, TEAMS)
            if teamId is None:
                logging.warning(f"Failed to find id for {teamName}.")
                continue
            logging.debug(f"Assigning team '{teamName}' to event '{division}'...")
            if dennys.add_team_to_event(url, eventId, teamId) is None:
                print(f"Failed to add {teamName} to {division}.")
