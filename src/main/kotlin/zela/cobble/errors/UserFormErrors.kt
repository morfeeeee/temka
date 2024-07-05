package zela.cobble.errors

import zela.cobble.storages.UserStorage


class UserFormErrors(val login: String?, val password: String?, val confirmPassword: String?,val storage: UserStorage) {



    fun containsOnlyLatinLettersAndDigits(name: String): Boolean {
        val regex = Regex("[a-zA-Z0-9]+( [a-zA-Z0-9]+)*")

        return regex.matches(name)
    }

    fun getListErrorsLogin(value: String?): List<String>{
        val listErrors = mutableListOf<String>()
        if(value == null || value == ""){
            listErrors.add("Логин должен быть непустой строкой")
        }
        else{
            if(!containsOnlyLatinLettersAndDigits(value)){
                listErrors.add("Логин должен содержать только латинские буквы(возможно вместе с цифрами)")
            }
            if(value in storage.listNames()){
                listErrors.add("Данный логин уже занят.")
            }
        }
        return listErrors
    }

    fun getListErrorsPassword(value: String?, value1: String?): List<String>{
        val listErrors = mutableListOf<String>()
        if(value == null || value == ""){
            listErrors.add("Пароль должен быть непустой строкой")
        }
        else{
            if(!confirm(value,value1)){
                listErrors.add("Пароли должны совпадать")
            }

        }
        return listErrors
    }

    fun confirm(value: String?, value1: String?): Boolean{
        return value == value1
    }

    fun nullToEmpty(value: String?): String{
        if(value == null){
            return ""
        }
        return value
    }

    fun getFormErrors(): GetErrors {
        val formErrors = mutableMapOf<String, List<String>>()
        formErrors[nullToEmpty(login)] = getListErrorsLogin(login)
        formErrors[nullToEmpty(password)] = getListErrorsPassword(password,confirmPassword)
        return GetErrors(formErrors)
    }
}