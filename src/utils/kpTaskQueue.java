package kpUtils.src.utils;

import org.rspeer.commons.logging.Log;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class kpTaskQueue
{
    // Used to block execution of the rest of the nodes,
    // we need this since if we fully block execution,
    // TickSystemNode wouldn't get executed last
    private static boolean blockNodes = false;

    public static void BlockExecution()
    {
        blockNodes = true;
    }

    public static boolean IsExecutionBlocked()
    {
        return blockNodes;
    }

    public static class Task
    {
        private final Runnable task;
        private final String name;
        private final int priority; // higher number = higher priority = run later

        public Task(String name, int priority, Runnable task)
        {
            this.name = name;
            this.priority = priority;
            this.task = task;
        }

        public void run()
        {
            synchronized (lock)
            {
                Log.info("[TASK QUEUE] - " + this.name);
                task.run();
            }
        }

        public String getName()
        {
            return name;
        }

        public int getPriority()
        {
            return priority;
        }
    }

    private static long tickCount = 0;

    private static final Object lock = new Object();

    private static final int AMOUNT_OF_TICKS = 36;
    private static ArrayList<ArrayList<Task>> tasks;
    private static boolean initialized = false;

    public static boolean Initialized()
    {
        return initialized;
    }

    public static void Initialize()
    {
        synchronized (lock)
        {
            tasks = new ArrayList<>(AMOUNT_OF_TICKS);
            for (int i = 0; i < AMOUNT_OF_TICKS; i++)
            {
                tasks.add(new ArrayList<>());
            }

            initialized = true;
        }
    }

    public static void AddTask(Task task, int tickIndex)
    {
        synchronized (lock)
        {
            tickIndex += 1;// +1 so 0 = 1

            if (tickIndex < tasks.size())
            {
                tasks.get(tickIndex).add(task);
            }
            else
            {
                Log.severe("Tried to add a task OOB | tasks size: " + tasks.size() + " | tickIndex : " + tickIndex);
            }
        }
    }

    public static ArrayList<ArrayList<Task>> GetAllTasks()
    {
        synchronized (lock)
        {
            return tasks;
        }
    }

    public static ArrayList<Task> GetCurrentTasks()
    {
        synchronized (lock)
        {
            tasks.get(1).sort(Comparator.comparingInt(Task::getPriority));
            return tasks.get(1);
        }
    }

    public static void PopTask()
    {
        synchronized (lock)
        {
            if (!tasks.isEmpty())
            {
                tasks.remove(0);
                tasks.add(new ArrayList<>());
            }
        }
    }

    //

    public static void ExecuteTasks()
    {
        for (Task task : GetCurrentTasks())
        {
            task.run();
        }

        PopTask();

        blockNodes = false; // Unblock execution

        tickCount++;
    }

    public static void RenderDebugUI(Graphics2D g2d)
    {
        if (Initialized())
        {
            int taskMul = 1;

            for (ArrayList<kpTaskQueue.Task> tasks : GetAllTasks())
            {
                String tickName = "Tick ";

                if (taskMul == 1)
                {
                    tickName = "Last Tick ";
                }

                StringBuilder taskNames = new StringBuilder(tickName + (tickCount + taskMul) + " - ");

                int taskAmount = tasks.size();
                for (int i = 0; i < taskAmount; i++)
                {
                    taskNames.append(tasks.get(i).getName());

                    if (taskAmount > 1 && i < taskAmount - 1)
                    {
                        taskNames.append(" | ");
                    }
                }

                kpPaint.DrawString(g2d, taskNames.toString(), 256, 20 * taskMul + 40, Color.WHITE);

                taskMul++;

                if (taskMul > 12) // Don't show all the ticks otherwise the whole screen gets filled up
                    break;
            }
        }

        return;
    }

    public static long GetTickCount()
    {
        return tickCount;
    }
}
