package com.mycompany.tetara;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SandBox {
    // Validator class
    static class Validator {
        private String publicKey;
        private int stakedAmount;

        public Validator(String publicKey, int stakedAmount) {
            this.publicKey = publicKey;
            this.stakedAmount = stakedAmount;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public int getStakedAmount() {
            return stakedAmount;
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
    }

    // BlockHeader class
    static class BlockHeader {
        int blockNumber;
        long timestamp;
        String previousHash;
        String stateRoot;
        String transactionRoot;
        String pohHash;
        String leaderSignature;

        public BlockHeader(int blockNumber, long timestamp, String previousHash, String stateRoot, String transactionRoot, String pohHash, String leaderSignature) {
            this.blockNumber = blockNumber;
            this.timestamp = timestamp;
            this.previousHash = previousHash;
            this.stateRoot = stateRoot;
            this.transactionRoot = transactionRoot;
            this.pohHash = pohHash;
            this.leaderSignature = leaderSignature;
        }
    }

    // Block class
    static class Block {
        BlockHeader header;
        List<Transaction> transactions;

        public Block(BlockHeader header, List<Transaction> transactions) {
            this.header = header;
            this.transactions = transactions;
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
                    System.out.println("Block Number: " + chain.get(chain.size() - 1).header.blockNumber);
                    System.out.println("Timestamp: " + chain.get(chain.size() - 1).header.timestamp);
                    System.out.println("Previous Hash: " + chain.get(chain.size() - 1).header.previousHash);
                    System.out.println("State Root: " + chain.get(chain.size() - 1).header.stateRoot);
                    System.out.println("Transaction Root: " + chain.get(chain.size() - 1).header.transactionRoot);
                    System.out.println("PoH Hash: " + chain.get(chain.size() - 1).header.pohHash);
                    System.out.println("Leader Signature: " + chain.get(chain.size() - 1).header.leaderSignature);
                    System.out.println("Transactions:");
                    for (Transaction tx : chain.get(chain.size() - 1).transactions) {
                        System.out.println("  TxID: " + tx.id + ", From: " + tx.from + ", To: " + tx.to + ", Amount: " + tx.amount + ", Timestamp: " + tx.timestamp);
                    }
                    break;
                default:
                for (Block block : chain) {
                    System.out.println("Block Number: " + block.header.blockNumber);
                    System.out.println("Timestamp: " + block.header.timestamp);
                    System.out.println("Previous Hash: " + block.header.previousHash);
                    System.out.println("State Root: " + block.header.stateRoot);
                    System.out.println("Transaction Root: " + block.header.transactionRoot);
                    System.out.println("PoH Hash: " + block.header.pohHash);
                    System.out.println("Leader Signature: " + block.header.leaderSignature);
                    System.out.println("Transactions:");
                    for (Transaction tx : block.transactions) {
                        System.out.println("  TxID: " + tx.id + ", From: " + tx.from + ", To: " + tx.to + ", Amount: " + tx.amount + ", Timestamp: " + tx.timestamp);
                    }
                    System.out.println();

                }
                break;

            }
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

        // Initialize validators
        List<Validator> validators = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : validatorStakes.entrySet()) {
            validators.add(new Validator(entry.getKey(), entry.getValue()));
        }

        // Elect a leader for block creation
        int epochNumber = 1;
        int epochCycle = 100000;
        int numSlots = 500000 ;  // Realistic number of slots per epoch


        System.out.println("Initializing Epoch " + epochNumber);
        initializeEpoch(epochNumber,epochCycle, numSlots, validators);
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
            if(slot == 0){
                // Create and add genesis block
                Block genesisBlock = createBlock(0, null, blockchain.mempool, "genesisLeaderPublicKey");
                blockchain.addBlock(genesisBlock);

                blockchain.printBlockchain("latestBlock");

                // Introduce a delay of 400ms
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
            }else if(slot != 0){

                // Create transactions for the slot
                blockchain.addTransaction(createTransaction("tx" + slot + "1", "Alice", "Bob", 10 + slot, "signature1"));
                blockchain.addTransaction(createTransaction("tx" + slot + "2", "Bob", "Charlie", 5 + slot, "signature2"));
                blockchain.addTransaction(createTransaction("tx" + slot + "3", "Charlie", "Dave", 2 + slot, "signature3"));

                // Create and add new block
                Block previousBlock = blockchain.chain.get(blockchain.chain.size() - 1);
                Block newBlock = createBlock(slot, previousBlock, blockchain.mempool, leader.getPublicKey());
                if (validateBlock(newBlock)) {
                    blockchain.addBlock(newBlock);
                }
                blockchain.printBlockchain("latestBlock");

                // Introduce a delay of 400ms
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");



                if(slot%epochCycle == 0){
                    // Introduce a delay of 400ms
                    System.out.println("EPOCH "+ epochNumber +" DONE");
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
        String previousHash = previousBlock == null ? "0" : Integer.toString(previousBlock.hashCode());
        long timestamp = System.currentTimeMillis();
        String stateRoot = "stateRoot";
        String transactionRoot = "transactionRoot";
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

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
