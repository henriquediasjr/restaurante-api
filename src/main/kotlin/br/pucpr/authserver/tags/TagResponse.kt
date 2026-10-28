package br.pucpr.authserver.tags

data class TagResponse(
    val id: Long,
    val name: String
) {
    constructor(tag: Tag) : this(id = tag.id!!, name = tag.name)
}
