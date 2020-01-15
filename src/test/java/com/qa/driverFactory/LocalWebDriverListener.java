package com.qa.driverFactory;

import com.aventstack.extentreports.ExtentTest;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.internal.BaseTestMethod;

import java.lang.reflect.Field;
import java.net.MalformedURLException;

public class LocalWebDriverListener implements IInvokedMethodListener {

    public static String browserName;
    public static String deviceName;
    public static String weburl;
    public static String maxTimeout;
    public static Long minTimeout;
    public volatile static String device;
    ExtentTest test = null;
    WebDriver driver = null;
    private static final ThreadLocal myThreadLocal = new ThreadLocal();
    DriverManager driverManager=new DriverManager();

    static Logger log = Logger.getLogger(LocalWebDriverListener.class);

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        log.debug("BEGINNING: org.stng.jbehave.LocalWebDriverListener.beforeInvocation");
        if (method.isTestMethod()) {
            // get browser name specified in the TestNG XML test suite file
            browserName = method.getTestMethod().getXmlTest().getLocalParameters().get("browserName");
            String driverHost = method.getTestMethod().getXmlTest().getLocalParameters().get("driverHost");
            String driverPort = method.getTestMethod().getXmlTest().getLocalParameters().get("driverPort");
            deviceName = method.getTestMethod().getXmlTest().getLocalParameters().get("devicename");
            weburl = method.getTestMethod().getXmlTest().getLocalParameters().get("weburl");
            String hubUrl = method.getTestMethod().getXmlTest().getLocalParameters().get("hubUrl");
            String configFilePath = method.getTestMethod().getXmlTest().getLocalParameters().get("configFilePath");
            String projectName = method.getTestMethod().getXmlTest().getLocalParameters().get("projectName");

            maxTimeout = method.getTestMethod().getXmlTest().getLocalParameters().get("maxTimeout");
            minTimeout = Long.parseLong(method.getTestMethod().getXmlTest().getLocalParameters().get("minTimeout"));

            log.info("getting driver for: " + browserName);
            log.info("device name is: " + deviceName);

            myThreadLocal.set(browserName);
            device = (String) myThreadLocal.get();

            try {
                driver = LocalDriverFactory.createInstance(driverHost, driverPort, browserName,
                         deviceName, projectName);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            DriverManager.setWebDriver(driver);
            log.info("Done! Created " + browserName + " driver!");
        } else {
            log.warn("Creating the Driver!!!");
        }
        log.debug("END: org.stng.jbehave.LocalWebDriverListener.beforeInvocation");
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        log.debug("BEGINNING: org.stng.jbehave.LocalWebDriverListener.afterInvocation");
        if (method.isTestMethod()) {
            try {
                String browser = DriverManager.getBrowserInfo();
                BaseTestMethod bm = (BaseTestMethod) testResult.getMethod();
                Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
                f.setAccessible(true);
                String newTestName = testResult.getTestContext().getCurrentXmlTest().getName() + " - "
                        + bm.getMethodName() + " - " + browser;
                log.info("Renaming test method name from: '" + bm.getMethodName() + "' to: '" + newTestName + "'");
                f.set(bm, newTestName);
            } catch (Exception ex) {
                System.out.println("afterInvocation exception:\n" + ex.getMessage());
                ex.printStackTrace();
            } finally {
                // close the browser
                WebDriver driver = null;
                try {
                    driver = DriverManager.getDriver();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (driver != null) {
                    driver.quit();
                }
            }
        }
        log.debug("END: org.stng.jbehave.LocalWebDriverListener.afterInvocation");
    }
}