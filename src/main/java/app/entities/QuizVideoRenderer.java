package app.entities;

import app.dto.OllamaDTO;
import app.service.FFmpegService;
import java.util.List;


public final class QuizVideoRenderer {

    public static byte[] render(byte[] backgroundImage, byte[] questionAudio, byte[] countdownAudio, byte[] answerAudio, byte[] backgroundMusic, OllamaDTO caption) throws Exception {

        VideoInstruction instruction = new VideoInstruction();

        instruction.parseCaption(caption);
        byte[] questionOverlay = instruction.createQuestionOverlay();
        byte[] countdownOverlay = instruction.createCountdownOverlay();
        byte[] answerOverlay = instruction.createAnswerOverlay();

        List<VideoFile> files = List.of(
                new VideoFile("Background image", "image", backgroundImage),
                new VideoFile("Question voiceover", "audio", questionAudio),
                new VideoFile("Countdown sound", "audio", countdownAudio),
                new VideoFile("Answer voiceover", "audio", answerAudio),
                new VideoFile("Background music", "audio", backgroundMusic),
                new VideoFile("Question overlay", "image", questionOverlay),
                new VideoFile("Countdown overlay", "image", countdownOverlay),
                new VideoFile("Answer overlay", "image", answerOverlay)
        );

        VideoRequest request = new VideoRequest(files, instruction.instruction());
        FFmpegService service = new FFmpegService();
        return service.ffmpegAPI(request);
    }


}