<p align="center">
  <a href="https://github.com/pkg-dot-zip/SingleRoomUdpChatService/" rel="noopener">
    <img width=400px height=400px src="docs/logo.png" alt="Project logo"></a>
</p>

<h3 align="center">SingleRoomUdpChatService</h3>

<div align="center">

  [![Stars](https://img.shields.io/github/stars/pkg-dot-zip/SingleRoomUdpChatService.svg)](https://github.com/pkg-dot-zip/SingleRoomUdpChatService/stargazers)
  [![GitHub Issues](https://img.shields.io/github/issues/pkg-dot-zip/SingleRoomUdpChatService.svg)](https://github.com/pkg-dot-zip/SingleRoomUdpChatService/issues)
  [![GitHub Pull Requests](https://img.shields.io/github/issues-pr/pkg-dot-zip/SingleRoomUdpChatService.svg)](https://github.com/pkg-dot-zip/SingleRoomUdpChatService/pulls)

</div>

<p align="center">A simple UDP-based chat system built in <a href="https://kotlinlang.org/">Kotlin</a> for a university network programming assignment.
</p>

## 📝 Table of Contents
- [About](#about)
- [Usage](#usage)
- [Built Using](#built_using)
- [Authors](#authors)

## 🧐 About <a name="about"></a>
This repository contains a simple UDP-based chat system built in Kotlin as part of a university assignment. The project involves creating a basic client-server architecture using datagram sockets to facilitate communication between clients. The server handles multiple clients, and users can send messages, change statuses, or disconnect from the system.

### What does it do? 🤔
The project establishes a communication system where a server listens for incoming messages from multiple clients over UDP. Each client can send messages to the server, and the server broadcasts these messages to all connected clients. It also supports user commands like setting their status to "available," "busy," or "offline." The system is built from scratch, with no pre-built libraries for handling UDP or messaging.

### Why?! 😱
This project serves as a practice assignment to strengthen understanding of socket programming and network protocols. The challenge was to implement a basic chat system using the User Datagram Protocol (UDP), where connection-less communication is leveraged. The assignment required handling packet transmission, managing client statuses, and broadcasting messages to all clients in real time.

This project **improves** skills in network programming, concurrency, and event-driven design, laying a strong foundation for developing more complex networking applications.

### Features 🌟
- **Multiclient Support**: The server handles multiple clients simultaneously, allowing them to communicate in a shared chat room.
- **User Status**: Clients can set their status to available, busy, or offline using commands like `/available`, `/busy`, and `/offline`.
- **UDP Communication**: The system uses UDP sockets to transmit messages between clients and the server.
- **Acknowledgement Handling**: Ensures that the server acknowledges messages from the client, providing basic reliability over UDP.
- **Broadcast Messaging**: The server broadcasts messages to all connected clients except the sender.
- **Inactivity Timeout**: Clients are automatically set to offline if inactive for more than 5 minutes.
- **Event System**: Coded using events architecture!
  
## 🎈 Usage <a name="usage"></a>
1. Clone the repository.
1. Open the project in [IntelliJ](https://www.jetbrains.com/idea/).
1. Run the server:
    - Execute the `Server` class located in the `com.pkg_dot_zip.server` package to start the server.
1. Run multiple clients:
    - Execute the `Client` class located in the `com.pkg_dot_zip.client` package to start a client. Repeat this to create another one.
1. Use commands like `/available`, `/busy`, and `/offline` to change status, or type a message.

## ⛏️ Built Using <a name = "built_using"></a>
- [IntelliJ](https://www.jetbrains.com/idea/) - IDE used
- [Kotlin](https://kotlinlang.org/) - Language used to program in
- [kotlin-logging](https://github.com/oshai/kotlin-logging) - Lightweight Multiplatform logging framework for Kotlin. A convenient and performant logging facade
- [UDP Sockets](https://docs.oracle.com/javase/8/docs/api/java/net/DatagramSocket.html) - Used for socket-based communication between clients and server

## ✍️ Authors <a name = "authors"></a>
- [@OnsPetruske](https://github.com/pkg-dot-zip) - Idea & Initial work.

See also the list of [contributors](https://github.com/pkg-dot-zip/SingleRoomUdpChatService/contributors) who participated in this project.
