package ca.polymtl.inf8480.tp1.shared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jelacs on 27/01/19.
 */
public class Fichier {

    private String fileName;
    private byte[] content;
    private boolean lock;
    private byte[] checksum;

    public Fichier(String name) throws java.io.IOException {
        this(name, null);

        byte[] b = null;

        try{
            Path filePath = Paths.get(Paths.get("").toAbsolutePath().toString() + '/' + name);
            b = Files.readAllBytes(filePath);
        } catch (IOException e){
            e.printStackTrace();
        }

        this.setContent(b);

    }

    public Fichier(String name, byte[] content) throws java.io.IOException {
        this.fileName = name;
        this.content = content;
        this.lock = Boolean.FALSE;
        this.setChecksum();
    }

    public String getfileName() {
        return fileName;
    }

    public void setfileName(String name) {
        this.fileName = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) throws java.io.IOException{
        this.content = content;
        this.writeContent(content);
    }

    public void writeContent(byte[] content) throws java.io.IOException {
        try {
            Path filePath = Paths.get(Paths.get("").toAbsolutePath().toString() + '/' + fileName);
            Files.write(filePath, content);
        } catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    public boolean getLock() {
        return lock;
    }

    public void setLock() {
        this.lock = Boolean.TRUE;
    }

    public void unlock(){
        this.lock = Boolean.FALSE;
    }

    public byte[] getChecksum() {
        return checksum;
    }


    public void setChecksum() throws java.io.IOException {
        byte[] d = null;

        try{
            Path filePath = Paths.get(Paths.get("").toAbsolutePath().toString() + '/' + fileName);
            byte[] b = Files.readAllBytes(filePath);
            d = MessageDigest.getInstance("MD5").digest(b);
        } catch (IOException e){
            e.printStackTrace();
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        this.checksum = d;
    }

    @java.lang.Override
    public java.lang.String toString(){
        return "Fichier {" +
                "nom='" + fileName + '\'' +
                ", contenu=" + java.util.Arrays.toString(content) +
                ", lockBy=" + java.lang.Boolean.toString(lock) +
                "}";
    }
}
