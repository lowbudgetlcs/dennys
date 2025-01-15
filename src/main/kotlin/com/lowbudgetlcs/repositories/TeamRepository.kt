package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import migrations.Players
import migrations.Teams

interface TeamRepository : Repository<Teams, Int> {
}