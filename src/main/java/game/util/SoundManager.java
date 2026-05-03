package game.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {

    private static double overallVolume = 50.0;
    private static double sfxVolume = 50.0;
    private static double musicVolume = 50.0;

    // ⭐️ 1. ตัวแปรเก็บเพลงพื้นหลังที่กำลังเล่นอยู่
    private static MediaPlayer bgmPlayer;

    public static double getOverallVolume() { return overallVolume; }
    public static double getSfxVolume() { return sfxVolume; }
    public static double getMusicVolume() { return musicVolume; }

    public static void setOverallVolume(double volume) {
        overallVolume = volume;
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = volume;
    }

    // ⭐️ 2. เมื่อสไลเดอร์ Music ขยับ ให้มันมาอัปเดตเสียงเพลงที่เล่นอยู่ทันที!
    public static void setMusicVolume(double volume) {
        musicVolume = volume;

        // ถ้ามีเพลงเล่นอยู่ ให้ปรับเสียงเดี๋ยวนั้นเลย
        if (bgmPlayer != null) {
            // ค่า Slider คือ 0-100 แต่ MediaPlayer รับค่า 0.0 - 1.0 (จึงต้องหาร 100)
            bgmPlayer.setVolume(musicVolume / 100.0);
        }
    }

    // ⭐️ 3. ระบบเล่นเพลงพื้นหลัง
    public static void playBGM(String fileName) {
        try {
            // ถ้ามีเพลงอื่นเล่นอยู่ ให้หยุดก่อน
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

            // ให้เล่นวนลูปไปเรื่อยๆ ไม่มีวันจบ
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            // ตั้งค่าความดังเริ่มต้นตามที่สไลเดอร์เซฟไว้
            bgmPlayer.setVolume(musicVolume / 100.0);

            // เริ่มเล่นเพลง
            bgmPlayer.play();

        } catch (Exception e) {
            System.out.println("BGM Error: " + e.getMessage());
        }
    }
}