package com.testacc220.csd3156_mobilegameproject.android

import android.os.Bundle
import android.util.Log
import com.badlogic.gdx.Gdx

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.firebase.FirebaseApp
import com.testacc220.csd3156_mobilegameproject.MainKt
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.testacc220.csd3156_mobilegameproject.AndroidLauncherInterface
import kotlinx.coroutines.suspendCancellableCoroutine

import javax.net.ssl.SSLContext
import kotlin.coroutines.resume


/** Launches the Android application. */
class AndroidLauncher : AndroidApplication(), AndroidLauncherInterface {
    public var lastHighscore = 0
    public var currUsrname = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        SSLContext.getInstance("TLSv1.2").apply {
            init(null, null, null)
            SSLContext.setDefault(this)
        }
        //addUserOld()
        // Initialize Firebase

//        //testFirestore()
//        //readDatabase()
        initialize(MainKt(this), AndroidApplicationConfiguration().apply {
            useImmersiveMode = true // Recommended, but not required.
        })
        FirebaseApp.initializeApp(this)
    }

    //public var usrName = ""
    //public var passWrd = ""
    //val db = FirebaseFirestore.getInstance()

    fun addUserOld() {
    //Log.d("sdsds", "DocumentSnapshot entered")
    //Log.d("sdsds", "instance added ok")
    val testData = hashMapOf(
        "highscore" to 24211,
        "password" to "123456",
        "username" to "nomatter",
    )
    //Log.d("sdsds", "hashmap added ok")
        val db = FirebaseFirestore.getInstance()
       db.clearPersistence()
    db.collection("PlayerData").document("yqtest").set(testData)
    //db.collection("PlayerData").add(testData)
        .addOnSuccessListener {
            Log.d("Hello", "DocumentSnapshot added ok")
        }
        .addOnFailureListener {
            Log.d("Hello", "DocumentSnapshot failed ok")
        }

}


    override fun readDatabase2(){
        //Log.d("sdsds", "DocumentSnapshot entered")
        val db = FirebaseFirestore.getInstance()
        val usrName = "tester"
        var hs = 0
        db.collection("PlayerData")
//        .orderBy("highscore",
//            Query.Direction.DESCENDING)
//        .whereEqualTo("username", usrName)
//        .get()
            .document(usrName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    hs = document.getLong("highscore")?.toInt()?:0
                    Log.d("Hello", "DocumentSnapshot data: $hs")
                } else {
                    Log.d("Hello", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get failed with ", exception)
            }
        //return hs
        // [END get_document]
    }

/*    override fun readUsrDatabase(onResult: (Int) -> Unit) {

        //usrName = "PukiMan2"

        db.collection("PlayerData")
            .document(usrName)
            .get()
            .addOnSuccessListener { document ->
                val hs = document.getLong("highscore")?.toInt() ?: 0
                Log.d("Hello", "DocumentSnapshotTest data: $hs")
                onResult(hs)  // ✅ Pass value back via callback
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get failed with ", exception)
                onResult(0)  // Pass 0 if failed
            }
    }*/


    override suspend fun checkUserNameAvail(desiredUsername: String): Boolean {
        Log.d("Hello", "checkUserNameAvail")
        Log.d("Hello", "desiredUsername is, $desiredUsername")

        return try {
            val db = FirebaseFirestore.getInstance()
            // Convert the Firebase async operation to a coroutine
            val document = suspendCancellableCoroutine { continuation ->
                db.collection("PlayerData")
                    .document(desiredUsername)
                    .get()
                    .addOnSuccessListener { document ->
                        continuation.resume(document)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Hello", "checkUserNameAvail get failed with ", exception)
                        continuation.resume(null)
                    }
            }

            if (document?.exists() == true) {
                Log.d("Hello", "username already taken")
                false  // username is taken
            } else {
                Log.d("Hello", "username free")
                true   // username is available
            }
        } catch (e: Exception) {
            Log.d("Hello", "checkUserNameAvail get failed with ", e)
            true  // Return true on failure as in original code
        }
    }

    override fun checkUserNameAvailOLD(desiredUsername : String, callback: (Boolean) -> Unit)
    {
        Log.d("Hello", "checkUserNameAvail")
        Log.d("Hello", "desiredUsername is, $desiredUsername")
        val db = FirebaseFirestore.getInstance()
        db.collection("PlayerData")
            .document(desiredUsername)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    //got the user name means is taken alr
                    callback(false)
                    Log.d("Hello", "username already taken")
                } else {
                    //username no exist
                    callback(true)
                    Log.d("Hello", "username free")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", " checkUserNameAvail get failed with ", exception)
                // here is failure to even get playerdata
                callback(true)
            }


    }
    /*override fun setUserDetails(setValUser: String, setValPw: String)
    {
        usrName = setValUser
        passWrd = setValPw
    }*/

    //override fun checkUserDetails(getValUser: String, getValPw: String, callback: (Boolean) -> Unit)

    override fun checkUserDetails(getValUser: String, getValPw: String, callback: (Int) -> Unit)
    {
       /* usrName = getValUser
        passWrd = getValPw*/
        Log.d("Hello", "username1 is $getValUser")
        Log.d("Hello", "password1 is $getValPw")
        val db = FirebaseFirestore.getInstance()
        db.collection("PlayerData")
            .document(getValUser)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) { //username is found?
                    val databaseSidePW = document.getString("password")
                    Log.d("Hello", "username2 is $getValUser")
                    Log.d("Hello", "password2 is $getValPw")
                    Log.d("Hello", "database password is $databaseSidePW")
                    if(getValPw == databaseSidePW) // if password of username match
                    {
                        callback(1)
                        currUsrname = getValUser
                        lastHighscore = document.getLong("highscore")?.toInt() ?: 0
                        Log.d("Hello", "callback true for checkuserdetails")
                    }
                    else // if password is wrong
                    {
                        callback(2)
                        Log.d("Hello", "callback false for checkuserdetails")
                    }

                } else { //username not found
                    Log.d("Hello", "call")
                    callback(3)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get for checksUserDetails failed with ", exception)
                callback(4) //network connection error
            }

            /*.addOnSuccessListener { document ->
                if (document != null) {
                    var hs = document.getLong("password")?.toInt()?:0
                    Log.d("Hello", "DocumentSnapshot data: $hs")
                } else {
                    Log.d("Hello", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Hello", "get failed with ", exception)
            }*/
    }

    override fun addUser(usrNameTmp : String, passWrdTmp : String) {
        Log.d("hello", "adduser")
        Log.d("hello", "adduser user is, $usrNameTmp")
        Log.d("hello", "adduser pw is, $passWrdTmp")

        // Validate input
        if (usrNameTmp.isNullOrEmpty() || passWrdTmp.isNullOrEmpty()) {
            Log.e("hello", "Username or password is null or empty")
            return
        }

        val testData = hashMapOf(
            "password" to passWrdTmp,
            "highscore" to 0,
            "username" to usrNameTmp

        )
        Log.d("hello", "adduser hash done")
        val db = FirebaseFirestore.getInstance()
        db.clearPersistence()
        if (db == null) {
            Log.e("hello", "Firestore instance is null")
            return

        }
        Log.d("hello", "adduser instance gotten")
        db.collection("PlayerData").document(usrNameTmp).set(testData)
            .addOnSuccessListener {
                currUsrname = usrNameTmp
                Log.d("hello", "user entry added ok")
            }
            .addOnFailureListener { e ->
                Log.e("hello", "Test document failed", e)
                //Log.d("hello", "user entry failed ok")
            }
        Log.d("hello", "adduser end func")
    }

    override fun updateHighscore(newHighscore : Int)
    {
        val db = FirebaseFirestore.getInstance()

//        val newScoreData = hashMapOf(
//            "password" to "testoo",
//            "highscore" to 1232)
        Log.d("ouch", "DocumentSnapshot successfully written for $currUsrname!")
        db.collection("PlayerData")
            .document(currUsrname)
            .update("highscore", newHighscore)
//            .set(newScoreData)
            .addOnSuccessListener { Log.d("ouch", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("ouch", "Error writing document", e) }
    }

    override fun getTopTenHs(onResult: (List<Pair<String, Int>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Remove the clearPersistence call
        db.collection("PlayerData")
            .orderBy("highscore", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { querySnap ->
                try {
                    val retHs = querySnap.documents.mapNotNull { document ->
                        val username = document.id.takeIf { it.isNotBlank() } ?: "Unknown"
                        val hs = document.getLong("highscore")?.toInt() ?: 0

                        Log.w("Hello", "Fetched: $username - Score: $hs")
                        Pair(username, hs)
                    }
                    Log.d("Hello", "Total leaderboard entries: ${retHs.size}")
                    // Ensure callback runs on main thread
                    Gdx.app.postRunnable {
                        onResult(retHs)
                    }
                } catch (e: Exception) {
                    Log.e("Hello", "Error processing query results", e)
                    Gdx.app.postRunnable {
                        onResult(emptyList())
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Hello", "Firebase query failed", e)
                Gdx.app.postRunnable {
                    onResult(emptyList())
                }
            }
    }

}

/*fun addUser() {
    //Log.d("sdsds", "DocumentSnapshot entered")
    //Log.d("sdsds", "instance added ok")
    val testData = hashMapOf(
        "highscore" to 24211,
        "password" to "rickyssss",
        "username" to "xiaomings ex",
    )
    //Log.d("sdsds", "hashmap added ok")

    db.collection("PlayerData").document("$usrName").set(testData)
    //db.collection("PlayerData").add(testData)
        .addOnSuccessListener {
            Log.d("sdsds", "DocumentSnapshot added ok")
        }
        .addOnFailureListener {
            Log.d("sdsds", "DocumentSnapshot failed ok")
        }

    *//*db.collection("PlayerData")
//        .orderBy("highscore",
//            Query.Direction.DESCENDING)
        .whereEqualTo("username", usrName)
        .get()
        .addOnSuccessListener { querySnap -> val hs = querySnap.documents.mapNotNull {
                                    document ->
                                        val username = document.getString("username")
                                        val hs = document.getLong("highscore")?.toInt()
                                        if(username != null && hs != null)
                                            Log.w("Hello", "managed to read value: $hs" )
            }
        }*//*
}*/

fun readDatabase() :Int{
    //Log.d("sdsds", "DocumentSnapshot entered")
    val db = FirebaseFirestore.getInstance()
    val usrName = "PukiMan2"
    var hs = 0
    db.collection("PlayerData")
//        .orderBy("highscore",
//            Query.Direction.DESCENDING)
//        .whereEqualTo("username", usrName)
//        .get()
        .document(usrName)
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                hs = document.getLong("highscore")?.toInt()?:0
                Log.d("Hello", "DocumentSnapshot data: $hs")
            } else {
                Log.d("Hello", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("Hello", "get failed with ", exception)
        }
    return hs
    // [END get_document]
}


