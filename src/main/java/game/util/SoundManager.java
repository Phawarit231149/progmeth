package game.util;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {

    private static double overallVolume = 50.0;
    private static double sfxVolume = 50.0;
    private static double musicVolume = 50.0;

    private static MediaPlayer bgmPlayer;
    private static String currentBgmFile = null;   // ติดตามไฟล์ BGM ปัจจุบัน
    private static AudioClip currentStinger = null; // ติดตามเสียง win/lose

    // ⭐️ Walk loop ใช้ MediaPlayer (ลู้ปแน่นอนกว่า AudioClip)
    private static boolean walkActive = false;
    private static MediaPlayer walkLoop = null;

    // ⭐️ UI SFX clips (โหลดครั้งเดียว ใช้ตลอด)
    private static AudioClip clickClip = null;
    private static AudioClip hoverClip = null;

    public static double getOverallVolume() { return overallVolume; }
    public static double getSfxVolume()     { return sfxVolume; }
    public static double getMusicVolume()   { return musicVolume; }

    public static void setOverallVolume(double volume) {
        overallVolume = volume;
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = volume;
        // อัปเดต volume ของ walk loop ทันทีถ้ากำลังเล่นอยู่ (boost x2, cap ที่ 1.0)
        if (walkLoop != null && walkActive) walkLoop.setVolume(walkVolume());
    }

    /** คำนวณ volume สำหรับ walk loop — boost 2x จาก sfxVolume, cap ที่ 1.0 */
    private static double walkVolume() {
        return Math.min(1.0, (sfxVolume / 100.0) * 2.0);
    }

    public static void setMusicVolume(double volume) {
        musicVolume = volume;
        if (bgmPlayer != null) {
            double scale = bgmVolumeScale(currentBgmFile);
            bgmPlayer.setVolume((musicVolume / 100.0) * scale);
        }
    }

    /** ปรับ scale volume ต่อไฟล์ — ไฟล์บางอัน mastering ดังเกิน ต้อง scale ลง */
    private static double bgmVolumeScale(String fileName) {
        if (fileName == null) return 1.0;
        return switch (fileName) {
            case "home.mp3" -> 0.4;       // home BGM ดังมาก ลดเหลือ 40%
            default          -> 1.0;
        };
    }

    public static void playBGM(String fileName) {
        try {
            // หยุดเสียง win/lose ถ้ามี (เปลี่ยนหน้าจอแล้วไม่ต้องเล่นต่อ)
            stopOneShot();

            // ถ้าไฟล์เดิม + กำลังเล่นอยู่ → ไม่ต้องเริ่มใหม่
            if (fileName.equals(currentBgmFile) && bgmPlayer != null
                    && bgmPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                return;
            }

            if (bgmPlayer != null) {
                bgmPlayer.stop();
            }

            URL resource = SoundManager.class.getResource("/sounds/" + fileName);
            if (resource == null) {
                System.out.println("หาไฟล์ BGM ไม่เจอ: " + fileName);
                return;
            }

            Media media = new Media(resource.toString());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            // ⭐️ ลด volume ของบางไฟล์ (home.mp3 ดังเกิน)
            double scale = bgmVolumeScale(fileName);
            bgmPlayer.setVolume((musicVolume / 100.0) * scale);
            bgmPlayer.play();
            currentBgmFile = fileName;

        } catch (Exception e) {
            System.out.println("BGM Error: " + e.getMessage());
        }
    }

    /** เล่นเสียงครั้งเดียวไม่วนซ้ำ — เหมาะกับเสียงชนะ/แพ้ (win.mp3, lose.mp3) */
    public static void playOneShot(String fileName) {
        try {
            stopOneShot();

            URL resource = SoundManager.class.getResource("/sounds/" + fileName);
            if (resource == null) {
                System.out.println("หาไฟล์ SFX ไม่เจอ: " + fileName);
                return;
            }
            currentStinger = new AudioClip(resource.toString());
            currentStinger.setVolume(sfxVolume / 100.0);
            currentStinger.play();
        } catch (Exception e) {
            System.out.println("OneShot Error: " + e.getMessage());
        }
    }

    /** หยุดเสียง stinger (win/lose) ที่กำลังเล่นอยู่ */
    public static void stopOneShot() {
        if (currentStinger != null) {
            try { currentStinger.stop(); } catch (Exception ignored) {}
            currentStinger = null;
        }
    }

    /** หยุด BGM ที่กำลังเล่นอยู่ */
    public static void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
        currentBgmFile = null;
    }

    /**
     * เล่น AudioClip SFX โดยมี scale คูณกับ sfxVolume
     * scale 1.0 = ดังเต็ม sfxVolume, scale 0.3 = ดังแค่ 30% ของ sfxVolume
     */
    public static void playSFX(AudioClip sfx, double scale) {
        if (sfx != null) {
            try {
                sfx.setVolume((sfxVolume / 100.0) * scale);
                sfx.play();
            } catch (Exception e) {
                System.out.println("SFX Playback Error: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ⭐️ WALK LOOP — ใช้ MediaPlayer ลู้ปแน่นอน
    // ═══════════════════════════════════════════════════════════════════════

    /** track ว่าตอนนี้ "อยู่ในสถานะกำลังเดิน" หรือไม่ */
    /**
     * Preload walk MediaPlayer + เริ่มเล่นทันทีที่ volume 0
     * วิธีนี้กันปัญหา async load — เพราะ MediaPlayer พร้อมเล่นตลอดเวลา
     * เราแค่ toggle volume เปิด/ปิดเสียง
     */
    public static void preloadWalk() {
        if (walkLoop != null) return;
        try {
            URL resource = SoundManager.class.getResource("/sounds/walk.mp3");
            if (resource == null) {
                System.out.println("[Walk] หาไฟล์ walk.mp3 ไม่เจอ");
                return;
            }
            System.out.println("[Walk] Loading: " + resource);
            Media media = new Media(resource.toString());
            walkLoop = new MediaPlayer(media);
            walkLoop.setCycleCount(MediaPlayer.INDEFINITE);
            walkLoop.setVolume(0);   // เริ่มที่เงียบ — เล่นในพื้นหลังตลอด
            walkLoop.setOnReady(() -> {
                System.out.println("[Walk] MediaPlayer READY");
                walkLoop.play();      // เริ่มเล่น (เงียบ)
                // ถ้ามีคำขอเดินก่อนหน้านี้แล้ว — เปิดเสียงทันที
                if (walkActive) walkLoop.setVolume(walkVolume());
            });
            walkLoop.setOnError(() ->
                System.out.println("[Walk] MediaPlayer error: " + walkLoop.getError()));
        } catch (Exception e) {
            System.out.println("[Walk] preload error: " + e.getMessage());
        }
    }

    /** เปิดเสียงเดิน (เพิ่ม volume) */
    public static void startWalkLoop() {
        walkActive = true;
        if (walkLoop == null) preloadWalk();
        if (walkLoop == null) return;
        walkLoop.setVolume(walkVolume());
        System.out.println("[Walk] startWalkLoop, status=" + walkLoop.getStatus()
                + ", vol=" + walkLoop.getVolume());
    }

    /** ปิดเสียงเดิน (volume 0) — MediaPlayer ยังเล่นในพื้นหลัง */
    public static void stopWalkLoop() {
        walkActive = false;
        if (walkLoop != null) walkLoop.setVolume(0);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ⭐️ UI BUTTON SFX (click + hover)
    // ═══════════════════════════════════════════════════════════════════════

    private static AudioClip loadClipLazy(AudioClip cached, String fileName) {
        if (cached != null) return cached;
        try {
            URL resource = SoundManager.class.getResource("/sounds/" + fileName);
            if (resource == null) {
                System.out.println("หาไฟล์ SFX ไม่เจอ: " + fileName);
                return null;
            }
            return new AudioClip(resource.toString());
        } catch (Exception e) {
            System.out.println("Clip load error: " + fileName + " — " + e.getMessage());
            return null;
        }
    }

    /** เล่นเสียง hover (slide.mp3) */
    public static void playHover() {
        hoverClip = loadClipLazy(hoverClip, "slide.mp3");
        if (hoverClip != null) {
            hoverClip.setVolume(sfxVolume / 100.0);
            hoverClip.play();
        }
    }

    /** เล่นเสียงคลิก (click.mp3) */
    public static void playClick() {
        clickClip = loadClipLazy(clickClip, "click.mp3");
        if (clickClip != null) {
            clickClip.setVolume(sfxVolume / 100.0);
            clickClip.play();
        }
    }

    /**
     * ผูกเสียง hover + click ให้ปุ่ม / Node ใดๆ
     * - hover (mouse enter) → slide.mp3
     * - click → click.mp3
     * ใช้ event filter เพื่อไม่ทับ onMouseEntered/onAction เดิม
     */
    public static void attachUiSfx(Node node) {
        if (node == null) return;
        node.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_ENTERED, ev -> {
            if (!node.isDisabled()) playHover();
        });
        node.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, ev -> {
            if (!node.isDisabled()) playClick();
        });
    }
}
