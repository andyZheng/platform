/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.registry.resource.test;

import org.apache.abdera.model.AtomDate;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axis2.AxisFault;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.ManageEnvironment;
import org.wso2.carbon.automation.core.utils.environmentutils.ProductUrlGeneratorUtil;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkFactory;
import org.wso2.carbon.automation.core.utils.frameworkutils.FrameworkProperties;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.resource.stub.ResourceAdminServiceExceptionException;
import org.wso2.carbon.registry.resource.stub.common.xsd.ResourceData;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ResourceManagementTestCase {

    private ManageEnvironment environment;
    private EnvironmentBuilder builder;
    private UserInfo userInfo;
    private ResourceAdminServiceClient resourceAdminClient;

    private static final String PATH = "/c3/";
    private static final String RES_NAME = "rootTestResource";
    private static final String RES_NAME_AFTER_RENAME = "TestResource";
    private static final String RES_NAME_AFTER_MOVING = "MovedTestResource";
    private static final String RES_MOVED_LOCATION = "/c1/c2/";
    private static final String RES_COPIED_LOCATION = "/c1/";
    private static final String RES_NAME_AFTER_COPYING = "CopiedTestResource";
    private static final String RES_DESC = "A test resource";

    public static final String REGISTRY_NAMESPACE = "http://wso2.org/registry";

    @BeforeClass(alwaysRun = true)
    public void initialize()
            throws LoginAuthenticationExceptionException, RemoteException, RegistryException {
        int userId = 1;
        builder = new EnvironmentBuilder().greg(userId);
        environment = builder.build();
        userInfo = UserListCsvReader.getUserInfo(userId);
        resourceAdminClient =
                new ResourceAdminServiceClient(environment.getGreg().getBackEndUrl(), userInfo.getUserName(),
                        userInfo.getPassword());
    }

    @Test
    public void testAddResource()
            throws ResourceAdminServiceExceptionException, RemoteException, MalformedURLException {

        String path = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" + File.separator
                + "GREG" + File.separator + "testresource.txt";
        DataHandler dataHandler = new DataHandler(new URL("file:///" + path));

        String fileType = "plain/text";
        resourceAdminClient.addResource(PATH+RES_NAME,fileType,RES_DESC,dataHandler);

        String authorUserName = resourceAdminClient.getResource(PATH + RES_NAME)[0].getAuthorUserName();
        assertTrue(userInfo.getUserName().equalsIgnoreCase(authorUserName), "Resource creation failure");

    }

    @Test(dependsOnMethods = "testAddResource", enabled = true)
    public void testFeed()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException {

        ResourceData[] rData = resourceAdminClient.getResource(PATH + RES_NAME);

        OMElement atomFeedOMElement = getAtomFeedContent(constructAtomUrl(PATH + RES_NAME));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                "Created time is incorrect");

    }

    @Test(dependsOnMethods = "testFeed", enabled = true)
    public void testRenameResource()
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminClient.renameResource(PATH, PATH+RES_NAME, RES_NAME_AFTER_RENAME);
        boolean found = false;
        ResourceData[] rData = resourceAdminClient.getResource(PATH + RES_NAME_AFTER_RENAME);

        for (ResourceData resource : rData) {
            if (RES_NAME_AFTER_RENAME.equalsIgnoreCase(resource.getName())) {
                found = true;
            }
        }

        assertTrue(found, "Rename collection error");
    }

    @Test(dependsOnMethods = "testRenameResource", enabled = true)
    public void testFeedAfterRename()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException {

        ResourceData[] rData = resourceAdminClient.getResource(PATH + RES_NAME_AFTER_RENAME);

        OMElement atomFeedOMElement = getAtomFeedContent(constructAtomUrl(PATH + RES_NAME_AFTER_RENAME));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                "Created time is incorrect after renaming.");

    }

    @Test(dependsOnMethods = "testFeedAfterRename")
    public void testCopyResource()
            throws ResourceAdminServiceExceptionException, RemoteException {

        //copy resource
        resourceAdminClient.copyResource(PATH, PATH+RES_NAME_AFTER_RENAME, RES_COPIED_LOCATION, RES_NAME_AFTER_COPYING);

        //check that the collection has been moved
        String copiedDesc =
                resourceAdminClient.getResource(RES_COPIED_LOCATION + RES_NAME_AFTER_COPYING)[0].getDescription();

        assertTrue(RES_DESC.equalsIgnoreCase(copiedDesc), "Resource has not being copied");

    }

    @Test(dependsOnMethods = "testCopyResource", enabled = true)
    public void testFeedAfterCopying()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException {

        ResourceData[] rData = resourceAdminClient.getResource(RES_COPIED_LOCATION + RES_NAME_AFTER_COPYING);

        OMElement atomFeedOMElement =
                getAtomFeedContent(constructAtomUrl(RES_COPIED_LOCATION + RES_NAME_AFTER_COPYING));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                "Created time is incorrect after copying.");

    }

    @Test(dependsOnMethods = "testFeedAfterCopying", enabled = true)
    public void testMoveResource()
            throws ResourceAdminServiceExceptionException, RemoteException, InterruptedException {


        //move the resource
        resourceAdminClient.moveResource(PATH, PATH+RES_NAME_AFTER_RENAME, RES_MOVED_LOCATION, RES_NAME_AFTER_MOVING);
        Thread.sleep(2000);


        //check that the collection has been moved
        String movedDesc =
                resourceAdminClient.getResource(RES_MOVED_LOCATION + RES_NAME_AFTER_MOVING)[0].getDescription();

        assertTrue(RES_DESC.equalsIgnoreCase(movedDesc), "Resource has not being moved");


    }

    @Test(dependsOnMethods = "testMoveResource", enabled = true)
    public void testFeedAfterMoving()
            throws ResourceAdminServiceExceptionException, IOException, XMLStreamException {

        ResourceData[] rData = resourceAdminClient.getResource(RES_MOVED_LOCATION + RES_NAME_AFTER_MOVING);

        OMElement atomFeedOMElement = getAtomFeedContent(constructAtomUrl(RES_MOVED_LOCATION + RES_NAME_AFTER_MOVING));

        assertNotNull(atomFeedOMElement, "No feed data available");

        //checking whether the created time is correct
        OMElement createdElement = atomFeedOMElement.getFirstChildWithName(
                new QName(REGISTRY_NAMESPACE, "createdTime"));

        assertTrue(createdElement.getText().equalsIgnoreCase(getAtomDateString(rData[0].getCreatedOn().getTime())),
                "Created time is incorrect after moving.");

    }

    @Test(dependsOnMethods = "testFeedAfterMoving", expectedExceptions = AxisFault.class)
    public void testDeleteCollection()
            throws ResourceAdminServiceExceptionException, RemoteException {

        resourceAdminClient.deleteResource(RES_MOVED_LOCATION+RES_NAME_AFTER_MOVING);
        resourceAdminClient.getResource(RES_MOVED_LOCATION+RES_NAME_AFTER_MOVING);


    }


    private String getAtomDateString(Date date) {
        AtomDate atomDate = new AtomDate(date);
        return atomDate.getValue();
    }

    private String constructAtomUrl(String feedPath) {

        FrameworkProperties gregProperties =
                FrameworkFactory.getFrameworkProperties(ProductConstant.GREG_SERVER_NAME);

        String registryURL;
        if (builder.getFrameworkSettings().getEnvironmentSettings().is_runningOnStratos()) {
            registryURL =
                    ProductUrlGeneratorUtil.getRemoteRegistryURLOfStratos(environment.getGreg().getProductVariables().
                            getHttpsPort(), environment.getGreg().getProductVariables().getHostName(),
                            gregProperties, userInfo);
        } else {
            registryURL =
                    ProductUrlGeneratorUtil.getRemoteRegistryURLOfProducts(environment.getGreg().getProductVariables().
                            getHttpsPort(), environment.getGreg().getProductVariables().getHostName(),
                            environment.getGreg().getProductVariables().getWebContextRoot());
        }
        return registryURL + "atom" + feedPath;
    }

    private OMElement getAtomFeedContent(String registryUrl) throws IOException,
            XMLStreamException {
        StringBuilder sb;
        InputStream inputStream = null;
        URL url = new URL(registryUrl);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            String userPassword = userInfo.getUserName() + ":" + userInfo.getPassword();
            String encodedAuthorization = Base64Utils.encode(userPassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic " +
                    encodedAuthorization);
            connection.connect();

            inputStream = connection.getInputStream();
            sb = new StringBuilder();
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            assert inputStream != null;
            inputStream.close();
        }

        return AXIOMUtil.stringToOM(sb.toString());

    }


    @AfterClass
    public void cleanUp() throws ResourceAdminServiceExceptionException, RemoteException {
        resourceAdminClient.deleteResource(PATH);
        resourceAdminClient.deleteResource(RES_COPIED_LOCATION);
    }


}
