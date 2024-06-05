/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oxin;

/**
 *
 * @author Riki A
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockchainShardingExample {

    // Define classes for entities
    static class Organization {
        String name;
        List<InternalNode> internalNodes;
        List<EndDeviceNode> endDeviceNodes;

        public Organization(String name, int numInternalNodes, int numEndDeviceNodes) {
            this.name = name;
            internalNodes = new ArrayList<>();
            for (int i = 0; i < numInternalNodes; i++) {
                internalNodes.add(new InternalNode(name + "_IN_" + (i + 1)));
            }
            endDeviceNodes = new ArrayList<>();
            for (int i = 0; i < numEndDeviceNodes; i++) {
                endDeviceNodes.add(new EndDeviceNode(name + "_ED_" + (i + 1)));
            }
        }
    }

    static class Node {
        String name;
        // ... other node properties (e.g., public key, etc.)

        public Node(String name) {
            this.name = name;
        }

        // ... methods for handling transactions, validating blocks, etc.
    }

    static class InternalNode extends Node {
        // ... internal node specific properties

        public InternalNode(String name) {
            super(name);
        }
    }

    static class EndDeviceNode extends Node {
        // ... end device node specific properties (e.g., storage capacity)
        private int storageCapacity = 10 * 1024 * 1024; // Example: 1MB storage

        public EndDeviceNode(String name) {
            super(name);
        }

        // Method to validate a shard of transaction data
        public boolean validateShard(byte[] shard) {
            // ... (Implement your validation logic here)
            // Example: Simple validation based on data integrity
            if (shard.length > 0) {
                System.out.println("Shard validated successfully by " + name);
                return true;
            } else {
                System.out.println("Shard validation failed by " + name);
                return false;
            }
        }
    }

    // ... Methods for transaction handling, block validation, etc.

    public static void main(String[] args) {
        // Create organizations and their nodes
        Organization bni = new Organization("BNI", 3, 20);
        Organization bca = new Organization("BCA", 3, 20);
        Organization btpn = new Organization("BTPN", 3, 20);

        // ... (Code for transaction generation, etc.)

        // Simulate a private transaction validation scenario
        // Assume BCA wants to validate a transaction with its end devices
        InternalNode bcaInternalNode = bca.internalNodes.get(0); // Choose an internal node from BCA
        List<EndDeviceNode> bcaEndDevices = bca.endDeviceNodes;

        // ... (Code for processing and validating the transaction)

        // Sharding the data for end devices
        // Assuming the data is in the form of a byte array 'transactionData'
        long totalDataSize = 100 * 1024 * 1024; // 100MB data size
        int shardSize = bcaEndDevices.get(0).storageCapacity; // Take storage capacity of first end device

        // Generate shards directly
        List<byte[]> shards = generateRandomData(totalDataSize, shardSize);
        System.out.println("SHARD DOT GET: " + shards.get(0));

        // Distribute shards to each end device
        int shardIndex = 0;
        for (EndDeviceNode endDevice : bcaEndDevices) {
            if (shardIndex < shards.size()) {
                // Send shard to end device
                System.out.println("Sending shard " + shardIndex + " to " + endDevice.name);
                // ... (Code for sending data to end device)
                endDevice.validateShard(shards.get(shardIndex)); // Validate shard on end device
                shardIndex++;
            } else {
                // All shards have been distributed
                break;
            }
        }

    // ... (Further processing, aggregation of results, etc.)

        // ... (Further processing, aggregation of results, etc.)
    }

    private static List<byte[]> generateRandomData(long totalSize, int shardSize) {
        List<byte[]> shards = new ArrayList<>();
        long remainingSize = totalSize;
        while (remainingSize > 0) {
            int currentShardSize = (int) Math.min(remainingSize, shardSize); // Clamp to shardSize
            System.out.println(currentShardSize);
            byte[] shard = new byte[currentShardSize];
            System.out.println("BEFORE nxtByte:" + shard.length);
//            System.out.println(shard[shard.length - 1]);
            new Random().nextBytes(shard);
            System.out.println("AFTER nxtByte:" +shard.length);
            
            shards.add(shard);
            remainingSize -= currentShardSize; 
        }
        return shards;
    }
}