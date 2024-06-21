package zela.cobble.templates

import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.FileLoader
import org.http4k.template.ViewModel
import java.io.StringWriter

typealias ContextAwareTemplateRenderer = (Map<String, Any?>, ViewModel) -> String

class ContextAwarePebbleTemplates(
    private val configure: (PebbleEngine.Builder) -> PebbleEngine.Builder = { it }
) {

    private class ContextAwarePebbleTemplateRenderer(
        private val engine: PebbleEngine,
    ) : ContextAwareTemplateRenderer {
        override fun invoke(context: Map<String, Any?>, viewModel: ViewModel): String {
            val writer = StringWriter()
            val wholeContext = context + mapOf("model" to viewModel)
            engine.getTemplate(viewModel.template() + ".peb").evaluate(writer, wholeContext)
            return writer.toString()
        }
    }


    fun HotReload(baseTemplateDir: String): ContextAwareTemplateRenderer {
        val loader = FileLoader()
        loader.prefix = baseTemplateDir
        return ContextAwarePebbleTemplateRenderer(
            configure(
                PebbleEngine.Builder().cacheActive(false).loader(loader),
            ).build()
        )
    }
}
