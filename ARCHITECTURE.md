# Architecture Overview

This document provides an overview of the Decentra Android application architecture.

## Overview

The Decentra Android app is a native Android application built with Kotlin that connects to self-hosted Decentra chat servers via WebSockets. It follows standard Android development patterns with a focus on simplicity and functionality.

## Technology Stack

### Core Technologies
- **Language**: Kotlin 1.9.10
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Build System**: Gradle 8.2 with Kotlin DSL

### Key Libraries
- **AndroidX**: Modern Android development libraries
  - `core-ktx`: Kotlin extensions
  - `appcompat`: Backward compatibility
  - `lifecycle`: Lifecycle-aware components
- **Material Components**: Material Design UI components
- **ConstraintLayout**: Flexible layout system
- **RecyclerView**: Efficient list display
- **OkHttp**: HTTP client and WebSocket implementation
- **Gson**: JSON serialization/deserialization
- **Kotlin Coroutines**: Asynchronous programming

## Application Structure

```
com.decentra.app/
├── MainActivity.kt           # Entry point - Authentication
├── ChatActivity.kt          # Main chat interface
├── models/
│   └── Message.kt           # Data model for messages
├── adapters/
│   └── MessageAdapter.kt    # RecyclerView adapter
└── network/
    └── DecentraWebSocketClient.kt  # WebSocket client
```

## Component Details

### MainActivity
**Purpose**: Authentication and server connection

**Responsibilities**:
- Collect user credentials and server URL
- Establish WebSocket connection
- Handle login/signup flow
- Navigate to ChatActivity on success

**Key Features**:
- Input validation
- Server URL format checking
- Error handling and user feedback
- Support for invite codes

### ChatActivity
**Purpose**: Main chat interface

**Responsibilities**:
- Display message history
- Send and receive real-time messages
- Manage WebSocket connection lifecycle
- Handle incoming message events

**Key Features**:
- Real-time message updates
- Message history display
- Optimistic UI updates
- Automatic scrolling to latest messages

### DecentraWebSocketClient
**Purpose**: WebSocket communication layer

**Responsibilities**:
- Establish and maintain WebSocket connections
- Send authentication requests
- Send chat messages
- Receive and route incoming messages
- Handle connection lifecycle events

**Key Features**:
- OkHttp-based WebSocket implementation
- JSON message formatting
- Callback mechanism for incoming messages
- Connection state management

### MessageAdapter
**Purpose**: Display messages in RecyclerView

**Responsibilities**:
- Bind message data to UI views
- Format timestamps
- Manage message list

**Key Features**:
- Efficient view recycling
- Timestamp formatting
- Dynamic message addition

### Message Model
**Purpose**: Data structure for chat messages

**Properties**:
- `username`: Sender's username
- `message`: Message content
- `timestamp`: When the message was sent

## Communication Flow

### Authentication Flow
```
User Input → MainActivity
    ↓
Validate Input
    ↓
Create WebSocketClient
    ↓
Connect to Server
    ↓
Send Auth Request (JSON)
    ↓
Receive Auth Response
    ↓
Navigate to ChatActivity (on success)
```

### Messaging Flow
```
User Types Message → ChatActivity
    ↓
Send via WebSocketClient
    ↓
Clear Input Field
    ↓
Server Broadcasts Message
    ↓
WebSocket onMessage Callback
    ↓
Parse JSON Message
    ↓
Add to MessageAdapter
    ↓
Display in RecyclerView
```

## WebSocket Protocol

The app communicates with the Decentra server using JSON messages over WebSocket.

### Authentication Message
```json
{
  "action": "login" | "signup",
  "username": "string",
  "password": "string",
  "invite_code": "string" (optional for signup)
}
```

### Chat Message
```json
{
  "action": "message",
  "message": "string"
}
```

### Incoming Message (from server)
```json
{
  "type": "message",
  "username": "string",
  "message": "string"
}
```

### Message History (from server)
```json
{
  "type": "history",
  "messages": [
    {
      "username": "string",
      "message": "string"
    }
  ]
}
```

## UI Design

### Design Principles
- Material Design guidelines
- Simple and intuitive interface
- Minimal user input required
- Clear feedback for all actions

### Layouts
1. **activity_main.xml**: Login/signup screen
   - Server URL input
   - Username and password fields
   - Invite code field (optional)
   - Login and Sign Up buttons
   - Status text

2. **activity_chat.xml**: Chat interface
   - RecyclerView for messages (top)
   - Message input field (bottom)
   - Send button

3. **item_message.xml**: Individual message item
   - Username (bold, colored)
   - Message text (in bubble)
   - Timestamp (small, gray)

### Color Scheme
- Primary: Purple (#6200EE)
- Secondary: Teal (#03DAC5)
- Message bubbles: Discord-inspired blue/gray

## State Management

### Current Approach
- Activity lifecycle manages UI state
- WebSocket client passed between activities via companion object
- Messages stored in adapter's internal list

### Limitations
- No persistent storage
- Messages lost on app restart
- Connection lost on activity recreation
- State management could be improved with ViewModel

## Threading Model

### UI Thread
- All UI updates
- RecyclerView operations
- User input handling

### Background Thread (OkHttp)
- WebSocket connection
- Network I/O
- Message parsing

### Thread Communication
- `runOnUiThread()` for updating UI from callbacks
- OkHttp handles threading internally

## Error Handling

### Connection Errors
- WebSocket connection failures
- Server unavailable
- Network issues

### Authentication Errors
- Invalid credentials
- Missing invite code
- Server-side validation failures

### Message Errors
- Send failures (logged, not shown to user)
- Parse errors (logged, message skipped)

### User Feedback
- Toast messages for errors
- Status text updates
- Activity navigation on success

## Security Considerations

### Current Implementation
- Cleartext traffic allowed (for local development)
- No credential storage
- WebSocket connection security depends on protocol (ws:// vs wss://)

### Recommendations
- Use wss:// for production
- Implement network security config
- Add certificate pinning for known servers
- Consider token-based authentication

## Performance Considerations

### Optimizations
- RecyclerView for efficient list display
- View binding for faster view access
- Minimal message parsing

### Potential Issues
- All messages loaded in memory
- No message pagination
- No message caching

## Testing Strategy

### Current State
- No tests implemented
- Manual testing required

### Recommended Testing
1. **Unit Tests**:
   - Message model
   - JSON parsing logic
   - Message adapter

2. **Integration Tests**:
   - WebSocket client
   - Authentication flow
   - Message sending/receiving

3. **UI Tests**:
   - Activity navigation
   - Message display
   - User input validation

## Build Configuration

### Gradle Modules
- Root project: Configuration and dependencies
- `app` module: Application code

### Build Types
- **Debug**: For development, includes debugging symbols
- **Release**: Optimized, can be signed for distribution

### Product Flavors
- Not currently used
- Could add flavors for different server environments

## Dependencies Management

### Version Control
- Dependencies defined in `app/build.gradle.kts`
- Version numbers hardcoded (could use version catalog)

### Repository Sources
- Google Maven
- Maven Central

## Deployment

### APK Generation
- Debug APK for testing
- Release APK for distribution

### Installation Methods
- Sideloading (primary method)
- ADB installation for development
- Could publish to F-Droid or similar

## Future Architecture Improvements

See [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for detailed recommendations including:
- Dependency injection
- ViewModel and LiveData
- Repository pattern
- Proper service for WebSocket
- Room database for caching
- WorkManager for background tasks

## Conclusion

The current architecture is simple and functional, suitable for a first version. It prioritizes:
1. Ease of understanding
2. Minimal dependencies
3. Core functionality
4. Room for improvement

For production use, consider implementing the improvements outlined in KNOWN_ISSUES.md.
