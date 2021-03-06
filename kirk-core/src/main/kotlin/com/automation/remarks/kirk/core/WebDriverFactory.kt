package com.automation.remarks.kirk.core

import com.automation.remarks.kirk.Configuration
import com.automation.remarks.kirk.ext.autoClose
import com.automation.remarks.kirk.ext.isAlive
import io.github.bonigarcia.wdm.ChromeDriverManager
import io.github.bonigarcia.wdm.FirefoxDriverManager
import io.github.bonigarcia.wdm.InternetExplorerDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import java.util.concurrent.ConcurrentHashMap


/**
 * Created by sergey on 25.06.17.
 */
class WebDriverFactory {

    private val driverContainer: MutableMap<Long, WebDriver> = ConcurrentHashMap(4)

    private val CHROME = "chrome"
    private val FIREFOX = "firefox"
    private val INTERNET_EXPLORER = "ie"

    private fun createDriver(): WebDriver {
        val browser = configuration.browserName()
        when (browser) {
            CHROME -> return createChromeDriver()
            FIREFOX -> return createFireFoxDriver()
            INTERNET_EXPLORER -> return createInternetExplorerDriver()

            else -> throw IllegalArgumentException("$browser browserName doesn't support!")
        }
    }

    private fun createChromeDriver(): WebDriver {
        ChromeDriverManager.getInstance().setup()
        return ChromeDriver(getOptions())
    }

    private fun createFireFoxDriver(): WebDriver {
        FirefoxDriverManager.getInstance().setup()
        return FirefoxDriver()
    }

    private fun createInternetExplorerDriver(): WebDriver {
        InternetExplorerDriverManager.getInstance().setup()
        return InternetExplorerDriver()
    }

    fun setWebDriver(webDriver: WebDriver): WebDriver {
        driverContainer.put(Thread.currentThread().id, webDriver)
        return webDriver
    }

    fun getDriver(): WebDriver {
        val driver = driverContainer[Thread.currentThread().id]
        if (driver != null && driver.isAlive()) {
            return driver
        }
        val newDriver = createDriver()
        newDriver.autoClose(configuration.autoClose())
        return setWebDriver(newDriver)
    }

    private fun getOptions(): ChromeOptions {
        val option = ChromeOptions()
        if (configuration.chromeArgs().isNotEmpty()) option.addArguments(configuration.chromeArgs())
        if (!configuration.chromeBin().isNullOrEmpty()) option.setBinary(configuration.chromeBin())
        if (configuration.chromeExtensions().isNotEmpty()) option.addExtensions(configuration.chromeExtensions())

        return option
    }
}

val driverFactory = WebDriverFactory()

var configuration: Configuration = loadConfig(Configuration::class)

fun getDriver(): WebDriver {
    return driverFactory.getDriver()
}