# High-Performance PII Redactor (Java)

## Overview
A zero-dependency, stream-based parser designed to detect and redact Personally Identifiable Information (PII) like Emails, Phone Numbers, and PAN Cards from massive server logs.

## The Engineering Challenge
Standard Regex engines (`java.util.regex`) suffer from performance degradation on large datasets due to backtracking (Time Complexity: Exponential in worst cases). This project implements a custom **Finite Automata-based Tokenizer** that processes text streams in **O(n)** linear time.

## How to Run
1. Generate a dummy log file:
   `javac LogGenerator.java && java LogGenerator large.log`
2. Run the Custom Redactor:
   `javac PIIRedactor.java && java PIIRedactor large.log output.txt`