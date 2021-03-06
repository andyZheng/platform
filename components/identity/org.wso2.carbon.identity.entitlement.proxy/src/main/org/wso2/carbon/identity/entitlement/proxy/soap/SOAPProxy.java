/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.carbon.identity.entitlement.proxy.soap;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.TransportOutDescription;
import org.wso2.carbon.identity.entitlement.proxy.*;
import org.wso2.carbon.identity.entitlement.proxy.exception.EntitlementProxyException;
import org.wso2.carbon.identity.entitlement.stub.EntitlementPolicyAdminServiceStub;
import org.wso2.carbon.identity.entitlement.stub.EntitlementServiceStub;
import org.wso2.carbon.identity.entitlement.stub.dto.EntitledAttributesDTO;
import org.wso2.carbon.identity.entitlement.stub.dto.EntitledResultSetDTO;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SOAPProxy extends AbstractPDPProxy {

    private final static String SESSION_TIME_OUT = "50977";

    private Map<String, EntitlementServiceStub> stubs = new ConcurrentHashMap<String, EntitlementServiceStub>();
    private Map<String, EntitlementPolicyAdminServiceStub> adminStubs = new ConcurrentHashMap<String, EntitlementPolicyAdminServiceStub>();
    private Map<String, Authenticator> authenticators = new ConcurrentHashMap<String, Authenticator>();

    private PDPConfig config;

    public SOAPProxy() throws Exception {

    }

    @Override
    public boolean getDecision(Attribute[] attributes, String appId) throws Exception {
        String xacmlRequest = XACMLRequetBuilder.buildXACML3Request(attributes);
        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementServiceStub stub = getEntitlementStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        String result = getDecision(xacmlRequest, stub, autheticator);
        stub._getServiceClient().cleanupTransport();

        return ("permit".equalsIgnoreCase(result));
    }

    @Override
    public String getActualDecision(Attribute[] attributes, String appId) throws Exception {

        String xacmlRequest = XACMLRequetBuilder.buildXACML3Request(attributes);
        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementServiceStub stub = getEntitlementStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        String result = getDecision(xacmlRequest, stub, autheticator);
        stub._getServiceClient().cleanupTransport();

        return result;
    }

    @Override
    public boolean subjectCanActOnResource(String subjectType, String alias, String actionId,
                                           String resourceId, String domainId, String appId) throws Exception {


        Attribute subjectAttribute = new Attribute("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject", subjectType, ProxyConstants.DEFAULT_DATA_TYPE, alias);
        Attribute actionAttribute = new Attribute("urn:oasis:names:tc:xacml:3.0:attribute-category:action", "urn:oasis:names:tc:xacml:1.0:action:action-id", ProxyConstants.DEFAULT_DATA_TYPE, actionId);
        Attribute resourceAttribute = new Attribute("urn:oasis:names:tc:xacml:3.0:attribute-category:resource", "urn:oasis:names:tc:xacml:1.0:resource:resource-id", ProxyConstants.DEFAULT_DATA_TYPE, resourceId);
        Attribute environmentAttribute = new Attribute("urn:oasis:names:tc:xacml:3.0:attribute-category:environment", "urn:oasis:names:tc:xacml:1.0:environment:environment-id", ProxyConstants.DEFAULT_DATA_TYPE, domainId);

        Attribute[] tempArr = {subjectAttribute, actionAttribute, resourceAttribute, environmentAttribute};

        String xacmlRequest = XACMLRequetBuilder.buildXACML3Request(tempArr);

        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementServiceStub stub = getEntitlementStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        String result = getDecision(xacmlRequest, stub, autheticator);
        stub._getServiceClient().cleanupTransport();

        return ("permit".equalsIgnoreCase(result));
    }

    @Override
    public boolean subjectCanActOnResource(String subjectType, String alias, String actionId,
                                           String resourceId, Attribute[] attributes, String domainId, String appId)
            throws Exception {

        Attribute[] attrs = new Attribute[attributes.length + 4];
        attrs[0] = new Attribute("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject", subjectType, ProxyConstants.DEFAULT_DATA_TYPE, alias);

        for (int i = 0; i < attributes.length; i++) {
            attrs[i + 1] = new Attribute("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject", attributes[i].getType(),
                    attributes[i].getId(), attributes[i].getValue());
        }

        attrs[attrs.length - 3] = new Attribute("urn:oasis:names:tc:xacml:3.0:attribute-category:action", "urn:oasis:names:tc:xacml:1.0:action:action-id", ProxyConstants.DEFAULT_DATA_TYPE, actionId);
        attrs[attrs.length - 2] = new Attribute("urn:oasis:names:tc:xacml:3.0:attribute-category:resource", "urn:oasis:names:tc:xacml:1.0:resource:resource-id", ProxyConstants.DEFAULT_DATA_TYPE, resourceId);
        attrs[attrs.length - 1] = new Attribute("urn:oasis:names:tc:xacml:3.0:attribute-category:environment", "urn:oasis:names:tc:xacml:1.0:environment:environment-id", ProxyConstants.DEFAULT_DATA_TYPE, domainId);

        String xacmlRequest = XACMLRequetBuilder.buildXACML3Request(attrs);

        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementServiceStub stub = getEntitlementStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        String result = getDecision(xacmlRequest, stub, autheticator);
        stub._getServiceClient().cleanupTransport();

        return ("permit".equalsIgnoreCase(result));
    }

    @Override
    public List<String> getResourcesForAlias(String alias, String appId) throws Exception {
        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementPolicyAdminServiceStub stub = getEntitlementAdminStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        List<String> results = getResources(getEntitledAttributes(alias, null,
                ProxyConstants.SUBJECT_ID, null, false, stub, autheticator));
        stub._getServiceClient().cleanupTransport();

        return results;
    }

    @Override
    public List<String> getActionableResourcesForAlias(String alias, String appId) throws Exception {
        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementPolicyAdminServiceStub stub = getEntitlementAdminStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        List<String> results = getResources(getEntitledAttributes(alias, null,
                ProxyConstants.SUBJECT_ID, null, true, stub, autheticator));
        stub._getServiceClient().cleanupTransport();

        return results;
    }

    @Override
    public List<String> getActionsForResource(String alias, String resource, String appId)
            throws Exception {
        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementPolicyAdminServiceStub stub = getEntitlementAdminStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        List<String> results = getActions(getEntitledAttributes(alias, resource,
                ProxyConstants.SUBJECT_ID, null, false, stub, autheticator));
        stub._getServiceClient().cleanupTransport();

        return results;
    }

    @Override
    public List<String> getActionableChidResourcesForAlias(String alias, String parentResource,
                                                           String action, String appId) throws Exception {
        String serverUrl = getServerUrl(appId);
        Credentials credentials = getCrdentials(appId);
        EntitlementPolicyAdminServiceStub stub = getEntitlementAdminStub(serverUrl);
        Authenticator autheticator = getAuthenticator(serverUrl, credentials.userName,
                credentials.password);
        List<String> results = getResources(getEntitledAttributes(alias, parentResource,
                ProxyConstants.SUBJECT_ID, action, true, stub, autheticator));
        stub._getServiceClient().cleanupTransport();

        return results;
    }

    @Override
    public void setPDPConfig(PDPConfig config) {
        this.config = config;

    }

    protected Authenticator getAuthenticator(String serverUrl, String userName, String password)
            throws Exception {

        if (authenticators.containsKey(serverUrl)) {
            return authenticators.get(serverUrl);
        }

        Authenticator authenticator;
        authenticator = new Authenticator(userName, password, serverUrl + "AuthenticationAdmin");
        setAuthCookie(false, getEntitlementStub(serverUrl), authenticator);
        setAuthCookie(false, getEntitlementAdminStub(serverUrl), authenticator);

        authenticators.put(serverUrl, authenticator);
        return authenticator;
    }

    protected EntitlementServiceStub getEntitlementStub(String serverUrl) throws Exception {

        if (stubs.containsKey(serverUrl)) {
            return stubs.get(serverUrl);
        }

        EntitlementServiceStub stub;
        ConfigurationContext configurationContext;
        configurationContext = ConfigurationContextFactory.createDefaultConfigurationContext();
        HashMap<String, TransportOutDescription> transportsOut = configurationContext
                .getAxisConfiguration().getTransportsOut();
        for (TransportOutDescription transportOutDescription : transportsOut.values()) {
            transportOutDescription.getSender().init(configurationContext, transportOutDescription);
        }

        stub = new EntitlementServiceStub(configurationContext, serverUrl + "EntitlementService");

        stubs.put(serverUrl, stub);
        return stub;
    }

    protected EntitlementPolicyAdminServiceStub getEntitlementAdminStub(String serverUrl)
            throws Exception {

        if (adminStubs.containsKey(serverUrl)) {
            return adminStubs.get(serverUrl);
        }

        EntitlementPolicyAdminServiceStub stub;
        ConfigurationContext configurationContext;
        configurationContext = ConfigurationContextFactory.createDefaultConfigurationContext();
        HashMap<String, TransportOutDescription> transportsOut = configurationContext
                .getAxisConfiguration().getTransportsOut();
        for (TransportOutDescription transportOutDescription : transportsOut.values()) {
            transportOutDescription.getSender().init(configurationContext, transportOutDescription);
        }

        stub = new EntitlementPolicyAdminServiceStub(configurationContext, serverUrl
                + "EntitlementPolicyAdminService");

        adminStubs.put(serverUrl, stub);
        return stub;
    }

    private String getDecision(String request, EntitlementServiceStub stub,
                               Authenticator autheticator) throws Exception {
        try {
            return getStatus(stub.getDecision(request));
        } catch (AxisFault e) {
            if (SESSION_TIME_OUT.equals(e.getFaultCode().getLocalPart())) {
                setAuthCookie(true, stub, autheticator);
                return getStatus(stub.getDecision(request));
            } else {
                throw e;
            }
        }
    }

    private EntitledAttributesDTO[] getEntitledAttributes(String subjectName, String resourceName,
                                                          String subjectId, String action, boolean enableChildSearch,
                                                          EntitlementPolicyAdminServiceStub stub, Authenticator autheticator) throws Exception {
        EntitledResultSetDTO results = null;
        try {
            results = stub.getEntitledAttributes(subjectName, resourceName, subjectId, action,
                    enableChildSearch, false);
        } catch (AxisFault e) {
            if (SESSION_TIME_OUT.equals(e.getFaultCode().getLocalPart())) {
                setAuthCookie(true, stub, autheticator);
                results = stub.getEntitledAttributes(subjectName, resourceName, subjectId, action,
                        enableChildSearch, false);
            } else {
                throw e;
            }
        }

        return results.getEntitledAttributesDTOs();
    }

    private List<String> getResources(EntitledAttributesDTO[] entitledAttrs) {
        List<String> list = new ArrayList<String>();

        if (entitledAttrs != null) {
            for (EntitledAttributesDTO dto : entitledAttrs) {
                list.add(dto.getResourceName());
            }
        }

        return list;
    }

    private List<String> getActions(EntitledAttributesDTO[] entitledAttrs) {
        List<String> list = new ArrayList<String>();

        if (entitledAttrs != null) {
            for (EntitledAttributesDTO dto : entitledAttrs) {
                list.add(dto.getAction());
            }
        }

        return list;
    }

    private String getStatus(String xmlstring) throws Exception {
        OMElement response = null;
        OMElement result = null;
        OMElement decision = null;

        response = AXIOMUtil.stringToOM(xmlstring);
        result = response.getFirstChildWithName(new QName("Result"));
        if (result != null) {
            decision = result.getFirstChildWithName(new QName("Decision"));
            if (decision != null) {
                return decision.getText();
            }
        }

        return "Invalid Status";
    }

    private void setAuthCookie(boolean isExpired, Stub stub, Authenticator authenticator)
            throws Exception {
        ServiceClient client = stub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING,
                authenticator.getCookie(isExpired));
    }

    private String getServerUrl(String appId) {
        if (config.getAppToPDPMap().containsKey(appId)) {
            String[] attributes = config.getAppToPDPMap().get(appId);
            if (attributes != null && attributes.length > 0) {
                return attributes[0];
            }
        }
        return null;
    }

    private Credentials getCrdentials(String appId) throws EntitlementProxyException {
        if (config.getAppToPDPMap().containsKey(appId)) {
            Credentials credentials = new Credentials();
            String[] attributes = config.getAppToPDPMap().get(appId);
            if (attributes != null && attributes.length > 2) {
                credentials.userName = attributes[1];
                credentials.password = attributes[2];
            } else {
                throw new EntitlementProxyException("User Name and Password Arguments are not provided correctley in the appConfig Array per the appID :" + appId);
            }
            return credentials;
        }
        return null;
    }

    static class Credentials {
        String userName;
        String password;
    }

}
