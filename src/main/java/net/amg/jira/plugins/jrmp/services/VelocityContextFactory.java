/*
 * Licensed to Author or Authors under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * Author licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.JiraVelocityUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Delegate Jira functionality to be able to easy tests classes
 * Created by Augustyn Wilk on 29.07.2015.
 * @author AugustynWilk@gmail.com
 */
@Component
public class VelocityContextFactory {
    @Autowired
    private JiraAuthenticationContext authenticationContext;

    //4Spring dependency injection:
    public VelocityContextFactory() {}

    public VelocityContext getDefaultContext() {
        return new VelocityContext(JiraVelocityUtils.getDefaultVelocityParams(authenticationContext));
    }
}
