package br.pucpr.authserver.categories

import br.pucpr.authserver.products.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

class CategoryServiceTest {
    private val repository = mockk<CategoryRepository>()
    private val productRepository = mockk<ProductRepository>()

    private val service = CategoryService(repository, productRepository)

    private val category = Category(1L, "Pratos", "Principais")

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `insert fails with CONFLICT when the name already exists`() {
        every { repository.findByName("Pratos") } returns category

        val ex = shouldThrow<ResponseStatusException> {
            service.insert(Category(null, "Pratos", "Outra"))
        }

        ex.statusCode shouldBe HttpStatus.CONFLICT
    }

    @Test
    fun `deleteById fails with CONFLICT when the category still has products`() {
        every { repository.findById(1L) } returns Optional.of(category)
        every { productRepository.existsByCategoryId(1L) } returns true

        val ex = shouldThrow<ResponseStatusException> { service.deleteById(1L) }

        ex.statusCode shouldBe HttpStatus.CONFLICT
        verify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `deleteById removes a category without products`() {
        every { repository.findById(1L) } returns Optional.of(category)
        every { productRepository.existsByCategoryId(1L) } returns false
        every { repository.delete(category) } returns Unit

        service.deleteById(1L)

        verify(exactly = 1) { repository.delete(category) }
    }

    @Test
    fun `update fails with NOT_FOUND for an unknown id`() {
        every { repository.findById(7L) } returns Optional.empty()

        val ex = shouldThrow<ResponseStatusException> { service.update(7L, "Novo", "Desc") }

        ex.statusCode shouldBe HttpStatus.NOT_FOUND
    }
}
