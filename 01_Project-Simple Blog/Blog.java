package tw.com.flag.diaryya;

/**
 * Created by Tony on 2017/10/31.
 */

public class Blog {
    private String title;
    private String content;
    private String image;
    private String username;    //getter and shetter -> press command+N

    public Blog(){

    }

    public Blog(String title, String content, String image, String username) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.username=username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
