package com.julian.miju2.domain.usecase

data class ParsedDocument(
    val documentId: String = "",
    val fullName: String = ""
)

class ParseDocumentTextUseCase {

    operator fun invoke(rawText: String): ParsedDocument {
        if (rawText.isBlank()) return ParsedDocument()

        val lines = rawText
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotBlank() }

        return ParsedDocument(
            documentId = extractDocumentId(rawText),
            fullName = extractFullName(lines)
        )
    }

    private fun extractDocumentId(rawText: String): String {
        val normalized = rawText.replace(Regex("(?<=\\d)[.,\\s](?=\\d)"), "")
        return Regex("\\d+")
            .findAll(normalized)
            .map { it.value }
            .maxByOrNull { it.length }
            ?: ""
    }

    private fun extractFullName(lines: List<String>): String {
        val letterLine = Regex("^[\\p{L} .'-]+$")
        return lines
            .filter { it.matches(letterLine) }
            .filter { it.trim().split(Regex("\\s+")).size >= 2 }
            .filter { it.length in 5..60 }
            .maxByOrNull { it.length }
            ?.uppercase()
            ?.trim()
            ?: ""
    }
}
