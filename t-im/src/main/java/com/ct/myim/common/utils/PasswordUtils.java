package com.ct.myim.common.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.security.KeyPair;

/**
 * 密码工具
 */
public class PasswordUtils {

    /**
     * 非对称加密私钥
     */
    private final static String PRIVATE_KEY="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAI131txYzNfQPZuLpTNIXMR6zzHJ395SNdx1lPRhNtllz08jiD+UpCSlZh0NlgEqPf1qoAP3vgsWllMmW1fa2wTJvlZRqrxkYMwn/qIOFmY+FE6Puisr9WWKcDNdfw0LUK3k0aCHq/0vAKAT9jMsgxF8WWLt8TUdhTEEfF79Rr01AgMBAAECgYAwilkf/Bw1RM+OyoihoweppxXm2ITHDOBMR9AXVi/ywjsyxVdfsYnsxQ/XkizRM4RXqrlDA0OjHFnyJ+wZpg+V+bgB/jsV+gDPmdqnKb8wRYITRNJp7xL0hjMutw7CZqtW+C7s/Rnoo4Kf+d/2HAqLDZLk50X8DKtJbp5e0RXkVQJBAP9TcScN4d8FegZ+74YmoCGE1sLdFfOdlF2Zvujy2Z0U2oHW54hOaVX8wDby8q/mdQr4gjXfKYga93fJaAH9fycCQQCN13LDKYQuU2PzgM0tXryqfyJLJHoaSiM+QlWChbH5ZiU+3pEyj+klWwn35zw0yrSNueARdjN9YPTv6n4hp5pDAkBkBgJL9GpJ/lFBfjTDbxpIR0wC5q2U1GStDm/1vj12Bhdbyh7GDUHCx9NdM2vTcQrlE1mfVR9mDHpp2OL/9Jj9AkAf03TTp5AaGOn3bDIdeQFaPDaEp8Wh+nqX8KhhJgB+FlZLGgN4VeQZVqkQ79iJe+YxWsCVCnGZM1UiM/pjMMkjAkAuWf7zvZYvVPPiD7U5GKQoINxLrPtne18uS/dI49USskyObuGxFec/AprmDGR8sZC3XvSmKmjuCD/noDn31mlX";

    /**
     * 非对称加密公钥
     */
    private final static String PUBLIC_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNd9bcWMzX0D2bi6UzSFzEes8xyd/eUjXcdZT0YTbZZc9PI4g/lKQkpWYdDZYBKj39aqAD974LFpZTJltX2tsEyb5WUaq8ZGDMJ/6iDhZmPhROj7orK/VlinAzXX8NC1Ct5NGgh6v9LwCgE/YzLIMRfFli7fE1HYUxBHxe/Ua9NQIDAQAB";


    /**
     * 密码加密
     * @param pwd
     * @return
     */
    public static String encryptPwd(String pwd){
        RSA rsa = new RSA(PRIVATE_KEY, PUBLIC_KEY);
       return rsa.encryptHex(pwd, KeyType.PrivateKey);
    }

    /**
     * 密码解密
     * @param pwd
     * @return
     */
    public static String decryptStrPwd(String pwd){
        RSA rsa = new RSA(PRIVATE_KEY, PUBLIC_KEY);
        return rsa.decryptStr(pwd, KeyType.PublicKey);
    }

}
