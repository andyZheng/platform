/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.autoscaler.service.util;

import java.util.HashMap;
import java.util.Map;


public class IaaSProvider {
    
    /**
     * Unique name of this IaaS provider
     */
    private String name;
    
    /**
     * Property map for this IaaS provider.
     */
    private Map<String, String> properties = new HashMap<String, String>();
    
    /**
     * Image identifier.
     */
    private String template;
    
    /**
     * Scale up order and scale down order of the IaaS.
     */
    private int scaleUpOrder, scaleDownOrder;
    
    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void setProperty(String key, String value) {
        
        if(key != null && value != null){
            properties.put(key, value);
        }
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getScaleUpOrder() {
        return scaleUpOrder;
    }

    public void setScaleUpOrder(int scaleUpOrder) {
        this.scaleUpOrder = scaleUpOrder;
    }

    public int getScaleDownOrder() {
        return scaleDownOrder;
    }

    public void setScaleDownOrder(int scaleDownOrder) {
        this.scaleDownOrder = scaleDownOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}
