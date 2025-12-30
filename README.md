# decentra-android

Android application for Decentra that can be sideloaded onto Android devices to connect to self-hosted Decentra chat servers.

## Documentation

- **[README.md](README.md)** (this file) - Overview, features, and usage
- **[BUILD.md](BUILD.md)** - Detailed build instructions and troubleshooting
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Technical architecture and design
- **[KNOWN_ISSUES.md](KNOWN_ISSUES.md)** - Known limitations and future improvements

## About Decentra

Decentra is a self-hosted, decentralized Discord-like chat server. This Android app allows you to connect to your own Decentra instance and participate in real-time chat. Learn more about Decentra at: https://github.com/SluberskiHomeLab/decentra

## Features

- **WebSocket-based real-time messaging**: Connect to self-hosted Decentra instances
- **User authentication**: Login or signup with username/password
- **Invite code support**: Required for non-first users (configurable on server)
- **Message history**: View recent chat messages
- **Simple and clean UI**: Material Design interface
- **Self-hostable**: Connect to any Decentra instance you have access to

## Prerequisites

To build and run this app, you need:

- Android Studio (latest version recommended)
- Android SDK (API level 24 or higher)
- Java Development Kit (JDK) 17 or higher
- A running Decentra server instance (see https://github.com/SluberskiHomeLab/decentra)

## Building the App

1. Clone this repository:
   ```bash
   git clone https://github.com/SluberskiHomeLab/decentra-android.git
   cd decentra-android
   ```

2. Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository folder
   - Wait for Gradle sync to complete

3. Build the APK:
   - In Android Studio: `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - Or via command line:
     ```bash
     ./gradlew assembleDebug
     ```

4. The APK will be generated in `app/build/outputs/apk/debug/app-debug.apk`

## Installing the App

### Sideloading on Android Device

1. Enable "Install from Unknown Sources" on your Android device:
   - Go to Settings > Security (or Settings > Apps & Notifications > Special app access)
   - Enable "Unknown sources" or "Install unknown apps"

2. Transfer the APK to your device:
   - Via USB, email, cloud storage, or direct download
   
3. Tap the APK file on your device and follow the prompts to install

## Using the App

1. **Launch the app** on your Android device

2. **Enter server details**:
   - Server URL: The WebSocket URL of your Decentra instance (e.g., `ws://192.168.1.100:8765`)
   - For secure connections, use `wss://` instead of `ws://`

3. **Authentication**:
   - **First user**: Enter username and password, then tap "Sign Up" (invite code not required)
   - **Subsequent users**: Enter username, password, and a valid invite code, then tap "Sign Up"
   - **Existing users**: Enter username and password, then tap "Login"

4. **Chat**:
   - Once authenticated, you'll enter the chat room
   - Type messages in the input field and tap "Send"
   - All messages from all users will appear in real-time

## Configuration

### Server URL Format

The server URL must use the WebSocket protocol:
- **Unencrypted**: `ws://your-server-address:port`
- **Encrypted**: `wss://your-server-address:port`

Examples:
- Local development: `ws://localhost:8765`
- Local network: `ws://192.168.1.100:8765`
- Public server: `wss://decentra.yourdomain.com`

### Network Permissions

The app requires internet permission to connect to the Decentra server. This is declared in `AndroidManifest.xml` and includes:
- `INTERNET`: Required for WebSocket connections
- `ACCESS_NETWORK_STATE`: To check network availability

The app also uses `usesCleartextTraffic="true"` to allow unencrypted `ws://` connections for local development.

## Project Structure

```
decentra-android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/decentra/app/
│   │       │   ├── MainActivity.kt          # Login/signup screen
│   │       │   ├── ChatActivity.kt          # Main chat interface
│   │       │   ├── models/
│   │       │   │   └── Message.kt           # Message data model
│   │       │   ├── adapters/
│   │       │   │   └── MessageAdapter.kt    # RecyclerView adapter for messages
│   │       │   └── network/
│   │       │       └── DecentraWebSocketClient.kt  # WebSocket client
│   │       ├── res/                          # Resources (layouts, strings, etc.)
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Dependencies

- **AndroidX**: Core Android libraries
- **Material Components**: Material Design UI components
- **OkHttp**: WebSocket client library
- **Gson**: JSON serialization/deserialization
- **Kotlin Coroutines**: Asynchronous programming

## Troubleshooting

### Connection Issues

- **"Connection failed"**: Verify the server URL is correct and the server is running
- **"Invalid server URL"**: Ensure the URL starts with `ws://` or `wss://`
- **Timeout**: Check your network connection and firewall settings

### Authentication Issues

- **"Authentication failed"**: Verify your credentials are correct
- **Invite code errors**: Ensure you have a valid invite code (not needed for first user)

### Build Issues

- **Gradle sync failed**: Ensure you have the latest Android Gradle Plugin installed
- **SDK not found**: Install the required Android SDK version (API 24-34) via Android Studio

## Development

### Running in Debug Mode

```bash
./gradlew installDebug
```

This will install the debug version of the app on a connected device or emulator.

### Testing

The app includes basic unit and instrumentation test templates. To run tests:

```bash
./gradlew test           # Unit tests
./gradlew connectedTest  # Instrumentation tests (requires device/emulator)
```

## Security Considerations

- **Cleartext traffic**: The app allows unencrypted `ws://` connections for local development. For production, use encrypted `wss://` connections.
- **Authentication**: Passwords are sent over the WebSocket connection. Always use `wss://` for secure authentication.
- **No persistent storage**: The app does not store credentials or messages locally for privacy.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## License

This project is open source. Please refer to the LICENSE file for more information.

## Related Projects

- **Decentra Server**: https://github.com/SluberskiHomeLab/decentra
- **Decentra Windows Client**: https://github.com/SluberskiHomeLab/decentra-win

## Support

For issues specific to this Android client, please open an issue in this repository. For Decentra server issues, see the main Decentra repository.

