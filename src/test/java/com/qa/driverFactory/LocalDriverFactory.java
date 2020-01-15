package com.qa.driverFactory;


import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class LocalDriverFactory{
    //static LocalWebDriverListener localWebDriverListener=new LocalWebDriverListener(deviceName);
    public static RemoteWebDriver createInstance(String driverHost, String driverPort, String browserName, String devicename, String projectName) throws MalformedURLException {
        RemoteWebDriver driver;
        DesiredCapabilities capabilities = null;
        String localUrl = "http://" + driverHost + ":" + driverPort + "/wd/hub";
        String remoteUrl = "https://" + driverHost + ":" + driverPort + "/wd/hub";
        browserName = (browserName != null) ? browserName : "chrome";


        switch (Browser.valueOf(browserName.toUpperCase())) {
            case SAFARI:
                driver = new SafariDriver();
                break;
            case FIREFOX:
                System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir") + "/src/test/resources/appDrivers/Mac/geckodriver");
                driver = new FirefoxDriver();
                break;
            case IE:
                driver = new InternetExplorerDriver();
                break;
            case CHROME:
                String chromeDriver = (devicename.equalsIgnoreCase("windows") ? "Win/chromedriver.exe" : "Mac/chromedriver");
                System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir") + "/src/test/resources/appDrivers/" + chromeDriver);
                driver = new ChromeDriver();
                System.out.printf("Local Chrome Driver is returned");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Browser.valueOf(browserName.toUpperCase()));
        }
        driver.manage().window().maximize(); //To maximize the browser window after launch
        driver.manage().timeouts().implicitlyWait(LocalWebDriverListener.minTimeout, TimeUnit.SECONDS);//Default implicit timeout
        return driver;
    }
    private static enum Browser {
        SAFARI,
        IE,
        FIREFOX,
        CHROME,

    }

}