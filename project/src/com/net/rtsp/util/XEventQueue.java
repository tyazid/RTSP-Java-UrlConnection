/*
 * Copyright (c) CANAL+ Technologies  All rights Reserved.
 *
 * All Rights Reserved. No part of this software and its documentation may
 * be used, copied, modified, distributed or transmitted, in any form or
 * by any means, without the prior written permission of CANAL+ Technologies.
 */
/*
 **  PROJET       : MHW+
 **  PACKAGE      : mhw.util.event
 **
 **  COMPOSANT    : MultiThreadedEventQueue.java
 **  VERSION      :
 **
 **  IDENTIFIER   : Id:
 **
 **  CREATION     : 16 nov. 2004 (09:31:32)
 **  AUTEUR       : Julien MARICHEZ (jmarichez)
 **
 **  MODIFICATION : 16 nov. 2004 (09:31:32)
 **  PAR          : Julien MARICHEZ (jmarichez)
 **
 **  EVOLUTION    :
 **  No-Log:
 **
 **  REMARQUE     :
 **
 */
package com.net.rtsp.util;

import java.security.AccessController;
import java.util.EventListener;
import java.util.EventObject;

/**
 * <pre>
 * One thread per group and per class loader: ie for n groups with each m classloader
 * n*m dispatch threads.
 * XEventQueue is not required to have listeners to dispatch events.
 * If no listeners are present the events will be sent to the system dispatcher of
 * the queue's dispatch group.
 * </pre>
 */
public class XEventQueue extends EventQueue {

    final String groupID;

    /**
     * Create an XEventQueue object.
     * 
     * @param dispatcher
     *            object used to dispatch events to client applications
     */
    public XEventQueue(Dispatcher dispatcher) {
        this(dispatcher, EventListener.class);
    }

    /**
     * Create an XEventQueue object. Specifying the type of the listeners will
     * result ina an array of this component type to be passed to the dispatcher
     * 
     * @param dispatcher
     *            object used to dispatch events to client applications
     * @param listenerType
     *            the class type of the listeners
     */
    public XEventQueue(Dispatcher dispatcher, Class listenerType) {
        this(dispatcher, null, listenerType);
    }

    /**
     * Create an XEventQueue object.
     * 
     * @param dispatcher
     *            object used to dispatch events to client applications
     * @param groupID
     *            identifies the dispach group
     */
    public XEventQueue(Dispatcher dispatcher, String groupID) {
        this(dispatcher, groupID, Object.class);
    }

    /**
     * Create an XEventQueue object. Specifying the type of the listeners will
     * result ina an array of this component type to be passed to the dispatcher
     * 
     * @param dispatcher
     *            object used to dispatch events to client applications
     * @param groupID
     *            identifies the dispach group, if null a groupID for this queue
     *            is set
     * @param listenerType
     *            the class type of the listeners
     * @exception SecurityException
     *                if permission to register to the dispatch group is denied
     */
    public XEventQueue(Dispatcher dispatcher, String groupID, Class listenerType) {
        super(dispatcher, listenerType);
        this.groupID = Utils  .getGroup(this, groupID);
       // AccessController.checkPermission(new DispatchGroupPermission(this.groupID = Utils  .getGroup(this, groupID)));
    }

    public void addListener(Object listener) {
        super.addListener(listener);

        Utils.add(this, listener);
    }

    /**
     * Returns the dispatch group of this queue
     * 
     * @return the dispatch group of this queue
     */
    public String getDispatchGroup() {
        return groupID;
    }

    public void pushEvent(EventObject event) {
        Utils.push(this, event);
    }

    public void removeListener(Object listener) {
        super.removeListener(listener);

        Utils.remove(this, listener);
    }

    /**
     * Returns true if the current thread is a classLoader's dispatch thread for
     * the specified dispach group
     * 
     * @return true if the current thread is a cl's dispatch thread for the
     *         specified dispach group
     */
    public static boolean isDispatchThread(String groupID, ClassLoader loader) {
        SimpleEventQueue[] sqs = Utils.listQueues(groupID, loader);

        if (sqs.length == 0)
            return false;

        return Utils.isDispatchThread(sqs[0]);
    }

    /**
     * Returns true if the current thread is the system dispatch thread for the
     * specified dispach group
     * 
     * @return true if the current thread is the system dispatch thread for the
     *         specified dispach group
     */
    public static boolean isSystemDispatchThread(String groupID) {
        return XEventQueue.isDispatchThread(groupID, null);
    }
}
