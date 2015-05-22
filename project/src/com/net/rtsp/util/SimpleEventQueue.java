 
package com.net.rtsp.util;

import java.util.EventListener;
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


class QueueThread extends Thread {
    private final EventQueue q;
    private volatile boolean killed;

    public QueueThread(EventQueue q) {
        super("QueueThread " + q);
        this.q = q;
    }

    public void kill() {
        killed = true;
        interrupt();
    }

    public void run() {
        EventQueue q = this.q;
	 QueueJob[] jobs;
        while (!killed) {
             jobs=null; 
            try {
                synchronized (q) {
                    while (q.isEmpty()) {
                        q.wait();
                    }

                    jobs = q.dequeueAll();
                }

                QueueThread.dispatch(jobs);
            } catch (InterruptedException ex) {

                // ex.printStackTrace();
            }
        }
    }

    boolean killed() {
        return killed; 
    }

    private static void dispatch(QueueJob[] jobs) {
        // in order for jobs
        for (int i = 0; i < jobs.length; i++) {
            QueueJob job = jobs[i];
            EventListener[] list = job.getListeners();
            Dispatcher dispatcher = job.getDispatcher();
            try {
                dispatcher.dispatch(list, job.event);
            } catch (Exception ex) {}
        }
    }
}
