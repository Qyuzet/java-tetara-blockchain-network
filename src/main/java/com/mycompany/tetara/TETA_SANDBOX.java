/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.tetara;

import com.mycompany.tetara.SandBox.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.SwingUtilities;

/**
 *
 * @author Riki A
 */
public class TETA_SANDBOX extends javax.swing.JFrame {

    /**
     * Creates new form OX_PPLATFORM
     */
    
     private void appendToTerminal(String text) {
        SwingUtilities.invokeLater(() -> { // Update Swing component on EDT
            
            
            txtTerminal.setText(text + "\n");
            
            // Auto-scroll to the bottom
        txtTerminal.setCaretPosition(txtTerminal.getDocument().getLength()); 
        
        });
    }
     
    private void appendToPPETerminal(String text) {
        SwingUtilities.invokeLater(() -> { // Update Swing component on EDT
            
            
            txtPPETerminal.setText(text + "\n");
            
            // Auto-scroll to the bottom
        txtPPETerminal.setCaretPosition(txtTerminal.getDocument().getLength()); 
        
        
        });
    }
     
     private void updateCurrentBlock (String block){
         SwingUtilities.invokeLater(() -> { // Update Swing component on EDT
            txtCurrentBlock.setText(block + "\n");
        });
     }
     
     private void updateCurrentLeader (String leader){
         SwingUtilities.invokeLater(() -> { // Update Swing component on EDT
            txtCurrentLeader.setText(leader + "\n");
        });
     }
     
     private void updateCurrentSlot (int pohValue){
         SwingUtilities.invokeLater(() -> { // Update Swing component on EDT
            txtCurrentSlot.setText(pohValue + "\n");
        });
     }
     
     private void updateCurrentEpoch (int epochValue){
         SwingUtilities.invokeLater(() -> { // Update Swing component on EDT
            txtCurrentEpoch.setText(epochValue + "\n");
        });
     }
     
    
    private static void initializeEpoch(TETA_SANDBOX sandboxInstance, int epochNumber, int epochCycle, int numSlots, List<Validator> validators,String speed ) {
        SandBox.Blockchain blockchain = new SandBox.Blockchain(validators);
        int  sleepTime;
        if(speed == "Fastest"){
            sleepTime = 0;
        }else if(speed == "Solana Network"){
            sleepTime = 400;
        }else{
            sleepTime = 1000;
        }
        

        System.out.println("Epoch " + epochNumber + " started");
        
        System.out.println("Generating leader schedule...");
        
        sandboxInstance.appendToTerminal("Epoch " + epochNumber + " started");
        sandboxInstance.appendToTerminal("Generating leader schedule...");
               

        for (int slot = 0; slot < numSlots; slot++) {
            Validator leader = selectLeaderForSlot(validators, slot);
            
            System.out.println("Epoch " + epochNumber);
            sandboxInstance.appendToTerminal("Epoch " + epochNumber);
            sandboxInstance.updateCurrentEpoch(epochNumber);
            
            System.out.println("Leader for slot " + slot + ": " + leader.getPublicKey());
            sandboxInstance.appendToTerminal("Leader for slot " + slot + ": " + leader.getPublicKey());
            sandboxInstance.updateCurrentLeader(leader.getPublicKey());
            
            System.out.println("");
            sandboxInstance.appendToTerminal("");

            if (slot == 0) {
                // Create and add genesis block
                SandBox.Block genesisBlock = createBlock(0, null, blockchain.mempool, "genesisLeaderPublicKey");
                blockchain.addBlock(genesisBlock);

                //blockchain.printBlockchain("latestBlock");
                
                 blockchain.printBlockchain("latestBlock", (block, output) -> { 
                    sandboxInstance.appendToTerminal(output.toString());
                    sandboxInstance.updateCurrentBlock(block.header.blockHash); 
                });
                  
                

                // Introduce a delay of 400ms
                try {
                    Thread.sleep(sleepTime);
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
                SandBox.Block previousBlock = blockchain.chain.get(blockchain.chain.size() - 1);
                SandBox.Block newBlock = createBlock(slot, previousBlock, blockchain.mempool, leader.getPublicKey());

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
                blockchain.shardAndStoreBlock(newBlock);
                //blockchain.printBlockchain("latestBlock");
                 blockchain.printBlockchain("latestBlock", (block, output) -> { 
                    sandboxInstance.appendToTerminal(output.toString());
                    sandboxInstance.updateCurrentBlock(block.header.blockHash);
                    sandboxInstance.updateCurrentSlot(block.header.pohValue);
                });

                // Introduce a delay of 400ms
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");

                if (slot % epochCycle == 0) {
                    // Introduce a delay of 400ms
                    System.out.println("EPOCH " + epochNumber + " DONE");
                    epochNumber++;
                    try {
                        Thread.sleep(sleepTime);
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
        blockchain.reconstructAndPrintBlockchain(); // Call the new method
        sandboxInstance.appendToPPETerminal(blockchain.reconstructAndPrintBlockchain());
        
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

    private static SandBox.Block createBlock(int blockNumber, SandBox.Block previousBlock, Mempool mempool, String leaderPublicKey) {
        String previousHash = previousBlock == null ? "0" : previousBlock.header.blockHash;
        long timestamp = System.currentTimeMillis();
        String stateRoot = "stateRoot";  // Placeholder for state root calculation
        String transactionRoot = calculateMerkleRoot(mempool.getTransactions());
        String pohHash = generatePoH(previousHash, timestamp);
        String leaderSignature = signBlock(leaderPublicKey, pohHash);

        List<Transaction> transactions = new ArrayList<>(mempool.getTransactions());
        mempool.clear();

        BlockHeader header = new BlockHeader(blockNumber, timestamp, previousHash, stateRoot, transactionRoot, pohHash, leaderSignature);
        return new SandBox.Block(header, transactions);
    }

    private static boolean validateBlock(SandBox.Block block) {
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
    
    public TETA_SANDBOX() {
        initComponents();
        
        

        
        

        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtEpochCycle = new javax.swing.JTextField();
        txtTotalSlots = new javax.swing.JTextField();
        btnInvoke = new javax.swing.JButton();
        txtSpeed = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtDAS = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        initEpoch = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtTerminal = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtPPETerminal = new javax.swing.JTextArea();
        jLabel24 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txtCurrentBlock = new javax.swing.JTextField();
        btnInterupt = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txtCurrentSlot = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtCurrentEpoch = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtCurrentLeader = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setFont(new java.awt.Font("Montserrat", 3, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("TETA-SANDBOX");

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Total Slots");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Epoch Cycle");

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Speed");

        txtEpochCycle.setBackground(new java.awt.Color(51, 51, 51));
        txtEpochCycle.setForeground(new java.awt.Color(255, 255, 255));
        txtEpochCycle.setText("100");

        txtTotalSlots.setBackground(new java.awt.Color(51, 51, 51));
        txtTotalSlots.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalSlots.setText("200");

        btnInvoke.setBackground(new java.awt.Color(51, 51, 51));
        btnInvoke.setForeground(new java.awt.Color(255, 255, 255));
        btnInvoke.setText(">>");
        btnInvoke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvokeActionPerformed(evt);
            }
        });

        txtSpeed.setBackground(new java.awt.Color(51, 51, 51));
        txtSpeed.setForeground(new java.awt.Color(255, 255, 255));
        txtSpeed.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fastest", "Solana Network", "Observer" }));

        jLabel5.setFont(new java.awt.Font("Montserrat", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("INIT EPOCH");

        txtDAS.setBackground(new java.awt.Color(51, 51, 51));
        txtDAS.setForeground(new java.awt.Color(255, 255, 255));
        txtDAS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enable", "Disable" }));

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("DAS");

        initEpoch.setBackground(new java.awt.Color(51, 51, 51));
        initEpoch.setForeground(new java.awt.Color(255, 255, 255));
        initEpoch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "0", " " }));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Teta - Kitchen");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEpochCycle, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTotalSlots, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDAS, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnInvoke, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(initEpoch, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initEpoch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEpochCycle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalSlots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInvoke)
                    .addComponent(txtDAS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));

        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Terminal");

        txtTerminal.setBackground(new java.awt.Color(51, 51, 51));
        txtTerminal.setColumns(20);
        txtTerminal.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        txtTerminal.setForeground(new java.awt.Color(255, 255, 255));
        txtTerminal.setRows(5);
        jScrollPane1.setViewportView(txtTerminal);

        txtPPETerminal.setBackground(new java.awt.Color(51, 51, 51));
        txtPPETerminal.setColumns(20);
        txtPPETerminal.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        txtPPETerminal.setForeground(new java.awt.Color(255, 255, 255));
        txtPPETerminal.setRows(5);
        jScrollPane2.setViewportView(txtPPETerminal);

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Terminal");

        jLabel19.setFont(new java.awt.Font("Montserrat", 3, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Shard & DAS Terminal");

        jLabel15.setFont(new java.awt.Font("Montserrat", 3, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Validation Terminal");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel24)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(174, 174, 174))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(602, 602, 602))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(174, 174, 174))))
        );

        jLabel7.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/TETARA-WHT-01 (2).png"))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Current Block");

        txtCurrentBlock.setBackground(new java.awt.Color(51, 51, 51));
        txtCurrentBlock.setForeground(new java.awt.Color(255, 255, 255));
        txtCurrentBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrentBlockActionPerformed(evt);
            }
        });

        btnInterupt.setBackground(new java.awt.Color(51, 51, 51));
        btnInterupt.setForeground(new java.awt.Color(255, 255, 255));
        btnInterupt.setText("X");
        btnInterupt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInteruptActionPerformed(evt);
            }
        });

        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Current Slot");

        txtCurrentSlot.setBackground(new java.awt.Color(51, 51, 51));
        txtCurrentSlot.setForeground(new java.awt.Color(255, 255, 255));

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Current Epoch");

        txtCurrentEpoch.setBackground(new java.awt.Color(51, 51, 51));
        txtCurrentEpoch.setForeground(new java.awt.Color(255, 255, 255));

        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Current Leader");

        txtCurrentLeader.setBackground(new java.awt.Color(51, 51, 51));
        txtCurrentLeader.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCurrentSlot, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurrentEpoch, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 60, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCurrentLeader, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                            .addComponent(txtCurrentBlock))))
                .addGap(18, 18, 18)
                .addComponent(btnInterupt)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtCurrentBlock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnInterupt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCurrentLeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCurrentSlot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCurrentEpoch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel22))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(31, 31, 31))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 1037, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInvokeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvokeActionPerformed
        // TODO add your handling code here:
        String setEpochInit = (String) initEpoch.getSelectedItem();//
        int setEpochCycle = Integer.parseInt(txtEpochCycle.getText());//
        int setTotalSlot = Integer.parseInt(txtTotalSlots.getText());//
        String isPPE = (String) txtDAS.getSelectedItem();
        String setTxtSpeed = (String) txtSpeed.getSelectedItem();
        
        
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
        validatorStakes.put("ppe_Node5", 100);

        // Initialize validators
        List<SandBox.Validator> validators = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : validatorStakes.entrySet()) {
            String type = entry.getKey().startsWith("ppe_") ? "ppe" : "public";
            validators.add(new SandBox.Validator(entry.getKey(), entry.getValue(), type));
        }

        // Elect a leader for block creation
        int epochNumber = setEpochInit.equals("1") ? 1 : 0;
        int epochCycle = setEpochCycle;
        int numSlots = setTotalSlot;  // Realistic number of slots per epoch
        
        new Thread(() -> {
        System.out.println("Initializing Epoch " + epochNumber);
        initializeEpoch(this, epochNumber, epochCycle, numSlots, validators,setTxtSpeed);
    }).start();
        
    }//GEN-LAST:event_btnInvokeActionPerformed

    private void btnInteruptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInteruptActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInteruptActionPerformed

    private void txtCurrentBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrentBlockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrentBlockActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TETA_SANDBOX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TETA_SANDBOX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TETA_SANDBOX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TETA_SANDBOX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TETA_SANDBOX().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInterupt;
    private javax.swing.JButton btnInvoke;
    private javax.swing.JComboBox<String> initEpoch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtCurrentBlock;
    private javax.swing.JTextField txtCurrentEpoch;
    private javax.swing.JTextField txtCurrentLeader;
    private javax.swing.JTextField txtCurrentSlot;
    private javax.swing.JComboBox<String> txtDAS;
    private javax.swing.JTextField txtEpochCycle;
    private javax.swing.JTextArea txtPPETerminal;
    private javax.swing.JComboBox<String> txtSpeed;
    private javax.swing.JTextArea txtTerminal;
    private javax.swing.JTextField txtTotalSlots;
    // End of variables declaration//GEN-END:variables
}
