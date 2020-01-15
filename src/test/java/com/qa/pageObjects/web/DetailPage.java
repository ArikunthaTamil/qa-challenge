package com.qa.pageObjects.web;

import com.qa.AidPack.AidPack;
import com.qa.logger.Log;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Detail Page Elements
 */
public class DetailPage extends LoadableComponent<DetailPage> {

    private RemoteWebDriver driver;
    public static AidPack aPack = null;
    public static Map<String,String> productDetails = new HashMap<>();

    @FindBy(css = ".icon-shopee-logo")
    private WebElement main_logo;

    @FindBy(css = ".qaNIZv")
    private WebElement item_name;

    @FindBy(css = "._2z6cUg")
    private WebElement rating;

    @FindBy(css = "._22sp0A")
    private WebElement items_sold;

    @FindBy(css = "._3Oj5_n")
    private WebElement no_of_ratings;

    @FindBy(css = "._3n5NQx")
    private WebElement price;

    @FindBy(css =".BtHdNz")
    private WebElement shipping_cost;

    @FindBy(css =".voucher-promo-value")
    private WebElement voucher;

    @FindBy(css =".crl7WW button[class='product-variation']")
    private List<WebElement> product_option;

    @FindBy(css ="input[class*='_18Y8Ul']")
    private WebElement input_qty;

    @FindBy(css =".BtHdNz")
    private WebElement available_qty;

    @FindBy(css =".icon-add-to-cart")
    private WebElement btn_addToCart;

    @FindBy(css =".shopee-cart-number-badge")
    private WebElement number_of_items_in_cart;


    public DetailPage(RemoteWebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        aPack = new AidPack(driver);
    }

    public void addToCart() {
        try {
            if(btn_addToCart.isDisplayed()){
                btn_addToCart.click();
            }
        } catch (Exception e) {
            Log.ERROR("Error: Add to cart");
        }

    }

    public void enterQuantity(String qty) {
        try{
            input_qty.sendKeys(qty);
        } catch (Exception e)
        {
            Log.ERROR("Element Not found");
        }
    }

    public void chooseVarity() {
        try{
            if(product_option.size() != 0) {
                aPack.clickWithJs(driver,product_option.get(0),false);
            }
        } catch (Exception e)
        {
            Log.ERROR("Element Not found");
        }
    }

    public void setProductDetails(){
        try {
            productDetails.put("name",item_name.getText());
            productDetails.put("rating",rating.getText());
            productDetails.put("ratings",no_of_ratings.getText());
            productDetails.put("sold",items_sold.getText());
            productDetails.put("price",price.getText());
            productDetails.put("shipping",shipping_cost.getText());
        } catch (Exception e){
        Log.ERROR("Error: Product details");
        }
    }

    @Override
    protected void load() {

    }

    @Override
    protected void isLoaded() throws Error {

    }
}

