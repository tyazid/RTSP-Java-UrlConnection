/*

 * Copyright (c) NDS TECHNOLOGIES FRANCE All rights Reserved.

 *

 * All Rights Reserved. No part of this software and its documentation may

 * be used, copied, modified, distributed or transmitted, in any form or

 * by any means, without the prior written permission of CANAL+ Technologies.

 *

 * @author bbizet

 * @version %version: 0.1.2 %

 */
/*

 **  PROJET       : MHW+

 **  PACKAGE      : event

 **

 **  COMPOSANT    : SimpleEventQueue.java

 **  VERSION      :

 **

 **  IDENTIFIER   : $Id: SimpleEventQueue.java,v 1.1.8.1 2008/06/03 15:49:20 hekra Exp $

 **

 **  CREATION     : 23 Juillet 2004 (12:05)

 **  AUTEUR       : Julien MARICHEZ (jmariche)

 **

 **  EVOLUTION    :

 **  $No-Log: $

 **

 **  REMARQUE     :

 **

 */
package com.net.rtsp.util;

import java.util.List;

/**
 * EventQueue implementation using a single Carousel to dispatch events.
 */
public class SimpleEventQueue extends EventQueue {

    final QueueThread thread;

    /**
     * Create an SimpleEventQueue object. Listener vector is cloned before the
     * event is dispatched.
     * 
     * @param dispatcher
     *            object used to dispatch events to client applications
     */
    public SimpleEventQueue(Dispatcher dispatcher) {
        this(dispatcher, Object.class);
    }

    public SimpleEventQueue(Dispatcher dispatcher, Class listenerType) {
        super(dispatcher, listenerType);
        thread = new QueueThread(this);
    }

    SimpleEventQueue(Dispatcher dispatcher, Class listenerType, List listeners) {
        super(dispatcher, listenerType, listeners);
        thread = new QueueThread(this);
    }

    /**
     * Returns true if the current thread is this event queue's dispatch thread
     * 
     * @return true if the current thread is this event queue's dispatch thread
     */
    public final boolean isDispatchThread() {
        return thread == Thread.currentThread();
    }

    public void killThread() {
        QueueThread t = thread;
        if (t != null)
            t.kill();
    }

    protected void finalize() throws Throwable {
        killThread();
        super.finalize();
    }

    void pushJob(QueueJob job) {
        if (thread.killed())
            throw new IllegalStateException("thread killed");
        synchronized (this) {
            if (!thread.isAlive())
                thread.start();
            super.pushJob(job);
            notify();
        }
    }
}
