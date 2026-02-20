# 🛡️ Zero-Dependency PII Redaction Engine

> An high-performance Personal Identifiable Information (PII) redactor for Java.  
> Built with a streaming architecture.

---

## 🚀 Key Features

- **Low-Allocation Streaming:** Processes massive log files character-by-character. Memory footprint remains flat regardless of file size.  
- **Extensible Strategy Pattern:** Designed following SOLID principles. Adding a new PII rule is as simple as adding a single class.  
- **Omni-Tool Design:** Usable as a CLI tool for DevOps log piping or imported as a `.jar` library in Spring Boot/Java applications.  
- **Deterministic Parsing:** Eliminates the risk of O(2ⁿ) regex catastrophic backtracking using a strict lexical analyzer.

---

## 💻 How to Use (For Developers)

### 1️⃣ As a Java Library (Maven / Gradle)

Import the library to instantly redact strings in memory before they hit your logging framework.

```java
import org.example.redactor.engine.RedactionEngine;

public class LogSanitizer {
    public static void main(String[] args) {
        String rawLog = "User [alex@test.com] logged in. Phone: 9876543210.";

        String safeLog = RedactionEngine.redactLine(rawLog);
        System.out.println(safeLog);
        // Output: User [EMAIL] logged in. Phone: [PHONE].
    }
}
```

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>[https://jitpack.io](https://jitpack.io)</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.ShivaniYadav7</groupId>
    <artifactId>high-perf-pii-redactor</artifactId>
    <version>v1.0.1</version>
</dependency>
```

### 2️⃣ As a DevOps CLI (Real-time Pipeline)
Pipe live dirty logs from your application directly into the redactor before forwarding them to Datadog / ELK.
# Stream live application logs through the Redactor
```bash
tail -f /var/log/app.log | java -jar pii-redactor.jar
```

### 3️⃣ As a Batch File Processor
Sanitize massive cold-storage files safely.
java -jar pii-redactor.jar dirty_database_dump.log sanitized_output.log

### 🧪 Supported PII Patterns
```table
| Type         | Example Input                                                     | Redacted Output     |
| ------------ | ----------------------------------------------------------------- | ------------------- |
| Email        | [admin.name@sub.domain.co.uk])                                    | [EMAIL]             |
| Phone        | +91-9876543210, (555)123-4567                                     | [PHONE]             |
| PAN Card     | ABCDE1234F                                                        | [PAN]               |
| Timestamps   | 2026-02-17                                                        | Preserved (Ignored) |
```

### ⚙️ Architecture & Testing
CI/CD Pipeline: Automated GitHub Actions pipeline runs tests on every PR.
Unit Tests: JUnit 5 suite covering valid tokens, boundary punctuation `email@test.com`, and invalid structures.
Time Complexity: O(N) where N = number of characters.

### 🛠️ Local Development
### Run automated test suite
```bash
./gradlew test
```

### Build executable distribution
```bash
./gradlew build
```
