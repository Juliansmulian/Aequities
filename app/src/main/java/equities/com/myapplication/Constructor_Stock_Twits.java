package equities.com.myapplication;


public class Constructor_Stock_Twits {
   String user_name;
   String message_time;
   String message;
   String user_image_url;
   String message_link;
   public String getUser_name() {
     return user_name;
   }  
   public void setUser_name(String user_name) {
     this.user_name = user_name;
   }  
   public String getMessage_time() {
     return message_time;
   }  
   public void setMessage_time(String message_time) {
     this.message_time = message_time;
   }  
   public String getMessage() { return message; }
   public void setMessage(String message){this.message =message;}

   public String getUser_image_url(){return user_image_url;}
   public void setUser_image_url(String user_image_url){this.user_image_url =user_image_url;}

    public void setMessage_link(String message_link) {
        this.message_link = message_link;
    }

    public String getMessage_link() {
        return message_link;
    }
}