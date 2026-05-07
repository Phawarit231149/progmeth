package game.util;

import javafx.scene.media.AudioClip; // ใช้ AudioClip สำหรับ SFX
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

    public static double getOverallVolume() { return overallVolume; }
    public static double getSfxVolume() { return sfxVolume; }
    public static double getMusicVolume() { return musicVolume; }

    public static void setOverallVolume(double volume) {
        overallVolume = volume;
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = volume;
    }

    public static void setMusicVolume(double volume) {
        musicVolume = volume;
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(musicVolume / 100.0);
        }
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
            bgmPlayer.setVolume(musicVolume / 100.0);
            bgmPlayer.play();
            currentBgmFile = fileName;

        } catch (Exception e) {
            System.out.println("BGM Error: " + e.getMessage());
        }
    }

    /** เล่นเสียงครั้งเดียวไม่วนซ้ำ — เหมาะกับเสียงชนะ/แพ้ (win.mp3, lose.mp3) */
    public static void playOneShot(String fileName) {
        try {
            // หยุดเสียงเดิมก่อน (ถ้ามี)
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

    public static void playSFX(AudioClip sfx) {
        if (sfx != null) {
            try {
                // ตั้งค่าความดังตามสไลเดอร์ SFX (0.0 - 1.0)
                sfx.setVolume(sfxVolume / 100.0);
                sfx.play();
            } catch (Exception e) {
                System.out.println("SFX Playback Error: " + e.getMessage());
            }
        }
    }
}