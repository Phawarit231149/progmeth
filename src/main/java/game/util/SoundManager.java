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

        } catch (Exception e) {
            System.out.println("BGM Error: " + e.getMessage());
        }
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