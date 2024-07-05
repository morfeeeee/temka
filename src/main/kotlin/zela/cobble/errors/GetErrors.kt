package zela.cobble.errors

class GetErrors(private val formErrors: Map<String, List<String>>) {

    fun isError(): Boolean{
        return getAllErrors().isNotEmpty()
    }

    fun getAllErrors(): List<String>{
        val listAllErrors = mutableListOf<String>()
        for(i in formErrors){
            listAllErrors += i.value
        }
        return listAllErrors.distinct()
    }




}