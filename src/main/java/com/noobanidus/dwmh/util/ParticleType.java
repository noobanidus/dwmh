package com.noobanidus.dwmh.util;

public enum ParticleType {
    NULL(-1),
    HEALING(0),
    TAMING(1),
    BREEDING(2),
    AGING(3);

    private int internalId;
    private int particleId;

    ParticleType(int id) {
        this.internalId = id;
    }

    public int getParticleId() {
        return particleId;
    }

    public void setParticleId(int particleId) {
        this.particleId = particleId;
    }

    public int getInternalId() {
        return internalId;
    }

    public static ParticleType byId (int id) {
        for (ParticleType type : values()) {
            if (type.getInternalId() == id) {
                return type;
            }
        }

        return NULL;
    }
}
