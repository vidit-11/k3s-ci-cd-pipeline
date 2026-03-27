# k3s-ci-cd-pipeline
CI/CD pipeline using k3s, github actions and monitoring tools like dozzle, VictoriaMetrics and Grafana



### Explanation of the queries grouped by their functional sections⬇️

# 1. Basic Statistics (General Health)
These queries provide a high-level overview of whether the application is running and how hard it is working.

Uptime: Shows how long the application has been running since its last restart.

Start Time: The exact date and time the process began.

Heap/Non-Heap Used: Gauges showing what percentage of the allocated Java memory is currently in use.

Process Open Files: Tracks how many file handles (connections, logs, etc.) the app has open. If this climbs too high, the app might crash.

CPU Usage: Compares how much CPU the System is using versus how much the specific Application process is using.

Load Average: Shows the "waiting line" for the CPU. A high load average means the CPU is struggling to keep up with tasks.

# 2. JVM Statistics - Memory & Threads
This section looks "under the hood" of the Java environment.

Memory Pools (Heap & Non-Heap): These queries drill down into specific areas of Java memory (like "Eden Space" or "Metaspace"). They track Used (current), Committed (guaranteed available), and Max (the hard limit) memory.

Direct/Mapped Buffers: Tracks memory used for fast I/O operations outside of the standard garbage-collected heap.

Threads:

- Live: Total active threads.

- Daemon: Background "housekeeping" threads.

- Peak: The highest number of threads reached since start.

Memory Allocate/Promote: Measures the rate at which the app is creating new objects and moving them into "old" memory storage.

# 3. JVM Statistics - GC (Garbage Collection)
Garbage Collection is the process of Java cleaning up unused memory.

GC Count: How many times the cleaner has run. Frequent spikes can mean the app is creating too much "trash" objects.

GC Stop the World Duration: This is critical. It measures how many seconds the application was completely frozen while Java cleaned up memory. You want these numbers to be as low as possible.

# 4. Database Connection Pool (HikariCP)
Your app doesn't open a new database connection for every user; it uses a "pool" of shared ones.

Connections Size: The total number of database connections currently open.

Connections (Active/Idle/Pending):

- Active: Connections currently doing work.

- Idle: Connections waiting for work.

- Pending: Users waiting for a connection to become free (if this is high, your app will feel slow).

Connection Timeout Count: How many times a user tried to talk to the database but gave up because no connections were available.

Creation/Usage/Acquire Time: Measures in seconds how long it takes to make a new connection, how long they are held, and how long it takes for a thread to "grab" one.

# 5. HTTP Statistics (Web Traffic)
This monitors the actual users interacting with your website or API.

Request Count: The number of incoming web requests, categorized by:

Method: (GET, POST, etc.)

Status: (200 = Success, 404 = Not Found, 500 = Server Error)

URI: The specific web address (endpoint) being hit.

Response Time: The average time (latency) it takes for your server to send a reply to a user's request.

# 6. Logback Statistics (Application Logs)
This section pulls data from Loki to show actual text logs from the code.

INFO logs: General "heartbeat" messages about normal operations.

ERROR logs: Critical failures that need immediate attention.

WARN/DEBUG/TRACE: Varying levels of detail used for troubleshooting specific bugs or unexpected (but not fatal) behavior
