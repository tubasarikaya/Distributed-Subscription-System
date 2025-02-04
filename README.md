# Distributed Subscription System

A fault-tolerant distributed subscription system implemented using Transport Layer communication functions across multiple programming languages (Ruby, Python, and Java). This project was developed as part of a socket programming course.

## Project Team

- Tuba Sarıkaya
- Elif Sude Çetinkaya
- Zeynep Ravza Dursun

## System Architecture

The system consists of two main components:

### Admin Component (admin.rb)

The admin component, written in Ruby, serves as the central control unit with the following capabilities:

- Establishes socket-based communication with servers
- Performs capacity queries to servers every 5 seconds
- Sends start commands (strt) to servers for inter-server communication
- Handles response messages for start commands
- Processes capacity values received from capacity queries

### Server Component (ServerX.java)

The server component, implemented in Java, handles client interactions and admin commands:

#### Admin Communication
- Receives and processes capacity queries from admin
- Sends capacity values in response to queries
- Processes start commands (strt) from admin
- Responds with "yep" or "nope" messages to start commands

#### Client Management
- Maintains socket-based communication with clients
- Processes incoming subscriber objects
- Handles subscription-related operations:
  - New subscriptions
  - Subscription cancellations
  - Online/offline status management

## Implementation Status

This project is a work in progress and represents the initial implementation of the distributed subscription system. The current version includes basic functionality for admin-server communication and client management.

## Features Implemented

- Socket-based communication between components
- Periodic capacity monitoring
- Server interconnection protocol
- Basic subscription management
- Client status tracking

## Technologies Used

- Ruby
- Java
- Socket Programming
- Transport Layer Protocols
