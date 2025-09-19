# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Prerequisites
- JDK 17 or higher
- Maven 3.6 or higher

### Build Commands
```bash
# Clean and build the project
mvn clean package

# Run the application
mvn exec:java -Dexec.mainClass="org.oxff.Main"

# Run the JAR file directly
java -jar target/uiTools-1.0-SNAPSHOT.jar

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
- **OperationCategory** enum (src/main/java/org/oxff/core/OperationCategory.java:6) - Operation categories (ENCODING_DECODING, FORMATTING, HASHING)

### Operation System
All operations implement the `Operation` interface with three methods:
- `execute(String input)` - Process input and return result
- `getCategory()` - Return operation category
- `getDisplayName()` - Return display name for UI

Operations are automatically registered in OperationFactory static block and appear in UI without additional configuration.

### UI Structure
- **StringFormatterUI** - Main Swing interface with categorized operation selection
- Uses FlatLaf for modern look and feel
- RSyntaxTextArea for enhanced text editing with syntax highlighting
- Supports clipboard operations and keyboard shortcuts (Ctrl+E to execute)

### Key Dependencies
- **Gson** - JSON processing
- **dom4j** - XML processing  
- **Apache Commons Codec** - Base64/Base32 encoding/decoding
- **FlatLaf** - UI theming
- **RSyntaxTextArea** - Enhanced text editor component

### Project Structure
```
src/main/java/org/oxff/
├── core/              # Core classes (OperationFactory, OperationCategory)
├── operation/         # Operation implementations
├── ui/                # UI components
└── Main.java          # Application entry point
```

### Adding New Operations
1. Create new class implementing `Operation` interface
2. Add instance to `allOperations` array in OperationFactory.java:16
3. Operation automatically appears in UI under its category