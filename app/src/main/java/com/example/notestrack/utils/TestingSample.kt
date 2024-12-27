package com.example.notestrack.utils

object TestingSample {

    fun validUserName(
        userName:String,
        password:String,
        confirmPassword:String
    ):Boolean{

        if (userName.trimIndent().isEmpty() || password.trimIndent().isEmpty()){
            return false
        }

        if (password!=confirmPassword){
            return false
        }

        return true
    }
}