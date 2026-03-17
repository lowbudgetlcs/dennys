package services

import com.lowbudgetlcs.domain.team.TeamService
import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.team.models.NewTeam
import com.lowbudgetlcs.domain.team.models.Team
import com.lowbudgetlcs.domain.team.models.TeamUpdate
import com.lowbudgetlcs.domain.team.models.toTeamId
import com.lowbudgetlcs.domain.team.models.toTeamName
import com.lowbudgetlcs.domain.team.models.types.TeamId
import com.lowbudgetlcs.domain.team.models.types.TeamName
import com.lowbudgetlcs.repositories.player.IPlayerRepository
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

        val teamRepo = mockk<ITeamRepository>(relaxed = false)
        val playerRepo = mockk<IPlayerRepository>(relaxed = false)
        val service = TeamService(teamRepo, playerRepo)

        beforeTest { clearAllMocks() }

        "createTeam succeeds for valid input" {
            val newTeam = NewTeam(TeamName("Golden Guardians"), null)
            val created =
                Team(
                    id = TeamId(1),
                    name = TeamName("Golden Guardians"),
                    logo = null,
                    eventId = null,
                )

            every { teamRepo.insert(newTeam) } returns created

            service.createTeam(newTeam) shouldBe created

            verify(exactly = 1) { teamRepo.insert(newTeam) }
        }

        "createTeam fails for blank name" {
            shouldThrow<IllegalArgumentException> {
                service.createTeam(NewTeam(TeamName(""), null))
            }
            // teamRepo.insert should never be called
            verify(exactly = 0) { teamRepo.insert(any()) }
        }

        "createTeam fails for too-long name" {
            shouldThrow<IllegalArgumentException> {
                service.createTeam(NewTeam(TeamName("x".repeat(81)), null))
            }
            verify(exactly = 0) { teamRepo.insert(any()) }
        }

        "getAllTeams returns teamRepo data" {
            val t1 = Team(TeamId(1), TeamName("A"), null, null)
            val t2 = Team(TeamId(2), TeamName("B"), "b.png", null)

            every { teamRepo.getAll() } returns listOf(t1, t2)

            val result = service.getAllTeams()
            result.map { it.id } shouldContainExactly listOf(t1.id, t2.id)

            verify(exactly = 1) { teamRepo.getAll() }
        }

        "getTeam throws for unknown id" {
            val id = TeamId(999)
            every { teamRepo.getById(id) } returns null

            val ex = shouldThrow<NoSuchElementException> { service.getTeam(id) }
            ex.message shouldBe "Team not found"

            verify(exactly = 1) { teamRepo.getById(id) }
        }

        "update() correctly updates name" {
            val id = 5.toTeamId()
            val newName = "New".toTeamName()
            val original = Team(id, "Original".toTeamName(), null, null)
            val updated = original.copy(name = newName)

            every { teamRepo.getById(id) } returns original
            every { teamRepo.getByName(newName)} returns listOf()
            every { teamRepo.update(any(), any()) } returns updated

            service.patchTeam(id, TeamUpdate(name = "New".toTeamName())) shouldBe updated

            verify(exactly = 1) { teamRepo.update(any(), any()) }
        }

        "update() correctly sets logo name" {
            val id = TeamId(7)
            val original = Team(id, "Original".toTeamName(), null, null)
            val updated = original.copy(logo = "Logo")

            every { teamRepo.getById(id) } returns original
            every { teamRepo.update(any(), any()) } returns updated

            service.patchTeam(id, TeamUpdate(logo = PatchField.Value("logo.png"))) shouldBe updated

            verify(exactly = 1) { teamRepo.update(any(), any()) }
        }
    })
