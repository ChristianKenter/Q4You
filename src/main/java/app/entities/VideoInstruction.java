package app.entities;

import app.dto.OllamaDTO;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoInstruction {
    String question;
    List<String> options = new ArrayList<>();
    String answer;

    private static final int WIDTH = 1080, HEIGHT = 1920, FPS = 25;
    private static final int QUESTION_SECONDS = 6, COUNTDOWN_SECONDS = 5, ANSWER_SECONDS = 4;
    private static final int QUESTION_FRAMES = QUESTION_SECONDS * FPS, COUNTDOWN_FRAMES = COUNTDOWN_SECONDS * FPS, ANSWER_FRAMES = ANSWER_SECONDS * FPS;
    private static final int TOTAL_SECONDS = QUESTION_SECONDS + COUNTDOWN_SECONDS + ANSWER_SECONDS;
    private static final int QUESTION_X = 70, QUESTION_Y = 230, QUESTION_WIDTH = WIDTH - 140, QUESTION_HEIGHT = 300;
    private static final int OPTION_X = 70, OPTION_Y = 580, OPTION_WIDTH = WIDTH - 140, OPTION_HEIGHT = 145, OPTION_GAP = 25;
    private static final Color WHITE = Color.WHITE, GREEN = new Color(40, 220, 115), BLACK = new Color(0, 0, 0, 220);
    private static final Font QUESTION_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 46);
    private static final Font OPTION_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 38);
    private static final Pattern ANSWER_PATTERN = Pattern.compile("(?i)^\\s*([A-D])[.) :]\\s*(.*)$");

    public void parseCaption(OllamaDTO response) {
        question = response.getQuestion();
        answer = response.getAnswer().replaceFirst("(?i)^\\s*Answer\\s*:?\\s*", "").trim();
        options = new ArrayList<>();
        for (String line : response.getOptions()) {
            if (line.matches("(?i)^\\s*[A-D][.) :]\\s*.+")) options.add(line.trim());
        }
    }

    public List<String> instruction() {
        int totalFrames = QUESTION_FRAMES + COUNTDOWN_FRAMES + ANSWER_FRAMES;
        String zoom = "if(lt(on," + QUESTION_FRAMES + "),1+0.35*on/" + QUESTION_FRAMES + ",if(lt(on," + (QUESTION_FRAMES + COUNTDOWN_FRAMES) + "),1.35,max(1.0,1.35-0.35*(on-" + (QUESTION_FRAMES + COUNTDOWN_FRAMES) + ")/" + ANSWER_FRAMES + ")))";
        String filter = "[0:v]scale=1200:2134:force_original_aspect_ratio=increase,crop=1200:2134,zoompan=z='" + zoom + "':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=1:s=" + WIDTH + "x" + HEIGHT + ":fps=" + FPS + ",trim=end_frame=" + totalFrames + ",setpts=PTS-STARTPTS[bg];"
                + "[5:v]format=rgba,trim=end_frame=" + totalFrames + ",setpts=PTS-STARTPTS[qoverlay];"
                + "[6:v]format=rgba,trim=end_frame=" + totalFrames + ",setpts=PTS-STARTPTS[countoverlay];"
                + "[7:v]format=rgba,trim=end_frame=" + totalFrames + ",setpts=PTS-STARTPTS[aoverlay];"
                + "[bg][qoverlay]overlay=0:0:enable='lt(t," + TOTAL_SECONDS + ")'[v1];"
                + "[v1][countoverlay]overlay=0:0:enable='between(t," + QUESTION_SECONDS + "," + (QUESTION_SECONDS + COUNTDOWN_SECONDS) + ")'[v2];"
                + "[v2][aoverlay]overlay=0:0:enable='gte(t," + (QUESTION_SECONDS + COUNTDOWN_SECONDS) + ")',format=yuv420p[vout];"
                + "[1:a]aresample=44100,aformat=sample_rates=44100:channel_layouts=stereo,apad,atrim=duration=" + QUESTION_SECONDS + ",asetpts=PTS-STARTPTS[q];"
                + "[2:a]aresample=44100,aformat=sample_rates=44100:channel_layouts=stereo,apad,atrim=duration=" + COUNTDOWN_SECONDS + ",asetpts=PTS-STARTPTS[c];"
                + "[3:a]aresample=44100,aformat=sample_rates=44100:channel_layouts=stereo,apad,atrim=duration=" + ANSWER_SECONDS + ",asetpts=PTS-STARTPTS[a];"
                + "[q][c][a]concat=n=3:v=0:a=1[voice];"
                + "[4:a]aresample=44100,aformat=sample_rates=44100:channel_layouts=stereo,volume=0.2,atrim=duration=" + TOTAL_SECONDS + ",asetpts=PTS-STARTPTS[music];"
                + "[voice][music]amix=inputs=2:duration=first:dropout_transition=0[aout]";

        List<String> args = new ArrayList<>(List.of(
                "-y", "-loop", "1", "-framerate", String.valueOf(FPS), "-i", "{{input:0}}",
                "-i", "{{input:1}}", "-i", "{{input:2}}", "-i", "{{input:3}}",
                "-stream_loop", "-1", "-i", "{{input:4}}",
                "-loop", "1", "-framerate", String.valueOf(FPS), "-i", "{{input:5}}",
                "-loop", "1", "-framerate", String.valueOf(FPS), "-i", "{{input:6}}",
                "-loop", "1", "-framerate", String.valueOf(FPS), "-i", "{{input:7}}",
                "-filter_complex", filter, "-map", "[vout]", "-map", "[aout]",
                "-t", String.valueOf(TOTAL_SECONDS), "-r", String.valueOf(FPS),
                "-c:v", "libx264", "-preset", "veryfast", "-crf", "20",
                "-pix_fmt", "yuv420p", "-c:a", "aac", "-b:a", "192k",
                "-movflags", "+faststart", "{{output}}"
        ));
        return args;
    }

    public byte[] createQuestionOverlay() throws IOException {
        BufferedImage image = transparentImage();
        Graphics2D g = graphics(image);
        g.setColor(BLACK);
        g.fillRoundRect(QUESTION_X, QUESTION_Y, QUESTION_WIDTH, QUESTION_HEIGHT, 38, 38);
        g.setFont(QUESTION_FONT);
        g.setColor(WHITE);
        drawCenteredWrappedText(g, question, QUESTION_X + 35, QUESTION_Y, QUESTION_WIDTH - 70, QUESTION_HEIGHT, 58, 4);

        g.setFont(OPTION_FONT);
        for (int i = 0; i < options.size(); i++) {
            int y = OPTION_Y + i * (OPTION_HEIGHT + OPTION_GAP);
            g.setColor(BLACK);
            g.fillRoundRect(OPTION_X, y, OPTION_WIDTH, OPTION_HEIGHT, 30, 30);
            g.setColor(WHITE);
            drawWrappedText(g, options.get(i), OPTION_X + 30, y + 52, OPTION_WIDTH - 60, 46, 2);
        }

        g.dispose();
        return toPng(image);
    }

    public byte[] createCountdownOverlay() throws IOException {
        BufferedImage image = transparentImage();
        Graphics2D g = graphics(image);
        int x = 100, y = 1550, width = WIDTH - 200, height = 32;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x, y, width, height, height, height);
        g.setColor(GREEN);
        g.fillRoundRect(x, y, width, height, height, height);
        g.dispose();
        return toPng(image);
    }

    public byte[] createAnswerOverlay() throws IOException {
        BufferedImage image = transparentImage();
        Graphics2D g = graphics(image);
        Matcher answerMatcher = ANSWER_PATTERN.matcher(answer.trim());
        String answerLetter = answerMatcher.matches() ? answerMatcher.group(1) : "";
        String answerText = answerMatcher.matches() ? answerMatcher.group(2).trim() : answer.trim();

        for (int i = 0; i < options.size(); i++) {
            Matcher optionMatcher = ANSWER_PATTERN.matcher(options.get(i));
            if (!optionMatcher.matches()) continue;

            String letter = optionMatcher.group(1);
            String text = optionMatcher.group(2).trim();
            boolean correct = (!answerLetter.isEmpty() && letter.equalsIgnoreCase(answerLetter))
                    || text.equalsIgnoreCase(answerText);

            if (!correct) continue;

            int y = OPTION_Y + i * (OPTION_HEIGHT + OPTION_GAP);
            g.setColor(GREEN);
            g.fillRoundRect(OPTION_X, y, OPTION_WIDTH, OPTION_HEIGHT, 30, 30);
            g.setColor(Color.BLACK);
            g.setFont(OPTION_FONT);
            drawWrappedText(g, options.get(i), OPTION_X + 30, y + 52, OPTION_WIDTH - 60, 46, 2);
            break;
        }

        g.dispose();
        return toPng(image);
    }

    private static BufferedImage transparentImage() {
        return new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D graphics(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g;
    }

    private static void drawCenteredWrappedText(Graphics2D g, String text, int x, int y, int maxWidth, int boxHeight, int lineHeight, int maxLines) {
        if (text == null || text.isBlank()) return;

        FontMetrics metrics = g.getFontMetrics();
        List<String> lines = wrapText(text, metrics, maxWidth, maxLines);
        int totalHeight = lines.size() * lineHeight;
        int baseline = y + (boxHeight - totalHeight) / 2 + (lineHeight - metrics.getHeight()) / 2 + metrics.getAscent();

        for (String line : lines) {
            g.drawString(line, x + (maxWidth - metrics.stringWidth(line)) / 2, baseline);
            baseline += lineHeight;
        }
    }

    private static void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth, int lineHeight, int maxLines) {
        if (text == null || text.isBlank()) return;

        FontMetrics metrics = g.getFontMetrics();
        for (String line : wrapText(text, metrics, maxWidth, maxLines)) {
            g.drawString(line, x, y);
            y += lineHeight;
        }
    }

    private static List<String> wrapText(String text, FontMetrics metrics, int maxWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.trim().split("\\s+")) {
            String candidate = line.isEmpty() ? word : line + " " + word;

            if (metrics.stringWidth(candidate) > maxWidth && !line.isEmpty()) {
                lines.add(line.toString());
                if (lines.size() >= maxLines) return lines;
                line.setLength(0);
                line.append(word);
            } else {
                if (!line.isEmpty()) line.append(' ');
                line.append(word);
            }
        }

        if (!line.isEmpty() && lines.size() < maxLines) lines.add(line.toString());
        return lines;
    }

    private static byte[] toPng(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }
}
