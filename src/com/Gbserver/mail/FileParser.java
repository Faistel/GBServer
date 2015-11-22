package com.Gbserver.mail;

import com.Gbserver.variables.ConfigManager;
import org.bukkit.OfflinePlayer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class FileParser {
    /*
    -={
    ---HEADER START---
    SENDER=
    RECEIVER=
    TIME=
    SUBJECT=
    READ=false/true
    ---HEADER END---
    Hello!

    Bye.
    }=-
     */
    private static FileParser instance = new FileParser();
    public static FileParser getInstance(){return instance;}
    public List<Message> getAllStored() throws IOException, ParseException {
        List<String> lines = Files.readAllLines(MailMan.datfile, Charset.defaultCharset());
        int playerline = -5;
        int playerend = lines.size();
        boolean in = false;
        List<Message> messages = new LinkedList<>();
        String currentEntry = "";
        for(String line : lines){
            if(line.equals("-={")){
                if(in) throw new ParseException("Divider entry inside a divider entry on line " + lines.indexOf(line), 1);
                in = true;
                currentEntry = "";
            }else if(line.equals("}=-")){
                if(!in) throw new ParseException("Divider exit not inside a divider entry on line " + lines.indexOf(line), 1);
                in = false;
                messages.add(Message.parseConfigEntry(currentEntry));
            }else{
                if(in){
                    currentEntry += line + "\n";
                }else{
                    if(!line.startsWith("//") || !line.isEmpty()) throw new ParseException("Unknown field outside divider entry: " + line, 1);
                }
            }

        }
        return messages;
    }

    public List<Message> getMessagesOf(OfflinePlayer player) throws IOException, ParseException {
        List<Message> msgs = new LinkedList<>();
        for(Message msg : getAllStored()){
            if(msg.getReceiver().equals(player)){
                msgs.add(msg);
            }
        }
        return msgs;
    }

    public void saveMessage(Message msg) throws IOException {
        Files.write(MailMan.datfile, msg.getConfigEntry().getBytes(), StandardOpenOption.APPEND);
    }
}
