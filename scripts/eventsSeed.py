#!/usr/bin/env python3
# pyright: basic

import url
import requests
import json
from argparse import ArgumentParser
from datetime import datetime
from zoneinfo import ZoneInfo

from requests.models import HTTPError

parser = ArgumentParser()
parser = url.args(parser)
base = url.base(parser)
url = f"{base}/event"

prefix = "Season 15"
events = [
    "Economy",
    "Commercial",
    "Tax Haven",
    "Financial",
    "Audit",
    "Executive",
    "CEO",
    "LCS",
]
start = "08/25/2025"
end = "11/30/2025"
startstamp = (
    datetime.strptime(start, "%m/%d/%Y").replace(tzinfo=ZoneInfo("UTC")).isoformat()
)
endstamp = (
    datetime.strptime(end, "%m/%d/%Y").replace(tzinfo=ZoneInfo("UTC")).isoformat()
)

for e in events:
    name = f"{prefix} {e}"
    payload = {
        "name": name,
        "description": "Welcome to Season 15!",
        "startDate": startstamp,
        "endDate": endstamp,
        "status": "ACTIVE",
    }
    res = requests.post(
        url, headers={"Content-Type": "application/json"}, data=json.dumps(payload)
    )
    try:
        res.raise_for_status()
    except HTTPError as e:
        print(f"Failed to create event: {payload}")
        print(e)
