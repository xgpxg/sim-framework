package com.yao2san.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RedisCommand {
    private static final List<String> ALL_COMMANDS = new ArrayList<>(Arrays.asList(
            ""
    ));

    public static String getCommandSting(String command) {
        String cmd = getCommand(command);
        KeysAndArgs keysAndArgs = getKeysAndArgs(command);
        int keysSize = keysAndArgs.keys.size();
        int argsSize = keysAndArgs.args.size();
        List<String> keys = new ArrayList<>();
        List<String> args = new ArrayList<>();
        for (int i = 0; i < keysSize; i++) {
            keys.add("KEYS[" + (i + 1) + "]");
        }
        for (int i = 0; i < argsSize; i++) {
            args.add("ARGV[" + (i + 1) + "]");
        }
        StringBuilder finalCmd = new StringBuilder();
        finalCmd.append("local result = redis.call('");
        finalCmd.append(cmd);
        finalCmd.append("'");
        if (keys.size() > 0) {
            finalCmd.append(",");
            finalCmd.append(String.join(",", keys));
        }
        if (args.size() > 0) {
            finalCmd.append(",");
            finalCmd.append(String.join(",", args));
        }
        finalCmd.append(") \n  return cjson.encode(result)");
        return finalCmd.toString();
    }

    /**
     * 提取命令
     *
     * @param command 原始命令
     */
    private static String getCommand(String command) {
        return command.trim().split(" ")[0];
    }

    /**
     * 提取keys和args
     *
     * @param command 原始命令
     */
    public static KeysAndArgs getKeysAndArgs(String command) {
        KeysAndArgs keysAndArgs = new KeysAndArgs();
        final String cmd = getCommand(command).toUpperCase();
        String[] parts = command.split(" ");
        //final List<String> split = Arrays.asList(parts).subList(1, parts.length);
        int keyNumbers = 0;
        switch (cmd) {
            /*第一个参数为key*/
            case "GET":
            case "GETDEL":
            case "INCR":
            case "HGET":
            case "HGETALL":
            case "HMGET":
            case "EXISTS":
            case "DUMP":
            case "TTL":
            case "PTTL":
            case "TYPE":
            case "SET":
            case "GETSET":
            case "APPEND":
            case "HSET":
            case "HMSET":
            case "EXPIRE":
            case "EXPIREAT":
            case "PEXPIRE":
            case "SORT":
            case "RESORT":
            case "MOVE":
            case "RANDOMKEY":
            case "WAIT":
            case "LINDEX":
            case "LINSERT":
            case "LEN":
            case "LPOP":
            case "LPOS":
            case "LPUSH":
            case "LPUSHX":
            case "LRANGE":
            case "LREM":
            case "LSET":
            case "LTRIM":
            case "ROPOP":
            case "ROPOPLPUSH":
            case "PUSH":
            case "PUSHX":
            case "SADD":
            case "SCARD":
            case "SISMEMBER":
            case "SMISMEMBER":
            case "SMEMBERS":
            case "SPOP":
            case "SREM":
            case "SSCAN":
            case "ZADD":
            case "ZCARD":
            case "ZCOUNT":
            case "ZINCRBY":
            case "ZLEXCOUNT":
            case "ZPOPMAX":
            case "ZSCAN":
            case "ZSCORE":
            case "ZMSCORE":
            case "ZREVRANK":
            case "ZREVRANGEBYSCORE":
            case "ZREVRANGE":
            case "ZREMRANGEBYSCORE":
            case "ZREMRANGEBYRANK":
            case "ZREMRANGEBYLEX":
            case "ZREM":
            case "ZRANK":
            case "ZRANGEBYSCORE":
            case "ZREVRANGEBYLEX":
            case "ZRANGEBYLEX":
            case "ZRANGE":
            case "ZRANDMEMBER":
            case "ZPOPMIN":
            case "BITCOUNT":
            case "BITFIELD":
            case "BITPOS":
            case "DECR":
            case "DECRBY":
            case "GETBIT":
            case "GETEX":
            case "GETRANGE":
            case "INCRBY":
            case "INCRBYFLOAT":
            case "PSETEX":
            case "SETBIT":
            case "SETEX":
            case "SETNX":
            case "SETRANGE":
            case "STRALGO":
            case "STRLEN":

                keysAndArgs.keys = Collections.singletonList(parts[1]);
                if (parts.length > 2) {
                    keysAndArgs.args = new ArrayList<>();
                    keysAndArgs.args.addAll(Arrays.asList(parts).subList(2, parts.length));
                }
                break;
            /*多对key*/
            case "DEL":
            case "RENAMENX":
            case "UNLINK":
            case "SDIFF":
            case "SINTER":
            case "SUNION":
            case "MGET":
                keysAndArgs.args = new ArrayList<>();
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(1, parts.length));
                break;

            /*多对key-value*/
            case "MSET":
            case "MSETNX":
                keysAndArgs.keys = new ArrayList<>();
                keysAndArgs.args = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    if (i % 2 == 1) {
                        keysAndArgs.keys.add(parts[i]);
                    } else {
                        keysAndArgs.args.add(parts[i]);
                    }
                }
                break;
            /*后置参数*/
            case "BLPOP":
            case "BRPOP":
            case "BZPOPMIN":
            case "BZPOPMAX":
                keysAndArgs.keys = new ArrayList<>();
                keysAndArgs.args = new ArrayList<>();
                keysAndArgs.keys.addAll(Arrays.asList(parts).subList(1, parts.length - 1));
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(parts.length - 1, parts.length));
                break;
            /*后置key*/

            case "SINTERSTORE":
            case "SUNIONSTORE":
            case "ZDIFF":
            case "ZDIFFSTORE":

                keysAndArgs.keys = new ArrayList<>();
                keysAndArgs.args = new ArrayList<>();
                keysAndArgs.keys.addAll(Arrays.asList(parts).subList(2, parts.length));
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(1, 1));
                break;
            /*中置key 第2个参数为key数量*/
            case "ZINTER":
            case "ZUNION":
                keysAndArgs.keys = new ArrayList<>();
                keysAndArgs.args = new ArrayList<>();
                keyNumbers = Integer.parseInt(Arrays.asList(parts).subList(1, 2).get(0));
                keysAndArgs.keys.addAll(Arrays.asList(parts).subList(2, 2 + keyNumbers));
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(parts.length - (keyNumbers + 2), parts.length));
                break;
            /*中置key 第3个参数为key数量*/
            case "ZINTERSTORE":
            case "ZUNIONSTORE":
                keysAndArgs.keys = new ArrayList<>();
                keysAndArgs.args = new ArrayList<>();
                keyNumbers = Integer.parseInt(Arrays.asList(parts).subList(2, 3).get(0));
                keysAndArgs.keys.addAll(Arrays.asList(parts).subList(3, keyNumbers));
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(1, 2));
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(parts.length - (keyNumbers + 3), parts.length));
                break;
            /*前置2参数*/
            case "BITOP":
                keysAndArgs.keys = new ArrayList<>();
                keysAndArgs.args = new ArrayList<>();
                keyNumbers = Integer.parseInt(Arrays.asList(parts).subList(1, 2).get(0));
                keysAndArgs.keys.addAll(Arrays.asList(parts).subList(3, parts.length));
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(1, 3));
                break;
            /*其他情况 按参数处理*/
            default:
                keysAndArgs.args = new ArrayList<>();
                keysAndArgs.args.addAll(Arrays.asList(parts).subList(1, parts.length));
                break;

        }
        if (keysAndArgs.keys == null) {
            keysAndArgs.keys = new ArrayList<>();
        }
        if (keysAndArgs.args == null) {
            keysAndArgs.args = new ArrayList<>();
        }
        return keysAndArgs;
    }

    /**
     * 提取参数
     *
     * @param command 原始命令
     */
    private List<Object> getArgs(String command) {
        return null;
    }


    /**
     * 获取redis命令分组
     *
     * @param command 原始命令
     */
    private CommandGroup getGroup(String command) {
        String[] clusterGroupCommands = new String[]{
                "info"
        };
        String[] hashesGroupCommands = new String[]{
                "hget", "hset"
        };
        final String cmd = this.getCommand(command);
        if (Arrays.asList(clusterGroupCommands).indexOf(cmd) > -1) {
            return CommandGroup.CLUSTER;
        }
        if (Arrays.asList(hashesGroupCommands).indexOf(cmd) > -1) {
            return CommandGroup.HASHES;
        }


        return null;
    }

    public static class KeysAndArgs {
        @Getter
        private List<String> keys;
        @Getter
        private List<Object> args;
    }

    enum CommandGroup {
        CLUSTER,
        CONNECTION,
        GEO,
        HASHES,
        HYPER_LOG_LOG,
        KEYS,
        LISTS,
        PUB_SUB,
        SCRIPTING,
        SERVER,
        SETS,
        SORTED_SETS,
        STREAMS,
        STRINGS,
        TRANSACTIONS
    }
}
