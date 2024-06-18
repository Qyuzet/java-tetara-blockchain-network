package com.mycompany.tetara;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SandBox {
    // Validator class
    static class Validator {
        private String publicKey;
        private int stakedAmount;
        private List<Block> blockchain;

        public Validator(String publicKey, int stakedAmount) {
            this.publicKey = publicKey;
            this.stakedAmount = stakedAmount;
            this.blockchain = new ArrayList<>();
        }

        public String getPublicKey() {
            return publicKey;
        }

        public int getStakedAmount() {
            return stakedAmount;
        }

        public List<Block> getBlockchain() {
            return blockchain;
        }

        public void addBlock(Block block) {
            blockchain.add(block);
        }

        public String signBlock(String blockHash) {
            // Simple signature using public key and block hash
            return publicKey + "_signed_" + blockHash;
        }
    }

    // Transaction class
    static class Transaction {
        String id;
        String from;
        String to;
        double amount;
        long timestamp;
        String signature;

        public Transaction(String id, String from, String to, double amount, String signature) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.signature = signature;
            this.timestamp = System.currentTimeMillis();
        }

        public String getId() {
            return id;
        }
    }

    // BlockHeader class
    static class BlockHeader {
        int blockNumber;
        int pohValue;
        long timestamp;
        String previousHash;
        String stateRoot;
        String transactionRoot;
        String pohHash;
        String leaderSignature;
        String blockHash;

        public BlockHeader(int blockNumber, long timestamp, String previousHash, String stateRoot, String transactionRoot, String pohHash, String leaderSignature) {
            this.blockNumber = blockNumber;
            this.pohValue = blockNumber;
            this.timestamp = timestamp;
            this.previousHash = previousHash;
            this.stateRoot = stateRoot;
            this.transactionRoot = transactionRoot;
            this.pohHash = pohHash;
            this.leaderSignature = leaderSignature;
            this.blockHash = generateBlockHash();
        }

        private String generateBlockHash() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String data = blockNumber + timestamp + previousHash + stateRoot + transactionRoot + pohHash + leaderSignature;
                byte[] hash = digest.digest(data.getBytes());
                return bytesToHex(hash);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    // Block class
    static class Block {
        BlockHeader header;
        List<Transaction> transactions;
        List<String> attestationSignatures;

        public Block(BlockHeader header, List<Transaction> transactions) {
            this.header = header;
            this.transactions = transactions;
            this.attestationSignatures = new ArrayList<>();
        }

        public void addAttestationSignature(String signature) {
            attestationSignatures.add(signature);
        }

        public List<String> getAttestationSignatures() {
            return attestationSignatures;
        }
    }

    // Mempool class
    static class Mempool {
        List<Transaction> transactions;

        public Mempool() {
            this.transactions = new ArrayList<>();
        }

        public void addTransaction(Transaction transaction) {
            transactions.add(transaction);
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public void clear() {
            transactions.clear();
        }
    }

    // Blockchain class
    static class Blockchain {
        List<Block> chain;
        Mempool mempool;

        public Blockchain() {
            this.chain = new ArrayList<>();
            this.mempool = new Mempool();
        }

        public void addBlock(Block block) {
            chain.add(block);
        }

        public void addTransaction(Transaction transaction) {
            mempool.addTransaction(transaction);
        }

        public void printBlockchain(String mode) {
            switch (mode) {
                case "latestBlock":
                    printBlock(chain.get(chain.size() - 1));
                    break;
                default:
                    for (Block block : chain) {
                        printBlock(block);
                    }
                    break;
            }
        }

        private void printBlock(Block block) {
            //System.out.println("Block Number: " + block.header.blockNumber);
            System.out.println("Block Hash: " + block.header.blockHash);
            System.out.println("Timestamp: " + block.header.timestamp);
            System.out.println("Previous Hash: " + block.header.previousHash);
            System.out.println("State Root: " + block.header.stateRoot);
            System.out.println("Transaction Root: " + block.header.transactionRoot);
            System.out.println("PoH Hash: " + block.header.pohHash);
            System.out.println("Block Value: " + block.header.pohValue);
            System.out.println("Leader Signature: " + block.header.leaderSignature);
            System.out.println("Transactions:");
            for (Transaction tx : block.transactions) {
                System.out.println("  TxID: " + tx.id + ", From: " + tx.from + ", To: " + tx.to + ", Amount: " + tx.amount + ", Timestamp: " + tx.timestamp);
            }
            System.out.println("Attestation Signatures:");
            for (String signature : block.attestationSignatures) {
                System.out.println("  " + signature);
            }
            System.out.println();
        }
    }

    // Main class
    public static void main(String[] args) {
        // Example of validators with their stakes
        Map<String, Integer> validatorStakes = new HashMap<>();
        validatorStakes.put("Validator1", 1000);
        validatorStakes.put("Validator2", 1500);
        validatorStakes.put("Validator3", 2000);
        validatorStakes.put("Validator4", 2500);
        validatorStakes.put("Validator5", 1000);
        validatorStakes.put("Validator6", 1500);
        validatorStakes.put("Validator7", 2000);
        validatorStakes.put("Validator8", 2500);
        validatorStakes.put("Validator9", 1500);
        validatorStakes.put("Validator10", 2000);


        // Initialize validators
        List<Validator> validators = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : validatorStakes.entrySet()) {
            validators.add(new Validator(entry.getKey(), entry.getValue()));
        }

        // Elect a leader for block creation
        int epochNumber = 1;
        int epochCycle = 432000;
        int numSlots = 1000000;  // Realistic number of slots per epoch

        System.out.println("Initializing Epoch " + epochNumber);
        initializeEpoch(epochNumber, epochCycle, numSlots, validators);
    }

    private static void initializeEpoch(int epochNumber, int epochCycle, int numSlots, List<Validator> validators) {
        Blockchain blockchain = new Blockchain();

        System.out.println("Epoch " + epochNumber + " started");
        System.out.println("Generating leader schedule...");

        for (int slot = 0; slot < numSlots; slot++) {
            Validator leader = selectLeaderForSlot(validators, slot);
            System.out.println("Epoch " + epochNumber);
            System.out.println("Leader for slot " + slot + ": " + leader.getPublicKey());
            System.out.println("");

            if (slot == 0) {
                // Create and add genesis block
                Block genesisBlock = createBlock(0, null, blockchain.mempool, "genesisLeaderPublicKey");
                blockchain.addBlock(genesisBlock);

                blockchain.printBlockchain("latestBlock");

                // Introduce a delay of 400ms
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
            } else {
                // Create transactions for the slot
                blockchain.addTransaction(createTransaction("tx" + slot + "1", "Alice", "Bob", 10 + slot, "signature1"));
                blockchain.addTransaction(createTransaction("tx" + slot + "2", "Bob", "Charlie", 5 + slot, "signature2"));
                blockchain.addTransaction(createTransaction("tx" + slot + "3", "Charlie", "Dave", 2 + slot, "signature3"));

                // Create and add new block
                Block previousBlock = blockchain.chain.get(blockchain.chain.size() - 1);
                Block newBlock = createBlock(slot, previousBlock, blockchain.mempool, leader.getPublicKey());

                // Collect attestation signatures from validators
                for (Validator validator : validators) {
                    String attestationSignature = validator.signBlock(newBlock.header.blockHash);
                    newBlock.addAttestationSignature(attestationSignature);

                    // Check if 2/3 majority is reached
                    if (newBlock.getAttestationSignatures().size() >= (validators.size() * 2 / 3)) {
                        if (validateBlock(newBlock)) {
                            // Finalize the block by adding it to the leader's blockchain and sharing with other validators
                            blockchain.addBlock(newBlock);
                            for (Validator v : validators) {
                                v.addBlock(newBlock);
                            }
                        }
                        break;
                    }
                }
                blockchain.printBlockchain("latestBlock");

                // Introduce a delay of 400ms
                try {
                    Thread.sleep(423);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");

                if (slot % epochCycle == 0) {
                    // Introduce a delay of 400ms
                    System.out.println("EPOCH " + epochNumber + " DONE");
                    epochNumber++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("");
                }
            }
        }
        System.out.println("Done Scheduling");
    }

    private static Validator selectLeaderForSlot(List<Validator> validators, int slot) {
        // Calculate total stake
        int totalStake = validators.stream().mapToInt(Validator::getStakedAmount).sum();

        // Generate a list of validators weighted by their stake
        List<Validator> weightedValidators = new ArrayList<>();
        for (Validator validator : validators) {
            for (int i = 0; i < validator.getStakedAmount(); i++) {
                weightedValidators.add(validator);
            }
        }

        // Use slot number as a seed for reproducibility
        Random random = new Random(slot);

        // Randomly select a validator from the weighted list
        int randomIndex = random.nextInt(weightedValidators.size());
        return weightedValidators.get(randomIndex);
    }

    private static Block createBlock(int blockNumber, Block previousBlock, Mempool mempool, String leaderPublicKey) {
        String previousHash = previousBlock == null ? "0" : previousBlock.header.blockHash;
        long timestamp = System.currentTimeMillis();
        String stateRoot = "stateRoot";  // Placeholder for state root calculation
        String transactionRoot = calculateMerkleRoot(mempool.getTransactions());
        String pohHash = generatePoH(previousHash, timestamp);
        String leaderSignature = signBlock(leaderPublicKey, pohHash);

        List<Transaction> transactions = new ArrayList<>(mempool.getTransactions());
        mempool.clear();

        BlockHeader header = new BlockHeader(blockNumber, timestamp, previousHash, stateRoot, transactionRoot, pohHash, leaderSignature);
        return new Block(header, transactions);
    }

    private static boolean validateBlock(Block block) {
        // Placeholder for actual block validation logic
        return true;
    }

    private static Transaction createTransaction(String id, String from, String to, double amount, String signature) {
        return new Transaction(id, from, to, amount, signature);
    }

    private static String generatePoH(String previousHash, long timestamp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((previousHash + timestamp).getBytes());
            byte[] hash = digest.digest();
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String signBlock(String publicKey, String pohHash) {
        // Placeholder for actual signature logic
        return publicKey + "_signed_" + pohHash;
    }

    private static String calculateMerkleRoot(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return "";
        }

        List<String> tree = new ArrayList<>();
        for (Transaction transaction : transactions) {
            tree.add(hash(transaction.getId()));
        }

        while (tree.size() > 1) {
            List<String> newTree = new ArrayList<>();
            for (int i = 0; i < tree.size(); i += 2) {
                if (i + 1 < tree.size()) {
                    newTree.add(hash(tree.get(i) + tree.get(i + 1)));
                } else {
                    newTree.add(tree.get(i));
                }
            }
            tree = newTree;
        }
        return tree.get(0);
    }

    private static String hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
