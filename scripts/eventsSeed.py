#!/usr/bin/env python3
# pyright: basic

import args
import dennys
import json
from argparse import ArgumentParser
from datetime import datetime
from zoneinfo import ZoneInfo

parser = ArgumentParser()
parser = args.args(parser)
base = args.baseUrl(parser)
url = f"{base}/event"
dataPath = args.dataPath(parser)


prefix = "Season 15"
start = "08/25/2025"
end = "11/30/2025"
startstamp = (
    datetime.strptime(start, "%m/%d/%Y").replace(tzinfo=ZoneInfo("UTC")).isoformat()
)
endstamp = (
    datetime.strptime(end, "%m/%d/%Y").replace(tzinfo=ZoneInfo("UTC")).isoformat()
)

with open(f"{dataPath}/events.json") as f:
    events = json.load(f)
    for e in events:
        name = f"{prefix} {e}"
        if dennys.createEvent(url, name, startstamp, endstamp) is None:
            print(f"Failed to create event '{e}'.")
