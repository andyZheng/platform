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
package org.wso2.carbon.mediator.test.xslt;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.registry.PropertiesAdminServiceClient;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.mediator.test.ESBMediatorTest;

import javax.activation.DataHandler;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class DynamicKeyXsltTransformationTestCase extends ESBMediatorTest {

    @Test(groups = {"wso2.esb"},
          description = "Do XSLT transformation by Select the key type as dynamic key and retrieve" +
                        " the transformation from that.")
    public void xsltTransformationFromDynamicKey() throws Exception {
        OMElement response;
        uploadResourcesToRegistry();
        response = axis2Client.sendCustomQuoteRequest(
                getMainSequenceURL(),
                null,
                "IBM");
        assertNotNull(response, "Response message null");
        assertTrue(response.toString().contains("Code"));
        assertTrue(response.toString().contains("IBM"));

    }


    @Override
    protected void uploadSynapseConfig() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/xslt/xslt_dynamic_key_synapse.xml");
    }

    private void uploadResourcesToRegistry() throws Exception {
        ResourceAdminServiceClient  resourceAdminServiceClient=
                new ResourceAdminServiceClient(esbServer.getBackEndUrl(), userInfo.getUserName(), userInfo.getPassword());
        PropertiesAdminServiceClient propertiesAdminServiceClient =
                new PropertiesAdminServiceClient(esbServer.getBackEndUrl(),userInfo.getUserName(), userInfo.getPassword());

        resourceAdminServiceClient.deleteResource("/_system/config/localEntries");
        resourceAdminServiceClient.addCollection("/_system/config/", "localEntries", "",
                                               "Contains dynamic sequence request entry");

        resourceAdminServiceClient.addResource(
                "/_system/config/localEntries/request_transformation.txt", "text/plain", "text files",
                new DataHandler("Dynamic Sequence request transformation".getBytes(), "application/text"));
        propertiesAdminServiceClient.setProperty("/_system/config/localEntries/request_transformation.txt",
                                               "resourceName", "request_transform.xslt");
        Thread.sleep(1000);

        resourceAdminServiceClient.deleteResource("/_system/governance/localEntries");
        resourceAdminServiceClient.addCollection("/_system/governance/", "localEntries", "",
                                               "Contains dynamic sequence response entry");
        resourceAdminServiceClient.addResource(
                "/_system/governance/localEntries/response_transformation_back.txt", "text/plain", "text files",
                new DataHandler("Dynamic Sequence response transformation".getBytes(), "application/text"));
        propertiesAdminServiceClient.setProperty("/_system/governance/localEntries/response_transformation_back.txt",
                                                 "resourceName", "response_transform.xslt");


    }
}
