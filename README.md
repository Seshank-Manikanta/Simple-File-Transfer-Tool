Java Socket File Transfer Application
A lightweight, multi-threaded Java application for transferring files over TCP sockets. It includes a server that handles incoming upload connections concurrently and a client that streams files in chunks to prevent memory overflow.

Technical Features
Multi-Threaded Server Architecture: Utilizes an asynchronous connection loop where each incoming TCP client connection is offloaded to a dedicated worker thread (new Thread(() -> uploadHandler(socket)).start()), enabling concurrent file uploads without blocking the main server thread.

Low-Memory Stream Chunking: Reads and transmits binary data using a fixed-size byte buffer (4096 bytes), ensuring zero risk of OutOfMemoryError even when transferring multi-gigabyte files.

Self-Framing Protocol: Implements an explicit two-stage transfer protocol:

Header Phase: Transmits metadata—the UTF-8 encoded file name and the exact file size as a 64-bit integer (Long).

Data Phase: Transmits the raw binary payload matching the declared length.

Path Traversal Mitigation: Sanitizes incoming user-controlled filenames on the server using new File(fileName).getName(), preventing attackers from writing files outside the intended storage directory via relative paths like ../../etc/passwd.

Graceful EOF Handling: Catches abrupt socket disconnections mid-stream using EOFException to ensure incomplete files are detected and socket resources are released cleanly.
