package rs.ac.uns.ftn.sportly.ui.model;

public class Message {
    private String text;
    private boolean mine;

    public Message(String text, boolean mine) {
        this.text = text;
        this.mine = mine;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
}
