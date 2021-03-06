/**
 * Copyright (c) 2009, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.proxyadmin.tooling.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.proxyadmin.common.ProxyAdminException;
import org.wso2.carbon.registry.core.session.UserRegistry;

/**
 * 
 */
public class ConfigHolder {

    private static ConfigHolder instance;
    private static final Log log = LogFactory.getLog(ConfigHolder.class);

    private UserRegistry registry;

    private ConfigHolder() {}

    public static ConfigHolder getInstance() {
        if(instance == null) {
            instance = new ConfigHolder();
        }
        return instance;
    }



    public UserRegistry getRegistry() throws ProxyAdminException {
        assertNull("Registry", registry);
        return registry;
    }

    public void setRegistry(UserRegistry registry) {
        this.registry = registry;
    }

    private void assertNull(String name, Object object) throws ProxyAdminException {
        if (object == null) {
            String message = name + " reference in the proxy admin config holder is null";
            log.error(message);
            throw new ProxyAdminException(message);
        }
    }
}
