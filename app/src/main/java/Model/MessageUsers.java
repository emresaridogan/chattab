package Model;

public class MessageUsers {

    private String id;
    private String username;
    private String imageurl;
    private String status;
    private String search;
    private String typing;


    public MessageUsers(String id, String username, String imageurl, String status, String search, String typing) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.status = status;
        this.search = search;
        this.typing = typing;
    }

    public MessageUsers() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }
}