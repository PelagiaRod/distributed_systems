package models;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Topic implements Serializable {
    private String channelName;

    public Topic(String name) {
        this.channelName = name;
    }

    public Topic() {
    }

    public void setchannelName(String n) {
        this.channelName = n;
    }

    public String getChannelName() {
        return this.channelName;
    }

    @Override
    public String toString() {
        String name = "The name of this channel is " + this.channelName;
        return name;
    }

    public String md5HashCode() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(this.channelName.getBytes(), 0, this.channelName.length());
            BigInteger no = new BigInteger(1, md.digest());
            return no.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
