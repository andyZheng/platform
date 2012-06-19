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
package org.wso2.carbon.eventbridge.receiver.thrift.internal.utils;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eventbridge.core.internal.utils.EventBridgeConstants;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.eventbridge.receiver.thrift.conf.ThriftEventReceiverConfiguration;

import javax.xml.namespace.QName;

/**
 * Helper class to build Agent Server Initial Configurations
 */
public final class ThriftEventReceiverBuilder {

    private static final Log log = LogFactory.getLog(ThriftEventReceiverBuilder.class);

    private ThriftEventReceiverBuilder() {
    }


    private static void populatePorts(OMElement config,
                                      int portOffset,
                                      ThriftEventReceiverConfiguration thriftEventReceiverConfiguration) {
        OMElement secureEventReceiverPort = config.getFirstChildWithName(
                new QName(EventBridgeConstants.EVENT_BRIDGE_NAMESPACE,
                          ThriftEventReceiverConstants.SECURE_PORT_ELEMENT));
        if (secureEventReceiverPort != null) {
            try {
                thriftEventReceiverConfiguration.setSecureEventReceiverPort(Integer.parseInt(secureEventReceiverPort.getText()) + portOffset);
            } catch (NumberFormatException ignored) {

            }
        }
        OMElement receiverPort = config.getFirstChildWithName(
                new QName(EventBridgeConstants.EVENT_BRIDGE_NAMESPACE,
                          ThriftEventReceiverConstants.PORT_ELEMENT));
        if (receiverPort != null) {
            try {
                thriftEventReceiverConfiguration.setEventReceiverPort(Integer.parseInt(receiverPort.getText()) + portOffset);
            } catch (NumberFormatException ignored) {

            }
        }
    }


    public static int readPortOffset(ServerConfigurationService serverConfiguration) {

        String portOffset = serverConfiguration.getFirstProperty(ThriftEventReceiverConstants.CARBON_CONFIG_PORT_OFFSET_NODE);

        try {
            return ((portOffset != null) ? Integer.parseInt(portOffset.trim()) : ThriftEventReceiverConstants.CARBON_DEFAULT_PORT_OFFSET);
        } catch (NumberFormatException e) {
            return ThriftEventReceiverConstants.CARBON_DEFAULT_PORT_OFFSET;
        }
    }


    public static void populateConfigurations(int portOffset,
                                              ThriftEventReceiverConfiguration thriftEventReceiverConfiguration,
                                              OMElement initialConfig) {
        if (initialConfig != null) {
            OMElement thriftReceiverConfig = initialConfig.getFirstChildWithName(
                    new QName(EventBridgeConstants.EVENT_BRIDGE_NAMESPACE,
                              ThriftEventReceiverConstants.THRIFT_EVENT_RECEIVER_ELEMENT));
            if (thriftReceiverConfig != null) {
                populatePorts(thriftReceiverConfig, portOffset, thriftEventReceiverConfiguration);
            }
        }
    }
}