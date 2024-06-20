package com.mycompany.tetara;

import java.util.*;

public class Kitchen {
    public static void main(String[] args) {
        long total_shardBlock = 19;
        int avail_ppeNode = 5;

        Map<String, Long> ppe = new LinkedHashMap<>();

        // Calculate initial distribution and distribute evenly
        long initialDistribution = total_shardBlock / avail_ppeNode;
        long remainder = total_shardBlock % avail_ppeNode;

        System.out.println(remainder);

        // Initialize the map with initial values
        for (int i = 1; i <= avail_ppeNode; i++) {
            long value = (i <= remainder) ? initialDistribution + 1 : initialDistribution;
            ppe.put("ppe_Node" + i, value);
        }

        // Print the map entries
        for (Map.Entry<String, Long> entry : ppe.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }



    }
}
