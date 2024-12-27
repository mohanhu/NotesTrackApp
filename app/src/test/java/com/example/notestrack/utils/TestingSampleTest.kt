package com.example.notestrack.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TestingSampleTest{

    @Test
    fun `checkIfUserNameIsEmpty`() {

        val result = TestingSample.validUserName(
            "j",
            "234",
            "321"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `checkIfPasswordAndConfirmIsSame`(){
        val result = TestingSample.validUserName(
            "g",
            "321",
            "321"
        )
        assertThat(result).isTrue()
    }
}