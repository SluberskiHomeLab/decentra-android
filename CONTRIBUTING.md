# Contributing to Decentra Android

Thank you for your interest in contributing to the Decentra Android app! This document provides guidelines for contributing to the project.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and beginners
- Focus on what is best for the community
- Show empathy towards other community members

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR-USERNAME/decentra-android.git
   cd decentra-android
   ```
3. **Open in Android Studio** and let it sync
4. **Create a branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Setup

### Requirements
- Android Studio (latest stable version)
- JDK 17 or higher
- Android SDK (API 24-34)
- A Decentra server for testing (see [decentra](https://github.com/SluberskiHomeLab/decentra))

### Running the App
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Connect a device or start an emulator
4. Click Run or press Shift+F10

## How to Contribute

### Reporting Bugs

Before creating a bug report:
- Check if the bug has already been reported
- Ensure you're using the latest version
- Test with a fresh Decentra server instance

When reporting a bug, include:
- Android version
- Device model (or emulator details)
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots if applicable
- Relevant logs from Logcat

### Suggesting Features

When suggesting a feature:
- Check if it's already been suggested
- Explain the problem it solves
- Describe the proposed solution
- Provide examples of similar features in other apps
- Consider implementation complexity

### Pull Requests

1. **Before starting**:
   - Discuss major changes in an issue first
   - Check [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for planned improvements
   - Ensure your change aligns with the project goals

2. **While coding**:
   - Follow the existing code style
   - Write clear, descriptive commit messages
   - Keep changes focused and atomic
   - Add comments for complex logic
   - Update documentation as needed

3. **Before submitting**:
   - Test your changes thoroughly
   - Ensure the app builds successfully
   - Run lint checks: `./gradlew lint`
   - Add/update tests if applicable
   - Update relevant documentation

4. **Submitting the PR**:
   - Use a clear, descriptive title
   - Reference related issues
   - Describe what changed and why
   - Include screenshots for UI changes
   - List any breaking changes

## Code Style

### Kotlin
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Prefer immutability (`val` over `var`)
- Use data classes for models
- Leverage Kotlin's null safety features

### Android
- Follow [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide)
- Use Material Design components
- Support accessibility features
- Handle configuration changes properly
- Follow lifecycle best practices

### Comments
- Write self-documenting code when possible
- Add comments for complex algorithms
- Document public APIs with KDoc
- Explain "why" not "what" in comments

## Testing

### Manual Testing
- Test on different Android versions (min API 24, target API 34)
- Test on different screen sizes
- Test with different network conditions
- Test error scenarios

### Automated Testing
While the project doesn't have extensive tests yet, contributions adding tests are welcome:

```kotlin
// Example unit test
@Test
fun `message should format timestamp correctly`() {
    val message = Message("user", "text", 1234567890000)
    // Assert expectations
}
```

## Areas for Contribution

See [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for detailed improvement opportunities:

### High Priority
- Implement reconnection logic
- Add message send confirmation
- Fix WebSocket lifecycle management
- Add unit tests

### Medium Priority
- Improve error messages
- Add loading indicators
- Implement network security config
- Create CI/CD pipeline

### Low Priority
- Add message formatting (markdown)
- Implement typing indicators
- Add internationalization
- Improve accessibility

## Documentation

When updating documentation:
- Keep it clear and concise
- Use examples where helpful
- Update screenshots if UI changes
- Maintain consistent formatting
- Check for broken links

Documentation files:
- **README.md**: User-facing overview
- **BUILD.md**: Build instructions
- **ARCHITECTURE.md**: Technical details
- **KNOWN_ISSUES.md**: Known problems and roadmap
- **CONTRIBUTING.md**: This file

## Commit Messages

Follow these guidelines:
- Use present tense ("Add feature" not "Added feature")
- Use imperative mood ("Move cursor to..." not "Moves cursor to...")
- First line: short summary (max 50 chars)
- Blank line after first line
- Detailed description if needed (wrap at 72 chars)
- Reference issues: "Fixes #123" or "Relates to #456"

Good examples:
```
Add reconnection logic for WebSocket

Implements exponential backoff reconnection when WebSocket
connection is lost. Includes visual feedback in UI.

Fixes #42
```

```
Fix crash when rotating device during authentication

Properly save and restore authentication state during
configuration changes.

Closes #38
```

## Review Process

1. **Automated checks**: CI will build and test your PR
2. **Code review**: Maintainers will review your code
3. **Feedback**: Address any requested changes
4. **Approval**: Once approved, PR will be merged
5. **Celebrate**: Your contribution is now part of the project! 🎉

## Questions?

If you have questions:
- Check existing documentation
- Search closed issues
- Ask in a new issue
- Reach out to maintainers

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

## Recognition

All contributors will be recognized in:
- GitHub's contributor list
- Release notes for significant contributions
- Special thanks section (planned)

Thank you for contributing to Decentra Android! 🚀
