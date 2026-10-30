package app.controllers.impl;

import app.controllers.IController;
import app.dto.OllamaDTO;
import app.entities.QuizVideoRenderer;
import app.service.KokoroService;
import app.service.OllamaService;
import app.service.ResourceService;
import io.javalin.http.Context;

public class VideoController implements IController {

    public void create(Context ctx) throws Exception {
        String category = ctx.formParam("category");

        String prompt =
                "Generate exactly one factual multiple-choice quiz question about " + category + ".\n" +
                        "The question must be about " + category + ", not another subject.\n" +
                        "Write exactly four answer options labeled A., B., C., and D., each on its own line.\n" +
                        "All four options must belong to the category and be relevant to the question.\n" +
                        "Exactly one option must be correct. The other three must be incorrect.\n" +
                        "Determine which option is correct before writing the answer line.\n\n" +
                        "The output must contain only these parts, in this order:\n" +
                        "1. The question.\n" +
                        "2. Four answer options, one per line.\n" +
                        "3. One final answer line.\n\n" +
                        "The final answer line must contain the word Answer, the correct letter, " +
                        "three periods, a space, the exact text of the correct option, and three periods.\n" +
                        "Copy the correct option text exactly. Do not substitute another option.\n" +
                        "Do not repeat the answer, add explanations, or include any extra text.\n" +
                        "Do not include blank lines.";

        OllamaService ollamaService = new OllamaService();
        OllamaDTO ollamaResponse = ollamaService.ollamaAPI(prompt);
        System.out.println(ollamaResponse);

        KokoroService kokoroService = new KokoroService();
        byte[] kokoroResponseQuestion = kokoroService.kokoroAPI(ollamaResponse.getQuestion());
        byte[] kokoroResponseAnswer = kokoroService.kokoroAPI(ollamaResponse.getAnswer());

        ResourceService resourceService = new ResourceService();
        byte[] imageResponse = resourceService.imageAPI("27.jpg");
        byte[] countDownResponse = resourceService.countdownAPI("countdown.wav");
        byte[] backgroundMusicResponse = resourceService.backgroundMusicAPI("backgroundmusic.wav");

        byte[] video = QuizVideoRenderer.render(
                imageResponse,
                kokoroResponseQuestion,
                countDownResponse,
                kokoroResponseAnswer,
                backgroundMusicResponse,
                ollamaResponse
        );
        ctx.contentType("video/mp4");
        ctx.result(video);
    }

    public void read(Context ctx){
    }

    public void update(Context ctx){
    }

    public void delete(Context ctx){
    }
}
