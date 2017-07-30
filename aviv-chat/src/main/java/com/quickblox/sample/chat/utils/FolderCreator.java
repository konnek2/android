package com.quickblox.sample.chat.utils;

import java.io.File;

/**
 * Created by Lenovo on 24-07-2017.
 */

public class FolderCreator {

    private static File KONNEK2_PROFILE = null;
    private static File KONNEK2_IMAGE = null;
    private static File KONNEK2_AUDIO = null;
    private static File KONNEK2_VIDEO = null;

    public static void createDirectory() {
        KONNEK2_PROFILE = new File(Constant.EXT_STORAGE_DIRECTORY
                + Constant.FOLDER_SEPARATOR + Constant.APP_NAME
                + Constant.FOLDER_SEPARATOR + Constant.PROFILE_FOLDER
        );
        KONNEK2_IMAGE = new File(Constant.EXT_STORAGE_DIRECTORY
                + Constant.FOLDER_SEPARATOR + Constant.APP_NAME
                + Constant.FOLDER_SEPARATOR + Constant.IMAGE_FOLDER
        );
        KONNEK2_AUDIO = new File(Constant.EXT_STORAGE_DIRECTORY
                + Constant.FOLDER_SEPARATOR + Constant.APP_NAME
                + Constant.FOLDER_SEPARATOR + Constant.AUDIO_FOLDER
        );
        KONNEK2_VIDEO = new File(Constant.EXT_STORAGE_DIRECTORY
                + Constant.FOLDER_SEPARATOR + Constant.APP_NAME
                + Constant.FOLDER_SEPARATOR + Constant.VIDEO_FOLDER
        );
        if (!KONNEK2_PROFILE.exists()) {
            KONNEK2_PROFILE.mkdirs();
        }

        if (!KONNEK2_IMAGE.exists()) {
            KONNEK2_IMAGE.mkdirs();
        }
        if (!KONNEK2_AUDIO.exists()) {
            KONNEK2_AUDIO.mkdirs();
        }
        if (!KONNEK2_VIDEO.exists()) {
            KONNEK2_VIDEO.mkdirs();
        }
    }

    public static boolean isCheckFileExit(String fileName) {
        File file = new File(Constant.EXT_STORAGE_DIRECTORY
                + Constant.FOLDER_SEPARATOR + Constant.APP_NAME
                + Constant.FOLDER_SEPARATOR + Constant.PROFILE_FOLDER
                + Constant.FOLDER_SEPARATOR + fileName + ".png");
        return file.exists();
    }

    public static String getProfileImagePath() {
        return Constant.EXT_STORAGE_DIRECTORY
                + Constant.FOLDER_SEPARATOR + Constant.APP_NAME
                + Constant.FOLDER_SEPARATOR + Constant.PROFILE_FOLDER
                + Constant.FOLDER_SEPARATOR;
    }

    public static File getImageFileFromSdCard(String fileName) {
        return new File(Constant.EXT_STORAGE_DIRECTORY
                + Constant.FOLDER_SEPARATOR + Constant.APP_NAME
                + Constant.FOLDER_SEPARATOR + Constant.PROFILE_FOLDER
                + Constant.FOLDER_SEPARATOR + fileName + ".png");
    }
}
