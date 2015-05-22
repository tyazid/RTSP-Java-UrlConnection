package com.net.rtsp.util;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class Utils {

    private static final Map SYS_QS = new HashMap();

  //  private static final Map CL_2_QS = new WeakHashMap();

    private Utils() {}

    static void add(XEventQueue q, Object listener) {
        Utils.getSQueue(q, listener).addListener(listener);
    }

    static String getGroup(XEventQueue q, String group) {
        if (group == null)
            return q.getClass().getName() + '@' + System.identityHashCode(q);

        return group;
    }

    static boolean isDispatchThread(SimpleEventQueue sq) {
        if (sq == null)
            return false;

        return sq.thread == Thread.currentThread();
    }

    static SimpleEventQueue[] listQueues(String groupID, ClassLoader loader) {
        Map map = Utils.getSQueueMap(loader);

        synchronized (map) {
            Map type2sq = (Map) map.get(groupID);
            Collection v = type2sq.values();

            return (SimpleEventQueue[]) v.toArray(new SimpleEventQueue[v.size()]);
        }
    }

    static void push(XEventQueue xq, EventObject evt) {
        Dispatcher d = xq.dispatcher;
        Object[] l = xq.getListeners();

        if (l.length == 0) {
            SimpleEventQueue sq = Utils.getSQueue(xq, null);
            sq.pushJob(new QueueJob(xq, d, evt));

        }
        else {
            Set set = new HashSet();

            for (int i = 0; i < l.length; i++) {
                SimpleEventQueue sq = Utils.getSQueue(xq, l[i]);

                if (sq != null && set.add(sq))
                    sq.pushJob(new QueueJob(xq, d, evt));
            }
        }
    }

    static void remove(XEventQueue q, Object listener) {
        Utils.getSQueue(q, listener).removeListener(listener);
    }

    private static SimpleEventQueue getSQueue(XEventQueue q, Object listener) {
        String groupID = q.groupID;
        Class listType = q.listenerType;
        ClassLoader loader = listener == null ? null : listener.getClass().getClassLoader();
        Map map = Utils.getSQueueMap(loader);

        synchronized (map) {
            Map type2sq = (Map) map.get(groupID);

            if (type2sq == null)
                map.put(groupID, type2sq = new/* Weak*/HashMap());

            SimpleEventQueue sq = (SimpleEventQueue) type2sq.get(listType);

            if (sq == null) {
            	
              //  ThreadGroup tg = loader == null ? null :ThreadGroup  Runtime.getRuntime().
               //                 .getThreadGroupFromClassLoader(loader);
               // if (tg == null || !tg.isDestroyed())
                    type2sq.put(listType, sq = new SimpleEventQueue(null, listType));
            }

            return sq;
        }
    }

    private static Map getSQueueMap(ClassLoader loader) {
       // if (loader == null)
            return Utils.SYS_QS;

     //   Map map = Utils.CL_2_QS;
//
//        synchronized (map) {
//            Map grp2Type = (Map) map.get(loader);
//
//            if (grp2Type == null)
//                map.put(loader, grp2Type = new HashMap());
//
//            return grp2Type;
//        }
    }
}
