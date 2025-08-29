import requests
import json
# pyright: basic
# TODO: Stronger typing, less any.
# TODO: Bind the url to the module instead of passinge to every function.

HEADERS = {"Content-Type": "application/json"}


def getEvents(url: str):
    try:
        res = requests.get(f"{url}/event", headers=HEADERS)
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def getTeams(url: str):
    try:
        res = requests.get(f"{url}/team", headers=HEADERS)
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def getEventWithTeams(url: str, eventId):
    try:
        res = requests.get(f"{url}/event/{eventId}/teams", headers=HEADERS)
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def createEvent(url, name, start, end):
    payload = {
        "name": name,
        "description": "Welcome to Season 15!",
        "startDate": start,
        "endDate": end,
        "status": "ACTIVE",
    }
    try:
        res = requests.post(
            url,
            headers=HEADERS,
            data=json.dumps(payload),
        )
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def createTeam(url, name):
    payload = {"name": name}
    try:
        res = requests.post(
            f"{url}",
            headers=HEADERS,
            data=json.dumps(payload),
        )
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def createSeries(url, eventId, lhsTeamId, rhsTeamId, gamesToWin):
    payload = {"team1Id": lhsTeamId, "team2Id": rhsTeamId, "gamesToWin": gamesToWin}
    try:
        res = requests.post(
            f"{url}/event/{eventId}/series",
            headers=HEADERS,
            data=json.dumps(payload),
        )
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def addTeamToEvent(url: str, eventId: int, teamId: int):
    payload = {"teamId": teamId}
    try:
        res = requests.post(
            f"{url}/event/{eventId}/teams",
            headers=HEADERS,
            data=json.dumps(payload),
        )
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def findEventId(name, events):
    e = [x["id"] for x in events if x["name"] == name]
    count = len(e)
    if count == 0:
        return None
    else:
        return e[0]


def findTeamId(name, teams):
    t = [x["id"] for x in teams if x["name"] == name]
    count = len(t)
    if count == 0:
        print(f"No team matched {name}")
        return None
    else:
        return t[0]
