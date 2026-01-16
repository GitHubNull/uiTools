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
mvn exec:java -Dexec.mainClass="org.oxff.ui.MainWindow"

# Run the JAR file directly
java -jar target/uiTools-1.6.4.jar

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
- **MainWindow** (src/main/java/org/oxff/ui/MainWindow.java:44) - Main application window with modular architecture
- **OperationFactory** (src/main/java/org/oxff/core/OperationFactory.java:35) - Factory pattern for creating and managing operations
- **Operation** interface (src/main/java/org/oxff/operation/Operation.java:9) - Core interface all operations must implement
- **OperationCategory** enum (src/main/java/org/oxff/core/OperationCategory.java:6) - Operation categories (ENCODING_DECODING, FORMATTING, HASHING, AUTOMATION, QRCODE, TIMESTAMP, GENERATOR)
- **Subcategory** (src/main/java/org/oxff/core/Subcategory.java:7) - Two-level classification system for operations
- **SubcategoryRegistry** (src/main/java/org/oxff/core/SubcategoryRegistry.java:9) - Registry for all available subcategories

### Operation System
All operations implement the `Operation` interface with the following methods:
- `execute(String input)` - Process input and return result
- `getCategory()` - Return operation category
- `getDisplayName()` - Return display name for UI
- `getSubcategory()` - Return operation subcategory (optional, for two-level grouping)
- `returnsImage()` - Return true if operation produces image output
- `getImageData(String input)` - Return image data as Base64 data URL

Operations are automatically registered in OperationFactory static block and appear in UI without additional configuration.

### UI Architecture
The UI uses a modular architecture with separated concerns:

**Main Components:**
- **MainWindow** - Main application window that coordinates all UI components
- **UIComponentRegistry** - Central registry for all UI components

**Builder Package** (`ui/builder/`):
- `OperationTreeBuilder` - Builds the operation selection tree
- `InputPanelBuilder` - Builds all input panels (text, image, automation, etc.)
- `ExpressionPanelBuilder` - Builds expression input panel
- `OutputPanelBuilder` - Builds output panel with text/image switching
- `ConfigPanelBuilder` - Builds configuration panels for specific operations
- `TimestampConfigPanelBuilder` - Specialized builder for timestamp operations
- `PasswordConfigListener` - Configuration listener for password generator
- `TimestampConfigListener` - Configuration listener for timestamp operations

**Controller Package** (`ui/controller/`):
- `OperationValidator` - Validates operation inputs
- `OperationExecutor` - Executes operations and handles results
- `UIStateManager` - Manages UI state transitions
- `OperationExecutionContext` - Context object for operation execution

**Handler Package** (`ui/handler/`):
- `EventHandler` - Main event handler for all UI interactions
- `ClipboardManager` - Handles clipboard operations
- `TextFileManager` - Handles file I/O for text content

**Image Package** (`ui/image/`):
- `ImageDisplayManager` - Manages image display in output panel
- `ImageFileManager` - Handles image file selection and loading

**Util Package** (`ui/util/`):
- `LogManager` - Manages application logging
- `KeyboardShortcutManager` - Sets up keyboard shortcuts
- `SettingsManager` - Manages persistent application settings using Java Preferences API

### Two-Level Operation Classification
Operations are organized using a two-level classification system:

**Categories (Level 1):**
- ENCODING_DECODING (编解码)
- FORMATTING (格式化)
- HASHING (哈希)
- AUTOMATION (自动化操作)
- QRCODE (二维码)
- TIMESTAMP (时间戳)
- GENERATOR (生成工具)

**Subcategories (Level 2):**
- Base64, Base32, Hex, URL, Unicode, JWT, 图片编码 (for ENCODING_DECODING)
- 时间戳转换, 时间戳格式化, UTC时间 (for TIMESTAMP)
- 默认 (default for other categories)

### Key Dependencies
- **Gson 2.10.1** - JSON processing
- **dom4j 2.1.4** - XML processing
- **Apache Commons Codec 1.16.0** - Base64/Base32 encoding/decoding
- **FlatLaf 3.2.5** - UI theming
- **RSyntaxTextArea 3.3.3** - Enhanced text editor component
- **Jayway JsonPath 2.8.0** - JSONPath expressions
- **Jaxen 1.2.0** - XPath expressions
- **JJWT 0.12.5** - JWT encoding/decoding
- **ZXing 3.5.2** - QR code generation/parsing

### Project Structure
```
src/main/java/org/oxff/
├── core/                          # Core classes
│   ├── OperationFactory.java      # Operation factory and registry
│   ├── OperationCategory.java    # Operation category enum
│   ├── Subcategory.java          # Subcategory class
│   └── SubcategoryRegistry.java  # Subcategory registry
├── operation/                     # Operation implementations
│   ├── Operation.java            # Operation interface
│   ├── automation/               # Automation operations
│   ├── encoding/                 # Encoding/decoding operations
│   │   ├── base64/
│   │   ├── base32/
│   │   ├── hex/
│   │   ├── url/
│   │   ├── unicode/
│   │   ├── jwt/
│   │   └── image/
│   ├── formatting/               # Formatting operations
│   ├── generator/                # Generator operations
│   ├── hashing/                  # Hash operations
│   ├── qrcode/                   # QR code operations
│   └── timestamp/                # Timestamp operations
├── ui/                           # UI components
│   ├── MainWindow.java          # Main application window
│   ├── builder/                 # UI component builders
│   ├── components/              # UI component registry
│   ├── controller/              # Business logic controllers
│   ├── handler/                 # Event handlers
│   ├── image/                   # Image processing
│   └── util/                    # UI utilities
└── Main.java                    # Application entry point (legacy, now uses MainWindow.main)
```

### Adding New Operations
1. Create new class implementing `Operation` interface
2. Implement required methods: `execute()`, `getCategory()`, `getDisplayName()`
3. Optionally implement `getSubcategory()` for two-level grouping
4. Add instance to `allOperations` array in OperationFactory.java:42
5. Operation automatically appears in UI under its category and subcategory

### Special Operation Types
- **Expression-based operations** (JsonFormatOperation, XmlFormatOperation): Support optional second parameter for XPath/JSONPath expressions
- **Image operations** (QRCodeGenerateOperation, QRCodeDecodeOperation, ImageToBaseOperation): Implement `returnsImage()` and `getImageData()` methods
- **Automation operations** (AutoInputOperation): Support configuration via reflection (delay, interval, clipboard source)
- **Timestamp operations** (GetCurrentTimeOperation, TimestampToDatetimeOperation, DatetimeToTimestampOperation): Use timestamp-specific configuration panels
- **Generator operations** (RandomPasswordOperation): Use specialized configuration panels

### UI State Management
The UI uses a dynamic panel switching system:
- Input panels use CardLayout to switch between: TEXT, IMAGE, AUTOMATION, BASE_ENCODING, PASSWORD_GENERATOR, and timestamp-specific panels
- Output panels support both text and image output modes
- Operations can require: expression input, image input, timezone selection, automation config, or custom config panels
- Input area is enabled/disabled based on selected operation requirements

### Settings Persistence
The application uses Java Preferences API for persistent settings:
- Settings are stored under `/org/oxff/uiTools` node
- Auto-save directory configuration is persisted across sessions
- Default auto-save directory: `$USER_HOME/uiTools_outputs`

### Testing Approach
No automated tests currently exist. Manual testing is required for:
- All operation types and their specific behaviors
- UI state transitions between different panel types
- Image processing operations
- Automation configuration
- Settings persistence
- Two-level operation classification in the tree view

### Important Notes
- The project primarily uses Chinese for UI text and documentation
- Main entry point is now `org.oxff.ui.MainWindow` (not `org.oxff.Main`)
- Recent refactoring created modular UI architecture with Builder pattern
- Operations are now organized in two-level hierarchy in the tree view
- GitHub Actions workflow creates platform-specific packages automatically
- Version tags must follow v*.*.* pattern for releases
- Current version: 1.6.4