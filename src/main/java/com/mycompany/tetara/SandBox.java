package com.mycompany.tetara;


import java.util.ArrayList;
import java.util.List;
public class SandBox {

    public static class Block {
        private BlockHeader header;
        private List<Transaction> transactions;
        private List<ExecutionResult> executionResults;

        public Block(BlockHeader header, List<Transaction> transactions, List<ExecutionResult> executionResults) {
            this.header = header;
            this.transactions = transactions;
            this.executionResults = executionResults;
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

        // Getters and Setters
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

        // Getters and Setters
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

        // Getters and Setters
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

        public List<byte[]> getAccountKeys() {
            return accountKeys;
        }

        public void setAccountKeys(List<byte[]> accountKeys) {
            this.accountKeys = accountKeys;
        }

        public MessageHeader getHeader() {
            return header;
        }

        public void setHeader(MessageHeader header) {
            this.header = header;
        }

        public List<Instruction> getInstructions() {
            return instructions;
        }

        public void setInstructions(List<Instruction> instructions) {
            this.instructions = instructions;
        }

        public byte[] getRecentBlockhash() {
            return recentBlockhash;
        }

        public void setRecentBlockhash(byte[] recentBlockhash) {
            this.recentBlockhash = recentBlockhash;
        }


        // Getters and Setters
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

// Getters and Setters
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
// Getters and Setters
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

        // Getters and Setters
    }

    public static void main(String[] args) {
        // Example of creating a Block
        BlockHeader header = new BlockHeader(
                new byte[32], new byte[32], 0, 1622540000L,
                new byte[32], new byte[32], new byte[32], 0, new byte[32]
        );

        List<byte[]> signatures = new ArrayList<>();
        signatures.add(new byte[64]);
        System.out.println(signatures);

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

        Transaction transaction = new Transaction(signatures, message);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        List<ExecutionResult> executionResults = new ArrayList<>();
        List<String> logs = new ArrayList<>();
        logs.add("Transaction executed successfully");
        executionResults.add(new ExecutionResult(logs, "Ok"));

        Block block = new Block(header, transactions, executionResults);

        System.out.println("Block created with slot number: " + block.header.slotNumber);

        System.out.println("Head: " + block.header.blockhash +" "+ block.header.getTimestamp() +" "+ block.header.getLeaderSchedule());
    }


}
