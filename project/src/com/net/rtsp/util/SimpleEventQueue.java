 
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
