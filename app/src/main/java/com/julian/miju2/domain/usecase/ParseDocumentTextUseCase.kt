package com.julian.miju2.domain.usecase

import java.text.Normalizer

data class ParsedDocument(
    val documentId: String = "",
    val fullName: String = ""
)

class ParseDocumentTextUseCase {

    // Palabras que aparecen en la cédula pero que NO son parte del nombre.
    private val noiseWords = setOf(
        "REPUBLICA", "COLOMBIA", "IDENTIFICACION", "PERSONAL", "CEDULA",
        "CIUDADANIA", "NUMERO", "FIRMA", "APELLIDOS", "NOMBRES", "NOMBRE",
        "FECHA", "NACIMIENTO", "LUGAR", "ESTATURA", "SEXO", "EXPEDICION",
        "REGISTRADOR", "NACIONAL", "NUIP", "NACIONALIDAD", "INDICE", "DERECHO"
    )

    operator fun invoke(rawText: String): ParsedDocument {
        if (rawText.isBlank()) return ParsedDocument()

        val lines = rawText.split('\n')
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
        val iAp = lines.indexOfFirst { normalize(it).contains("APELLIDOS") }
        val iNom = lines.indexOfFirst { normalize(it).contains("NOMBRES") }
        if (iAp == -1 || iNom == -1) return ""   // Que el usuario lo escriba

        val claimed = mutableSetOf<Int>()
        val apellidos = valueForLabel(lines, iAp, otherLabel = iNom, claimed)
        val nombres = valueForLabel(lines, iNom, otherLabel = iAp, claimed)

        return listOf(nombres, apellidos)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .trim()
    }

    private fun valueForLabel(
        lines: List<String>,
        index: Int,
        otherLabel: Int,
        claimed: MutableSet<Int>
    ): String {
        // 1) misma línea: quitar la etiqueta y ver si queda un nombre
        val sameLine = stripLabels(lines[index])
        if (isNameLine(sameLine)) {
            claimed.add(index)
            return sameLine.uppercase()
        }

        val preferBefore = otherLabel > index
        val order = if (preferBefore) listOf(index - 1, index + 1) else listOf(index + 1, index - 1)

        for (i in order) {
            if (i in claimed) continue
            val line = lines.getOrNull(i) ?: continue
            if (isNameLine(line)) {
                claimed.add(i)
                return line.uppercase()
            }
        }
        return ""
    }

    private fun isNameLine(line: String): Boolean {
        val clean = line.trim()
        if (clean.length !in 2..40) return false
        if (!clean.matches(Regex("^[\\p{L} .'-]+$"))) return false  // solo letras/espacios
        // Descarta si TODAS sus palabras son ruido (encabezados/etiquetas)
        val tokens = normalize(clean).split(Regex("\\s+"))
        return tokens.any { it.isNotBlank() && it !in noiseWords }
    }

    private fun stripLabels(line: String): String =
        line.replace(Regex("(?i)apellidos|nombres"), "").trim()

    private fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .uppercase()
}
