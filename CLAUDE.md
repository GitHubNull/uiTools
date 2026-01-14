# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Prerequisites
- JDK 11 or higher
- Maven 3.6 or higher

### Build Commands
```bash
# Clean and build the project
mvn clean package

# Run the application
mvn exec:java -Dexec.mainClass="org.oxff.Main"

# Run the JAR file directly
java -jar target/uiTools-1.4.0.jar

# Test build (skip tests)
mvn clean package -DskipTests
```

### Release Process
The project uses GitHub Actions for automated releases. When a version tag (v*.*.*) is pushed:
1. Maven builds the JAR with shade plugin (creates executable JAR)
2. Native packages are created for Linux (deb), macOS (dmg), and Windows (exe)
3. GitHub Release is created with all artifacts

## Architecture Overview

### Core Components
- **Main.java** (src/main/java/org/oxff/Main.java:14) - Application entry point with FlatLaf UI setup
- **OperationFactory** (src/main/java/org/oxff/core/OperationFactory.java:10) - Factory pattern for creating and managing operations
- **Operation** interface (src/main/java/org/oxff/operation/Operation.java:8) - Core interface all operations must implement
- **OperationCategory** enum (src/main/java/org/oxff/core/OperationCategory.java:6) - Operation categories (ENCODING_DECODING, FORMATTING, HASHING, AUTOMATION, QRCODE, TIMESTAMP)

### Operation System
All operations implement the `Operation` interface with three methods:
- `execute(String input)` - Process input and return result
- `getCategory()` - Return operation category
- `getDisplayName()` - Return display name for UI

Operations are automatically registered in OperationFactory static block and appear in UI without additional configuration.

### UI Architecture
The UI has been refactored into a modular architecture with separated concerns:
- **Main.java** - Application entry point that creates and shows StringFormatterUI
- **StringFormatterUI** - Main controller (formerly StringFormatterUIRefactored) that coordinates all components:
  - `components/` - UI component registry and builders
  - `controller/` - Business logic (OperationValidator, OperationExecutor, UIStateManager)
  - `handler/` - Event handling and clipboard management
  - `image/` - Image processing managers
  - `util/` - Utilities (LogManager, KeyboardShortcutManager)

### Key Dependencies
- **Gson** - JSON processing
- **dom4j** - XML processing
- **Apache Commons Codec** - Base64/Base32 encoding/decoding
- **FlatLaf** - UI theming
- **RSyntaxTextArea** - Enhanced text editor component
- **Jayway JsonPath** - JSONPath expressions
- **Jaxen** - XPath expressions
- **ZXing** - QR code generation/parsing

### Project Structure
```
src/main/java/org/oxff/
├── core/              # Core classes (OperationFactory, OperationCategory)
├── operation/         # Operation implementations (20+ operations)
├── ui/                # UI components (original and refactored)
│   ├── components/    # UI component registry and builders
│   ├── controller/    # Business logic controllers
│   ├── handler/       # Event handlers
│   ├── image/         # Image processing
│   └── util/          # UI utilities
└── Main.java          # Application entry point
```

### Adding New Operations
1. Create new class implementing `Operation` interface
2. Add instance to `allOperations` array in OperationFactory.java:16
3. Operation automatically appears in UI under its category

### Special Operation Types
- **Expression-based operations** (JsonFormatOperation, XmlFormatOperation): Support optional second parameter for XPath/JSONPath expressions
- **Image operations** (QRCodeGenerateOperation, QRCodeDecodeOperation): Handle image data and file paths
- **Automation operations** (AutoInputOperation): Support configuration via reflection (delay, interval, clipboard source)

### UI State Management
The refactored UI uses a state management system:
- Operations can require: expression input, image input, timezone selection, or automation config
- UI panels are switched using CardLayout based on selected operation
- Input area is disabled for operations that don't require text input

### Testing Approach
No automated tests currently exist. Manual testing is required for:
- All operation types and their specific behaviors
- UI state transitions
- Image processing operations
- Automation configuration

### Important Notes
- The project primarily uses Chinese for UI text and documentation
- Recent refactoring created modular UI architecture while maintaining original functionality
- GitHub Actions workflow creates platform-specific packages automatically
- Version tags must follow v*.*.* pattern for releases