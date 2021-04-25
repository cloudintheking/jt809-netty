package io.github.cloudintheking.jt809.utils;

import io.github.cloudintheking.jt809.attribute.Attributes;
import io.github.cloudintheking.jt809.attribute.Session;
import io.netty.channel.Channel;

public class SessionUtil {
    public static boolean hasLogin(Channel channel) {

        return getSession(channel) != null;
    }

    public static Session getSession(Channel channel) {

        return channel.attr(Attributes.SESSION).get();
    }
}
