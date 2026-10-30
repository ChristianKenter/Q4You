package app.entities;

public class VideoFile {
    private String name;
    private String type;
    private String fileName;
    private byte[] data;

    public VideoFile(String name, String type, String fileName, byte[] data) {
        this.name = name;
        this.type = type;
        this.fileName = fileName;
        this.data = data;
    }

    public VideoFile(String name, String type, byte[] data) {
        this(name, type, name, data);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}