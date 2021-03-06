package com.vaadin.cdi.internal;

/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.vaadin.server.VaadinSession;

public class CDIUtil {

    private static final String[] commonBeanManagerLookups = {
            "java:comp/BeanManager", "java:comp/env/BeanManager" };

    public static BeanManager lookupBeanManager() {
        BeanManager beanManager = null;
        try {
            InitialContext initialContext = new InitialContext();
            for (String beanManagerLookup : commonBeanManagerLookups) {
                try {
                    beanManager = (BeanManager) initialContext
                            .lookup(beanManagerLookup);
                    getLogger().fine(
                            "BeanManager found by '" + beanManagerLookup + "'");
                    break;
                } catch (NamingException e) {
                    getLogger().fine(
                            "BeanManager was not found by '"
                                    + beanManagerLookup + "'");
                }
            }
        } catch (NamingException e) {
            getLogger().warning("Could not instantiate InitialContext");
        }
        if (beanManager == null) {
            getLogger().severe("Could not get BeanManager through JNDI");
        }
        return beanManager;
    }

    public static long getSessionId() {
        return getSessionId(VaadinSession.getCurrent());
    }

    public static long getSessionId(VaadinSession session) {

        Long id = (Long) session.getAttribute("cdi-session-id");
        if (id == null) {
            id = UUID.randomUUID().getLeastSignificantBits();
            session.setAttribute("cdi-session-id", id);
        }
        return id;
    }

    private static Logger getLogger() {
        return Logger.getLogger(CDIUtil.class.getCanonicalName());
    }
}
