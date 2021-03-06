/*
 * Copyright (c) 2006, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.governance.lcm.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.governance.lcm.beans.LifecycleBean;
import org.wso2.carbon.registry.core.*;
import org.wso2.carbon.registry.core.config.RegistryConfigurationProcessor;
import org.wso2.carbon.registry.core.config.RegistryContext;
import org.wso2.carbon.registry.core.config.StaticConfiguration;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.core.utils.RegistryUtils;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ServerConstants;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommonUtil {

    private static final Log log = LogFactory.getLog(CommonUtil.class);
    private static String contextRoot = null;
    private static RegistryService registryService;
    public static final String searchLCMPropertiesQuery =
                    RegistryConstants.QUERIES_COLLECTION_PATH +
                    "/governance/searchLCMProperties";

    private static Validator lifecycleSchemaValidator = null;
    private static Validator aspectSchemaValidator = null;

    public static synchronized void setRegistryService(RegistryService service) {
        if (registryService == null) {
            registryService = service;
        }
    }

    public static RegistryService getRegistryService() {
        return registryService;
    }

    public static UserRegistry getRootSystemRegistry() throws RegistryException {
        if (registryService == null) {
            return null;
        } else {
            return registryService.getRegistry(CarbonConstants.REGISTRY_SYSTEM_USERNAME);
        }
    }

    /*public static UserRegistry getRegistry() throws RegistryException {

        MessageContext messageContext = MessageContext.getCurrentMessageContext();
        if (messageContext == null) {

            String msg = "Could not get the user's Registry session. Message context not found.";
            log.error(msg);
            throw new RegistryException(msg);
        }

        HttpServletRequest request =
                (HttpServletRequest) messageContext.getProperty("transport.http.servletRequest");

        return getRegistry(request);
    }

    public static UserRegistry getRegistry(HttpServletRequest request) throws RegistryException {

        UserRegistry registry =
                (UserRegistry) request.getSession().getAttribute(RegistryConstants.USER_REGISTRY);

        if (registry == null) {
            String msg = "User's Registry instance is not found. " +
                    "Users have to login to retrieve a registry instance. ";
            log.error(msg);
            throw new RegistryException(msg);
        }

        return registry;
    }


    public static UserRegistry getSystemRegistry() throws RegistryException {

        MessageContext messageContext = MessageContext.getCurrentMessageContext();
        if (messageContext == null) {
            String msg = "Could not get the user's Registry session. Message context not found.";
            log.error(msg);
            throw new RegistryException(msg);
        }

        HttpServletRequest request =
                (HttpServletRequest) messageContext.getProperty("transport.http.servletRequest");

        UserRegistry registry =
                (UserRegistry) request.getSession().getAttribute(RegistryConstants.SYSTEM_REGISTRY);

        if (registry == null) {
            String msg = "System Registry instance is not found. " +
                    "Users have to login to retrieve a system registry instance for the tenant. ";
            log.error(msg);
            throw new RegistryException(msg);
        }

        return registry;
    }*/

    public static boolean updateLifecycle(String oldName, LifecycleBean bean, Registry registry, Registry rootRegistry) throws RegistryException, XMLStreamException {
        String payload = LifecycleBeanPopulator.serializeLifecycleBean(bean);
        return updateLifecycle(oldName, payload, registry, rootRegistry);
    }

    public static boolean updateLifecycle(String oldName, String payload, Registry registry, Registry rootRegistry) throws RegistryException, XMLStreamException {
        if (isLifecycleNameInUse(oldName, registry, rootRegistry))
            throw new RegistryException("Could not update lifecycle name since it is already in use!");

        String newName = null;
        OMElement element = buildOMElement(payload);
//        We have added an validation here too since someone can directly call this method
        validateOMContent(element);

        if (element != null) {
            newName = element.getAttributeValue(new QName("name"));
        }

        if (newName == null || newName.equals(""))
            return false; // invalid configuration

        if (oldName == null || oldName.equals("")) {
            String path = getContextRoot() + newName;
            Resource resource;
            if (lifeCycleExists(newName, registry)) {
                return false; // we are adding a new lifecycle
            }
            else {
                resource = new ResourceImpl();
            }
            resource.setContent(payload);
            try {
                registry.beginTransaction();
                registry.put(path, resource);
                generateAspect(path, registry);
                registry.commitTransaction();
            } catch (Exception e) {
                registry.rollbackTransaction();
                String errorMsg = "Unable to update aspect. ";

                if (e.getCause() instanceof ClassNotFoundException) {
                    errorMsg += "Could not find class " + e.getCause().getMessage();
                } else if (e.getCause() instanceof com.ctc.wstx.exc.WstxParsingException) {
                    errorMsg += e.getCause().getMessage();
                }
                throw new RegistryException(errorMsg, e);
            }
            return true;
        }

        if (newName.equals(oldName)) {
            //updating the rest of the content
            String oldPath = getContextRoot() + oldName;
            Resource resource;
            if (lifeCycleExists(oldName, registry)) {
                resource = registry.get(oldPath);
            }
            else {
                resource = new ResourceImpl(); // will this ever happen?
            }
            resource.setContent(payload);
            try {
                registry.beginTransaction();
                registry.put(oldPath, resource);
                generateAspect(oldPath, registry);
                registry.commitTransaction();
            } catch (Exception e) {
                registry.rollbackTransaction();
                String errorMsg = "Unable to update aspect. ";

                if (e.getCause() instanceof ClassNotFoundException) {
                    errorMsg += "Could not find class " + e.getCause().getMessage();
                } else if (e.getCause() instanceof com.ctc.wstx.exc.WstxParsingException) {
                    errorMsg += e.getCause().getMessage();
                }
                throw new RegistryException(errorMsg, e);
            }
            return true;
        }
        else {
            String oldPath = getContextRoot() + oldName;
            String newPath = getContextRoot() + newName;

            if (lifeCycleExists(newName, registry)) {
                return false; // we are trying to use the name of a existing lifecycle
            }

            Resource resource;
            if (lifeCycleExists(oldName, registry)) {
                resource = registry.get(oldPath);
            }
            else {
                resource = new ResourceImpl(); // will this ever happen?
            }

            resource.setContent(payload);
            try {
                registry.beginTransaction();
                registry.put(newPath, resource);
                generateAspect(newPath, registry);
                registry.delete(oldPath);
                removeAspect(oldName, registry);
                registry.commitTransaction();
            } catch (Exception e) {
                registry.rollbackTransaction();
                throw new RegistryException("Unable to renew aspect", e);
            }
            return true;
        }
    }

    public static boolean createLifecycle(String configuration, Registry registry, Registry rootRegistry) throws RegistryException, XMLStreamException {
//        String configuration = LifecycleBeanPopulator.serializeLifecycleBean(bean);
        return addLifecycle(configuration, registry, rootRegistry);
    }

    public static boolean addLifecycle(String payload, Registry registry, Registry rootRegistry) throws RegistryException, XMLStreamException {
        String name;
        OMElement element = null;
        element = buildOMElement(payload);

//        We have added an validation here too since someone can directly call this method
        validateOMContent(element);
        name = element.getAttributeValue(new QName("name"));

        if (isLifecycleNameInUse(name, registry, rootRegistry))
            throw new RegistryException("The added lifecycle name is already in use!");

        String path = getContextRoot() + name;
        Resource resource;
        if (!lifeCycleExists(name, registry)) {
            resource = new ResourceImpl();
        }
        else {
            return false; // Already existing resource.
        }
        resource.setContent(payload);
        try {
            registry.beginTransaction();
            registry.put(path, resource);
            generateAspect(path, registry);
            registry.commitTransaction();
        } catch (Exception e) {
            registry.rollbackTransaction();
            String errorMsg = "Unable to initiate aspect. ";

            if (e.getCause() instanceof ClassNotFoundException) {
                errorMsg += "Could not find class " + e.getCause().getMessage();
            } else if (e.getCause() instanceof com.ctc.wstx.exc.WstxParsingException) {
                errorMsg += e.getCause().getMessage();
            }
            throw new RegistryException(errorMsg.replaceAll("<","&lt;").replaceAll(">","&gt;"), e);
        }
        return true;
    }

    public static void validateOMContent(OMElement element) throws RegistryException {
        if(!validateOMContent(element, getSCXMLSchemaValidator(getLifecycleSchemaLocation()))){
            String message = "Unable to validate the lifecycle configuration";
            log.error(message);
            throw new RegistryException(message);
        }
    }

    public static OMElement buildOMElement(String payload) throws RegistryException {
        OMElement element;
        try {
            element = AXIOMUtil.stringToOM(payload);
            element.build();
        } catch (Exception e) {
            String message = "Unable to parse the XML configuration. Please validate the XML configuration";
            log.error(message,e);
            throw new RegistryException(message,e);
        }
        return element;
    }

    public static boolean lifeCycleExists(String name, Registry registry) throws RegistryException {
        return registry.resourceExists(getContextRoot() + name);
    }

    public static boolean deleteLifecycle(String name, Registry registry, Registry rootRegistry) throws RegistryException, XMLStreamException {
        if (isLifecycleNameInUse(name, registry, rootRegistry))
            throw new RegistryException("Lifecycle could not be deleted, since it is already in use!");

        String path = getContextRoot() + name;
        if (registry.resourceExists(path)) {
            try {
                registry.beginTransaction();
                registry.delete(path);
                removeAspect(name, registry);
                registry.commitTransaction();
            } catch (Exception e) {
                registry.rollbackTransaction();
                throw new RegistryException("Unable to remove aspect", e);
            }
            return true;
        }
        return false;
    }

    public static String getLifecycleConfiguration(String name, Registry registry) throws RegistryException, XMLStreamException {
        String path = getContextRoot() + name;
        Resource resource;
        if (lifeCycleExists(name, registry)) {
            resource = registry.get(path);
            return RegistryUtils.decodeBytes((byte[])resource.getContent());
        }

        return null;
    }
    public static String getLifecycleConfigurationVersion(String name, Registry registry) throws RegistryException, XMLStreamException {
        String path = getContextRoot() + name;
        Resource resource;
        if (lifeCycleExists(name, registry)) {
            resource = registry.get(path);
            return String.valueOf(((ResourceImpl)resource).getVersionNumber());
        }

        return null;
    }

    public static boolean generateAspect(String resourceFullPath, Registry registry) throws RegistryException, XMLStreamException {
        RegistryContext registryContext = registry.getRegistryContext();
        if (registryContext == null) {
            return false;
        }
        Resource resource = registry.get(resourceFullPath);
        if (resource != null) {
            String content = null;
            if (resource.getContent() != null) {
                content = RegistryUtils.decodeBytes((byte[])resource.getContent());
            }
            if (content != null) {
                OMElement aspect = AXIOMUtil.stringToOM(content);
                if (aspect != null) {
                    OMElement dummy = OMAbstractFactory.getOMFactory().createOMElement("dummy", null);
                    dummy.addChild(aspect);
                    Aspect aspectinstance = RegistryConfigurationProcessor.updateAspects(dummy);
                    Iterator aspectElement = dummy.getChildrenWithName(new QName("aspect"));
                    String name = "";
                    if (aspectElement != null) {
                        OMElement aspectelement = (OMElement) aspectElement.next();
                        name = aspectelement.getAttributeValue(new QName("name"));
                    }
                    registry.addAspect(name,aspectinstance);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeAspect(String aspectname, Registry registry) throws RegistryException {
        return registry.removeAspect(aspectname);
    }

    public static String[] getLifecycleList(Registry registry) throws RegistryException{
        Collection collection;
        try {
            collection = (Collection)registry.get(getContextRoot());
        } catch (Exception e) {
            return null;
        }

        if (collection == null) {
            CollectionImpl lifeCycleCollection = new CollectionImpl();
            registry.put(getContextRoot(), lifeCycleCollection);
            return null;
        }
        else {
            if (collection.getChildCount() == 0) {
                return null;
            }

            String[] childrenList = collection.getChildren();
            String[] lifeCycleNameList = new String[collection.getChildCount()];
            for (int i = 0; i < childrenList.length; i++) {
                String path = childrenList[i];
                lifeCycleNameList[i] = path.substring(path.lastIndexOf(RegistryConstants.PATH_SEPARATOR) + 1);
            }
            return lifeCycleNameList;
        }
    }


    public static String getContextRoot() {
        if (contextRoot == null) {
            return RegistryConstants.LIFECYCLE_CONFIGURATION_PATH;
        }
        return contextRoot;
    }

    public static void setContextRoot(String contextRoot) {
        if (!contextRoot.endsWith(RegistryConstants.PATH_SEPARATOR))
            contextRoot += RegistryConstants.PATH_SEPARATOR;
        CommonUtil.contextRoot = contextRoot;
    }

    public static boolean addDefaultLifecyclesIfNotAvailable(Registry registry, Registry rootRegistry)
            throws RegistryException, FileNotFoundException, XMLStreamException {
        if (!registry.resourceExists(RegistryConstants.LIFECYCLE_CONFIGURATION_PATH)) {
            Collection lifeCycleConfigurationCollection = new CollectionImpl();
            String description = "Lifecycle configurations are stored here.";
            lifeCycleConfigurationCollection.setDescription(description);
            registry.put(RegistryConstants.LIFECYCLE_CONFIGURATION_PATH, lifeCycleConfigurationCollection);

            String defaultLifecycleConfig = System.getProperty(ServerConstants.CARBON_HOME) + File.separator+ "repository" +
                    File.separator + "resources" + File.separator + "lifecycles" + File.separator + "configurations.xml";

            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(new FileReader(defaultLifecycleConfig));
                String str;
                while ((str = in.readLine()) != null) {
                    sb.append(str).append("\n");
                }
                in.close();
            } catch (IOException e) {
                throw new RegistryException(e.toString());
            }

            addLifecycle(sb.toString(), registry, rootRegistry);
        }
        else {
            // invoke all the aspects with configurations for lifecycles
            Resource lifecycleRoot = registry.get(getContextRoot());
            if (!(lifecycleRoot instanceof Collection)) {
                String msg = "Failed to continue as the lifecycle configuration root: " + getContextRoot() +
                        " is not a collection.";
                log.error(msg);
                throw new RegistryException(msg);
            }
            Collection lifecycleRootCol = (Collection)lifecycleRoot;
            String[] lifecycleConfigPaths = lifecycleRootCol.getChildren();
            if (lifecycleConfigPaths != null) {
                for (String lifecycleConfigPath: lifecycleConfigPaths) {
                    generateAspect(lifecycleConfigPath, registry);
                }
            }
        }

        return true;
    }

    public static boolean isLifecycleNameInUse(String name, Registry registry, Registry rootRegistry) throws RegistryException, XMLStreamException {
        if (name.contains("<aspect")) {
            OMElement element = AXIOMUtil.stringToOM(name);
            if (element != null) {
                name = element.getAttributeValue(new QName("name"));

                boolean aspectFound = false;
                String[] aspects = registry.getAvailableAspects();
                if(aspects != null){
                    for (String aspect: aspects) {
                        if (name.equals(aspect)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            else
                throw new RegistryException("Lifecycle Configuration does not contain the name attribute");
        }

        String sql = null;
//        if (!registry.resourceExists(searchLCMPropertiesQuery)) {
            if(StaticConfiguration.isVersioningProperties()) {
                sql = "SELECT R.REG_PATH_ID, R.REG_NAME FROM REG_RESOURCE R , REG_PROPERTY PP, REG_RESOURCE_PROPERTY RP "
                        + "WHERE R.REG_VERSION=RP.REG_VERSION AND "
                        + "RP.REG_PROPERTY_ID=PP.REG_ID AND PP.REG_NAME=? AND PP.REG_VALUE=?";
            }
            else
            {
                sql = "SELECT R.REG_PATH_ID, R.REG_NAME FROM REG_RESOURCE R, REG_PROPERTY PP, REG_RESOURCE_PROPERTY RP WHERE "
                        + "R.REG_PATH_ID=RP.REG_PATH_ID AND "
                        + "((R.REG_NAME=RP.REG_RESOURCE_NAME) OR (R.REG_NAME IS NULL AND RP.REG_RESOURCE_NAME IS NULL)) AND "
                        + "RP.REG_PROPERTY_ID=PP.REG_ID AND "
                        + "PP.REG_NAME=? AND PP.REG_VALUE=?";
            }
            // This modification is done to stop the queries been stored to the registry.
            /*Resource q1 = registry.newResource();
            q1.setContent(sql);
            q1.setMediaType(RegistryConstants.SQL_QUERY_MEDIA_TYPE);
            q1.addProperty(RegistryConstants.RESULT_TYPE_PROPERTY_NAME,
                    RegistryConstants.RESOURCES_RESULT_TYPE);
            registry.put(searchLCMPropertiesQuery, q1);*/
//        }

        Map<String, String> parameters = new HashMap<String, String>();
//        if (sql != null && RegistryUtils.isRegistryReadOnly(registry.getRegistryContext())) {
//            parameters.put("query", sql);
//        }
        parameters.put("query", sql);
        parameters.put("1", "registry.LC.name");
        parameters.put("2", name);
//        Collection result = rootRegistry.executeQuery(RegistryConstants.CONFIG_REGISTRY_BASE_PATH +
//                searchLCMPropertiesQuery, parameters);
        Collection result = rootRegistry.executeQuery(null, parameters);

        String[] servicePaths = result.getChildren();

        if ((servicePaths == null) || (servicePaths.length == 0)) {
            return false;
        }

        return true;
    }


//    The methods have been duplicated in several places because there is no common bundle to place them.
//    We have to keep this inside different bundles so that users will not run in to problems if they uninstall some features
    public static boolean validateOMContent(OMElement omContent, Validator validator) {
        try {
            InputStream is = new ByteArrayInputStream(omContent.toString().getBytes("utf-8"));
            Source xmlFile = new StreamSource(is);
            if (validator != null) {
                validator.validate(xmlFile);
            }
        } catch (SAXException e) {
            log.error("Unable to parse the XML configuration. Please validate the XML configuration",e);
            return false;
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported content",e);
            return false;
        } catch (IOException e) {
            log.error("Unable to parse the XML configuration. Please validate the XML configuration",e);
            return false;
        }
        return true;
    }

    public static Validator getSCXMLSchemaValidator(String schemaPath){

        if (lifecycleSchemaValidator == null) {
            try {
                SchemaFactory schemaFactory = SchemaFactory
                        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(new File(schemaPath));
                lifecycleSchemaValidator = schema.newValidator();
            } catch (SAXException e) {
                log.error("Unable to get a schema validator from the given file path : " + schemaPath);
            }
        }
        return lifecycleSchemaValidator;
    }
    public static Validator getAspectSchemaValidator(String schemaPath){

        if (aspectSchemaValidator == null) {
            try {
                SchemaFactory schemaFactory = SchemaFactory
                        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = schemaFactory.newSchema(new File(schemaPath));
                aspectSchemaValidator = schema.newValidator();
            } catch (SAXException e) {
                log.error("Unable to get a schema validator from the given file path : " + schemaPath);
            }
        }
        return aspectSchemaValidator;
    }

    public static String getLifecycleSchemaLocation(){
        return CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator +
                "conf" + File.separator + "lifecycle-config.xsd";
    }
    public static String getAspectSchemaLocation(){
        return CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator +
                "conf" + File.separator + "local.xsd";
    }
}
