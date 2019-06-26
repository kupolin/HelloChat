package me.jonlin.android.hellochat;

public class InstantMessage
{
    private String message;
    private String author;

    public InstantMessage(){}

    public InstantMessage(String msg, String auth)
    {
        this.message = msg;
        this.author = auth;
    }

    public String getMessage()
    {
        return message;
    }

    public String getAuthor()
    {
        return author;
    }
}
