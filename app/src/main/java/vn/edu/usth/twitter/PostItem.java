package vn.edu.usth.twitter;


public class PostItem {

    private String name,content, id;
    private String contentImageUrl, profileImgUrl;
    private int comment, rt, like;
    /*String name, String id, String content, int profileImg, int comment, int rt, int like*/
    public PostItem(String name, String profileImg, String id, String content, String contentImageUrl) {
        this.name = name;
        this.profileImgUrl = profileImg;
        this.id = id;
        this.content = content;
        this.contentImageUrl = contentImageUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public  void setId(String id){
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImg) {
        this.profileImgUrl = profileImg;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(String imageUrl) { // Corrected method name
        this.contentImageUrl = imageUrl;
    }

}