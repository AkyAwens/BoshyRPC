package dev.akyawen.boshyrpc.util;

public enum Difficulties {
	EZ_MODE(0, "Ez Mode"),
	TOTTALY_AVERAGE(1, "Tottaly-Average Mode"),
	HARDON(2, "Hard-on Mode"),
	RAGE_MODE(3, "You-re gonna get raped mode");
	
	private final int difficultyNumber;
    private final String name;
    
    Difficulties(int difficultyNumber, String name) {
        this.difficultyNumber = difficultyNumber;
        this.name = name;
    }
    
    public int getDifficultyNumber() {
        return difficultyNumber;
    }

    public String getName() {
        return name;
    }

    public static Difficulties fromDiffNumber(int frameNumber) {
        for (Difficulties diff : values()) {
            if (diff.getDifficultyNumber() == frameNumber) {
                return diff;
            }
        }
        return null;
    }
}
