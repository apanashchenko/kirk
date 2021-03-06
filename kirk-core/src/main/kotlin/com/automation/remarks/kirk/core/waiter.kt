package com.automation.remarks.kirk.core

import com.automation.remarks.kirk.conditions.Condition
import com.automation.remarks.kirk.conditions.ConditionAssert
import com.automation.remarks.kirk.ex.ConditionMismatchException
import com.automation.remarks.kirk.ext.saveScreenshot
import com.automation.remarks.kirk.locators.ElementLocator
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement


/**
 * Created by sergey on 25.06.17.
 */
fun <T> waitFor(driver: WebDriver,
                locator: ElementLocator<T>,
                condition: Condition<T>,
                timeout: Int,
                poolingInterval: Double) {

    val endTime = System.currentTimeMillis() + timeout
    while (true) {
        try {
            return ConditionAssert.evaluate(locator.find(), condition)
        } catch (ex: ConditionMismatchException) {
            if (System.currentTimeMillis() > endTime) {
                highlightElement(driver, locator)

                val message = """
            failed while waiting ${timeout / 1000} seconds
            to assert $condition
            for ${locator.description}
            reason: ${ex.message}
            screenshot: file://${driver.saveScreenshot().absolutePath}
                        """
                throw TimeoutException(message)
            }
            Thread.sleep(poolingInterval.toLong())
        } catch (ex: org.openqa.selenium.NoSuchElementException) {
            if (System.currentTimeMillis() > endTime) {
                val message = """
            failed while waiting ${timeout / 1000} seconds
            for existence of ${locator.description}
            reason: no such element
                either wrong locator
                or did not have time open load

                screenshot: file://${driver.saveScreenshot().absolutePath}
                """
                throw TimeoutException(message)
            }
            Thread.sleep(poolingInterval.toLong())
        }
    }
}

private fun <T> highlightElement(driver: WebDriver, locator: ElementLocator<T>) {
    val element = locator.find()
    when (element) {
        is List<*> -> {
            for (el in element) {
                highlightElement(driver, el as WebElement)
            }
        }
        is WebElement -> highlightElement(driver, element)
    }
}

private fun highlightElement(driver: WebDriver, element: WebElement) {

    val size = configuration.highlightSize()
    val style = configuration.highlightStyle()
    val color = configuration.highlightColor()
    if (configuration.highlightBorder()) {
        JsExecutor(driver).execute(element) {
            "arguments[0].style.setProperty('border', '$size $style $color');"
        }
    }
}

fun sleep(i: Long) {
    Thread.sleep(i)
}