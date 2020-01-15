package com.qa.stepDefinitions.web;

import com.qa.listeners.Reporter;
import com.qa.pageObjects.web.DetailPage;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.qa.driverFactory.DriverManager;
import com.qa.pageObjects.web.HomePage;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import java.util.List;

public class StepImplementation {
    static Logger log;
    RemoteWebDriver driver = DriverManager.getDriver();
    private HomePage homePage;
    private DetailPage detailPage;
    private List<WebElement> searchResults;
    public Scenario scenario;

    static {
        log = Logger.getLogger(StepImplementation.class);
    }

    public StepImplementation() throws Exception {
        homePage = new HomePage(this.driver);
        detailPage = new DetailPage(this.driver);
    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }
    @Given("^I'm on shopee home page$")
    public void i_m_on_shopee_home_page() throws Throwable {
        try{
            homePage.launch_URL();
        }catch (Exception e){
            Assert.fail("Fail: Open URL: "+e.getStackTrace());
        }
    }

    @Given("^User already logged in and cart is empty$")
    public void user_already_logged_in_and_cart_is_empty() throws Throwable {
        if(homePage.user_logged_in()){
            Assert.fail("Fail: User not logged in already");
        }
    }

    @When("^I enter search \"([^\"]*)\" and click on search button$")
    public void i_enter_search_and_click_on_search_button(String keyword) throws Throwable {
        if(!homePage.searchWithKeyword(keyword)){
            Assert.fail("Fail: Search Input operation is not complete");
        }
    }

    @Then("^I should see the search results header and lists$")
    public void i_should_see_the_search_results_header_and_lists() throws Throwable {
        if(!homePage.checkforSearchResultHeader()){
            Assert.fail("Fail: Search result header is not displayed");
        }
    }

    @Then("^I navigate through first \"([^\"]*)\" pages and store all item price$")
    public void i_navigate_through_first_pages_and_store_all_item_price(String page) throws Throwable {
        if(!homePage.getItemsPrice(Integer.parseInt(page))){
            Assert.fail("Fail: Search result header is not displayed");
        }

    }

    @Then("^I Calculate maximum item price and click on the item$")
    public void i_Calculate_maximum_item_price_and_click_on_the_item() throws Throwable {
        Assert.assertNotNull(homePage.findMaxPrice());
        Assert.assertTrue(homePage.clickOnItem(5));
    }

    @Given("^I enter \"([^\"]*)\" no of items to buy$")
    public void i_enter_no_of_items_to_buy(String qty) throws Throwable {
        detailPage.enterQuantity(qty);
        detailPage.chooseVarity();
    }

    @Given("^I store all the details of item$")
    public void i_store_all_the_details_of_item() throws Throwable {
        detailPage.setProductDetails();
        Reporter.addStepLog(detailPage.productDetails.toString());
    }

    @When("^I click on add to card$")
    public void i_click_on_add_to_card() throws Throwable {

        //To DO
    }

    @Then("^I should see the cart updated with same \"([^\"]*)\" no of items$")
    public void i_should_see_the_cart_updated_with_same_no_of_items(String items) throws Throwable {

        //To DO

    }
}