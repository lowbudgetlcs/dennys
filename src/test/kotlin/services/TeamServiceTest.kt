package services

import com.lowbudgetlcs.domain.PatchField
import com.lowbudgetlcs.domain.player.core.port.IPlayerRepository
import com.lowbudgetlcs.domain.team.core.TeamService
import com.lowbudgetlcs.domain.team.core.model.NewTeam
import com.lowbudgetlcs.domain.team.core.model.Team
import com.lowbudgetlcs.domain.team.core.model.TeamUpdate
import com.lowbudgetlcs.domain.team.core.model.types.TeamId
import com.lowbudgetlcs.domain.team.core.model.types.TeamName
import com.lowbudgetlcs.domain.team.core.model.types.toTeamId
import com.lowbudgetlcs.domain.team.core.model.types.toTeamName
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify

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

            coEvery { teamRepo.insert(newTeam) } returns created

            service.createTeam(newTeam) shouldBe created

            coVerify(exactly = 1) { teamRepo.insert(newTeam) }
        }

        "createTeam fails for blank name" {
            shouldThrow<IllegalArgumentException> {
                service.createTeam(NewTeam(TeamName(""), null))
            }
            // teamRepo.insert should never be called
            coVerify(exactly = 0) { teamRepo.insert(any()) }
        }

        "createTeam fails for too-long name" {
            shouldThrow<IllegalArgumentException> {
                service.createTeam(NewTeam(TeamName("x".repeat(81)), null))
            }
            coVerify(exactly = 0) { teamRepo.insert(any()) }
        }

        "getAllTeams returns teamRepo data" {
            val t1 = Team(TeamId(1), TeamName("abcd"), null, null)
            val t2 = Team(TeamId(2), TeamName("cdas"), "b.png", null)

            coEvery { teamRepo.getAll() } returns listOf(t1, t2)

            val result = service.getAllTeams()
            result.map { it.id } shouldContainExactly listOf(t1.id, t2.id)

            coVerify(exactly = 1) { teamRepo.getAll() }
        }

        "getTeam throws for unknown id" {
            val id = TeamId(999)
            coEvery { teamRepo.getById(id) } returns null

            val ex = shouldThrow<NoSuchElementException> { service.getTeam(id) }
            ex.message shouldBe "Team not found"

            coVerify(exactly = 1) { teamRepo.getById(id) }
        }

        "update() correctly updates name" {
            val id = 5.toTeamId()
            val newName = "New".toTeamName()
            val original = Team(id, "Original".toTeamName(), null, null)
            val updated = original.copy(name = newName)

            coEvery { teamRepo.getById(id) } returns original
            coEvery { teamRepo.getByName(newName)} returns listOf()
            coEvery { teamRepo.update(any(), any()) } returns updated

            service.patchTeam(id, TeamUpdate(name = "New".toTeamName())) shouldBe updated
        }

        "update() correctly sets logo name" {
            val id = TeamId(7)
            val original = Team(id, "Original".toTeamName(), null, null)
            val updated = original.copy(logo = "Logo")

            coEvery { teamRepo.getById(id) } returns original
            coEvery { teamRepo.update(any(), any()) } returns updated

            service.patchTeam(id, TeamUpdate(logo = PatchField.Value("logo.png"))) shouldBe updated
        }
    })
