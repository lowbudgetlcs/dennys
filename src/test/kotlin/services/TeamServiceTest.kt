package services

import com.lowbudgetlcs.domain.models.team.*
import com.lowbudgetlcs.domain.services.team.TeamService
import com.lowbudgetlcs.repositories.team.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class TeamServiceTest :
    StringSpec({

        val repo = mockk<ITeamRepository>(relaxed = false)
        val service = TeamService(repo)

        beforeTest { clearAllMocks() }

        "createTeam succeeds for valid input" {
            val newTeam = NewTeam(TeamName("Golden Guardians"), null)
            val created =
                Team(
                    id = TeamId(1),
                    name = TeamName("Golden Guardians"),
                    logoName = null,
                    eventId = null,
                )

            every { repo.insert(newTeam) } returns created

            service.createTeam(newTeam) shouldBe created

            verify(exactly = 1) { repo.insert(newTeam) }
        }

        "createTeam fails for blank name" {
            shouldThrow<IllegalArgumentException> {
                service.createTeam(NewTeam(TeamName(""), null))
            }
            // repo.insert should never be called
            verify(exactly = 0) { repo.insert(any()) }
        }

        "createTeam fails for too-long name" {
            shouldThrow<IllegalArgumentException> {
                service.createTeam(NewTeam(TeamName("x".repeat(81)), null))
            }
            verify(exactly = 0) { repo.insert(any()) }
        }

        "getAllTeams returns repo data" {
            val t1 = Team(TeamId(1), TeamName("A"), null, null)
            val t2 = Team(TeamId(2), TeamName("B"), TeamLogoName("b.png"), null)

            every { repo.getAll() } returns listOf(t1, t2)

            val result = service.getAllTeams()
            result.map { it.id } shouldContainExactly listOf(t1.id, t2.id)

            verify(exactly = 1) { repo.getAll() }
        }

        "getTeam throws for unknown id" {
            val id = TeamId(999)
            every { repo.getById(id) } returns null

            val ex = shouldThrow<NoSuchElementException> { service.getTeam(id) }
            ex.message shouldBe "Team not found"

            verify(exactly = 1) { repo.getById(id) }
        }

        "renameTeam updates name" {
            val id = TeamId(5)
            val updated = Team(id, TeamName("New"), null, null)

            every { repo.updateTeamName(id, TeamName("New")) } returns updated

            service.renameTeam(id, "New") shouldBe updated

            verify(exactly = 1) { repo.updateTeamName(id, TeamName("New")) }
        }

        "renameTeam rejects blank and overlong names" {
            val id = TeamId(3)

            shouldThrow<IllegalArgumentException> { service.renameTeam(id, "") }
            shouldThrow<IllegalArgumentException> { service.renameTeam(id, "x".repeat(81)) }

            verify(exactly = 0) { repo.updateTeamName(any(), any()) }
        }

        "updateLogoName sets logo when non-null" {
            val id = TeamId(7)
            val updated = Team(id, TeamName("Logo Team"), TeamLogoName("logo.png"), null)

            every { repo.updateTeamLogoName(id, TeamLogoName("logo.png")) } returns updated

            service.updateLogoName(id, "logo.png") shouldBe updated

            verify(exactly = 1) { repo.updateTeamLogoName(id, TeamLogoName("logo.png")) }
        }

        "updateLogoName rejects null" {
            val id = TeamId(8)
            shouldThrow<IllegalArgumentException> { service.updateLogoName(id, null) }
            verify(exactly = 0) { repo.updateTeamLogoName(any(), any()) }
        }
    })
