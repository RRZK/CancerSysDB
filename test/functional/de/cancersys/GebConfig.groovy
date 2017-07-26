package de.cancersys

/*import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile*/
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/configuration.html
*/

driver = {
    //set the firefox locale to 'en-us' since the tests expect english
    //see http://stackoverflow.com/questions/9822717 for more details
/*    FirefoxProfile profile = new FirefoxProfile()
    profile.setPreference("intl.accept_languages", "en-us")
    def driverInstance = new FirefoxDriver(profile)
    driverInstance.manage().window().maximize()
    driverInstance*/
    //Old Chrome Options
/*    ChromeOptions profile = new ChromeOptions ()
    profile.setBinary("chromium-browser");
    def driverInstance = new ChromeDriver( profile)
    driverInstance.manage().window().maximize()
    driverInstance*/

    //Chrome/Chromium
    //Dont Forget The Parameters
    //-Dwebdriver.chrome.driver=/usr/lib/chromium-browser/chromedriver
    new ChromeDriver()

/*

    new PhantomJSDriver()
*/

}

baseNavigatorWaiting = true
atCheckWaiting = true
waiting {
    timeout = 10
    retryInterval = 0.5
}
