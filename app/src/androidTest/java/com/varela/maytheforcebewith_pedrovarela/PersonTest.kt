package com.varela.maytheforcebewith_pedrovarela

import android.app.Person
import android.webkit.URLUtil
import junit.framework.TestCase

class PersonTest : TestCase() {

    fun testNameIsNotEmpty(){
        val person = com.varela.maytheforcebewith_pedrovarela.model.Person("1234",
            "Blue", ArrayList(), "Male", "Brown", "166",
            "World", "123", "Mister", "Blue", "123456",
            "1234,", ArrayList(), ArrayList(), "http://", ArrayList()
        )

        val nameIsNotEmpty = person.name != "" && person.name != "null"
        assertTrue(nameIsNotEmpty)
    }

    fun testProfileURLIsValid(){
        val person = com.varela.maytheforcebewith_pedrovarela.model.Person("1234",
            "Blue", ArrayList(), "Male", "Brown", "166",
            "World", "123", "Mister", "Blue", "123456",
            "1234,", ArrayList(), ArrayList(), "htts://pedrovarela.pt", ArrayList()
        )

        val urlIsValid = URLUtil.isValidUrl(person.url)
        assertTrue(urlIsValid)
    }
}