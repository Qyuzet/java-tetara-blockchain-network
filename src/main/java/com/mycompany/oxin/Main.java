package com.mycompany.oxin;

import java.util.*;

class Block {
    private String data;
    private String previousHash;
    private String hash;
    private boolean isPublic;

    public Block(String data, String previousHash, boolean isPublic) {
        this.data = data;
        this.previousHash = previousHash;
        this.isPublic = isPublic;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return Integer.toHexString(Objects.hash(data, previousHash));
    }

    public String getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public boolean isPublic() {
        return isPublic;
    }
}

class Node {
    protected List<Block> blockchain;

    public Node() {
        blockchain = new ArrayList<>();
    }

    public void addBlock(Block block) {
        blockchain.add(block);
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public String getNodeType() {
        return "Node";
    }
}

class InternalNode extends Node {
    public InternalNode() {
        super();
    }

    @Override
    public String getNodeType() {
        return "Internal Node";
    }
}

class EndDeviceNode extends Node {
    public EndDeviceNode() {
        super();
    }

    @Override
    public String getNodeType() {
        return "End Device Node";
    }
}

class Blockchain {
    private Map<String, List<Node>> organizations;

    public Blockchain() {
        organizations = new HashMap<>();
    }

    public void addOrganization(String orgName, List<InternalNode> internalNodes) {
        organizations.put(orgName, new ArrayList<>(internalNodes));
    }

    public void addEndDevice(String orgName, EndDeviceNode endDevice) {
        if (!organizations.containsKey(orgName)) {
            organizations.put(orgName, new ArrayList<>());
        }
        organizations.get(orgName).add(endDevice);
    }

    public void createAndDistributeBlock(String orgName, String data, boolean isPublic) {
        List<Node> orgNodes = organizations.get(orgName);
        String previousHash = "0";
        if (!orgNodes.isEmpty()) {
            previousHash = orgNodes.get(0).getBlockchain().isEmpty() ? "0" : orgNodes.get(0).getBlockchain().get(orgNodes.get(0).getBlockchain().size() - 1).getHash();
        }
        Block newBlock = new Block(data, previousHash, isPublic);

        // Add the block to internal nodes of the specified organization
        for (Node node : orgNodes) {
            if (node instanceof InternalNode) { // Only add to internal nodes
                node.addBlock(newBlock);
                System.out.println("Added block to " + node.getNodeType() + " of " + orgName + ": " + data);
            }
        }

        // If the data is public, distribute to internal nodes of other organizations
        if (isPublic) {
            for (String otherOrg : organizations.keySet()) {
                if (!otherOrg.equals(orgName)) {
                    List<Node> otherOrgNodes = organizations.get(otherOrg);
                    for (Node node : otherOrgNodes) {
                        if (node instanceof InternalNode) { // Only add to internal nodes of other organizations
                            node.addBlock(newBlock);
                            System.out.println("Added public block to " + node.getNodeType() + " of " + otherOrg + ": " + data);
                        }
                    }
                }
            }
            // Shard the public data and distribute to end devices of all organizations
            shardAndDistributePublicDataToEndDevices(data);
        } else {
            // Shard the private data for end devices within the same organization
            shardAndDistributeDataToEndDevices(data, orgName);
        }
    }

    private void shardAndDistributePublicDataToEndDevices(String data) {
        int totalEndDevices = 0;
        for (String orgName : organizations.keySet()) {
            for (Node node : organizations.get(orgName)) {
                if (node instanceof EndDeviceNode) {
                    totalEndDevices++;
                }
            }
        }

        int shardSize = data.length() / totalEndDevices;
        int currentEndDevice = 0;

        for (String orgName : organizations.keySet()) {
            for (Node node : organizations.get(orgName)) {
                if (node instanceof EndDeviceNode) {
                    int startIdx = currentEndDevice * shardSize;
                    int endIdx = (currentEndDevice == totalEndDevices - 1) ? data.length() : (currentEndDevice + 1) * shardSize; // Ensure the last shard gets the remaining data
                    String shardData = data.substring(startIdx, endIdx);
                    String previousHash = node.getBlockchain().isEmpty() ? "0" : node.getBlockchain().get(node.getBlockchain().size() - 1).getHash();
                    Block shardBlock = new Block(shardData, previousHash, false);
                    node.addBlock(shardBlock);

                    // Print out the shard data being distributed
                    System.out.println("Distributing shard data to " + node.getNodeType() + " of " + orgName + ": " + shardData);
                    currentEndDevice++;
                }
            }
        }
    }

    private void shardAndDistributeDataToEndDevices(String data, String orgName) {
        List<Node> orgNodes = organizations.get(orgName);
        List<EndDeviceNode> orgEndDevices = new ArrayList<>();
        for (Node node : orgNodes) {
            if (node instanceof EndDeviceNode) {
                orgEndDevices.add((EndDeviceNode) node);
            }
        }

        int numEndDevices = orgEndDevices.size();
        int shardSize = data.length() / numEndDevices;

        for (int i = 0; i < numEndDevices; i++) {
            int startIdx = i * shardSize;
            int endIdx = (i == numEndDevices - 1) ? data.length() : (i + 1) * shardSize; // Ensure the last shard gets the remaining data
            String shardData = data.substring(startIdx, endIdx);
            String previousHash = orgEndDevices.get(i).getBlockchain().isEmpty() ? "0" : orgEndDevices.get(i).getBlockchain().get(orgEndDevices.get(i).getBlockchain().size() - 1).getHash();
            Block shardBlock = new Block(shardData, previousHash, false);
            orgEndDevices.get(i).addBlock(shardBlock);

            // Print out the shard data being distributed
            System.out.println("Distributing shard data to " + orgEndDevices.get(i).getNodeType() + " of " + orgName + ": " + shardData);
        }
    }

    public Map<String, List<Node>> getOrganizations() {
        return organizations;
    }
}

public class Main {
    public static void main(String[] args) {

        Blockchain blockchain = new Blockchain();

        // Create internal nodes for each organization
        List<InternalNode> bniInternalNodes = Arrays.asList(new InternalNode(), new InternalNode(), new InternalNode());
        List<InternalNode> bcaInternalNodes = Arrays.asList(new InternalNode(), new InternalNode(), new InternalNode());
        List<InternalNode> btpnInternalNodes = Arrays.asList(new InternalNode(), new InternalNode(), new InternalNode());

        // Add organizations and their internal nodes
        blockchain.addOrganization("BNI", bniInternalNodes);
        blockchain.addOrganization("BCA", bcaInternalNodes);
        blockchain.addOrganization("BTPN", btpnInternalNodes);

        // Create and add end-device nodes for each organization
        for (int i = 0; i < 5; i++) {
            EndDeviceNode bniEndDevice = new EndDeviceNode();
            EndDeviceNode bcaEndDevice = new EndDeviceNode();
            EndDeviceNode btpnEndDevice = new EndDeviceNode();

            blockchain.addEndDevice("BNI", bniEndDevice);
            blockchain.addEndDevice("BCA", bcaEndDevice);
            blockchain.addEndDevice("BTPN", btpnEndDevice);
        }

        // Create and distribute a public block across all end devices
        blockchain.createAndDistributeBlock("BCA", "Sample public data to share across all banks", true);

        // Create and distribute a private block within BCA
        blockchain.createAndDistributeBlock("BCA", "Sample private data for BCA only", false);

        // Print out the blockchain data for each organization and their nodes
        Map<String, List<Node>> organizations = blockchain.getOrganizations();
        for (String orgName : organizations.keySet()) {
            System.out.println("Organization: " + orgName);
            List<Node> nodes = organizations.get(orgName);
            for (Node node : nodes) {
                System.out.println(node.getNodeType() + " blockchain data for " + orgName + ":");
                for (Block block : node.getBlockchain()) {
                    System.out.println("Data: " + block.getData() + ", Hash: " + block.getHash() + ", PreviousHash: " + block.getPreviousHash());
                }
            }
        }
    }
}
