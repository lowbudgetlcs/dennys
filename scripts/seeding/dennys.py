import requests
import json
# TODO: Stronger typing, less any.
# TODO: Bind the url to the module instead of passing to every function.

HEADERS = {"Content-Type": "application/json"}


def get_events(url: str):
    try:
        res = requests.get(f"{url}/event", headers=HEADERS)
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def get_teams(url: str):
    try:
        res = requests.get(f"{url}/team", headers=HEADERS)
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def get_event_with_teams(url: str, event_id: int):
    try:
        res = requests.get(f"{url}/event/{event_id}/teams", headers=HEADERS)
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def create_event(url: str, name: str, start: str, end: str):
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


def create_team(url: str, name: str):
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


def create_series(
    url: str, event_id: int, team_ids: tuple[int, int], games_to_win: int
):
    team1, team2 = team_ids
    payload = {"team1Id": team1, "team2Id": team2, "gamesToWin": games_to_win}
    try:
        res = requests.post(
            f"{url}/event/{event_id}/series",
            headers=HEADERS,
            data=json.dumps(payload),
        )
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def add_team_to_event(url: str, event_id: int, team_id: int):
    payload = {"teamId": team_id}
    try:
        res = requests.post(
            f"{url}/event/{event_id}/teams",
            headers=HEADERS,
            data=json.dumps(payload),
        )
        res.raise_for_status()
        return res.json()
    except requests.HTTPError:
        return None


def find_event_id(name: str, events):
    e = [x["id"] for x in events if x["name"] == name]
    count = len(e)
    if count == 0:
        return None
    else:
        return e[0]


def find_team_id(name: str, teams):
    t = [x["id"] for x in teams if x["name"] == name]
    count = len(t)
    if count == 0:
        print(f"No team matched {name}")
        return None
    else:
        return t[0]
