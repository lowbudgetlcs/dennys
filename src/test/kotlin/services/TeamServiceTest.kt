package services

import com.lowbudgetlcs.domain.models.team.NewTeam
import com.lowbudgetlcs.domain.models.team.Team
import com.lowbudgetlcs.domain.models.team.TeamId
import com.lowbudgetlcs.domain.models.team.TeamLogoName
import com.lowbudgetlcs.domain.models.team.TeamName
import com.lowbudgetlcs.domain.models.team.TeamUpdate
import com.lowbudgetlcs.domain.models.team.toTeamLogoName
import com.lowbudgetlcs.domain.models.team.toTeamName
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

        "update() correctly updates name" {
            val id = TeamId(5)
            val original = Team(id, TeamName("Original"), null, null)
            val updated = original.copy(name = "New".toTeamName())

            every { repo.getById(id) } returns original
            every { repo.update(any(), any()) } returns updated

            service.patchTeam(id, TeamUpdate(name = "New".toTeamName())) shouldBe updated

            verify(exactly = 1) { repo.update(any(), any()) }
        }

        "update correctly sets logo name" {
            val id = TeamId(7)
            val original = Team(id, "Original".toTeamName(), null, null)
            val updated = original.copy(logoName = "Logo".toTeamLogoName())

            every { repo.getById(id) } returns original
            every { repo.update(any(), any()) } returns updated

            service.patchTeam(id, TeamUpdate(logoName = "logo.png".toTeamLogoName())) shouldBe updated

            verify(exactly = 1) { repo.update(any(), any()) }
        }
    })
