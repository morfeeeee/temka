package zela.cobble.web.models


import org.http4k.template.ViewModel

class ErrorUserFormVM(val login: String,val password: String,val confirmPassword: String, val error: List<String>): ViewModel
