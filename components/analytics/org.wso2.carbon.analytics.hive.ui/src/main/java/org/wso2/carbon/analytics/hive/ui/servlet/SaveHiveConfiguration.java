/**
 * Copyright (c) 2009, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wso2.carbon.analytics.hive.ui.servlet;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.analytics.hive.stub.HiveExecutionServiceHiveExecutionException;
import org.wso2.carbon.analytics.hive.ui.client.HiveExecutionClient;
import org.wso2.carbon.ui.CarbonUIUtil;
import org.wso2.carbon.utils.ServerConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaveHiveConfiguration extends HttpServlet {
    private static Log log = LogFactory.getLog(SaveScriptProcessor.class);

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        String serverURL = CarbonUIUtil.getServerURL(getServletContext(), request.getSession());
        ConfigurationContext configContext =
                (ConfigurationContext) getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
        String cookie = (String) request.getSession().getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);


        String driver = request.getParameter("driver");
        String url = request.getParameter("url");
        String username = request.getParameter("hiveusername");
        String password = request.getParameter("hivepassword");


        try {
            HiveExecutionClient client = new HiveExecutionClient(cookie, serverURL, configContext);
            PrintWriter writer = response.getWriter();
            if (!client.saveConfiguration(driver, url, username, password)) {
                writer.print("Configuration is not correct! couldn't connect to hive with provided configuration.\n Try Again!");
            } else {
                writer.print("Successfully updated the configuration");
            }
        } catch (HiveExecutionServiceHiveExecutionException e) {
            log.error("exception while updating the configuration", e);
        } catch (AxisFault axisFault) {
            log.error("exception while updating the configuration", axisFault);
        } catch (RemoteException e) {
            log.error("exception while updating the configuration", e);
        } catch (IOException e) {
            log.error("exception while updating the configuration", e);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException if a servlet-specific error occurs
     * @throws java.io.IOException            if an I/O error occurs
     */

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(SaveScriptProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(SaveScriptProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */

    public String getServletInfo() {
        return "used to save the configuration";
    }// </editor-fold>
}
