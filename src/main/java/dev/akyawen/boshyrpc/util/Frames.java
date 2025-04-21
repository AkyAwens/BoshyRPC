package dev.akyawen.boshyrpc.util;

public enum Frames {
	PRE_TITLE_SCREEN(0, "Pre Title Screen"),
	PRE_TITLE_SCREEN1(1, "Pre Title Screen"),
	TITLE_SCREEN(2, "Title Screen"),
	WORLD_1(3, "World 1"),
	HELLO_KITTY(4, "Hello Kitty"),
	WORLD_2(5, "World 2"),
	DEE_DEE_DEE(6, "Dee Dee Dee"),
	RYU(7, "Ryu"),
	WORLD_3(8, "World 3"),
	GHASTLY(9, "Ghastly"),
	MARIO(10, "Mario"),
	WORLD_4(11, "World 4"),
	BIOLLANTE(12, "Biollante"),
	WORLD_5(13, "World 5"),
	SONIC(14, "Sonic"),
	WORLD_6(15, "World 6"),
	TROPHIES(16, "Trophies"),
	SKELETON_KING(17, "Skeleton King"),
	ONLINE(18, "Online"),
	I_WANNA_BE_THE_LOBBY(19, "I Wanna Be The Lobby"),
	OPTIONS(20, "Options"),
	CHARECTER_SELECTOR(21, "Charecter Selector"),
	WORLD_7_1ST_HALF(22, "World 7(1st half)"),
	WORLD_7_2ND_HALF(23, "World 7(2nd half)"),
	MEGAMAN(24, "Megaman"),
	WORLD_8(25, "World 8"),
	WORLD_9(27, "World 9"),
	BOMBERMAN(28, "Bomberman"),
	TELEPORTER_ROOM(29, "Teleporter Room"),
	OWL(30, "Owl"),
	WORLD_10(31, "World 10"),
	MISSINGNO(32, "Missingno"),
	MARIO_SECRET(33, "Mario Secret"),
	WHITE_FRAME_1(34, "White Frame 1"),
	WORLD_11(35, "World 11"),
	TUTORIAL(36, "Tutorial"),
	JUSTIN_TV_WORLD(37, "Justin.tv World"),
	WORLD_12(38, "World 12"),
	GRADIUS(39, "Gradius"),
	WHITE_FRAME_2(40, "White Frame 2"),
	SCROLLING_BLOCKS(41, "Scrolling Blocks"),
	FAKE_BSOD(42, "Fake BSOD"),
	WINNER_IS_YOU(43, "Winner Is You!"),
	RANDOM_BOSS_1(44, "Random Boss 1"),
	RANDOM_BOSS_2(45, "Random Boss 2"),
	SOLGRYN(46, "Solgryn"),
	SOLGRYN_EXPLODING(47, "Solgryn Exploding"),
	CREDITS(48, "Credits"),
	PRIZE_ROOM(49, "Prize Room"),
	POKEMON_WORLD(50, "Pokemon World"),
	WORLD_11_TOWER(51, "World 11 Tower"),
	WORLD_6_COPY(52, "World 6 Copy"),
	WORLD_OF_WARCRAFT_WORLD(53, "World Of Warcraft World"),
	CHEETAHMEN(54, "Cheetahmen"),
	FROZEN_FRAME_WITH_WORLD_1_MUSIC(55, "Frozen frame with world 1 music"),
	LEVEL_EDITOR(56, "Level Editor"),
	WHITE_FRAME_3(57, "White Frame 3"),
	CHARECTER_CREATOR(58, "Charecter Creator"),
	GANON(59, "Ganon"),
	OLD_SONIC(60, "Old Sonic"),
	WIERD_DANCING_FRAME(61, "Wierd Dancing Frame");
	
	private final int frameNumber;
    private final String name;
    
    Frames(int frameNumber, String name) {
        this.frameNumber = frameNumber;
        this.name = name;
    }
    
    public int getFrameNumber() {
        return frameNumber;
    }

    public String getName() {
        return name;
    }

    public static Frames fromFrameNumber(int frameNumber) {
        for (Frames frame : values()) {
            if (frame.getFrameNumber() == frameNumber) {
                return frame;
            }
        }
        return null;
    }
}
