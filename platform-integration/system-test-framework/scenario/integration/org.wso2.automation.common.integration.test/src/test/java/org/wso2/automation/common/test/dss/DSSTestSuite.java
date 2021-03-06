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
package org.wso2.automation.common.test.dss;

import org.testng.annotations.AfterSuite;
import org.wso2.automation.common.test.dss.datasource.DataSourceInitializationAtStartUp;
import org.wso2.automation.common.test.dss.faultyservice.InvalidClosingTagFaultyServiceTest;
import org.wso2.automation.common.test.dss.fileservice.CSVSampleServiceTest;
import org.wso2.automation.common.test.dss.fileservice.ExcelSampleServiceTest;
import org.wso2.automation.common.test.dss.rssmanager.RSSManagerTest;
import org.wso2.automation.common.test.dss.service.OutputMappingAsAttributeDataServiceTest;
import org.wso2.automation.common.test.dss.syntax.ReturnRequestStatusTest;
import org.wso2.automation.common.test.dss.syntax.WhiteSpaceWithQueryParamsTest;
import org.wso2.platform.test.core.ProductConstant;
import org.wso2.platform.test.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.platform.test.core.utils.frameworkutils.FrameworkFactory;
import org.wso2.platform.test.core.utils.suiteutills.MasterTestSuite;
import org.wso2.platform.test.core.utils.suiteutills.SuiteVariables;
import org.wso2.automation.common.test.dss.faultyservice.EditFaultyDataServiceTest;
import org.wso2.automation.common.test.dss.faultyservice.FaultyServiceTest;
import org.wso2.automation.common.test.dss.faultyservice.ServiceFaultyTest;
import org.wso2.automation.common.test.dss.fileservice.CSVDataServiceTest;
import org.wso2.automation.common.test.dss.fileservice.ExcelDataServiceTest;
import org.wso2.automation.common.test.dss.fileservice.GSpreadDataServiceTest;
import org.wso2.automation.common.test.dss.scheduletask.AddScheduleTaskTest;
import org.wso2.automation.common.test.dss.scheduletask.ReScheduleTaskTest;
import org.wso2.automation.common.test.dss.service.BatchRequestTest;
import org.wso2.automation.common.test.dss.service.CarbonDataSourceTest;
import org.wso2.automation.common.test.dss.service.DistributedTransactionTest;
import org.wso2.automation.common.test.dss.service.EventingServiceTest;
import org.wso2.automation.common.test.dss.service.FileServiceTest;
import org.wso2.automation.common.test.dss.service.InputParametersValidationTest;
import org.wso2.automation.common.test.dss.service.MySqlDataServiceTest;
import org.wso2.automation.common.test.dss.service.MySqlStoredProcedureTest;
import org.wso2.automation.common.test.dss.service.NestedQueryTest;
import org.wso2.automation.common.test.dss.service.RestFulServiceTest;
import org.wso2.automation.common.test.dss.service.SecureDataServiceTest;

import java.util.ArrayList;
import java.util.List;

public class DSSTestSuite extends MasterTestSuite {

    @AfterSuite
    public void suiteRunner() {
        List<SuiteVariables> suiteVariablesList = new ArrayList<SuiteVariables>();
        EnvironmentBuilder env = new EnvironmentBuilder();

        suiteVariablesList.add(new SuiteVariables("CSVSampleDataService", CSVSampleServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("CSVDataServiceWithRegistry", CSVDataServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("ExcelDataService", ExcelSampleServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("ExcelDataServiceWithRegistry", ExcelDataServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("GoogleSpreadSheetDataService", GSpreadDataServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("MySqlDataServiceTest", MySqlDataServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("MySqlStoredProcedureTest", MySqlStoredProcedureTest.class));
        suiteVariablesList.add(new SuiteVariables("NestedQueryTest", NestedQueryTest.class));
        suiteVariablesList.add(new SuiteVariables("CarbonDataSourceTest", CarbonDataSourceTest.class));
        suiteVariablesList.add(new SuiteVariables("DistributedTransactionTest", DistributedTransactionTest.class));
        suiteVariablesList.add(new SuiteVariables("RestFulServiceTest", RestFulServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("BatchRequestTest", BatchRequestTest.class));
        suiteVariablesList.add(new SuiteVariables("SecureDataServiceTest", SecureDataServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("InputParametersValidationTest", InputParametersValidationTest.class));
        suiteVariablesList.add(new SuiteVariables("FileServiceTest", FileServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("EventingServiceTest", EventingServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("FaultyServiceTest", FaultyServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("ServiceFaultyTest", ServiceFaultyTest.class));
        suiteVariablesList.add(new SuiteVariables("EditFaultyDataServiceTest", EditFaultyDataServiceTest.class));
        suiteVariablesList.add(new SuiteVariables("ReturnRequestStatusTest", ReturnRequestStatusTest.class));
        suiteVariablesList.add(new SuiteVariables("WhiteSpaceWithQueryParamsTest", WhiteSpaceWithQueryParamsTest.class));
        suiteVariablesList.add(new SuiteVariables("OutputMappingAsAttributeDataServiceTest", OutputMappingAsAttributeDataServiceTest.class));

        if (env.getFrameworkSettings().getEnvironmentSettings().is_runningOnStratos()) {
            suiteVariablesList.add(new SuiteVariables("RSSManager", RSSManagerTest.class));

        } else {
            suiteVariablesList.add(new SuiteVariables("AddScheduleTaskTest", AddScheduleTaskTest.class));
            suiteVariablesList.add(new SuiteVariables("ReScheduleTaskTest", ReScheduleTaskTest.class));
            suiteVariablesList.add(new SuiteVariables("DataSourceInitializationAtStartUp", DataSourceInitializationAtStartUp.class));
            suiteVariablesList.add(new SuiteVariables("InvalidClosingTagFaultyServiceTest", InvalidClosingTagFaultyServiceTest.class));

        }
        setServerList(ProductConstant.DSS_SERVER_NAME + "," + ProductConstant.ESB_SERVER_NAME);
        superSuite("DataServiceServer", suiteVariablesList).run();
    }

    public static void main(String[] args) {
        new DSSTestSuite().suiteRunner();
    }

}