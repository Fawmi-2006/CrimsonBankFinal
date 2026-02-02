package com.crimsonbank.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Utility class for creating and managing user avatars.
 * Supports displaying profile images or generating circular avatars
 * with the first letter of a user's name.
 */
public class AvatarUtil {

    private static final int DEFAULT_AVATAR_SIZE = 40;
    private static final int[] AVATAR_COLORS = {
            0x3B82F6, // Blue
            0x10B981, // Emerald
            0xF59E0B, // Amber
            0xEF4444, // Red
            0x8B5CF6, // Violet
            0xEC4899, // Pink
            0x14B8A6, // Teal
            0xF97316  // Orange
    };

    /**
     * Creates a circular avatar with the first letter of a name.
     * The background color is deterministically chosen based on the name.
     *
     * @param name the user's full name or display name
     * @param size the size of the avatar in pixels
     * @return a StackPane containing the circular avatar
     */
    public static StackPane createAvatarWithName(String name, int size) {
        if (name == null || name.isEmpty()) {
            name = "U";
        }

        String firstLetter = name.substring(0, 1).toUpperCase();
        int colorIndex = name.hashCode() % AVATAR_COLORS.length;
        if (colorIndex < 0) colorIndex = -colorIndex;
        int colorValue = AVATAR_COLORS[colorIndex];

        Color bgColor = Color.web(String.format("#%06X", colorValue));

        Circle circle = new Circle(size / 2.0);
        circle.setFill(bgColor);

        Label letter = new Label(firstLetter);
        letter.setStyle(
                "-fx-font-size: " + (size * 0.4) + "px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white;"
        );

        StackPane avatar = new StackPane(circle, letter);
        avatar.setStyle(
                "-fx-pref-width: " + size + "px; " +
                "-fx-pref-height: " + size + "px; " +
                "-fx-min-width: " + size + "px; " +
                "-fx-min-height: " + size + "px;"
        );
        avatar.setAlignment(Pos.CENTER);

        return avatar;
    }

    /**
     * Creates a circular avatar with the default size.
     *
     * @param name the user's full name or display name
     * @return a StackPane containing the circular avatar
     */
    public static StackPane createAvatarWithName(String name) {
        return createAvatarWithName(name, DEFAULT_AVATAR_SIZE);
    }

    /**
     * Creates an avatar that displays a profile image if available,
     * otherwise falls back to a letter-based avatar.
     *
     * @param imagePath the path to the profile image file
     * @param name the user's full name (for fallback avatar)
     * @param size the size of the avatar in pixels
     * @return a StackPane containing either the image or the letter avatar
     */
    public static StackPane createAvatarWithImage(String imagePath, String name, int size) {
        String resolvedPath = normalizeImagePath(imagePath);
        if (resolvedPath != null) {
            try {
                Image image = new Image(resolvedPath);
                if (!image.isError()) {
                    ImageView imageView = new ImageView(image);
                    double width = image.getWidth();
                    double height = image.getHeight();
                    if (width > 0 && height > 0) {
                        double side = Math.min(width, height);
                        double x = (width - side) / 2.0;
                        double y = (height - side) / 2.0;
                        imageView.setViewport(new javafx.geometry.Rectangle2D(x, y, side, side));
                    }
                    imageView.setFitWidth(size);
                    imageView.setFitHeight(size);
                    imageView.setPreserveRatio(false);
                    imageView.setSmooth(true);

                    Circle clip = new Circle(size / 2.0, size / 2.0, size / 2.0);
                    imageView.setClip(clip);

                    StackPane imageAvatar = new StackPane(imageView);
                    imageAvatar.setStyle(
                            "-fx-pref-width: " + size + "px; " +
                            "-fx-pref-height: " + size + "px; " +
                            "-fx-min-width: " + size + "px; " +
                            "-fx-min-height: " + size + "px;"
                    );
                    imageAvatar.setAlignment(Pos.CENTER);
                    return imageAvatar;
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
        // Fallback to letter avatar if image is not available
        return createAvatarWithName(name, size);
    }

    private static String normalizeImagePath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        String trimmed = imagePath.trim();
        if (trimmed.startsWith("file:") || trimmed.startsWith("http:") || trimmed.startsWith("https:")) {
            return trimmed;
        }
        java.nio.file.Path path = java.nio.file.Paths.get(trimmed);
        if (java.nio.file.Files.exists(path)) {
            return path.toUri().toString();
        }
        return null;
    }

    /**
     * Creates an avatar with the default size that displays a profile image if available.
     *
     * @param imagePath the path to the profile image file
     * @param name the user's full name (for fallback avatar)
     * @return a StackPane containing either the image or the letter avatar
     */
    public static StackPane createAvatarWithImage(String imagePath, String name) {
        return createAvatarWithImage(imagePath, name, DEFAULT_AVATAR_SIZE);
    }

}
