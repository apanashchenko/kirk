package com.automation.remarks.kirk.test

import com.automation.remarks.kirk.Kirk
import com.automation.remarks.kirk.conditions.have
import org.testng.annotations.Test

/**
 * Created by sergey on 24.07.17.
 */
class ElementCollectionConditionsTest : BaseTest() {

    @Test
    fun testNotCondition() {
        Kirk.drive {
            to(url)
            all(".l").shouldNot(have.elementWithText("Один"))
            //TODO this is BUG
        }
    }
}