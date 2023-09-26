package vn.edu.usth.twitter;


public class PostItem {

    private String name,content, id;
    private String imageUrl;
    private int profileImg,imageContent;
    private int comment, rt, like;
    /*String name, String id, String content, int profileImg, int comment, int rt, int like*/
    public PostItem(String name, int profileImg, String id, String content, String imageUrl) {
        this.name = name;
        this.profileImg = profileImg;
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
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

    public int getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(int profileImg) {
        this.profileImg = profileImg;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) { // Corrected method name
        this.imageUrl = imageUrl;
    }

}