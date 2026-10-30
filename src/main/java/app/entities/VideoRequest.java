package app.entities;

import java.util.List;

public class VideoRequest {
    private List<VideoFile> files;
    private List<String> instructions;

    public VideoRequest(List<VideoFile> files, List<String> instructions) {
        this.files = files;
        this.instructions = instructions;
    }

    public List<VideoFile> getFiles() {
        return files;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setFiles(List<VideoFile> files) {
        this.files = files;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
}