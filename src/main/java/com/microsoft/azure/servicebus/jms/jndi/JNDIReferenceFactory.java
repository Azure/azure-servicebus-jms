// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms.jndi;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

public class JNDIReferenceFactory implements ObjectFactory {
    
    public JNDIReferenceFactory() { }
    
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {
        Object result = null;
        if (obj instanceof Reference) {
            Reference reference = (Reference) obj;
            Class<?> theClass = loadClass(this, reference.getClassName());
            if (JNDIStorable.class.isAssignableFrom(theClass)) {
                JNDIStorable store = (JNDIStorable) theClass.getDeclaredConstructor().newInstance();
                Map<String, String> properties = new HashMap<>();
                for (Enumeration<RefAddr> iter = reference.getAll(); iter.hasMoreElements();) {
                    StringRefAddr addr = (StringRefAddr) iter.nextElement();
                    properties.put(addr.getType(), (addr.getContent() == null) ? "" : addr.getContent().toString());
                }
                store.setProperties(properties);
                result = store;
            }
        } else {
            throw new RuntimeException("Object " + obj + " is not a reference");
        }
        
        return result;
    }
    
    static Reference createReference(String instanceClassName, JNDIStorable po) throws NamingException {
        Reference result = new Reference(instanceClassName, JNDIReferenceFactory.class.getName(), null);
        try {
            Map<String, String> props = po.getProperties();
            for (Map.Entry<String, String> entry : props.entrySet()) {
                javax.naming.StringRefAddr addr = new javax.naming.StringRefAddr(entry.getKey(), entry.getValue());
                result.add(addr);
            }
        } catch (Exception e) {
            throw new NamingException(e.getMessage());
        }
        
        return result;
    }

    private static Class<?> loadClass(Object thisObj, String className) throws ClassNotFoundException {
        // try local ClassLoader first.
        ClassLoader loader = thisObj.getClass().getClassLoader();
        Class<?> theClass;
        if (loader != null) {
            theClass = loader.loadClass(className);
        } else {
            // Will be null in jdk1.1.8
            // use default classLoader
            theClass = Class.forName(className);
        }
        
        return theClass;
    }
}
