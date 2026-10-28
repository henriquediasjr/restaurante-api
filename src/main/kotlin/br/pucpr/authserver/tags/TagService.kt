package br.pucpr.authserver.tags

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TagService(
    val repository: TagRepository
) {
    fun insert(tag: Tag): Tag {
        tag.name = tag.name.lowercase()
        if (repository.findByName(tag.name) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Tag ${tag.name} already exists")
        }
        return repository
            .save(tag)
            .also { log.info("Tag {} created!", it.name) }
    }

    fun findAll() = repository.findAll(Sort.by("name"))

    companion object {
        private val log = LoggerFactory.getLogger(TagService::class.java)
    }
}
