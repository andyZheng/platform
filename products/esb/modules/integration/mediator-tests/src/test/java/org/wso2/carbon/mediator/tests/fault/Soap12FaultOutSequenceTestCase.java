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
package org.wso2.carbon.mediator.tests.fault;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.Test;
import org.wso2.esb.integration.ESBIntegrationTestCase;
import org.wso2.esb.integration.axis2.SampleAxis2Server;
import org.wso2.esb.integration.axis2.StockQuoteClient;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class Soap12FaultOutSequenceTestCase extends ESBIntegrationTestCase {
    private StockQuoteClient axis2Client;

    public void init() throws Exception {
        axis2Client = new StockQuoteClient();
        String filePath = "/mediators/fault/soap12_fault_out_sequence_synapse.xml";
        loadESBConfigurationFromClasspath(filePath);
        launchBackendAxis2Service(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
    }

    @Test(groups = {"wso2.esb"}, description = "Creating SOAP1.2 fault messages from out sequence")
    public void testSOAP12FaultFromOutSequence() throws AxisFault {
        OMElement response;
        try {
            response = axis2Client.sendSimpleStockQuoteRequest(
                    getMainSequenceURL(),
                    "http://localhost:9000/services/SimpleStockQuoteService",
                    "WSO2");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            log.info("Test passed with Fault Message : " + expected.getMessage());
            assertEquals(expected.getReason(), "Custom ERROR Message", "Custom ERROR Message mismatched");
            assertEquals(expected.getFaultCode().getLocalPart(), "VersionMismatch", "Fault code value mismatched");
            assertEquals(expected.getFaultCode().getPrefix(), "soap12Env", "Fault code prefix mismatched");
            assertEquals(expected.getFaultRoleElement().getRoleValue(), "automation", "Role mismatched");
            assertEquals(expected.getFaultNodeElement().getNodeValue(), "automation-node", "Fault node mismatched");
            assertEquals(expected.getFaultDetailElement().getText(), "fault details by automation", "Fault detail mismatched");

        }

    }

    @Override
    protected void cleanup() {
        super.cleanup();
        axis2Client.destroy();
    }
}
