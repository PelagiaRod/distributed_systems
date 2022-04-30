import java.io.Serializable;


public class Topic implements Serializable {
    private String channelName;

    public Topic(String name){
        this.channelName = name;
    }

    public Topic(){}

    public void setchannelName(String n){
        this.channelName = n;
    }

    public String getChannelName(){
        return this.channelName;
    }

    @Override
    public String toString()    {
        String name = "The name of this channel is " + this.channelName;
        return name;
    }

}




