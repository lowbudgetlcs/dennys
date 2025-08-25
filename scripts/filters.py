# pyright: basic


def getEventId(name, events):
    e = [x["id"] for x in events if x["name"] == name]
    count = len(e)
    if count > 1:
        print(f"More than one event matched {name}")
        print(e)
        return None
    elif count == 0:
        print(f"No event matched {name}")
        return None
    else:
        return e[0]


def getTeamId(name, teams):
    t = [x["id"] for x in teams if x["name"] == name]
    count = len(t)
    if count > 1:
        print(f"More than one team matched {name}")
        print(t)
        return None
    elif count == 0:
        print(f"No team matched {name}")
        return None
    else:
        return t[0]
