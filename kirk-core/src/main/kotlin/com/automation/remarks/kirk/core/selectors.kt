package com.automation.remarks.kirk.core

import org.openqa.selenium.By

/**
 * Created by sergey on 01.07.17.
 */
fun byXpath(xpath: String): By {
    return By.xpath(xpath)
}