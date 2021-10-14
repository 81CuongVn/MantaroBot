package net.kodehawa.mantarobot.utils.patreon;

public enum PatreonReward {
    SUPPORTER(0), FRIEND(1), PATREON_BOT(2),
    MILESTONER(4), SERVER_SUPPORTER(7),
    AWOOSOME(25), FUNDER(35), BUT_WHY(50);

    // Extra
    private final int keys;
    PatreonReward(int keys) {
        this.keys = keys;
    }

    public int getKeys() {
        return keys;
    }

    public boolean isPatreonBot() {
        return getKeys() >= 2;
    }

    public static PatreonReward fromName(String name) {
        for (PatreonReward pr : values()) {
            if (pr.name().equalsIgnoreCase(name)) {
                return pr;
            }
        }

        return null;
    }
}
