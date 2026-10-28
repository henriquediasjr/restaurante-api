package br.pucpr.authserver.products

import br.pucpr.authserver.categories.Category
import br.pucpr.authserver.categories.CategoryRepository
import br.pucpr.authserver.tags.Tag
import br.pucpr.authserver.tags.TagRepository
import br.pucpr.authserver.users.SortDir
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.util.Optional

class ProductServiceTest {
    private val repository = mockk<ProductRepository>()
    private val categoryRepository = mockk<CategoryRepository>()
    private val tagRepository = mockk<TagRepository>()

    private val service = ProductService(repository, categoryRepository, tagRepository)

    private val category = Category(1L, "Pratos", "Principais")

    private fun oneProduct(id: Long? = 1L) =
        Product(id, "Risoto", "Cogumelos", BigDecimal("42.50"), category)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `insert fails with NOT_FOUND when the category does not exist`() {
        every { categoryRepository.findById(9L) } returns Optional.empty()

        val ex = shouldThrow<ResponseStatusException> {
            service.insert(ProductRequest("Risoto", "Cogumelos", BigDecimal("42.50"), 9L))
        }

        ex.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    @Test
    fun `insert saves the product with its category`() {
        every { categoryRepository.findById(1L) } returns Optional.of(category)
        every { repository.save(any()) } answers { firstArg<Product>().also { it.id = 5L } }

        val result = service.insert(ProductRequest("Risoto", "Cogumelos", BigDecimal("42.50"), 1L))

        result.id shouldBe 5L
        result.category shouldBe category
    }

    @Test
    fun `search rejects an invalid sort field`() {
        val ex = shouldThrow<ResponseStatusException> {
            service.search(null, null, null, null, null, "color", SortDir.ASC)
        }

        ex.statusCode shouldBe HttpStatus.BAD_REQUEST
    }

    @Test
    fun `search sorts descending and lowercases the tag`() {
        every { repository.search(null, null, null, null, "vegano", Sort.by("price").descending()) } returns
                listOf(oneProduct())

        val result = service.search(null, null, null, null, "VEGANO", "price", SortDir.DESC)

        result.size shouldBe 1
    }

    @Test
    fun `addTag returns false when the product already has the tag`() {
        val product = oneProduct().also { it.tags.add(Tag(1L, "vegano")) }
        every { repository.findById(1L) } returns Optional.of(product)

        service.addTag(1L, "Vegano") shouldBe false

        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `addTag fails with NOT_FOUND for an unknown tag`() {
        every { repository.findById(1L) } returns Optional.of(oneProduct())
        every { tagRepository.findByName("vegano") } returns null

        val ex = shouldThrow<ResponseStatusException> { service.addTag(1L, "vegano") }

        ex.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    @Test
    fun `removeTag returns true and saves when the tag was present`() {
        val product = oneProduct().also { it.tags.add(Tag(1L, "vegano")) }
        every { repository.findById(1L) } returns Optional.of(product)
        every { repository.save(product) } returns product

        service.removeTag(1L, "vegano") shouldBe true

        product.tags.size shouldBe 0
    }
}
