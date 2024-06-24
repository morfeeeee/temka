package zela.cobble.storages

import zela.cobble.domain.User


class UserStorage(val users: MutableList<User>) {


    fun deleteUserByNameLog(username: String): Boolean {
        val iterator = users.iterator()
        while (iterator.hasNext()) {
            val user = iterator.next()
            if (user.nameLog == username) {
                iterator.remove()
                return true
            }
        }
        return false
    }

    fun updateUserByUsername(username: String, updatedUser: User): Boolean {
        for (i in users.indices) {
            if (users[i].nameLog == username) {
                users[i] = updatedUser
                return true
            }
        }
        return false
    }




    fun getListUsers(): List<User>{
        val list = mutableListOf<User>()
        for(element in users){
            list.add(element)
        }
        return list
    }

    fun addUser(user: User) {
        users.add(user)
    }

    fun findUser(username: String): User? {
        return users.find { it.nameLog == username }
    }
    fun listNames(): List<String>{
        val names = mutableListOf<String>()
        for(element in users){
            names.add(element.nameLog)
        }
        return names
    }
}