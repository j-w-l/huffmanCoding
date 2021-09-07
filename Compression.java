import java.util.*;

import java.io.*;

/**
 * Compression methods for Huffman encoding.
 *
 * Created: 2/7/2020. Revised: 6/16/2021.
 * @author Jonathan Lee.
 */

public class Compression {
    BinaryTree<HuffmanData> codeTree = null;
    Map<Character, String> codeMap;


    /**
     * Constructor.
     */
    public Compression() {
        codeMap = new HashMap<>();
    }


    /**
     * Creates frequency table from file.
     * @param fileName: file to be read.
     * @return the corresponding frequency table.
     */
    HashMap<Character, Integer> createFrequencyTable(String fileName) {
        // Read in the file, one character at a time.
        // If the character is already in the table, increment its current corresponding value.
        // Else, add the character to the table with value 1.
        HashMap<Character, Integer> frequencyTable = new HashMap<>();
        BufferedReader input;

        // Open the file, if possible
        try {
            input = new BufferedReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return null;
        }


        // Read the file
        try {
            int cInt = input.read(); // reading next character's integer representation
            while (cInt != -1) {
                char c = (char)cInt;
                if (frequencyTable.containsKey(c)) {
                    frequencyTable.put(c, frequencyTable.get(c) + 1);
                }
                else {
                    frequencyTable.put(c, 1);
                }
                cInt = input.read(); // reading next character's integer representation
            }
        }

        catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        }
        catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }

        return frequencyTable;
    }


    /**
     * Creates and returns Huffman tree.
     * @param frequency: maps characters to their frequency in some text
     * @return the corresponding Huffman tree.
     */
    BinaryTree<HuffmanData> createHuffmanTree(HashMap<Character, Integer> frequency) {
        PriorityQueue<BinaryTree<HuffmanData>> PQ = new PriorityQueue<BinaryTree<HuffmanData>>( (BinaryTree<HuffmanData> b1, BinaryTree<HuffmanData> b2) -> b1.data.frequency - b2.data.frequency);

        // Create singleton Huffman trees for each character; place in PQ.
        for (char c : frequency.keySet()) {
            PQ.add(new BinaryTree<>(new HuffmanData(c, frequency.get(c))));
        }

        // Extract lowest two frequency trees, merge them, and place merged version back in PQ.
        while (PQ.size() > 1) {
            BinaryTree<HuffmanData> lowest = PQ.poll();
            BinaryTree<HuffmanData> second_lowest = PQ.poll();

            BinaryTree<HuffmanData> merger = new BinaryTree(new HuffmanData(lowest.data.frequency + second_lowest.data.frequency), lowest, second_lowest);

            PQ.add(merger);

        }

        // Last remaining tree in PQ is our Huffman code tree.
        return PQ.poll();
    }


    /**
     * Retrieve the char->code word map in ONE traversal of the tree.
     * @param tree: Huffman coding tree for a given file.
     * @return the codes corresponding to each character in said file.
     */
    HashMap<Character, String> retrieveCodeMap(BinaryTree<HuffmanData> tree) {
        HashMap<Character, String> codeMapping = new HashMap<>();
        helpTraverse(tree, codeMapping, "");
        return codeMapping;
    }

    void helpTraverse(BinaryTree<HuffmanData> tree, HashMap<Character, String> codeMapping, String curPath) {
        if (tree == null) {
            return;
        }

        if (tree.data.character != 0) {
            codeMapping.put(tree.data.character, curPath);
        }

        helpTraverse(tree.getLeft(), codeMapping, curPath + "0");
        helpTraverse(tree.getRight(), codeMapping, curPath + "1");
    }


    /**
     * Compresses a file by writing each char in inputFile as its corresponding code in the outputFile.
     * @param CodeMap: maps each character to its code.
     * @param inputFile: input file path.
     * @param outputFile: output file path.
     * @return nothing: just writes into the outputFile.
     */
    void compression(HashMap<Character, String> CodeMap, String inputFile, String outputFile) {
        BufferedReader input;
        BufferedBitWriter output;

        // Open the file, if possible
        try {
            input = new BufferedReader(new FileReader(inputFile));
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        try {
            output = new BufferedBitWriter(outputFile);
        }
        catch (Exception e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        // Read the file
        try {
            int cInt = input.read(); // reading next character's integer representation
            while (cInt != -1) {
                char c = (char)cInt;
                String interpret = CodeMap.get(c);
                for (int j = 0; j < interpret.length(); j++) {
                    if (interpret.charAt(j) == '0') {
                        output.writeBit(false);
                    }
                    else {
                        output.writeBit(true);
                    }
                }
                cInt = input.read(); // reading next character's integer representation
            }
        }
        catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
            output.close();
        }
        catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }

    }


    /**
     * Decompresses a file by writing each code in inputFile as its corresponding char in the outputFile by
     * traversing the code tree. No shared prefixes guarantees no ambiguity.
     * @param tree: Huffman code tree for some file.
     * @param inputFile: input file path.
     * @param outputFile: output file path.
     * @return nothing: just writes into the outputFile.
     */
    void decompression(BinaryTree<HuffmanData> tree, String inputFile, String outputFile) {
        BufferedBitReader input;
        BufferedWriter output;

        // Open the file, if possible
        try {
            input = new BufferedBitReader(inputFile);
        }
        catch (Exception e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        try {
            output = new BufferedWriter(new FileWriter(outputFile));
        }
        catch (Exception e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        // Read the file
        BinaryTree<HuffmanData> temp = tree;
        try {
            boolean bit;
            while (input.hasNext()) {
                bit = input.readBit();
                if (bit == false) {
                    temp = temp.getLeft();
                }
                else {
                    temp = temp.getRight();
                }

                if (temp.getLeft() == null && temp.getRight() == null) {
                    output.write(temp.data.character);
                    temp = tree;
                }
            }
        }
        catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
            output.close();
        }
        catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }
    }

    
    public static void main(String[] args) {
        Compression compress = new Compression();

//        HashMap<Character, Integer> frequencyTable = compress.createFrequencyTable("inputs/USConstitution.txt");
        HashMap<Character, Integer> frequencyTable = compress.createFrequencyTable("inputs/WarAndPeace.txt");

        BinaryTree<HuffmanData> tree = compress.createHuffmanTree(frequencyTable);
        HashMap<Character, String> map = compress.retrieveCodeMap(tree);
//        compress.compression(map, "inputs/USConstitution.txt", "inputs/compress_USConstitution.txt");
        compress.compression(map, "inputs/WarAndPeace.txt", "inputs/compress_WarAndPeace.txt");

        // Test that decompression can accurately decompress test files.
//        compress.decompression(tree, "inputs/compress_USConstitution.txt", "inputs/decompress_USConstitution.txt");
        compress.decompression(tree, "inputs/compress_WarAndPeace.txt", "inputs/decompress_WarAndPeace.txt");
    }
}
