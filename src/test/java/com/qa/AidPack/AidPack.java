package com.qa.AidPack;

import com.qa.driverFactory.LocalWebDriverListener;
import com.qa.logger.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AidPack {
    private RemoteWebDriver driver;

    public AidPack(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public static boolean waitforLoad(RemoteWebDriver driver,WebElement ele){
        try{
            WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(LocalWebDriverListener.maxTimeout));
            WebElement element = wait.until(ExpectedConditions.visibilityOf(ele));
        } catch (Exception e){
            Log.INFO("Element Not found");
            return false;
        }
        return true;
    }

    public static void waitForPageLoad(RemoteWebDriver driver) {
        int waitFor = 2;
        Log.INFO("Info : Wait for page to load");
        ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                String pageLoadStatus = (String) ((JavascriptExecutor) driver).executeScript("return document.readyState");
                Log.INFO("Info : Page Load Status - " + pageLoadStatus);
                return pageLoadStatus.equals("complete") || pageLoadStatus.equals("interactive");
            }
        };
        Log.INFO("Info : Maximum wait for - " + waitFor);
        WebDriverWait wait = new WebDriverWait(driver, waitFor);
        wait.until(pageLoadCondition);
    }

    public static boolean clickWithJs(RemoteWebDriver driver, WebElement element, boolean isWaitRequired) {
        boolean isTrue=false;
        String logMsg = " : Element clicked by JS - "+element.toString();
        try {
            isTrue = isWaitRequired ? waitforLoad(driver, element) : false;
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", element);
            Log.INFO("Pass"+logMsg);
            isTrue = true;
        } catch (Exception e) {
            Log.ERROR("Error"+logMsg+" - "+e);
        }
        return isTrue;
    }

    public static boolean scrollWithJs(RemoteWebDriver driver, WebElement element) {
        boolean isTrue=false;
        String logMsg = " : Element Scroll by JS - "+element.toString();
        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].scrollIntoView();", element);
            Log.INFO("Pass"+logMsg);
            isTrue = true;
        } catch (Exception e) {
            Log.ERROR("Error"+logMsg+" - "+e);
        }
        return isTrue;
    }
}
