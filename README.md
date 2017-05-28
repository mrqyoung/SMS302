SMS302
===============

短信或者来电转发到 Email

 * 使用 256-bit AES 加密本地数据和短信数据
 * 邮箱自己发送给自己接收，方便配置邮箱过滤器
 * 可配置 SMTP 服务器，默认仅支持 ssl 模式
 * 可选择发送加密短信或者明文短信


# 源码使用方式

Clone 或者下载代码，修改加密参数如 `IV` 和 `KEY`，构建生成 release 安装包。
或者修改加密方式等其它任何细节后构建。


# 直接下载 release 版本 

[SMS302-V0.2-release.apk](https://github.com/mrqyoung/SMS302-V0.2-release.apk)
（建议使用源码并修改加密参数后生成只属于你自己的安装包）

## App 使用帮助

1. [Email] 输入「@Help」点击按钮显示帮助
2. [Email] 输入「@SetKey」，并在[Password]中输入密码，点击按钮设置主密码
3. [Email] 输入邮箱，并在[Password]中输入密码，点击按钮设置邮箱信息
    3a. 若在 2 中未设置主密码，则转发的短信不加密
4. [Email] 输入「@Test」点击按钮发送测试邮件
5. [Email] 输入「@SetSMTP」，并在[Password]中输入"HOST:PORT"可设置邮箱服务器
    5a. 默认的邮箱服务器为QQ邮箱"smtp.qq.com:587"
6. 修改任何数据只需直接重设；SetKey后需要重设邮箱密码
7. [Email] 输入「@Clear」点击按钮清空所有密码
8. 收到加密短信后，复制短信内容到 `SMS302` 的 [Email] 输入框，点击帮助文字显示出即可解密。注意：请使用「@SetKey」在两台手机上设置同样的主密码


## AES 加密部分使用了 [AESCrypt-Android ](https://github.com/scottyab/AESCrypt-Android)

使用了 `com.scottyab.aescrypt.AESCrypt` (Copyright (c) 2014 Scott Alexander-Bown)
项目地址：https://github.com/scottyab/AESCrypt-Android
文件地址：https://github.com/scottyab/AESCrypt-Android/blob/master/aescrypt/src/main/java/com/scottyab/aescrypt/AESCrypt.java


## Licence

    Copyright 2017 Mr.Q.Young (Yorn)
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 

