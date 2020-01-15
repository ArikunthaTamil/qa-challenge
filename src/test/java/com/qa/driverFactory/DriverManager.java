package com.qa.driverFactory;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.IInvokedMethod;

public class DriverManager {

    public static String deviceName;
    public volatile static String device;
    private static final ThreadLocal myThreadLocal = new ThreadLocal();
    public static final ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
    static Logger log;

    static {
        log = Logger.getLogger(DriverManager.class);
    }

    public static RemoteWebDriver getDriver() throws Exception {
        if (driver.get() == null) {
            // this is need when running tests from IDE
            log.info("Thread has no WedDriver, creating new one");
            setWebDriver(LocalDriverFactory.createInstance(null,null,null, null,null));
        }
        log.debug("Getting instance of remote driver" + driver.get().getClass());
        return (RemoteWebDriver) driver.get();
    }

    public static void setWebDriver(WebDriver driver) {
        DriverManager.driver.set(driver);
    }

    /**
     * Returns a string containing current browser name, its version and OS name.
     * This method is used in the the *WebDriverListeners to change the test name.
     * */

  public synchronized void beforeInvocation(IInvokedMethod method) {
        log.debug("BEGINNING: org.stng.jbehave.LocalWebDriverListener.beforeInvocation");
        if (method.isTestMethod()) {
            // get browser name specified in the TestNG XML test suite file

            deviceName = method.getTestMethod().getXmlTest().getLocalParameters().get("devicename");
            System.out.println("Device Name....."+deviceName);
            myThreadLocal.set(deviceName);
            device = (String) myThreadLocal.get();

            System.out.println("Device Name is...."+device);

            System.out.println("I am here in driver manager...."+device);


        }
    }
    public static String getBrowserInfo() throws Exception {
        log.debug("Getting browser info");
        // we have to cast WebDriver object to RemoteWebDriver here, because the first one does not have a method
        // that would tell you which browser it is driving. (sick!)
        Capabilities cap = ((RemoteWebDriver) DriverManager.getDriver()).getCapabilities();
        String b = cap.getBrowserName();
        String os = cap.getPlatform().toString();
        String v = cap.getVersion();
        //String deviceName = (String) getDeviceName();
        return String.format("%s v:%s %s", b, v, os);
    }

    public String setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        System.out.println("Device is "+deviceName);
        return deviceName;
    }

    public String getDeviceName() {
        System.out.println("Device name is"+deviceName);
        return deviceName;
    }

}