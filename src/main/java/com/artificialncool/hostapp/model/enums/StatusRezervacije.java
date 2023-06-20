package com.artificialncool.hostapp.model.enums;

public enum StatusRezervacije {
    U_OBRADI("U_OBRADI"),
    PRIHVACENO("PRIHVACENO"),
    ODBIJENO("ODBIJENO"),
    OTKAZANO("OTKAZANO");

    private final String status;

    StatusRezervacije(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
