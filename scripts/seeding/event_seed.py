#!/usr/bin/env python3
# pyright: basic

import args
import dennys
import json
from argparse import ArgumentParser
from datetime import datetime
from zoneinfo import ZoneInfo
import logging

logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)

parser = ArgumentParser()
parser = args.args(parser)
base = args.base_url(parser)
url = f"{base}/event"
data_path = args.data_path(parser)


prefix = "Season 15"
start = "08/25/2025"
end = "11/30/2025"
startstamp = (
    datetime.strptime(start, "%m/%d/%Y").replace(tzinfo=ZoneInfo("UTC")).isoformat()
)
endstamp = (
    datetime.strptime(end, "%m/%d/%Y").replace(tzinfo=ZoneInfo("UTC")).isoformat()
)

with open(f"{data_path}/events.json") as f:
    events = json.load(f)
    for e in events:
        name = f"{prefix} {e}"
        logger.info(f"Creating event '{name}'...")
        if dennys.create_event(url, name, startstamp, endstamp) is None:
            print(f"Failed to create event '{e}'.")
