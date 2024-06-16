package com.mycompany.tetara;
import java.util.*;
public class SandBox {
    // Example of validators with their stakes
    private static Map<String, Integer> validators = new HashMap<>();

    static {
        validators.put("Validator1", 1000);
        validators.put("Validator2", 1500);
        validators.put("Validator3", 2000);
        validators.put("Validator4", 2500);
    }

    public static void main(String[] args) {
        int epochNumber = 1;
        int numSlots = 2;  // Realistic number of slots per epoch

        System.out.println("Initializing Epoch " + epochNumber);

        initializeEpoch(epochNumber, numSlots, validators);

        initializeEpoch(epochNumber, numSlots, validators);

    }

    private static void initializeEpoch(int epochNumber, int numSlots, Map<String, Integer> validators) {
        System.out.println("Epoch " + epochNumber + " started");
        System.out.println("Generating leader schedule...");

        for (int slot = 0; slot < numSlots; slot++) {
            String leader = selectLeaderForSlot(validators, slot);
            if (slot < 10) { // Print only the first 10 slots for demonstration purposes
                System.out.println("Leader for slot " + slot + ": " + leader);
            }
            System.out.print(".");

        }
        System.out.println("Done Scheduling");
    }

    private static String selectLeaderForSlot(Map<String, Integer> validators, int slot) {
        // Calculate total stake
        int totalStake = 0;
        for (int stake : validators.values()) {
            totalStake += stake;
        }


        // Generate a list of validators weighted by their stake
        List<String> weightedValidators = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : validators.entrySet()) {
            String validator = entry.getKey();
            int stake = entry.getValue();
            for (int i = 0; i < stake; i++) {
                weightedValidators.add(validator);
            }
        }

        // Use slot number as a seed for reproducibility
        Random random = new Random(slot);

        // Randomly select a validator from the weighted list
        int randomIndex = random.nextInt(weightedValidators.size());
        return weightedValidators.get(randomIndex);
    }
}
