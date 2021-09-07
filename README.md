# Huffman Coding
Utilizes Huffman coding to losslessly compress and decompress files.

## Execution
Simply run Compression.java's main method to compress and decompress Tolstoy's War and Peace. Uncomment lines 259 and 268 to also compress and decompress the U.S. Constitution. (Note: you may need to alter the pathnames for these test files, depending on which folders you place them in.)

## Overview
File compression algorithms can be lossy or lossless: the former "throws away" information, the latter does not. Huffman's scheme is lossless, and employs a variable-length encoding of characters, in contrast to how ASCII encodes each character with 7 bits. The efficiency gain is that oft-occuring characters can be assigned shorter codes (binary sequences of bits).

This project implements Huffman coding for file compression and decompression, and demonstrates this by accurately (and quickly!) compressing and decompressing large documents, such as the U.S. Constitution and Tolstoy's War and Peace.
