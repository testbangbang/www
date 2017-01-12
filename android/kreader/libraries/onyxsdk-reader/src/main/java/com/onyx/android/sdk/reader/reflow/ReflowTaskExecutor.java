package com.onyx.android.sdk.reader.reflow;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by joy on 10/13/16.
 */
public class ReflowTaskExecutor {
    private ExecutorService reflowExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            return t;
        }
    });

    ImageReflowManager manager;

    private ReflowTask currentTask;
    private LinkedList<ReflowTask> taskQueue = new LinkedList<>();

    public ReflowTaskExecutor(ImageReflowManager manager) {
        this.manager = manager;
    }

    private Runnable generateRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (taskQueue) {
                        if (taskQueue.isEmpty()) {
                            return;
                        }
                        currentTask = taskQueue.poll();
                    }
                    currentTask.execute();
                    synchronized (taskQueue) {
                        currentTask = null;
                    }
                    manager.signalTaskFinished();
                } catch (Throwable tr) {
                    if (currentTask != null) {
                        currentTask.setException(tr);
                    }
                }
            }
        };
    }

    public void submitTask(ReflowTask task) {
        synchronized (taskQueue) {
            if (isIdenticalToCurrentTask(task.getPageName())) {
                return;
            }
            if (isInTaskQueue(task.getPageName()) && !task.isAbortPendingTasks()) {
                return;
            }
            if (task.isAbortPendingTasks()) {
                for (ReflowTask t : taskQueue) {
                    t.setAbort();
                }
                taskQueue.clear();
            }
            taskQueue.add(task);
        }
        reflowExecutor.submit(generateRunnable());
    }

    public void abort() {
        synchronized (taskQueue) {
            if (currentTask != null) {
                currentTask.setAbort();
            }
            for (ReflowTask t : taskQueue) {
                t.setAbort();
            }
            taskQueue.clear();
        }
    }

    public boolean isPageWaitingReflow(final String pageName) {
        synchronized (taskQueue) {
            if (isIdenticalToCurrentTask(pageName)) {
                return true;
            }
            if (isInTaskQueue(pageName)) {
                return true;
            }
            return false;
        }
    }

    public String getCurrentTaskPage() {
        synchronized (taskQueue) {
            if (currentTask == null) {
                return null;
            }
            return currentTask.getPageName();
        }
    }

    private boolean isIdenticalToCurrentTask(final String pageName) {
        return currentTask != null && currentTask.getPageName().equals(pageName);
    }

    private boolean isInTaskQueue(final String pageName) {
        for (ReflowTask task : taskQueue) {
            if (task.isAbort()) {
                continue;
            }
            if (task.getPageName().equals(pageName)) {
                return true;
            }
        }
        return false;
    }
}
