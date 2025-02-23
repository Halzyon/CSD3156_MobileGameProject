package com.testacc220.csd3156_mobilegameproject

interface AndroidLauncherInterface {
    //fun readUsrDatabase(onResult: (Int) -> Unit)
    //fun setUserDetails(setValUser: String, setValPw: String)
    fun checkUserNameAvailOLD(desiredUsername : String, callback: (Boolean) -> Unit)
    suspend fun checkUserNameAvail(desiredUsername: String): Boolean
    fun addUser(usrNameTmp : String, passWrdTmp : String)
    fun checkUserDetails(getValUser: String, getValPw: String, callback: (Boolean) -> Unit)
    fun readDatabase2()

    //fun readUsrDatabase(onResult: (Int) -> Unit)
    fun getTopTenHs(onResult: (List<Pair<String, Int>>) -> Unit)
//    fun regUsr()
}
