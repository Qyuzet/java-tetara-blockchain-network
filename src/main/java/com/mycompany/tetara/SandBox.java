package com.mycompany.tetara;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class SandBox {

    public interface IValidator {
        String getPublicKey();
        int getStakedAmount();
        List<SandBox.Block> getBlockchain();
        void addBlock(SandBox.Block block);
        String signBlock(String blockHash);
        String getType();
    }
    




    static class Validator implements IValidator{
        private String publicKey;
        private int stakedAmount;
        private List<Block> blockchain;
        private String type; // "public" or "ppe"

        public Validator(String publicKey, int stakedAmount, String type) {
            this.publicKey = publicKey;
            this.stakedAmount = stakedAmount;
            this.blockchain = new ArrayList<>();
            this.type = type;
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
            return publicKey + "_signed_" + blockHash;
        }

        public String getType() {
            return type;
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

        @Override
        public String toString() {
            return "Transaction{" +
                    "id='" + id + '\'' +
                    ", from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", amount=" + amount +
                    ", timestamp=" + timestamp +
                    ", signature='" + signature + '\'' +
                    '}';
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

    // Blockchain class (Modified)
    static class Blockchain {
        List<Block> chain;
        Mempool mempool;
        List<Validator> validators; // Store all validators (public and ppe)
        Map<String, String> shardLocations; // Tracks shard locations
        
         public interface BlockPrintCallback {
            void printBlockOutput(Block block, String output);
        }



        public Blockchain(List<Validator> validators) {
            this.chain = new ArrayList<>();
            this.mempool = new Mempool();
            this.validators = validators;
            this.shardLocations = new HashMap<>();
        }

        public void addBlock(Block block) {
            chain.add(block);
        }

        public void addTransaction(Transaction transaction) {
            mempool.addTransaction(transaction);
        }

        public void printBlockchain(String mode, BlockPrintCallback callback) {
            switch (mode) {
                
                case "latestBlock":
                    printBlock(chain.get(chain.size() - 1));
                    Block block = chain.get(chain.size() - 1);
                    String blockOutput = printBlock(block);
                    callback.printBlockOutput(block, blockOutput); 
                    break;
                default:
                    
                    for (Block blocks : chain) {
                        printBlock(blocks);
                        blockOutput = printBlock(blocks);
                        callback.printBlockOutput(blocks, blockOutput); // Pas
                    }
                    break;
            }
        }

        private String printBlock(Block block) {
            //System.out.println("Block Number: " + block.header.blockNumber);
            System.out.println("Block Hash: " + block.header.blockHash);
            System.out.println("Timestamp: " + block.header.timestamp);
            System.out.println("Previous Hash: " + block.header.previousHash);
            System.out.println("State Root: " + block.header.stateRoot);
            System.out.println("Transaction Root: " + block.header.transactionRoot);
            System.out.println("PoH Hash: " + block.header.pohHash);
            System.out.println("PoH Value: " + block.header.pohValue);
            System.out.println("Leader Signature: " + block.header.leaderSignature);
            System.out.println("Transactions:");
            
            StringBuilder output = new StringBuilder();
            output.append("Block Hash: " + block.header.blockHash + "\n");
            output.append("Timestamp: " + block.header.timestamp + "\n");
            output.append("Previous Hash: " + block.header.previousHash + "\n");
            output.append("State Root: " + block.header.stateRoot + "\n");
            output.append("Transaction Root: " + block.header.transactionRoot + "\n");
            output.append("PoH Hash: " + block.header.pohHash + "\n");
            output.append("PoH Value: " + block.header.pohValue + "\n");
            output.append("Leader Signature: " + block.header.leaderSignature + "\n");
            output.append("Transactions:\n");
            
            
            for (Transaction tx : block.transactions) {
                System.out.println("  TxID: " + tx.id + ", From: " + tx.from + ", To: " + tx.to + ", Amount: " + tx.amount + ", Timestamp: " + tx.timestamp);
                 output.append("  TxID: " + tx.id + ", From: " + tx.from + ", To: " + tx.to + ", Amount: " + tx.amount + ", Timestamp: " + tx.timestamp + "\n");
            }
            System.out.println("Attestation Signatures:");
            output.append("Attestation Signatures:\n");
            for (String signature : block.attestationSignatures) {
                System.out.println("  " + signature);
                output.append("  " + signature + "\n");
            }
            System.out.println();
            output.append("\n");
            
            
            
            return output.toString();
           
        }

         public interface totalTransactionDataCallback {
            void onTotalTransactionData(long output);
        }
         
         public interface BlockchainReconstructionCallback{
             void onBlockchainReconstructed(String output);
         }

        public void shardAndStoreBlock(Block block, totalTransactionDataCallback callback) {
            List<String> shards = splitBlockIntoShards(block, callback);
            assignShardsToPPENodes(shards, block.header.blockNumber);
        }


        private List<String> splitBlockIntoShards(Block block, totalTransactionDataCallback callback) {
            int maxShardSizeBytes = 1024; // 1 KB
            List<String> shards = new ArrayList<>();

            // Shard 1: Block Header
            shards.add("SBlock_" + block.header.blockNumber + "_1: Block Hash: " + block.header.blockHash +
                    ", Timestamp: " + block.header.timestamp +
                    ", Previous Hash: " + block.header.previousHash +
                    ", State Root: " + block.header.stateRoot +
                    ", Transaction Root: " + block.header.transactionRoot +
                    ", PoH Hash: " + block.header.pohHash +
                    ", PoH Value: " + block.header.pohValue +
                    ", Leader Signature: " + block.header.leaderSignature);

            // Shard 2 (and more): Transactions
            int shardSize = 0;
            int shardIndex = 2;
            int totalTransactionDataSize = 0;
            StringBuilder transactionShard = new StringBuilder();
            for (Transaction tx : block.transactions) {
                String transactionData = "TxID: " + tx.id + ", From: " + tx.from + ", To: " + tx.to + ", Amount: " + tx.amount + ", Timestamp: " + tx.timestamp;

                if (shardSize + transactionData.length() > maxShardSizeBytes) {
                    shards.add("SBlock_" + block.header.blockNumber + "_" + shardIndex + ": Transactions: " + transactionShard.toString());
                    shardIndex++;
                    shardSize = 0;
                    transactionShard = new StringBuilder();
                }
                transactionShard.append(transactionData).append(", ");
                shardSize += transactionData.length();
            }
            if (transactionShard.length() > 0) {
                shards.add("SBlock_" + block.header.blockNumber + "_" + shardIndex + ": " + transactionShard.toString());
            }

            // Shard N (and more): Attestation Signatures
            shardSize = 0;
            shardIndex++;
            StringBuilder attestationShard = new StringBuilder();
            for (String signature : block.attestationSignatures) {
                String signatureData = signature;

                if (shardSize + signatureData.length() > maxShardSizeBytes) {
                    shards.add("SBlock_" + block.header.blockNumber + "_" + shardIndex + ": Attestation Signatures: " + attestationShard.toString());
                    shardIndex++;
                    shardSize = 0;
                    attestationShard = new StringBuilder();
                }
                attestationShard.append(signatureData).append(", ");
                shardSize += signatureData.length();
                totalTransactionDataSize += signatureData.length();
            }
            if (attestationShard.length() > 0) {
                shards.add("SBlock_" + block.header.blockNumber + "_" + shardIndex + ": Attestation Signatures: " + attestationShard.toString());
            }

            callback.onTotalTransactionData(totalTransactionDataSize);
            return shards;
        }

        private void assignShardsToPPENodes(List<String> shards, int blockNumber) {
            List<Validator> ppeNodes = validators.stream()
                    .filter(v -> v.getType().equals("ppe"))
                    .collect(Collectors.toList());

            if (ppeNodes.isEmpty()) {
                throw new IllegalStateException("No PPE nodes available for sharding.");
            }

            int numShards = shards.size();
            int numPPENodes = ppeNodes.size();
            Map<String, Long> ppeShardDistribution = new LinkedHashMap<>();
            long initialDistribution = numShards / numPPENodes;
            long remainder = numShards % numPPENodes;

            for (int i = 1; i <= numPPENodes; i++) {
                long value = (i <= remainder) ? initialDistribution + 1 : initialDistribution;
                ppeShardDistribution.put("ppe_Node" + i, value);
            }

            Random random = new Random();
            int shardIndex = 0;

            // Create a single block for each PPE node to store shards
            for (Validator ppeNode : ppeNodes) {
                List<Transaction> shardTransactions = new ArrayList<>();

                // Determine the number of shards for this PPE node
                long numShardsForNode = ppeShardDistribution.get(ppeNode.getPublicKey());

                for (int i = 0; i < numShardsForNode; i++) {
                    if (shardIndex < shards.size()) {
                        String shardId = "SBlock_" + blockNumber + "_" + (shardIndex + 1);
                        String shardData = shards.get(shardIndex);

                        // Store shard location using the PPE node's public key
                        shardLocations.put(shardId, ppeNode.getPublicKey());

                        // Add shard as a Transaction to the list
                        shardTransactions.add(new Transaction(shardId, "", "", 0.0, shardData));
                        shardIndex++;
                    }
                }

                // Add the block with shard transactions to the PPE node's blockchain
                ppeNode.blockchain.add(new Block(null, shardTransactions));
            }
        }

        public void reconstructAndPrintBlockchain(BlockchainReconstructionCallback callback) {
            System.out.println("Reconstructing the entire blockchain:");

            // Find the highest block number from shardLocations
            int highestBlockNumber = shardLocations.keySet().stream()
                    .map(shardId -> Integer.parseInt(shardId.split("_")[1])) // Extract block number from shard ID
                    .max(Integer::compare)
                    .orElse(0); // Default to 0 if no shards found

            // Reconstruct and print each block
            StringBuilder PPEOutput = new StringBuilder();
            for (int blockNumber = 0; blockNumber <= highestBlockNumber; blockNumber++) {
                //reconstructAndPrintBlock(blockNumber);
                String reconstructedBlockData = reconstructAndPrintBlock(blockNumber);
                callback.onBlockchainReconstructed(reconstructAndPrintBlock(blockNumber).toString());
                //PPEOutput.append(reconstructAndPrintBlock(blockNumber));
            }
            //return PPEOutput.toString();
        }

        public String reconstructAndPrintBlock(int blockNumber) {
            StringBuilder reconstructedBlockData = new StringBuilder();

            // Get shard IDs for the specified block number and sort them
            List<String> sortedShardIds = shardLocations.keySet().stream()
                    .filter(shardId -> shardId.startsWith("SBlock_" + blockNumber + "_"))
                    .sorted() // Sort shard IDs in ascending order
                    .collect(Collectors.toList());

            // Reconstruct the block using sorted shard IDs
            for (String shardId : sortedShardIds) {
                String ppeNodePublicKey = shardLocations.get(shardId);

                Validator ppeNode = validators.stream()
                        .filter(node -> node.getPublicKey().equals(ppeNodePublicKey))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("PPE Node not found: " + ppeNodePublicKey));

                String shardData = ppeNode.getBlockchain()
                        .stream()
                        .filter(block -> block.transactions.get(0).getId().equals(shardId))
                        .findFirst()
                        .map(block -> block.transactions.get(0).signature)
                        .orElse("Shard not found");

                reconstructedBlockData.append(shardData).append("\n");
            }

            System.out.println("Reconstructed Block Data:\n" + reconstructedBlockData.toString());
            return "Reconstructed Block Data:\n" + reconstructedBlockData.toString();
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

        // Add PPE nodes
        validatorStakes.put("ppe_Node1", 100); // Example PPE node stakes
        validatorStakes.put("ppe_Node2", 150);
        validatorStakes.put("ppe_Node3", 200);
        validatorStakes.put("ppe_Node4", 250);
        validatorStakes.put("ppe_Node5", 150);

        // Initialize validators
        List<Validator> validators = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : validatorStakes.entrySet()) {
            String type = entry.getKey().startsWith("ppe_") ? "ppe" : "public";
            validators.add(new Validator(entry.getKey(), entry.getValue(), type));
        }

        // Elect a leader for block creation
        int epochNumber = 1;
        int epochCycle = 1000;
        int numSlots = 10000;  // Realistic number of slots per epoch

        System.out.println("Initializing Epoch " + epochNumber);
        initializeEpoch(epochNumber, epochCycle, numSlots, validators);
    }

    private static void initializeEpoch(int epochNumber, int epochCycle, int numSlots, List<Validator> validators) {
        Blockchain blockchain = new Blockchain(validators);

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

                //blockchain.printBlockchain("latestBlock");

                 blockchain.printBlockchain("latestBlock", (block, output) -> { 
                   System.out.print("");
                    System.out.println(output.toString()); 
                });
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
                // Shard and store the block data on PPE nodes
                blockchain.shardAndStoreBlock(newBlock, output -> {
                    // Call the new method
                });
                //blockchain.printBlockchain("latestBlock");
                 blockchain.printBlockchain("latestBlock", (block, output) -> { 
                   System.out.print("");
                    System.out.println(output.toString()); 
                });
                // Introduce a delay of 400ms
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");

                if (slot % epochCycle == 0) {
                    // Introduce a delay of 400ms
                    System.out.println("EPOCH " + epochNumber + " DONE");
                    epochNumber++;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("");
                }
            }
        }
        System.out.println("Done Scheduling");
        System.out.println("Reconstructing Block:");
        blockchain.reconstructAndPrintBlock(2);
        //blockchain.reconstructAndPrintBlockchain(); // Call the new method
        blockchain.reconstructAndPrintBlockchain(output -> {
             System.out.println("");
         });
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
        return new Transaction(id, from, String.valueOf(to), amount, signature);
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