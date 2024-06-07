# flamegrep

**flamegrep** is a concurrent file search tool written in Java. It allows you to search for a keyword within files in a specified directory concurrently, leveraging multiple threads for faster processing.

## Usage

1. Clone the repository to your local machine.
2. Compile the Java source code using `javac ConcurrentFileSearch.java`.
3. Run the program using `java ConcurrentFileSearch <start-directory> <keyword>`, where:
    - `<start-directory>` is the path to the directory where you want to start the search.
    - `<keyword>` is the keyword you want to search for within the files.

Example:
```bash
java ConcurrentFileSearch /path/to/directory keyword
```

## Features

- Concurrent file searching using multiple threads for improved performance.
- Handles both text and binary files.
- Prints the file paths where the keyword is found.

## Requirements

- Java Development Kit (JDK) installed on your system.
- Compatible with Java 8 and above.

## Contributing

Feel free to contribute to this project by submitting pull requests or reporting issues in the issue tracker.