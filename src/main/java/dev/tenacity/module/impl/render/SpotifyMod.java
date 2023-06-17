package dev.tenacity.module.impl.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.IOUtils;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.spotify.SpotifyAPI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SpotifyMod extends Module {

    private final StringSetting clientID = new StringSetting("Client ID");
    private final ModeSetting backgroundColor = new ModeSetting("Background", "Average", "Average", "Spotify Grey", "Sync");
    private final ModeSetting progressBarColor = new ModeSetting("Progress Bar", "Green", "Average", "Green", "White");

    private final Dragging drag = Tenacity.INSTANCE.createDrag(this, "spotify", 5, 150);
    public final float height = 50;
    public final float albumCoverSize = height;
    private final float playerWidth = 135;
    private final float width = albumCoverSize + playerWidth;

    private final Animation scrollTrack = new DecelerateAnimation(10000, 1, Direction.BACKWARDS);
    private final Animation scrollArtist = new DecelerateAnimation(10000, 1, Direction.BACKWARDS);
    public Animation playAnimation = new DecelerateAnimation(250, 1);

    public String[] buttons = {FontUtil.SKIP_LEFT, FontUtil.SKIP_RIGHT, FontUtil.SHUFFLE};
    public HashMap<String, Animation> buttonAnimations;


    public SpotifyAPI api;
    private CurrentlyPlayingContext currentPlayingContext;
    private Track currentTrack;
    public boolean playingMusic;
    public boolean hoveringPause;
    private boolean downloadedCover;
    private ResourceLocation currentAlbumCover;
    private Color imageColor = Color.WHITE;

    public SpotifyMod() {
        super("Spotify", Category.RENDER, "UI for spotify");
        addSettings(clientID, backgroundColor, progressBarColor);
    }

    private final Color greyColor = new Color(30, 30, 30);
    private final Color progressBackground = new Color(45, 45, 45);
    private final Color shuffleColor = new Color(50, 255, 100);
    private final Color hoveredColor = new Color(195, 195, 195);
    private final Color circleColor = new Color(50, 50, 50);

    @Override
    public void onShaderEvent(ShaderEvent event) {
        if (api.currentTrack == null || api.currentPlayingContext == null) return;
        float x = drag.getX(), y = drag.getY();

        if (event.isBloom()) {
            if (event.getBloomOptions().getSetting("Spotify").isEnabled()) {

                Color color2 = ColorUtil.darker(imageColor, .65f);

                switch (backgroundColor.getMode()) {
                    case "Average":
                        float[] hsb = Color.RGBtoHSB(imageColor.getRed(), imageColor.getGreen(), imageColor.getBlue(), null);
                        if (hsb[2] < .5f) {
                            color2 = ColorUtil.brighter(imageColor, .65f);
                        }

                        RoundedUtil.drawGradientVertical(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, color2, imageColor);
                        break;
                    case "Spotify Grey":
                        RoundedUtil.drawRound(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, greyColor);
                        break;
                    case "Sync":
                        Pair<Color, Color> colors = HUDMod.getClientColors();
                        RoundedUtil.drawGradientCornerLR(x + (albumCoverSize - 40), y, playerWidth + 12, height - 5, 6, colors.getFirst(), colors.getSecond());
                        break;
                }



                if (currentAlbumCover != null && downloadedCover) {
                    RenderUtil.resetColor();
                    mc.getTextureManager().bindTexture(currentAlbumCover);
                    //  GL11.glEnable(GL11.GL_BLEND);
                  //  RoundedUtil.drawRoundTextured(x, y, albumCoverSize, albumCoverSize, 7.5f, 1);
                }

            } else {
                Pair<Color, Color> colors = HUDMod.getClientColors();
               // RoundedUtil.drawGradientCornerLR(x, y, playerWidth + (albumCoverSize), height, 6, colors.getFirst(),colors.getSecond());
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (api.currentTrack == null || api.currentPlayingContext == null) return;
        //Check if the song has changed
        if (currentTrack != api.currentTrack || currentPlayingContext != api.currentPlayingContext) {
            this.currentTrack = api.currentTrack;
            this.currentPlayingContext = api.currentPlayingContext;
        }
        playingMusic = currentPlayingContext.getIs_playing();


        float x = drag.getX(), y = drag.getY();
        drag.setWidth(width);
        drag.setHeight(height);


        Color color2 = ColorUtil.darker(imageColor, .65f);

        switch (backgroundColor.getMode()) {
            case "Average":
                float[] hsb = Color.RGBtoHSB(imageColor.getRed(), imageColor.getGreen(), imageColor.getBlue(), null);
                if (hsb[2] < .5f) {
                    color2 = ColorUtil.brighter(imageColor, .65f);
                }

                RoundedUtil.drawGradientVertical(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, color2, imageColor);
                break;
            case "Spotify Grey":
                RoundedUtil.drawRound(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, greyColor);
                break;
            case "Sync":
                Pair<Color, Color> colors = HUDMod.getClientColors();

             //   RoundedUtil.drawRoundOutline(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6,2, colors.getFirst(), colors.getSecond());
         //       RoundedUtil.drawGradientCornerLR(x + (albumCoverSize - 15), y, playerWidth + 15, height, 6, colors.getFirst(), colors.getSecond());
                break;
        }

        final int diff = currentTrack.getDurationMs() - currentPlayingContext.getProgress_ms();
        final long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
        final long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;

        final String trackRemaining = String.format("-%s:%s", diffMinutes < 10 ? "0" + diffMinutes : diffMinutes, diffSeconds < 10 ? "0" + diffSeconds : diffSeconds);


        //Scroll and scissor the track name and artist if needed
        RenderUtil.scissor(x + albumCoverSize, y, playerWidth, height, () -> {
            final StringBuilder artistsDisplay = new StringBuilder();
            for (int artistIndex = 0; artistIndex < currentTrack.getArtists().length; artistIndex++) {
                final ArtistSimplified artist = currentTrack.getArtists()[artistIndex];
                artistsDisplay.append(artist.getName()).append(artistIndex + 1 == currentTrack.getArtists().length ? '.' : ", ");
            }
            if (scrollTrack.finished(Direction.BACKWARDS)) {
                scrollTrack.reset();
            }
            if (scrollArtist.finished(Direction.BACKWARDS)) {
                scrollArtist.reset();
            }
            boolean needsToScrollTrack = tenacityBoldFont26.getStringWidth(currentTrack.getName()) > playerWidth;
            boolean needsToScrollArtist = tenacityFont22.getStringWidth(artistsDisplay.toString()) > playerWidth;

            float trackX = (float) (((x + albumCoverSize) - tenacityBoldFont22.getStringWidth(currentTrack.getName())) +
                    ((tenacityBoldFont22.getStringWidth(currentTrack.getName()) + playerWidth) * scrollTrack.getLinearOutput()));

            tenacityBoldFont20.drawString(currentTrack.getName(), needsToScrollTrack ? trackX : x + albumCoverSize + 5 , y + 5, -1);

            float artistX = (float) (((x + albumCoverSize) - tenacityFont18.getStringWidth(artistsDisplay.toString())) +
                    ((tenacityFont18.getStringWidth(artistsDisplay.toString()) + playerWidth) * scrollArtist.getLinearOutput()));

            tenacityFont18.drawString(artistsDisplay.toString(), needsToScrollArtist ? artistX : x + albumCoverSize + 5, y + 17, -1);
        });

        //Draw time left on song
        tenacityFont20.drawString(trackRemaining, x + width - (tenacityFont16.getStringWidth(trackRemaining) + 50),
                y + height - (tenacityFont16.getHeight() + 27), -1);

        float progressBarWidth = (playerWidth - 35);
        float progressBarHeight = 3;
        float progress = progressBarWidth * (currentPlayingContext.getProgress_ms() / (float) currentTrack.getDurationMs());
        Color progressColor;

        switch (progressBarColor.getMode()) {
            case "Average":
                progressColor = backgroundColor.is("Average") ? color2 : imageColor;
                break;
            case "Green":
                progressColor = new Color(50, 255, 100);
                break;
            default:
                progressColor = Color.WHITE;
                break;
        }
        Pair<Color, Color> colors = HUDMod.getClientColors();

        //RoundedUtil.drawGradientCornerLR(x + albumCoverSize + 5, y + height - (progressBarHeight + 15f), progressBarWidth, progressBarHeight, 1.5f, colors.getFirst(),colors.getSecond());
        RoundedUtil.drawGradientCornerLR(x + albumCoverSize + 6.5f, y + height - (progressBarHeight + 15.0f), progress, progressBarHeight, 1.5f, colors.getFirst(),colors.getSecond());


        float spacing = 0;

        RenderUtil.resetColor();



        if (currentAlbumCover != null && downloadedCover) {
            mc.getTextureManager().bindTexture(currentAlbumCover);
            GlStateManager.color(1, 1, 1);
            GL11.glEnable(GL11.GL_BLEND);
            // spotify image
            RoundedUtil.drawRoundTextured(x + 11.5f, y + 3, albumCoverSize- 10, albumCoverSize - 10, 5, 1);
        }
        if ((currentAlbumCover == null || !currentAlbumCover.getResourcePath().contains(currentTrack.getAlbum().getId()))) {
            downloadedCover = false;
            final ThreadDownloadImageData albumCover = new ThreadDownloadImageData(null, currentTrack.getAlbum().getImages()[1].getUrl(), null, new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage image) {
                    imageColor = ColorUtil.averageColor(image, image.getWidth(), image.getHeight(), 1);
                    downloadedCover = true;
                    return image;
                }

                @Override
                public void skinAvailable() {
                }
            });
            mc.getTextureManager().loadTexture(currentAlbumCover = new ResourceLocation("spotifyAlbums/" + currentTrack.getAlbum().getId()), albumCover);

        }

        playAnimation.setDirection(!playingMusic || hoveringPause ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtil.drawRound(x + albumCoverSize / 2f - 22.5f, y + albumCoverSize / 2f - 22.5f, 45, 45, 23, ColorUtil.applyOpacity(circleColor, (float) (.47 * playAnimation.getOutput().floatValue())));

        CustomFont iconFont40 = iconFont.size(40);
        String playIcon = currentPlayingContext.getIs_playing() ? FontUtil.PLAY : FontUtil.PAUSE;



    }


    @Override
    public void onEnable() {
        if (mc.thePlayer == null) {
            toggle();
            return;
        }
        if (buttonAnimations == null) {
            buttonAnimations = new HashMap<>();
            for (String button : buttons) {
                buttonAnimations.put(button, new DecelerateAnimation(250, 1, Direction.BACKWARDS));
            }
        }

        String clientID = this.clientID.getString();
        if (api == null) api = new SpotifyAPI();

        if (clientID.equals("")) {
            clientID = getClientIDFromJson();
            if (clientID.equals("")) {
                toggleSilent();
                return;
            }
        }

        api.build(clientID);
        setClientID(clientID);
        api.startConnection();
        super.onEnable();
    }


    public void setClientID(String clientID) {
        JsonObject keyObject = new JsonObject();
        keyObject.addProperty("clientID", clientID);
        try {
            Writer writer = new BufferedWriter(new FileWriter(SpotifyAPI.CLIENT_ID_DIR));
            SpotifyAPI.GSON.toJson(keyObject, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientIDFromJson() {
        JsonObject fileContent;
        try {
            fileContent = JsonParser.parseReader(new FileReader(SpotifyAPI.CLIENT_ID_DIR)).getAsJsonObject();
            if (fileContent.has("clientID")) {
                return fileContent.get("clientID").getAsString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        openYoutubeTutorial();
        NotificationManager.post(NotificationType.WARNING, "Error", "No Client ID found");
        return "";
    }

    public void openYoutubeTutorial() {
        IOUtils.openLink("https://www.youtube.com/watch?v=3jOR29h1i40");
    }


}
