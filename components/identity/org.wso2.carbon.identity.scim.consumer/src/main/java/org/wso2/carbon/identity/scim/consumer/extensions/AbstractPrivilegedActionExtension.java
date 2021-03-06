/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.identity.scim.consumer.extensions;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.util.JavaUtils;
import org.wso2.carbon.identity.scim.common.impl.AbstractProvisioningHandler;
import org.wso2.carbon.server.admin.privilegedaction.PrivilegedAction;

public abstract class AbstractPrivilegedActionExtension extends AbstractProvisioningHandler
        implements PrivilegedAction {
    public boolean doesHandle(MessageContext messageContext) {
        String method;
        AxisOperation operation = messageContext.getOperationContext().getAxisOperation();
        if ((operation.getName() != null) && ((method =
                JavaUtils.xmlNameToJavaIdentifier(operation.getName().getLocalPart())) != null)) {
            if (method.equals(getSOAPAction())) {
                return true;
            }
        }
        return false;
    }

    public abstract String getSOAPAction();

}
