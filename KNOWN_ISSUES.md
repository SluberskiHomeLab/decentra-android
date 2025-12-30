# Known Issues and Future Improvements

This document tracks known issues and potential improvements for the Decentra Android application.

## Architecture Improvements

### 1. WebSocket Client Management

**Issue**: The WebSocket client is currently passed between activities using a static companion object field, which can lead to memory leaks and makes testing difficult.

**Impact**: 
- Potential memory leaks if activities are destroyed while holding references
- Race conditions when multiple activities access the same static client
- Difficult to test due to global state

**Recommended Solution**:
- Implement proper dependency injection (e.g., using Hilt or Koin)
- Use a singleton service or repository pattern to manage WebSocket connections
- Pass connection details via Intent extras and recreate the client in each activity
- Use Android's ViewModel and LiveData for reactive state management

### 2. WebSocket Timeout Configuration

**Issue**: The read timeout is set to 0 in `DecentraWebSocketClient`, which disables timeout entirely.

**Impact**:
- Connection can hang indefinitely if server becomes unresponsive
- No automatic recovery from network issues

**Recommended Solution**:
```kotlin
private val client = OkHttpClient.Builder()
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .pingInterval(20, TimeUnit.SECONDS)  // Keep connection alive
    .build()
```

### 3. Message Confirmation

**Issue**: Messages are not confirmed by the server before being displayed in the UI.

**Impact**:
- No indication if message sending fails
- Can lead to confusion if messages appear sent but are not delivered

**Recommended Solution**:
- Wait for server confirmation before adding messages to UI
- Add a "pending" state for messages being sent
- Show error indicators for failed messages
- Implement retry mechanism for failed sends

### 4. Reconnection Logic

**Issue**: No automatic reconnection if the WebSocket connection drops.

**Impact**:
- Users must manually restart the app if connection is lost
- Poor user experience during network transitions

**Recommended Solution**:
- Implement exponential backoff reconnection strategy
- Show connection status in UI
- Queue messages while disconnected and send when reconnected
- Use Android's ConnectivityManager to detect network changes

## Security Improvements

### 1. Cleartext Traffic

**Issue**: The app allows unencrypted `ws://` connections via `usesCleartextTraffic="true"`.

**Impact**:
- Passwords and messages sent in plain text over the network
- Vulnerable to man-in-the-middle attacks

**Recommended Solution**:
- Only allow cleartext for localhost in debug builds
- Enforce `wss://` for production builds
- Add network security configuration:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false" />
    <debug-overrides>
        <domain-config cleartextTrafficPermitted="true">
            <domain includeSubdomains="true">localhost</domain>
            <domain includeSubdomains="true">10.0.2.2</domain>
        </domain-config>
    </debug-overrides>
</network-security-config>
```

### 2. Credential Storage

**Issue**: No persistent storage of server URL or credentials.

**Current Behavior**: Users must re-enter connection details on each app restart.

**Recommended Solution (if persistent storage is desired)**:
- Use Android's EncryptedSharedPreferences for storing server URL
- Never store passwords - use tokens instead
- Implement remember me functionality with secure token storage

## UI/UX Improvements

### 1. Loading States

**Issue**: No loading indicators during connection and authentication.

**Recommended Solution**:
- Add progress bars during connection
- Disable input fields while connecting
- Show shimmer effects while loading message history

### 2. Error Messages

**Issue**: Generic error messages don't provide enough context.

**Recommended Solution**:
- More specific error messages for different failure scenarios
- Suggestions for fixing common issues
- In-app troubleshooting guide

### 3. Message Formatting

**Issue**: Messages are plain text only.

**Recommended Solution**:
- Support for markdown formatting
- Link detection and clickable links
- Image/file sharing support
- Emoji support

### 4. Typing Indicators

**Issue**: No indication when other users are typing.

**Recommended Solution**:
- Implement typing indicators using WebSocket events
- Show "User is typing..." message

## Performance Improvements

### 1. Message Pagination

**Issue**: All messages are loaded into memory, which could cause issues with large message histories.

**Recommended Solution**:
- Implement pagination with lazy loading
- Use Android's Paging library
- Cache messages in local database (Room)

### 2. Image Loading

**Issue**: No image loading or caching (if images are added in the future).

**Recommended Solution**:
- Use Coil or Glide for efficient image loading
- Implement proper caching strategy

## Testing

### Missing Test Coverage

**Issue**: No unit tests or integration tests currently exist.

**Recommended Solution**:
- Add unit tests for WebSocket client
- Add UI tests for activities
- Mock WebSocket connections for testing
- Implement screenshot tests for UI verification

### Recommended Test Structure:
```
app/src/test/java/com/decentra/app/
├── network/
│   └── DecentraWebSocketClientTest.kt
├── models/
│   └── MessageTest.kt
└── adapters/
    └── MessageAdapterTest.kt

app/src/androidTest/java/com/decentra/app/
├── MainActivityTest.kt
├── ChatActivityTest.kt
└── MessageFlowTest.kt
```

## Build and CI/CD

### 1. Automated Builds

**Recommended**: Set up GitHub Actions for:
- Automated APK builds on PR
- Lint checks
- Unit test execution
- Release APK generation

### 2. Code Quality

**Recommended Tools**:
- Detekt for Kotlin static analysis
- ktlint for code formatting
- Android Lint for Android-specific issues

## Accessibility

### Current State
The app uses Material Components which provide basic accessibility support.

### Improvements Needed
- Add content descriptions for all UI elements
- Test with TalkBack
- Ensure proper focus order
- Support dynamic text sizing
- Add high contrast mode support

## Internationalization

### Current State
All strings are in English and defined in `strings.xml`.

### Recommended
- Add translations for common languages
- Support RTL languages
- Use string formatting for dynamic content
- Test with different locales

## Priority Recommendations

For immediate improvement, prioritize:

1. **High Priority**:
   - Implement proper reconnection logic
   - Add message confirmation
   - Fix WebSocket client lifecycle management
   - Add unit tests

2. **Medium Priority**:
   - Improve error messages
   - Add loading states
   - Implement network security config
   - Add basic CI/CD

3. **Low Priority**:
   - Message formatting
   - Typing indicators
   - Internationalization
   - Advanced caching

## Contributing

When addressing these issues:
1. Create a new branch for each improvement
2. Add tests for new functionality
3. Update documentation
4. Submit a pull request with detailed description

For questions or discussions about these issues, please open an issue in the GitHub repository.
