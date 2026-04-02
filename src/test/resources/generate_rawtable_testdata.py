#!/usr/bin/env python3
"""
Generate binary test data files for RawTableReader / ByteWiseFileAccessor tests.
Run from the project root: python3 src/test/resources/generate_rawtable_testdata.py
"""
import os

OUT = "src/test/resources/rawtable"
os.makedirs(OUT, exist_ok=True)

READ_BUFFER_SIZE = 8192

# --- Issue 5a: \r\n straddles the 8 KB buffer boundary ---
# \r is the LAST byte of the first 8192-byte buffer fill (index 8191).
# \n is the first byte of the second fill (index 8192).
# Expected: line 1 = "A" * 8191 + "\r\n", line 2 = "B" * 10 + "\r\n"
with open(f"{OUT}/cr_lf_at_boundary.dat", "wb") as f:
    f.write(b"A" * (READ_BUFFER_SIZE - 1))  # 8191 A's
    f.write(b"\r\n")                         # \r at index 8191, \n at 8192
    f.write(b"B" * 10)
    f.write(b"\r\n")

# --- Issue 5b: bare \r (no \n) straddles the 8 KB buffer boundary ---
# \r is the last byte of the first buffer fill; next byte is NOT \n.
# Expected: line 1 = "A" * 8191 + "\r", line 2 = "C" * 10 + "\n"
with open(f"{OUT}/bare_cr_at_boundary.dat", "wb") as f:
    f.write(b"A" * (READ_BUFFER_SIZE - 1))  # 8191 A's
    f.write(b"\r")                           # \r at index 8191
    f.write(b"C" * 10)                       # next byte is 'C', not '\n'
    f.write(b"\n")

# --- Issue 5c: \r is the very last byte of the file (EOF after \r) ---
# Expected: single line = "A" * 8191 + "\r"  (not null, not truncated)
with open(f"{OUT}/cr_at_eof.dat", "wb") as f:
    f.write(b"A" * (READ_BUFFER_SIZE - 1))
    f.write(b"\r")

# --- Issue 4: 0xFF byte mid-data ---
# Old readByte() widened byte to int; 0xFF sign-extended to -1 and hit case -1 (EOF).
# New readBytes() treats 0xFF as regular data.
# File: "HELLO\xFF WORLD\n"
# Old behavior: readNextLine() returns "HELLO" (stops at 0xFF as fake EOF)
# New behavior: readNextLine() returns "HELLO\xff WORLD"
with open(f"{OUT}/ff_byte_mid_line.dat", "wb") as f:
    f.write(b"HELLO\xff WORLD\n")
    f.write(b"SECOND LINE\n")

print("Test data written to", OUT)
