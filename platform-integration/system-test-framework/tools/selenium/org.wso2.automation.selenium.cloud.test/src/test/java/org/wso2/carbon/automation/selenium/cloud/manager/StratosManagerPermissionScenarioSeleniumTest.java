package org.wso2.carbon.automation.selenium.cloud.manager;

import com.thoughtworks.selenium.Selenium;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.*;
import org.wso2.platform.test.core.BrowserManager;
import org.wso2.platform.test.core.ProductConstant;
import org.wso2.platform.test.core.utils.UserInfo;
import org.wso2.platform.test.core.utils.UserListCsvReader;
import org.wso2.platform.test.core.utils.environmentutils.ProductUrlGeneratorUtil;
import org.wso2.platform.test.core.utils.seleniumutils.StratosUserLogin;
import org.wso2.platform.test.core.utils.seleniumutils.UserManagementSeleniumUtils;

import java.net.MalformedURLException;

import static org.testng.Assert.assertTrue;


public class StratosManagerPermissionScenarioSeleniumTest {
    private static final Log log = LogFactory.getLog(StratosManagerPermissionScenarioSeleniumTest.
                                                             class);
    private static Selenium selenium;
    private static WebDriver driver;
    String productName = "manager";
    String userName;
    String password;
    String domain;

    @BeforeClass(alwaysRun = true)
    public void init() throws MalformedURLException, InterruptedException {
        UserInfo userDetails = UserListCsvReader.getUserInfo(6);
        userName = userDetails.getUserName();
        password = userDetails.getPassword();
        domain = userDetails.getDomain();
        String baseUrl = new ProductUrlGeneratorUtil().getServiceHomeURL(
                                                                                ProductConstant.MANAGER_SERVER_NAME);
        log.info("baseURL is :" + baseUrl);
        driver = BrowserManager.getWebDriver();
        selenium = new WebDriverBackedSelenium(driver, baseUrl);
        driver.get(baseUrl);
    }

    @Test(groups = {"wso2.manager"}, description = "Login Permission Scenario test", priority = 1)
    public void testUserLoginPermissionScenario() throws Exception {
        String roleName = "login1";
        String newUserName = "mgrlogin";
        String newFulluserName = newUserName + "@" + domain;
        String newUserpassword = "welUserManagementSeleniumUtils.come";


        try {
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            log.info("Stratos IS Login Success");
            gotoUserManagementPage();
            UserManagementSeleniumUtils.deleteUserByName(driver, newUserName);
            createNewUser(newUserName, newUserpassword);
            gotoRoleManagementPage();
            UserManagementSeleniumUtils.deleteRoleByName(driver, roleName);
            addRole(newUserName, roleName);
            userLogout();
            //loging with new user credientails
            StratosUserLogin.userLogin(driver, selenium, newFulluserName,
                                       newUserpassword, productName);
            assertTrue(driver.getPageSource().contains("dashboard"),
                       "New User Failed to Log in :");
            userLogout();
            //login with admin credientails
            StratosUserLogin.userLogin(driver, selenium, userName, password, productName);
            gotoUserManagementPage();
            UserManagementSeleniumUtils.deleteUserByName(driver, newUserName);
            gotoRoleManagementPage();
            UserManagementSeleniumUtils.deleteRoleByName(driver, roleName);
            userLogout();
            log.info("*******IS Stratos - Login Only Permission Scenario Test - Passed **********");
        } catch (AssertionError e) {
            log.info("Login Only Permission Scenario Test Failed :" + e);
            userLogout();
            throw new AssertionError("Login Only Permission Scenario Test Failed " + e);
        } catch (WebDriverException e) {
            log.info("Login Only Permission Scenario Test Failed :" + e);
            userLogout();
            throw new WebDriverException("Login Only Permission Scenario Test Failed :" + e);
        } catch (Exception e) {
            log.info("Login Only Permission Scenario Test Failed :" + e);
            userLogout();
            throw new Exception("Login Only Permission Scenario Test Failed :" + e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        driver.quit();
    }


    private void gotoUserManagementPage() throws InterruptedException {
        driver.findElement(By.id("menu-panel-button3")).click();
        driver.findElement(By.linkText("Users and Roles")).click();
        driver.findElement(By.linkText("Users")).click();
    }

    private void gotoRoleManagementPage() throws InterruptedException {
        driver.findElement(By.id("menu-panel-button3")).click();
        driver.findElement(By.linkText("Users and Roles")).click();
        driver.findElement(By.linkText("Roles")).click();
    }

    private void createNewUser(String newUserName, String newUserpassword)
            throws InterruptedException {
        driver.findElement(By.linkText("Add New User")).click();
        //enter user info
        driver.findElement(By.name("username")).sendKeys(newUserName);
        driver.findElement(By.name("password")).sendKeys(newUserpassword);
        driver.findElement(By.name("retype")).sendKeys(newUserpassword);
        driver.findElement(By.xpath("//input[2]")).click();
        log.info("New user was created :" + newUserName);
    }

    private void addRole(String userName, String roleName) throws InterruptedException {
        driver.findElement(By.linkText("Add New Role")).click();
        driver.findElement(By.name("roleName")).sendKeys(roleName);
        driver.findElement(By.xpath("//tr[2]/td/input")).click();
        driver.findElement(By.xpath("//div[3]/table/tbody/tr/td[4]/div")).click();
        driver.findElement(By.xpath("//input[2]")).click();
        driver.findElement(By.xpath("//input")).sendKeys(userName);
        driver.findElement(By.xpath("//td[3]/input")).click();
        driver.findElement(By.name("selectedUsers")).click();
        driver.findElement(By.xpath("//input[2]")).click();
    }

    private void userLogout() throws InterruptedException {
        driver.findElement(By.linkText("Sign-out")).click();
    }
}
