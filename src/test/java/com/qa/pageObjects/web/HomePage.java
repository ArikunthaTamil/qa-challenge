package com.qa.pageObjects.web;

import com.qa.AidPack.*;
import com.qa.driverFactory.LocalWebDriverListener;
import com.qa.logger.Log;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Home Page Elements
 */
public class HomePage extends LoadableComponent<HomePage>{

    private RemoteWebDriver driver;
    private WebDriverWait wait;
    public static ArrayList<Float> priceList = new ArrayList<Float>();
    Integer counter = 0;
    public static Float maxItem = null;
    public static AidPack aPack = null;

    @FindBy(css = ".icon-shopee-logo")
    private WebElement main_logo;

    @FindBy(css = ".shopee-avatar")
    private WebElement login_avatar;

    @FindBy(css = ".shopee-popup__close-btn")
    private WebElement btn_close_ad;

    @FindBy(css = ".shopee-search-item-result__item div span:nth-child(2)._341bF0")
    private List<WebElement> item_prices;

    @FindBy(css = "input[class^='shopee-searchbar-input']")
    private WebElement search_input;

    @FindBy(css = ".btn-solid-primary")
    private WebElement btn_search;

    @FindBy(css = ".shopee-search-result-header")
    private WebElement search_result_header;

    @FindBy(css = ".shopee-page-controller .shopee-icon-button--right")
    private WebElement next_page;

    @FindBy(css = ".shopee-page-controller .shopee-icon-button--left")
    private WebElement prev_page;

    @FindBy(css = ".shopee-page-controller button[class^=shopee-button]")
    private List<WebElement> pages;


    public HomePage(RemoteWebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        aPack = new AidPack(driver);
    }

    public void launch_URL() throws Exception {
        try {

        this.driver.get(LocalWebDriverListener.weburl);
        isLoaded(); //wait for page load to complete
        close_ad(); //handle Ad if displayed

        } catch (Exception e) {
            Log.ERROR("Error: Page not loaded");
        }
    }

    public void close_ad() {

        try {
            if (aPack.waitforLoad(driver, btn_close_ad)) { //Check if Ad displayed
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn_close_ad);
                driver.switchTo().defaultContent(); //To switch to home page default content
            }
        } catch (Exception e) {
            Log.ERROR("Error: Ad is not displayed");
        }

    }

    public boolean user_logged_in() {
        Boolean avatar = false;
        try{
            avatar = login_avatar.isDisplayed();
        } catch (Exception e)
        {
            Log.INFO("Element Not found");
        }
        return avatar;
    }

    public boolean searchWithKeyword(String keyword) {
        try{
           if(search_input.isDisplayed()){
               search_input.sendKeys(keyword);
               aPack.clickWithJs(driver,btn_search,false);
           }
        } catch (Exception e)
        {
            Log.ERROR("Search operation");
            return false;
        }
        return true;
    }

    public boolean checkforSearchResultHeader(){
        return aPack.waitforLoad(driver,search_result_header);
    }

    public boolean getItemsPrice(Integer pages){
        try{
            while(pages > 0){
                loadFiftyItems(); //To load all 50 items in the page
                addItemPrice(item_prices);
                if(pages == 1) {
                    break;
                }
                aPack.clickWithJs(driver,next_page,false);
                pages--;
            }
        }catch (Exception e){
            Log.ERROR("Error: Storing item price from first 5 pages");
            return false;
        }
        return true;
    }

    public void loadFiftyItems(){
        while (item_prices.size() != 50){
            aPack.scrollWithJs(driver,item_prices.get(item_prices.size()-1));
        }
    }

    public void addItemPrice(List<WebElement> ele){
        try {
            while (counter < ele.size()) {
                priceList.add(Float.parseFloat(ele.get(counter).getText()));
                counter++;
            }
            counter = 0;
        }catch (Exception e){
            Log.ERROR("Error: Adding items to list");
        }
    }

    public Float findMaxPrice() {
        try {
            Collections.sort(priceList);
            maxItem = priceList.get(priceList.size() - 1);
        } catch (Exception e) {
            Log.ERROR("Error: Finding highest price from list");
        }
        return maxItem;
    }

    public boolean clickOnItem(Integer page) {
        Boolean response = false;
        try {
            while(page > 0){
                loadFiftyItems(); //To load all 50 items in the page
                if(!findAndClickOnItem(item_prices,maxItem)){
                    aPack.clickWithJs(driver,prev_page,false);
                    page--;
                    continue;
                }
                response = true;
                break;
            }
        } catch (Exception e) {
            Log.ERROR("Error: Clicking on highest price item from result");
            return false;
        }
        return response;
    }

    public boolean findAndClickOnItem(List<WebElement> ele, Float price){
        try {
            while (counter < ele.size()) {
                if(Float.parseFloat(ele.get(counter).getText()) == price){
                    aPack.clickWithJs(driver,ele.get(counter),false);
                    return true;
                }
                counter++;
            }
            counter = 0;
        }catch (Exception e){
            Log.ERROR("Error: Finding item from result");
        }
        return false;
    }

    @Override
    protected void load() {

    }

    @Override
    protected void isLoaded() throws Error {
        aPack.waitForPageLoad(driver);
    }
}

