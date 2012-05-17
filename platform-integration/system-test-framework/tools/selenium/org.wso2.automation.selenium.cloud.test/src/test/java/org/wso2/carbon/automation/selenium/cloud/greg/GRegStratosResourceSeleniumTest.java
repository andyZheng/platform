/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.automation.selenium.cloud.greg;

import com.thoughtworks.selenium.Selenium;
import junit.framework.AssertionFailedError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.platform.test.core.BrowserManager;
import org.wso2.platform.test.core.ProductConstant;
import org.wso2.platform.test.core.utils.UserInfo;
import org.wso2.platform.test.core.utils.UserListCsvReader;
import org.wso2.platform.test.core.utils.environmentutils.ProductUrlGeneratorUtil;
import org.wso2.platform.test.core.utils.seleniumutils.GRegSeleniumUtils;
import org.wso2.platform.test.core.utils.seleniumutils.StratosUserLogin;

import java.net.MalformedURLException;

import static org.testng.Assert.*;


public class GRegStratosResourceSeleniumTest {
    private static final Log log = LogFactory.getLog(GRegStratosResourceSeleniumTest.class);
    private static Selenium selenium;
    private static WebDriver driver;
    String productName = "greg";
    String userName;
    String password;

    @BeforeClass(alwaysRun = true)
    public void init() throws MalformedURLException, InterruptedException {
        UserInfo userDetails = UserListCsvReader.getUserInfo(3);
        userName = userDetails.getUserName();
        password = userDetails.getPassword();
        String baseUrl = new ProductUrlGeneratorUtil().getServiceHomeURL(
                                                                                ProductConstant.GREG_SERVER_NAME);
        log.info("baseURL is :" + baseUrl);
        driver = BrowserManager.getWebDriver();
        selenium = new WebDriverBackedSelenium(driver, baseUrl);
        driver.get(baseUrl);
    }


    @Test(groups = {"wso2.greg"}, description = "add a resource to a collection", priority = 1)
    public void testAddResourceToCollection() throws Exception {
        String collectionPath = "/selenium_root/resource_root/resource/a1";
        String resourceName = "res1";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath);   //Create Collection  1
            assertEquals(collectionPath, selenium.getValue("//input"),
                         "New Created Collection1 does not Exists :");
            addResource(resourceName);          //add Resource
            findLocation(collectionPath);
            assertTrue(selenium.isTextPresent("res1"), "Resource res1 does not Exists:");
            userLogout();
            log.info("********GReg Stratos - Add Resource To a Collection test - Passed********");
        } catch (AssertionFailedError e) {
            log.info("Add Resource To a Collection test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Add Resource To a Collection test Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Add Resource To a Collection test Failed :" + e);
            userLogout();
            throw new WebDriverException("Add Resource To a Collection test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Add Resource To a Collection test Failed :" + e);
            userLogout();
            throw new Exception("Add Resource To a Collection test Failed :" + e);
        }
    }


    @Test(groups = {"wso2.greg"}, description = "add a comment to a resource", priority = 2)
    public void testAddCommentToResource() throws Exception {
        String collectionPath = "/selenium_root/resource_root/comment/a1";
        String resourceName = "res1";
        String comment = "resourceComment";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath);
            assertEquals(collectionPath, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            addResource(resourceName);
            findLocation(collectionPath + "/" + resourceName);
            addComment(comment);
            deleteComment();
            userLogout();
            log.info("********GReg Stratos - Add Comment To a Resource test - Passed ***********");
        } catch (AssertionFailedError e) {
            log.info("Add Comment To a Resource test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Add Comment To a Resource test Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Add Comment To a Resource test Failed :" + e);
            userLogout();
            throw new WebDriverException("Add Comment To a Resource test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Add Comment To a Resource test Failed :" + e);
            userLogout();
            throw new Exception("Add Comment To a Resource test Failed :" + e);
        }
    }

    @Test(groups = {"wso2.greg"}, description = "add a tag to resource", priority = 3)
    public void testAddTagToResource() throws Exception {
        String collectionPath = "/selenium_root/resource_root/tag/a1";
        String resourceName = "res1";
        String tagName = "resource";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath);
            assertEquals(collectionPath, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            addResource(resourceName);
            findLocation(collectionPath + "/" + resourceName);
            applyTag(tagName);

            Actions builder = new Actions(driver);
            WebElement tagElement = driver.findElement(By.xpath("//td[4]/div[15]/div/div[12]/div[3]/a"));
            builder.moveToElement(tagElement).build().perform();

            deleteTag();

            userLogout();
            log.info("********GReg Stratos- Add Tag To a Resource test - Passed ***********");
        } catch (AssertionFailedError e) {
            log.info("Add Tag To a Resource test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Add Tag To a Resource test Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Add Tag To a Resource test Failed :" + e);
            userLogout();
            throw new WebDriverException("Add Tag To a Resource test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Add Tag To a Resource test Failed :" + e);
            userLogout();
            throw new Exception("Add Tag To a Resource test Failed :" + e);
        }
    }

    @Test(groups = {"wso2.greg"}, description = "add a life cycle to resource", priority = 4)
    public void testAddLifeCycleToResource() throws Exception {
        String collectionPath = "/selenium_root/resource_root/lifecycle/a1";
        String resourceName = "res1";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath);
            assertEquals(collectionPath, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            addResource(resourceName);
            findLocation(collectionPath + "/" + resourceName);
            addServiceLifeCycle();
            promoteState();
            waitForLifeCycleStateTransition("Tested");
            promoteState();
            waitForLifeCycleStateTransition("Production");
            deleteServiceLifeCycle();
            userLogout();
            log.info("********GReg Stratos -Add Life Cycle To a Resource test - Passed **********");
        } catch (AssertionFailedError e) {
            log.info("Add Life Cycle To a Resource test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Add Life Cycle To a Resource test Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Add Life Cycle To a Resource test Failed :" + e);
            userLogout();
            throw new WebDriverException("Add Life Cycle To a Resource test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Add Life Cycle To a Resource test Failed :" + e);
            userLogout();
            throw new Exception("Add Life Cycle To a Resource test Failed :" + e);
        }
    }

    @Test(groups = {"wso2.greg"}, description = "add a rating to resource", priority = 5)
    public void testAddRatingToResource() throws Exception {
        String collectionPath = "/selenium_root/resource_root/rating/a1";
        String resourceName = "res1";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath);
            assertEquals(collectionPath, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            addResource(resourceName);
            findLocation(collectionPath + "/" + resourceName);
            applyRating();
            userLogout();
            log.info("********GReg Stratos - Add a Rating To a Resource test - Passed ***********");
        } catch (AssertionFailedError e) {
            log.info("Add a Rating To a Resource Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Add a Rating To a Resource Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Add a Rating To a Resource Failed :" + e);
            userLogout();
            throw new WebDriverException("Add a Rating To a Resource Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Add a Rating To a Resource Failed :" + e);
            userLogout();
            throw new Exception("Add a Rating To a Resource Failed :" + e);
        }
    }

    @Test(groups = {"wso2.greg"}, description = "add a rating to resource", priority = 6)
    public void testRenameResource() throws Exception {
        String collectionPath = "/selenium_root/resource_root/rename/a1";
        String resourceName = "res1";
        String rename = "rename_res1";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath);
            assertEquals(collectionPath, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            addResource(resourceName);
            findLocation(collectionPath);
            renameResource(rename);
            assertTrue(selenium.isTextPresent(rename), "Renamed Resource does not Exists :");
            userLogout();
            log.info("********GReg Stratos - Rename a Resource test - Passed ***********");
        } catch (AssertionFailedError e) {
            log.info("Rename a Resource test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Rename a Resource test Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Rename a Resource test Failed :" + e);
            userLogout();
            throw new WebDriverException("Rename a Resource test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Rename a Resource test Failed :" + e);
            userLogout();
            throw new Exception("Rename a Resource test Failed :" + e);
        }
    }

    @Test(groups = {"wso2.greg"}, description = "move a resource", priority = 7)
    public void testMoveResource() throws Exception {
        String collectionPath1 = "/selenium_root/resource_root/move1/a1";
        String collectionPath2 = "/selenium_root/resource_root/move2/b1";
        String resourceName1 = "res1";
        String resourceName2 = "res2";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath1);
            assertEquals(collectionPath1, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            addResource(resourceName1);
            findLocation("/");
            addCollection(collectionPath2);
            assertEquals(collectionPath2, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            findLocation("/selenium_root/resource_root/move1/a1/");
            moveResource(collectionPath2);
            findLocation(collectionPath2);
            addResource(resourceName2);
            findLocation(collectionPath2);
            assertTrue(selenium.isTextPresent("res1"), "Moved res1 resource does not Exists :");
            findLocation(collectionPath1);
            assertFalse(selenium.isTextPresent("res1"), "res1 resource has not been moved :");
            userLogout();
            log.info("********GReg Stratos - Move a Resource test - Passed ***********");
        } catch (AssertionFailedError e) {
            log.info("Move a Resource test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Move a Resource test Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Move a Resource test Failed :" + e);
            userLogout();
            throw new WebDriverException("Move a Resource test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Move a Resource test Failed :" + e);
            userLogout();
            throw new Exception("Move a Resource test Failed :" + e);
        }
    }

    @Test(groups = {"wso2.greg"}, description = "delete a resource", priority = 8)
    public void testDeleteResource() throws Exception {
        String collectionPath1 = "/selenium_root/resource_root/delete/a1";
        String resourceName1 = "res1";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath1);
            assertEquals(collectionPath1, selenium.getValue("//input"),
                         "New Created Collection does not Exists :");
            addResource(resourceName1);
            findLocation(collectionPath1);
            deleteResource();
            findLocation(collectionPath1);
            assertFalse(selenium.isTextPresent("res1"), "Resource res1 does Exists:");
            userLogout();
            log.info("********GReg Stratos - Delete Resource test - Passed ***********");
        } catch (AssertionFailedError e) {
            log.info("Delete Resource test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Delete Resource test Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Delete Resource test Failed :" + e);
            userLogout();
            throw new WebDriverException("Delete Resource test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Delete Resource test Failed :" + e);
            userLogout();
            throw new Exception("Delete Resource test Failed :" + e);
        }
    }

    @Test(groups = {"wso2.greg"}, description = "copy a resource", priority = 9)
    public void testCopyResource() throws Exception {
        String collectionPath1 = "/selenium_root/resource_root/copy1/a1";
        String collectionPath2 = "/selenium_root/resource_root/copy2/b1";
        String resourceName1 = "res1";
        String resourceName2 = "res2";
        String copyPath = "/selenium_root/resource_root/copy2/b1";

        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoDetailViewTab();
            GRegSeleniumUtils.deleteResourceFromBrowser(driver, "selenium_root");
            addCollection(collectionPath1);
            assertEquals(collectionPath1, selenium.getValue("//input"),
                         "New Created Collection1 does not Exists :");
            addResource(resourceName1);
            findLocation("/");
            addCollection(collectionPath2);
            assertEquals(collectionPath2, selenium.getValue("//input"),
                         "New Created Collection2 does not Exists :");
            findLocation("/selenium_root/resource_root/copy1/a1/");
            copyResource(copyPath);
            findLocation(collectionPath2);
            addResource(resourceName2);
            findLocation(collectionPath2);
            assertTrue(selenium.isTextPresent("res1"),
                       "copied res1 resource does not Exists in copied to location :");
            findLocation(collectionPath1);
            assertTrue(selenium.isTextPresent("res1"), "copied resource does not exists : :");
            userLogout();
            log.info("********GReg Stratos - Copy a Resource test - Passed ***********");
        } catch (AssertionFailedError e) {
            log.info("Copy a Resource test Failed :" + e);
            userLogout();
            throw new AssertionFailedError("Copy a Resource tes Failed :" +
                                           e);
        } catch (WebDriverException e) {
            log.info("Copy a Resource test Failed :" + e);
            userLogout();
            throw new WebDriverException("Copy a Resource test Failed :" +
                                         e);
        } catch (Exception e) {
            log.info("Copy a Resource test Failed :" + e);
            userLogout();
            throw new Exception("Copy a Resource test Failed :" + e);
        }
    }


    @AfterClass(alwaysRun = true)
    public void cleanup() {
        driver.quit();
    }

    private void deleteResource() throws InterruptedException {
        driver.findElement(By.id("actionLink1")).click();
        driver.findElement(By.linkText("Delete")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }


    private void copyResource(String copyPath) throws InterruptedException {
        driver.findElement(By.id("actionLink1")).click();
        driver.findElement(By.linkText("Copy")).click();
        driver.findElement(By.id("copy_destination_path1")).sendKeys(copyPath);
        driver.findElement(By.xpath("//td/table/tbody/tr[2]/td/input")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }

    private void moveResource(String collectionPath2) throws InterruptedException {
        driver.findElement(By.id("actionLink1")).click();
        driver.findElement(By.linkText("Move")).click();
        driver.findElement(By.id("move_destination_path1")).sendKeys(collectionPath2);
        driver.findElement(By.xpath("//tr[5]/td/table/tbody/tr[2]/td/input")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }


    private void renameResource(String rename) throws InterruptedException {
        driver.findElement(By.id("actionLink1")).click();
        driver.findElement(By.linkText("Rename")).click();
        driver.findElement(By.id("resourceEdit1")).clear();
        driver.findElement(By.id("resourceEdit1")).sendKeys(rename);
        driver.findElement(By.xpath("//tr[6]/td/table/tbody/tr[2]/td/input")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }

    private void applyRating() throws InterruptedException {
        // Add rating 1
        driver.findElement(By.xpath("//span/img[3]")).click();
        waitForValue("1.0");
        assertTrue(driver.findElement(By.xpath("//tr/td[4]/div[12]/div/div[6]/div/table/tbody/" +
                                               "tr[2]/td[2]")).getText().contains("(1.0)"),
                   "Rating 1 has failed :");

        // Add rating 2
        driver.findElement(By.xpath("//span/img[5]")).click();
        waitForValue("2.0");
        assertTrue(driver.findElement(By.xpath("//tr/td[4]/div[12]/div/div[6]/div/table/tbody/" +
                                               "tr[2]/td[2]")).getText().contains("(2.0)"),
                   "Rating 2 has failed :");
        // Add rating 3
        driver.findElement(By.xpath("//img[7]")).click();
        waitForValue("3.0");
        assertTrue(driver.findElement(By.xpath("//tr/td[4]/div[12]/div/div[6]/div/table/tbody/" +
                                               "tr[2]/td[2]")).getText().contains("(3.0)"),
                   "Rating 3 has failed :");
        // Add rating 4
        driver.findElement(By.xpath("//img[9]")).click();
        waitForValue("4.0");
        assertTrue(driver.findElement(By.xpath("//tr/td[4]/div[12]/div/div[6]/div/table/tbody/" +
                                               "tr[2]/td[2]")).getText().contains("(4.0)"),
                   "Rating 4 has failed :");
        // Add rating 5
        driver.findElement(By.xpath("//img[11]")).click();
        waitForValue("5.0");
        assertTrue(driver.findElement(By.xpath("//tr/td[4]/div[12]/div/div[6]/div/table/tbody/" +
                                               "tr[2]/td[2]")).getText().contains("(5.0)"),
                   "Rating 5 has failed :");
    }


    private void deleteServiceLifeCycle() throws InterruptedException {
        driver.findElement(By.xpath("//div[3]/div[2]/table/tbody/tr/td/div/a")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }

    private void addServiceLifeCycle() throws InterruptedException {
        driver.findElement(By.id("lifecycleIconMinimized")).click();
        driver.findElement(By.linkText("Add Lifecycle")).click();
        assertTrue(selenium.isTextPresent("Enable Lifecycle"), "Enable LifeCycle Text does not appear :");
        assertEquals("Add", selenium.getValue("//div[3]/div[3]/form/table/tbody/tr[2]/td/input"),
                     "Lifecycle Add Button does not appear :");
        assertEquals("Cancel", selenium.getValue("//div[3]/div[3]/form/table/tbody/tr[2]/td/input[2]"),
                     "Lifecycle Cancel button does not appear");
        driver.findElement(By.xpath("//div[3]/div[3]/form/table/tbody/tr[2]/td/input")).click();
        assertTrue(driver.findElement(By.id("myTable")).isDisplayed());
    }

    private void promoteState() throws Exception {
        GRegSeleniumUtils.waitForElement(driver, "id", "option0");
        GRegSeleniumUtils.waitForElement(driver, "id", "option1");
        GRegSeleniumUtils.waitForElement(driver, "id", "option2");

        driver.findElement(By.id("option0")).click();
        waitForLifeCycleCheck("option0");
        driver.findElement(By.id("option1")).click();
        waitForLifeCycleCheck("option1");
        Thread.sleep(5000);
        driver.findElement(By.id("option2")).click();
        waitForLifeCycleCheck("option2");

        driver.findElement(By.xpath("//div[14]/div[3]/div[2]/table/tbody/tr[2]/td/div/input")).click();

        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }

    private void deleteTag() throws InterruptedException {
        driver.findElement(By.xpath("//a[2]/img")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }

    private void applyTag(String tagName) throws InterruptedException {

        if (!driver.findElement(By.id("tagAddDiv")).isDisplayed()) {
            driver.findElement(By.id("tagsIconMinimized")).click();
        }

        driver.findElement(By.linkText("Add New Tag")).click();//click on Add New Tag
        GRegSeleniumUtils.waitForElement(driver, "id", "tfTag");
        driver.findElement(By.id("tfTag")).sendKeys(tagName);
        driver.findElement(By.xpath("//div[2]/input[3]")).click();
        GRegSeleniumUtils.waitForElement(driver, "xpath",
                                         "//td[4]/div[15]/div/div[12]/div[3]/a");
    }


    private void deleteComment() throws InterruptedException {
        driver.findElement(By.id("closeC0")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }

    private void addComment(String comment) throws InterruptedException {
        if (!driver.findElement(By.id("commentsIconExpanded")).isDisplayed()) {
            driver.findElement(By.id("commentsIconMinimized")).click();
        }
        driver.findElement(By.linkText("Add Comment")).click();
        assertTrue(selenium.isTextPresent("Add New Comment"),
                   "Add comment window pop -up title failed :");
        assertEquals("Add", selenium.getValue("//div[15]/div/div[3]/div[3]/form/table/tbody/" +
                                              "tr[2]/td/input"),
                     "Add comment window  button add not present failed :");
        assertEquals("Cancel", selenium.getValue("//div[15]/div/div[3]/div[3]/form/table/" +
                                                 "tbody/tr[2]/td/input[2]"),
                     "Add comment window  pop -up failed :");
        driver.findElement(By.id("comment")).sendKeys(comment);
        driver.findElement(By.xpath("//div[15]/div/div[3]/div[3]/form/table/tbody/tr[2]/td/input")).click();
    }


    private void findLocation(String path) throws Exception {
        driver.findElement(By.id("uLocationBar")).clear();
        driver.findElement(By.id("uLocationBar")).sendKeys(path);
        driver.findElement(By.xpath("//input[2]")).click();
    }


    private void addCollection(String collectionPath) throws Exception {
        driver.findElement(By.linkText("Add Collection")).click();
        driver.findElement(By.id("collectionName")).sendKeys(collectionPath);
        driver.findElement(By.id("colDesc")).sendKeys("Selenium Test");
        driver.findElement(By.xpath("//div[7]/form/table/tbody/tr[5]/td/input")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }


    private void gotoDetailViewTab() throws Exception {
        driver.findElement(By.linkText("Browse")).click();    //Click on Browse link
        GRegSeleniumUtils.waitForBrowserPage(driver);
        driver.findElement(By.id("stdView")).click();        //Go to Detail view Tab
        assertTrue(selenium.isTextPresent("Browse"), "Browse Detail View Page fail :");
        assertTrue(selenium.isTextPresent("Metadata"), "Browse Detail View Page fail Metadata:");
    }

    private void userLogout() throws InterruptedException {
        driver.findElement(By.linkText("Sign-out")).click();
    }

    private void addResource(String resourceName) throws Exception {
        driver.findElement(By.linkText("Add Resource")).click();
        assertTrue(selenium.isTextPresent("Add Resource"), "Add new resource page failed :");
        //select create text content
        selenium.select("id=addMethodSelector", "label=Create Text content");
        selenium.click("css=option[value=\"text\"]");
        // Enter name
        driver.findElement(By.id("trFileName")).sendKeys(resourceName);
        driver.findElement(By.id("trMediaType")).sendKeys("txt");
        driver.findElement(By.id("trDescription")).sendKeys("selenium test resource");
        driver.findElement(By.id("trPlainContent")).sendKeys("selenium test123");
        // Click on Add button
        driver.findElement(By.xpath("//tr[4]/td/form/table/tbody/tr[6]/td/input")).click();
        assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                   "Popup not found :");
        driver.findElement(By.xpath("//button")).click();
    }

    private boolean waitForValue(String value) {
        long currentTime = System.currentTimeMillis();
        long exceededTime;
        do {
            try {
                if (driver.findElement(By.xpath("//tr/td[4]/div[12]/div/div[6]/div/table/tbody/" +
                                                "tr[2]/td[2]")).getText().contains(value)) {
                    break;
                }
            } catch (WebDriverException ignored) {
                log.info("Waiting for the element");
            }
            exceededTime = System.currentTimeMillis();
        } while (!(((exceededTime - currentTime) / 1000) > 60));
        return false;
    }

    private boolean waitForLifeCycleCheck(String optionId) {
        long currentTime = System.currentTimeMillis();
        long exceededTime;
        do {
            try {
                if (driver.findElement(By.id(optionId)).getAttribute("checked") != null) {
                    return true;
                }
            } catch (WebDriverException ignored) {
                log.info("Waiting for LC checklist options");
            }
            exceededTime = System.currentTimeMillis();

        } while (!(((exceededTime - currentTime) / 1000) > 60));
        return false;
    }

    private void waitForLifeCycleStateTransition(String state) {
        long currentTime = System.currentTimeMillis();
        long exceededTime;
        Boolean status = false;
        do {
            try {
                if (driver.findElement(By.xpath("//tr/td[4]/div[14]/div[3]/div[2]/table/tbody/tr/td" +
                                                "/div[2]/table/tbody/tr[2]/td")).getText().contains(state)) {
                    status = true;
                    break;
                }
            } catch (WebDriverException ignored) {
                log.info("Waiting for LC checklist options");
            }
            exceededTime = System.currentTimeMillis();

        } while (!(((exceededTime - currentTime) / 1000) > 30));
        assertTrue(status, "LifeCycle state not found - " + state);
    }
}
