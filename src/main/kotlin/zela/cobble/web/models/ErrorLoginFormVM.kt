package zela.cobble.web.models

import org.http4k.template.ViewModel

class ErrorLoginFormVM(val login: String, val password: String, val error: String): ViewModel {
}