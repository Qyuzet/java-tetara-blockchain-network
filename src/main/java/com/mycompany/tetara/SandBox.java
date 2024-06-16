package com.mycompany.tetara;

import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class SandBox {

    public static class Block {

        private BlockHeader header;
        private List<Transaction> transactions;
        private List<ExecutionResult> executionResults;
        private Block previousBlock; // Reference to the previous block

        public Block(BlockHeader header, List<Transaction> transactions, List<ExecutionResult> executionResults, Block previousBlock) {
            this.header = header;
            this.transactions = transactions;
            this.executionResults = executionResults;
            this.previousBlock = previousBlock;
        }

        public BlockHeader getHeader() {
            return header;
        }

        public void setHeader(BlockHeader header) {
            this.header = header;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        public List<ExecutionResult> getExecutionResults() {
            return executionResults;
        }

        public void setExecutionResults(List<ExecutionResult> executionResults) {
            this.executionResults = executionResults;
        }

        public Block getPreviousBlock() {
            return previousBlock;
        }

        public void setPreviousBlock(Block previousBlock) {
            this.previousBlock = previousBlock;
        }
    }

    public static class BlockHeader {
        private byte[] parentBlockHash;
        private byte[] blockhash;
        private long slotNumber;
        private long timestamp;
        private byte[] leaderSchedule;
        private byte[] stateRoot;
        private byte[] transactionsRoot;
        private long pohValue;
        private byte[] pohHash;

        public BlockHeader(byte[] parentBlockHash, byte[] blockhash, long slotNumber, long timestamp,
                           byte[] leaderSchedule, byte[] stateRoot, byte[] transactionsRoot, long pohValue, byte[] pohHash) {
            this.parentBlockHash = parentBlockHash;
            this.blockhash = blockhash;
            this.slotNumber = slotNumber;
            this.timestamp = timestamp;
            this.leaderSchedule = leaderSchedule;
            this.stateRoot = stateRoot;
            this.transactionsRoot = transactionsRoot;
            this.pohValue = pohValue;
            this.pohHash = pohHash;
        }

        public byte[] getBlockhash() {
            return blockhash;
        }

        public void setBlockhash(byte[] blockhash) {
            this.blockhash = blockhash;
        }

        public byte[] getLeaderSchedule() {
            return leaderSchedule;
        }

        public void setLeaderSchedule(byte[] leaderSchedule) {
            this.leaderSchedule = leaderSchedule;
        }

        public byte[] getParentBlockHash() {
            return parentBlockHash;
        }

        public void setParentBlockHash(byte[] parentBlockHash) {
            this.parentBlockHash = parentBlockHash;
        }

        public long getSlotNumber() {
            return slotNumber;
        }

        public void setSlotNumber(long slotNumber) {
            this.slotNumber = slotNumber;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public byte[] getStateRoot() {
            return stateRoot;
        }

        public void setStateRoot(byte[] stateRoot) {
            this.stateRoot = stateRoot;
        }

        public byte[] getTransactionsRoot() {
            return transactionsRoot;
        }

        public void setTransactionsRoot(byte[] transactionsRoot) {
            this.transactionsRoot = transactionsRoot;
        }

        public long getPohValue() {
            return pohValue;
        }

        public void setPohValue(long pohValue) {
            this.pohValue = pohValue;
        }

        public byte[] getPohHash() {
            return pohHash;
        }

        public void setPohHash(byte[] pohHash) {
            this.pohHash = pohHash;
        }
    }

    public static class Transaction {
        private List<byte[]> signatures;
        private Message message;

        public Transaction(List<byte[]> signatures, Message message) {
            this.signatures = signatures;
            this.message = message;
        }

        public List<byte[]> getSignatures() {
            return signatures;
        }

        public void setSignatures(List<byte[]> signatures) {
            this.signatures = signatures;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    public static class Message {
        private MessageHeader header;
        private List<byte[]> accountKeys;
        private byte[] recentBlockhash;
        private List<Instruction> instructions;

        public Message(MessageHeader header, List<byte[]> accountKeys, byte[] recentBlockhash, List<Instruction> instructions) {
            this.header = header;
            this.accountKeys = accountKeys;
            this.recentBlockhash = recentBlockhash;
            this.instructions = instructions;
        }

        public MessageHeader getHeader() {
            return header;
        }

        public void setHeader(MessageHeader header) {
            this.header = header;
        }

        public List<byte[]> getAccountKeys() {
            return accountKeys;
        }

        public void setAccountKeys(List<byte[]> accountKeys) {
            this.accountKeys = accountKeys;
        }

        public byte[] getRecentBlockhash() {
            return recentBlockhash;
        }

        public void setRecentBlockhash(byte[] recentBlockhash) {
            this.recentBlockhash = recentBlockhash;
        }

        public List<Instruction> getInstructions() {
            return instructions;
        }

        public void setInstructions(List<Instruction> instructions) {
            this.instructions = instructions;
        }
    }

    public static class MessageHeader {
        private byte numRequiredSignatures;
        private byte numReadonlyUnsignedAccounts;
        private byte numReadonlySignedAccounts;

        public MessageHeader(byte numRequiredSignatures, byte numReadonlyUnsignedAccounts, byte numReadonlySignedAccounts) {
            this.numRequiredSignatures = numRequiredSignatures;
            this.numReadonlyUnsignedAccounts = numReadonlyUnsignedAccounts;
            this.numReadonlySignedAccounts = numReadonlySignedAccounts;
        }

        public byte getNumRequiredSignatures() {
            return numRequiredSignatures;
        }

        public void setNumRequiredSignatures(byte numRequiredSignatures) {
            this.numRequiredSignatures = numRequiredSignatures;
        }

        public byte getNumReadonlyUnsignedAccounts() {
            return numReadonlyUnsignedAccounts;
        }

        public void setNumReadonlyUnsignedAccounts(byte numReadonlyUnsignedAccounts) {
            this.numReadonlyUnsignedAccounts = numReadonlyUnsignedAccounts;
        }

        public byte getNumReadonlySignedAccounts() {
            return numReadonlySignedAccounts;
        }

        public void setNumReadonlySignedAccounts(byte numReadonlySignedAccounts) {
            this.numReadonlySignedAccounts = numReadonlySignedAccounts;
        }
    }

    public static class Instruction {
        private byte programIdIndex;
        private List<Byte> accounts;
        private List<Byte> data;

        public Instruction(byte programIdIndex, List<Byte> accounts, List<Byte> data) {
            this.programIdIndex = programIdIndex;
            this.accounts = accounts;
            this.data = data;
        }

        public byte getProgramIdIndex() {
            return programIdIndex;
        }

        public void setProgramIdIndex(byte programIdIndex) {
            this.programIdIndex = programIdIndex;
        }

        public List<Byte> getAccounts() {
            return accounts;
        }

        public void setAccounts(List<Byte> accounts) {
            this.accounts = accounts;
        }

        public List<Byte> getData() {
            return data;
        }

        public void setData(List<Byte> data) {
            this.data = data;
        }
    }

    public static class ExecutionResult {
        private List<String> logs;
        private String status;

        public ExecutionResult(List<String> logs, String status) {
            this.logs = logs;
            this.status = status;
        }

        public List<String> getLogs() {
            return logs;
        }

        public void setLogs(List<String> logs) {
            this.logs = logs;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Function to create a blockchain with a specified number of transactions
    public static Block createBlockchain(int numTransactions, Block previousBlock) {

        // Create a sample block header
        BlockHeader header = new BlockHeader(
                new byte[32], new byte[32], 0, 1622540000L,
                new byte[32], new byte[32], new byte[32], 0, new byte[32]
        );

        // Create transactions
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < numTransactions; i++) {
            transactions.add(createTransaction());
        }

        // Create execution results (for demonstration)
        List<ExecutionResult> executionResults = new ArrayList<>();
        List<String> logs = new ArrayList<>();
        logs.add("Transaction executed successfully");
        executionResults.add(new ExecutionResult(logs, "Ok"));

        // Create and return the blockchain block
        // Create and return the blockchain block
        Block newBlock = new Block(header, transactions, executionResults, previousBlock);
        return newBlock;
    }

    // Helper method to create a sample transaction (customize as needed)
    private static Transaction createTransaction() {
        List<byte[]> signatures = new ArrayList<>();
        signatures.add(new byte[64]);

        List<byte[]> accountKeys = new ArrayList<>();
        accountKeys.add(new byte[32]);
        accountKeys.add(new byte[32]);

        List<Instruction> instructions = new ArrayList<>();
        List<Byte> instructionAccounts = new ArrayList<>();
        instructionAccounts.add((byte) 0);
        instructionAccounts.add((byte) 1);
        List<Byte> instructionData = new ArrayList<>();
        instructionData.add((byte) 1);
        instructionData.add((byte) 2);
        instructionData.add((byte) 3);
        instructionData.add((byte) 4);
        instructions.add(new Instruction((byte) 0, instructionAccounts, instructionData));

        MessageHeader messageHeader = new MessageHeader((byte) 1, (byte) 1, (byte) 0);
        Message message = new Message(messageHeader, accountKeys, new byte[32], instructions);

        return new Transaction(signatures, message);
    }

    public static void showBlockchain(Block blockchain) {
        // Print block information
        System.out.println("Block created with slot number: " + blockchain.getHeader().getSlotNumber());

        System.out.println("Header: ");
        System.out.println("  Blockhash: " + bytesToHex(blockchain.getHeader().getBlockhash()));
        System.out.println("  Timestamp: " + blockchain.getHeader().getTimestamp());
        System.out.println("  Leader Schedule: " + bytesToHex(blockchain.getHeader().getLeaderSchedule()));
        System.out.println("  Poh Hash: " + bytesToHex(blockchain.getHeader().getPohHash()));
        System.out.println("  Transactions Root: " + bytesToHex(blockchain.getHeader().getTransactionsRoot()));
        System.out.println("  Parent BlockHash: " + bytesToHex(blockchain.getHeader().getParentBlockHash()));
        System.out.println("  Poh Value: " + blockchain.getHeader().getPohValue());

        // Print transactions
        System.out.println("\nTransactions: ");
        int txIndex = 1;
        for (Transaction tx : blockchain.getTransactions()) {
            System.out.println("Transaction " + txIndex + ": ");
            System.out.println("  Signatures: " + tx.getSignatures());
            System.out.println("  Message: ");
            System.out.println("    Recent Blockhash: " + bytesToHex(tx.getMessage().getRecentBlockhash()));
            System.out.println("    Account Keys: " + tx.getMessage().getAccountKeys());
            System.out.println("    Instructions: ");
            int instrIndex = 1;
            for (Instruction instruction : tx.getMessage().getInstructions()) {
                System.out.println("      Instruction " + instrIndex + ": ");
                System.out.println("        Program Id Index: " + instruction.getProgramIdIndex());
                System.out.println("        Accounts: " + instruction.getAccounts());
                System.out.println("        Data: " + instruction.getData());
                instrIndex++;
            }
            txIndex++;
        }

        // Print execution results (for demonstration)
        System.out.println("\nExecution Results: ");
        for (ExecutionResult result : blockchain.getExecutionResults()) {
            System.out.println("  Logs: " + result.getLogs());
            System.out.println("  Status: " + result.getStatus());
        }

        // Print the hash of the previous block if available
        Block previousBlock = blockchain.getPreviousBlock();
        if (previousBlock != null) {
            System.out.println("\nPrevious Block Hash: " + bytesToHex(previousBlock.getHeader().getBlockhash()));
        } else {
            System.out.println("\nGenesis Block (no previous block)");
        }
    }

    public static void main(String[] args) {
        // Create the genesis block
        Block genesisBlock = createBlockchain(1, null);

        byte[] inputBytes = new byte[32]; // Replace with your byte array
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Update digest with inputBytes
            md.update(inputBytes);

            // Generate the hash
            byte[] hashBytes = md.digest();
            genesisBlock.header.setBlockhash(hashBytes);

            // Convert hashBytes to a hexadecimal string
            String hashHex = bytesToHex(hashBytes);

            // Print the hash
            System.out.println("SHA-256 Hash: " + hashHex);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not available.");
            e.printStackTrace();
        }



        // Create subsequent blocks
        Block blockchain1 = createBlockchain(1, genesisBlock);
        Block blockchain2 = createBlockchain(1, blockchain1);

        // Display blockchain information
        System.out.println("Blockchain 1:");
        showBlockchain(blockchain1);

        System.out.println("\nBlockchain 2:");
        showBlockchain(blockchain2);
    }

    // Helper method to convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
