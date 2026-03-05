package com.enactor.bus.reservation.system.model;

public class Seat {
    private final String label;

    // Outbound segments: 0:A-B, 1:B-C, 2:C-D
    private final boolean[] outboundSegments = new boolean[3];

    // Return segments: 0:D-C, 1:C-B, 2:B-A
    private final boolean[] returnSegments = new boolean[3];

    public Seat(String label) {
        this.label = label;
    }

    public boolean isAvailable(int start, int end, boolean isReturn) {
        boolean[] targetSegments = isReturn ? returnSegments : outboundSegments;
        for (int i = start; i < end; i++) {
            if (targetSegments[i]) {
                return false;
            }
        }
        return true;
    }



    public String getLabel() { return label; }
}