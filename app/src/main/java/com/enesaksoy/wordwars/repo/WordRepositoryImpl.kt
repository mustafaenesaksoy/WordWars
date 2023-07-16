package com.enesaksoy.wordwars.repo

import com.enesaksoy.wordwars.model.KeyResult
import com.enesaksoy.wordwars.model.WordResult
import com.enesaksoy.wordwars.service.KeyAPI
import com.enesaksoy.wordwars.service.WordAPI
import com.enesaksoy.wordwars.util.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.tasks.await

class WordRepositoryImpl(private val keyApi: KeyAPI,
                         private val wordAPI: WordAPI,
                         private val auth: FirebaseAuth,
                         private val firestore: FirebaseFirestore
                         ): WordRepository {

    override suspend fun getWord(): Resource<WordResult> {
        return try {
            val response = wordAPI.getWord()
            if(response.isSuccessful){
                response.body()?.let {
                    return@let Resource.success(it)
                }?: Resource.error("Error!",null)
            }else{
                Resource.error("No data!",null)
            }
        }catch (e: Exception){
            Resource.error(e.localizedMessage,null)
        }
    }

    override suspend fun getWordList(query: String): Resource<KeyResult> {
        return try {
            val response = keyApi.getWordList(text = query)
            if(response.isSuccessful){
                response.body()?.let {
                    return@let Resource.success(it)
                }?: Resource.error("Error!",null)
            }else{
                Resource.error("No data!",null)
            }
        }catch (e: Exception){
            Resource.error(e.localizedMessage,null)
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<AuthResult> {
        if (email.equals("") || password.equals("")) {
            return Resource.error("Email and password cannot be left blank.", null)
        }
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.success(result)
        } catch (e: Exception) {
            Resource.error("User Error", null)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        userName: String
    ): Resource<AuthResult> {
        if (email.equals("") || password.equals("") || userName.equals("")) {
            return Resource.error("Username,Email and password cannot be left blank.", null)
        }
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            auth.currentUser.let { user ->
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .build()
                user?.updateProfile(profileUpdate)?.await()
                val userMap = HashMap<String, Any>()
                userMap.put("name", userName)
                userMap.put("email", email)
                userMap.put("isActive", false)
                userMap.put("competitions", listOf(""))
               firestore.collection("Users").add(userMap).await()
                return@let Resource.success(result)
            }
        } catch (e: Exception) {
            Resource.error("User Error", null)
        }
    }

    override suspend fun updateIsActive() : Boolean{
        val usersRef = firestore.collection("Users")
        val query = usersRef.whereEqualTo("email",auth.currentUser?.email)
        val tasks = query.get().await()
        for(task in tasks.documents){
            if (task.get("isActive") == true){
                usersRef.document(task.id).update(hashMapOf<String, Any>("isActive" to false))
                return false
            }else{
                usersRef.document(task.id).update(hashMapOf<String, Any>("isActive" to true))
                return true
            }
        }
        return false
    }

    override suspend fun addActiveUsers(isActive: Boolean) : Resource<Int>{
        return try {
            if (isActive) {
                val auReference = firestore.collection("Active Users").get().await()
                val count = auReference.size()
                val userMap = HashMap<String, Any>()
                userMap.put("name", auth.currentUser!!.displayName!!)
                userMap.put("email", auth.currentUser!!.email!!)
                userMap.put("number", count + 1)
                firestore.collection("Active Users").add(userMap).await()
                return Resource.success(count + 1)
            }else{
                val actUser = firestore.collection("Active Users")
                val query = actUser.whereEqualTo("email", auth.currentUser?.email)
                val tasks = query.get().await()
                for (task in tasks.documents){
                    actUser.document(task.id).delete().await()
                }
                return Resource.success(0)
            }
        }catch (e : Exception){
            Resource.error(e.localizedMessage,null)
        }
    }

    override suspend fun makeMatch(number: Int,word: String): Resource<Pair<Boolean, String>> {
        return try {
            val competItem = HashMap<String, Any>()
            val auReference = firestore.collection("Active Users")
            val query = auReference.whereEqualTo("number", number + 1)
            val tasks = query.get().await()
            competItem.put("Word", word)
            competItem.put("Home Name", auth.currentUser!!.displayName!!)
            competItem.put("Home Email", auth.currentUser!!.email!!)
            competItem.put("Home Answer", "")
            competItem.put("Away Answer", "")
            if (!tasks.isEmpty){
                for (task in tasks.documents){
                    competItem.put("Away Name", task.get("name")!!)
                    competItem.put("Away Email", task.get("email")!!)
                    firestore.collection("Competitions").add(competItem).await()
                    return Resource.success(Pair(true, task.get("name")!!.toString()))
                }
            }else{
               return Resource.success(Pair(false, ""))
            }
            Resource.success(Pair(false, ""))
        }catch (e : Exception){
            Resource.error(e.localizedMessage,null)
        }
    }

    override suspend fun navigateCompet(number: Int): Resource<Pair<String, String>> {
        return try {
            val competReference = firestore.collection("Competitions")
            val query = competReference.whereEqualTo("Away Email", auth.currentUser!!.email)
            val tasks = query.get().await()
            if (!tasks.isEmpty) {
                for (task in tasks.documents) {
                    return Resource.success(Pair(task.get("Word").toString(), task.get("Home Name").toString()))
                }
            }
            Resource.success(Pair("", ""))
            navigateCompet(number)
        }catch (e: Exception){
            Resource.error(e.localizedMessage,null)
        }
    }

    override suspend fun addAnswerFStore(answer: String, isTruth: Boolean,isHome: Boolean) {
        try {
            val competReference = firestore.collection("Competitions")
            val isTruthStr = isTruth.toString()

            if (isHome){
                val answerMap = hashMapOf<String, Any>("Home Answer" to answer+" ${isTruthStr}")
                val query = competReference.whereEqualTo("Home Email", auth.currentUser!!.email)
                val tasks = query.get().await()
                if (!tasks.isEmpty) {
                    for (task in tasks.documents) {
                        competReference.document(task.id).update(answerMap).await()
                    }
                }
            }else{
                val answerMap = hashMapOf<String, Any>("Away Answer" to answer+" ${isTruthStr}")
                val query = competReference.whereEqualTo("Away Email", auth.currentUser!!.email)
                val tasks = query.get().await()
                if (!tasks.isEmpty) {
                    for (task in tasks.documents) {
                        competReference.document(task.id).update(answerMap).await()
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun getAnswerFStore(isHome: Boolean): Resource<String> {
        return try {
            val competReference = firestore.collection("Competitions")
            if (isHome){
                val query = competReference.whereEqualTo("Home Email", auth.currentUser!!.email)
                val tasks = query.get().await()
                if (!tasks.isEmpty) {
                    for (task in tasks.documents) {
                        return Resource.success(task.get("Away Answer").toString())
                    }
                }
                Resource.success("")
            }else{
                val query = competReference.whereEqualTo("Away Email", auth.currentUser!!.email)
                val tasks = query.get().await()
                if (!tasks.isEmpty) {
                    for (task in tasks.documents) {
                        return Resource.success(task.get("Home Answer").toString())
                    }
                }
                Resource.success("")
            }
        }catch (e : Exception){
            Resource.error(e.localizedMessage, null)
        }
    }

    override suspend fun deleteAnswerFStore(isHome: Boolean) {
        try{
            val competReference = firestore.collection("Competitions")

            if (isHome){
                val updateMap = hashMapOf<String, Any>("Away Answer" to "")
                val query = competReference.whereEqualTo("Home Email", auth.currentUser!!.email)
                val tasks = query.get().await()
                if (!tasks.isEmpty) {
                    for (task in tasks.documents) {
                        competReference.document(task.id).update(updateMap).await()
                    }
                }
            }else{
                val updateMap = hashMapOf<String, Any>("Home Answer" to "")
                val query = competReference.whereEqualTo("Away Email", auth.currentUser!!.email)
                val tasks = query.get().await()
                if (!tasks.isEmpty) {
                    for (task in tasks.documents) {
                        competReference.document(task.id).update(updateMap).await()
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun addCompettoUser(word: String, isWon: String) {
        try {
            val newElement = "$word $isWon"
            val userRef = firestore.collection("Users")
            val query = userRef.whereEqualTo("email", auth.currentUser!!.email)
            val tasks = query.get().await()
            if (!tasks.isEmpty) {
                for (task in tasks.documents) {
                    userRef.document(task.id).update("competitions", FieldValue.arrayUnion(newElement)).await()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun deleteCompet() {
        try {
            val actUser = firestore.collection("Competitions")
            val query = actUser.whereEqualTo("Home Email", auth.currentUser?.email)
            val tasks = query.get().await()
            for (task in tasks.documents){
                actUser.document(task.id).delete().await()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun getUserCompetitions(): Resource<List<String>> {
        return try {
            val userRef = firestore.collection("Users")
            val query = userRef.whereEqualTo("email", auth.currentUser!!.email)
            val tasks = query.get().await()
            if (!tasks.isEmpty) {
                for (task in tasks.documents) {
                    return Resource.success(task.get("competitions") as List<String>)
                }
            }
            Resource.success(listOf(""))
        }catch (e: Exception){
            Resource.error(e.localizedMessage, null)
        }
    }
}