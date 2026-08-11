package com.lowbudgetlcs.gateways.riot.tournament

enum class RiotRegion(
    val platformId: String,
) {
    BR("BR1"),
    EUNE("EUN1"),
    EUW("EUW1"),
    JP("JP1"),
    LAN("LA1"),
    LAS("LA2"),
    NA("NA1"),
    OCE("OC1"),
    PBE("PBE1"),
    RU("RU"),
    TR("TR1"),
    KR("KR"),
    ;

    companion object {
        fun platformIdOf(region: String): String? =
            entries.firstOrNull { it.name.equals(region, ignoreCase = true) }?.platformId
    }
}
