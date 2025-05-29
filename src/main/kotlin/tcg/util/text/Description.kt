package tcg.util.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

open class Description {
    private val lines: MutableList<Component> = mutableListOf()
    private var currentLine: Component = Component.empty()

    fun addText(text: String): Description {
        currentLine = currentLine.append(Component.text(text))
        return this
    }

    fun addStyledText(
        text: String,
        color: TextColor? = null,
        decorations: Set<TextDecoration> = emptySet()
    ): Description {
        var component = Component.text(text)
        color?.let { component = component.color(it) }
        decorations.forEach { component = component.decorate(it) }
        currentLine = currentLine.append(component)
        return this
    }

    fun addNewLine(): Description {
        lines += currentLine
        currentLine = Component.empty()
        return this
    }

    fun build(): Component {
        if (currentLine != Component.empty()) {
            lines += currentLine
        }
        return Component.join(JoinConfiguration.newlines(), lines)
    }

    fun asLore(): List<Component> {
        if (currentLine != Component.empty()) {
            lines += currentLine
            currentLine = Component.empty()
        }
        return lines
    }
}
