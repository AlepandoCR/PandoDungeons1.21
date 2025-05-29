package tcg.util.text

object TextUtils {
    fun replaceUnderscoresWithSpaces(input: String): String {
        return input.replace('_', ' ')
    }

    fun capitalizeWords(input: String): String {
        return input
            .split(" ")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }

    fun reFactorizeCardId(input: String): String{
        var r = replaceUnderscoresWithSpaces(input)

        r = capitalizeWords(r)

        return r
    }

}