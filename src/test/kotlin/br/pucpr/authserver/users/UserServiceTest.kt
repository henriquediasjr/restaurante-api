package br.pucpr.authserver.users

import br.pucpr.authserver.roles.RoleRepository
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserServiceTest {
    private val repository = mockk<UserRepository>()
    private val roleRepository = mockk<RoleRepository>()

    private val service = UserService(repository, roleRepository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(repository, roleRepository)
    }

    @Test
    fun `insert should save and return the saved user`() {
        val user = oneUser(id = null)
        val saved = oneUser(id = 1)
        every { repository.findByEmail(user.email) } returns null
        every { repository.save(user) } returns saved

        val result = service.insert(user)

        verify(exactly = 1) { repository.findByEmail(user.email) }
        result shouldBe saved
    }
}