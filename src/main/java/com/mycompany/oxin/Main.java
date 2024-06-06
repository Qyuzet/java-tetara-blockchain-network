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
    protected boolean isVirtual;

    public Node(boolean isVirtual) {
        blockchain = new ArrayList<>();
        this.isVirtual = isVirtual;
    }

    public void addBlock(Block block) {
        blockchain.add(block);
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public String getNodeType() {
        return "Node";
    }
}

class InternalNode extends Node {
    public InternalNode(boolean isVirtual) {
        super(isVirtual);
    }

    @Override
    public String getNodeType() {
        return "Internal Node";
    }
}

class EndDeviceNode extends Node {
    public EndDeviceNode(boolean isVirtual) {
        super(isVirtual);
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

    public void createAndDistributeBlock(String orgName, String data, String inodeType, String endDeviceType) {
        List<Node> orgNodes = organizations.get(orgName);
        String previousHash = "0";
        if (!orgNodes.isEmpty()) {
            previousHash = orgNodes.get(0).getBlockchain().isEmpty() ? "0" : orgNodes.get(0).getBlockchain().get(orgNodes.get(0).getBlockchain().size() - 1).getHash();
        }
        boolean isPublic = endDeviceType.contains("public");
        Block newBlock = new Block(data, previousHash, isPublic);

        // Add the block to internal nodes based on inodeType
        for (Node node : orgNodes) {
            if (node instanceof InternalNode) {
                if ((inodeType.equals("Inode_public") && node.getNodeType().equals("Internal Node")) ||
                        (inodeType.equals("Inode_private") && node.getNodeType().equals("Internal Node"))) {
                    node.addBlock(newBlock);
                    System.out.println("Added block to " + node.getNodeType() + " of " + orgName + ": " + data);
                }
            }
        }

        // Distribute data to end devices based on endDeviceType
        if (endDeviceType.equals("ED_public")) {
            shardAndDistributePublicDataToEndDevices(data);
        } else {
            shardAndDistributeDataToEndDevices(data, orgName);
        }

        // If inodeType is public, add block to internal nodes of other organizations
        if (inodeType.equals("Inode_public")) {
            for (String otherOrg : organizations.keySet()) {
                if (!otherOrg.equals(orgName)) {
                    List<Node> otherOrgNodes = organizations.get(otherOrg);
                    for (Node node : otherOrgNodes) {
                        if (node instanceof InternalNode) {
                            node.addBlock(newBlock);
                            System.out.println("Added public block to " + node.getNodeType() + " of " + otherOrg + ": " + data);
                        }
                    }
                }
            }
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
                    int endIdx = (currentEndDevice == totalEndDevices - 1) ? data.length() : (currentEndDevice + 1) * shardSize;
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
            int endIdx = (i == numEndDevices - 1) ? data.length() : (i + 1) * shardSize;
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
        List<InternalNode> bniInternalNodes = Arrays.asList(new InternalNode(false), new InternalNode(false), new InternalNode(false));
        List<InternalNode> bcaInternalNodes = Arrays.asList(new InternalNode(false), new InternalNode(false), new InternalNode(false));
        List<InternalNode> btpnInternalNodes = Arrays.asList(new InternalNode(false), new InternalNode(false), new InternalNode(false));

        // Add organizations and their internal nodes
        blockchain.addOrganization("BNI", bniInternalNodes);
        blockchain.addOrganization("BCA", bcaInternalNodes);
        blockchain.addOrganization("BTPN", btpnInternalNodes);

        // Create and add end-device nodes for each organization
        for (int i = 0; i < 5; i++) {
            EndDeviceNode bcaEndDevice = new EndDeviceNode(false);
            EndDeviceNode btpnEndDevice = new EndDeviceNode(false);
            blockchain.addEndDevice("BCA", bcaEndDevice);
            blockchain.addEndDevice("BTPN", btpnEndDevice);
        }

        for (int i = 0; i < 2; i++) {
            EndDeviceNode bniEndDevice = new EndDeviceNode(false);
            blockchain.addEndDevice("BNI", bniEndDevice);
        }

        // Adding virtual end-device nodes
        for (int i = 0; i < 5; i++) {
            EndDeviceNode bcaVirtualEndDevice = new EndDeviceNode(true);
            EndDeviceNode btpnVirtualEndDevice = new EndDeviceNode(true);
            blockchain.addEndDevice("BCA", bcaVirtualEndDevice);
            blockchain.addEndDevice("BTPN", btpnVirtualEndDevice);
        }

        for (int i = 0; i < 2; i++) {
            EndDeviceNode bniVirtualEndDevice = new EndDeviceNode(true);
            blockchain.addEndDevice("BNI", bniVirtualEndDevice);
        }

        // Create and distribute blocks
        blockchain.createAndDistributeBlock("BCA", "Sample private data for BCA only", "Inode_private", "ED_private");
        blockchain.createAndDistributeBlock("BTPN", "Sample public data for BTPN", "Inode_public", "ED_public");

        // Print out the blockchains for each node in each organization
        for (Map.Entry<String, List<Node>> entry : blockchain.getOrganizations().entrySet()) {
            String orgName = entry.getKey();
            List<Node> nodes = entry.getValue();
            System.out.println("\nOrganization: " + orgName);
            for (Node node : nodes) {
                System.out.println(node.getNodeType() + (node.isVirtual() ? " (Virtual)" : ""));
                for (Block block : node.getBlockchain()) {
                    System.out.println(" - Block Hash: " + block.getHash() + ", Data: " + block.getData());
                }
            }
        }
    }
}
