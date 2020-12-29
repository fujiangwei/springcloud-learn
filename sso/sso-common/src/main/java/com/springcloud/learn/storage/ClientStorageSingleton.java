package com.springcloud.learn.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author kingson
 * @Description: 客户端存储单例
 * @date
 **/
public class ClientStorageSingleton {

    private static Map<String, Set<String>> clientStorageMap = Maps.newHashMap();

    private static class ClientStorageSingletonHolder {
        private static ClientStorageSingleton singleton = new ClientStorageSingleton();
    }

    public static ClientStorageSingleton getInstance() {
        return ClientStorageSingletonHolder.singleton;
    }

    public void setClientStorage(String token, String url) {
        // System.out.println("客户端退出url setClientStorage clientStorageMap " + clientStorageMap);
        if (!clientStorageMap.containsKey(token)) {
            Set<String> clientUrlList = Sets.newHashSet(url);
            clientStorageMap.put(token, clientUrlList);
        } else {
            clientStorageMap.get(token).add(url);
        }
    }

    public Set<String> getClientStorage(String token) {
        // System.out.println("客户端退出url getClientStorage clientStorageMap " + clientStorageMap);
        if (clientStorageMap.containsKey(token)) {
            return clientStorageMap.get(token);
        }

        return new HashSet<String>();
    }

    public Set<String> getAndRemoveClientStorage(String token) {
        // System.out.println("客户端退出url getAndRemoveClientStorage clientStorageMap " + clientStorageMap);
        if (clientStorageMap.containsKey(token)) {
            // remove返回值
            //      如果指定 key，返回指定键 key 关联的值，如果指定的 key 映射值为 null 或者该 key 并不存在于该 HashMap 中，此方法将返回null。
            //      如果指定了 key 和 value，删除成功返回 true，否则返回 false。
            Set<String> remove = clientStorageMap.remove(token);
            // System.out.println("客户端退出url getAndRemoveClientStorage after remove clientStorageMap " + clientStorageMap);
            return remove;
        }

        return new HashSet<String>();
    }
}